package com.example.pefami.benpaob.login;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.example.pefami.benpaob.BaseActivity;
import com.example.pefami.benpaob.Constant;
import com.example.pefami.benpaob.R;
import com.example.pefami.benpaob.tool.SPUtils;
import com.example.pefami.benpaob.tool.ToastUtils;
import com.example.pefami.benpaob.tool.VerifyUtils;

import java.util.Timer;
import java.util.TimerTask;

import cn.bmob.sms.BmobSMS;
import cn.bmob.sms.exception.BmobException;
import cn.bmob.sms.listener.RequestSMSCodeListener;
import cn.bmob.sms.listener.VerifySMSCodeListener;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.SaveListener;

public class RegistActivity extends BaseActivity implements View.OnClickListener {
    private EditText et_input_phone;
    private TextView et_get_verify;
    private EditText et_input_verify;
    private EditText et_input_passwd;
    private EditText et_input_repasswd;
    private CheckBox cb_agreement;
    private TextView tv_next;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        Bmob.initialize(this, Constant.BMOB_APP_KEY);
        et_input_phone= (EditText) findViewById(R.id.et_input_phone );
        et_get_verify= (TextView) findViewById(R.id.et_get_verify);
        et_get_verify.setOnClickListener(this);
        et_input_verify= (EditText) findViewById(R.id.et_input_verify);
        et_input_passwd= (EditText) findViewById(R.id.et_input_passwd);
        et_input_repasswd= (EditText) findViewById(R.id.et_input_repasswd);
        cb_agreement= (CheckBox) findViewById(R.id.cb_agreement);
        tv_next= (TextView) findViewById(R.id.tv_next);
        tv_next.setOnClickListener(this);
        cb_agreement.setOnCheckedChangeListener(checkedChangeListener);
        tv_next.setClickable(cb_agreement.isChecked());

    }
    private CompoundButton.OnCheckedChangeListener checkedChangeListener=new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            tv_next.setClickable(b);
            tv_next.setBackgroundResource(b? R.drawable.btn_blue_bg:R.drawable.btn_gray_bg);
            tv_next.setTextColor(b? Color.WHITE:Color.BLACK);
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_next:
                //请求注册
                toRegist();
                break;
            case R.id.et_get_verify:
                requestVerify();
                break;
        }
    }

    /**
     * 请求注册
     */
    private void toRegist() {
        //判断密码是否相同
        final String passwd=et_input_passwd.getText().toString();
        String repasswd=et_input_repasswd.getText().toString();
        if(TextUtils.isEmpty(passwd)||TextUtils.isEmpty(repasswd)||!passwd.equals(repasswd)){
            ToastUtils.show("两次密码输入不一致");
            return;
        }
        //
        final String phone=et_input_phone.getText().toString().trim();
        if(!VerifyUtils.isPhone(phone)){
            return;
        }
        final String verify = et_input_verify.getText().toString().trim();
        if(TextUtils.isEmpty(verify)){
            ToastUtils.show("请输入验证码");
            return;
        }
        BmobSMS.verifySmsCode(getApplicationContext(),phone,verify, new VerifySMSCodeListener() {
            @Override
            public void done(BmobException ex) {
                // TODO Auto-generated method stub
                if(ex==null){//短信验证码已验证成功
                    Log.i("bmob", "验证通过");
                    BmobUser user=new BmobUser();
                    user.setMobilePhoneNumber(phone);
                    user.setMobilePhoneNumberVerified(true);
                    user.setPassword(passwd);
                    user.setUsername(phone);
                    user.signUp(new SaveListener<BmobUser>() {
                        @Override
                        public void done(BmobUser bmobUser, cn.bmob.v3.exception.BmobException e) {
                                if(e==null){
                                    ToastUtils.show("注册成功");
                                    SPUtils.save(SPUtils.USERNAME,phone);
                                    Intent intent=new Intent(getApplicationContext(),CheckInCarActivity.class);
                                    startActivity(intent);
                                    finish();
                                }else{
                                    switch (e.getErrorCode()){
                                        case 209:
                                            ToastUtils.show("该手机号码已经存在");
                                            break;
                                    }
                                }
                        }
                    });
                }else{
                    Log.i("bmob", "验证失败：code ="+ex.getErrorCode()+",msg = "+ex.getLocalizedMessage());
                    ToastUtils.show("验证码错误");
                }
            }
        });
    }

    /**
     * 请求验证码
     */
    private void requestVerify() {
        String phone=et_input_phone.getText().toString().trim();
        if(!VerifyUtils.isPhone(phone)){
            return;
        }
        et_get_verify.setEnabled(false);
        et_get_verify.setTextColor(Color.BLACK);
        et_get_verify.setBackgroundResource(R.drawable.btn_gray_bg);
        BmobSMS.requestSMSCode(getApplicationContext(),phone, "BPB",new RequestSMSCodeListener() {
            @Override
            public void done(Integer smsId,BmobException ex) {
                if(ex==null){//验证码发送成功
                    Log.i("bmob", "短信id："+smsId);//用于查询本次短信发送详情
                    //设计验证码过时失效
                    verifyCodeTime();
                }else{
                    Log.i("bmob", "短信error："+ex);//用于查询本次短信发送详情
                    if(ex.getErrorCode()==10010){
                        ToastUtils.show("该手机号发送短信达到限制");
                    }
                    et_get_verify.setEnabled(true);
                    et_get_verify.setBackgroundResource(R.drawable.btn_blue_bg);
                    et_get_verify.setTextColor(Color.WHITE);
                }
            }
        });
    }
    private TimerTask timerTask;
    private Timer timer = new Timer();;
    private static int time=60;
    private void verifyCodeTime() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() { // UI thread
                    @Override
                    public void run() {
                        if (time <= 0) {
                            et_get_verify.setEnabled(true);
                            et_get_verify.setText(getString(R.string.get_verify));
                            et_get_verify.setBackgroundResource(R.drawable.btn_blue_bg);
                            et_get_verify.setTextColor(Color.WHITE);
                            timerTask.cancel();
                            time=60;
                        } else {
                            et_get_verify.setText(getString(R.string.get_verify) + "(" + time + ")");
                            time--;
                        }
                    }
                });
            }
        };
        timer.schedule(timerTask, 0, 1000);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            Intent intent=new Intent(this,LoginActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
