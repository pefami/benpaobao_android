package com.example.pefami.benpaob.login;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.pefami.benpaob.BaseActivity;
import com.example.pefami.benpaob.Constant;
import com.example.pefami.benpaob.R;
import com.example.pefami.benpaob.home.HomeActivity;
import com.example.pefami.benpaob.tool.SPUtils;
import com.example.pefami.benpaob.tool.ToastUtils;
import com.example.pefami.benpaob.tool.UIUtils;
import com.example.pefami.benpaob.tool.VerifyUtils;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;


public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private EditText et_username;
    private EditText et_password ;
    private TextView tv_login;
    private TextView tv_toregist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Bmob.initialize(this, Constant.BMOB_APP_KEY);
        et_username= (EditText) findViewById(R.id.et_username);
        et_password= (EditText) findViewById(R.id.et_password);
        tv_login= (TextView) findViewById(R.id.tv_login);
        tv_toregist= (TextView) findViewById(R.id.tv_toregist);
        tv_login.setOnClickListener(this);
        tv_toregist.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_login:
                //帐号登录
                login();
                break;
            case R.id.tv_toregist:
                Intent intent=new Intent(this,RegistActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    //登录方法
    private void login() {
        final String phone=et_username.getText().toString().trim();
        String passwd=et_password.getText().toString().trim();
        if(!VerifyUtils.isPhone(phone)){
            return;
        }
        if(passwd==null||passwd.length()==0){
            ToastUtils.show("密码不能为空");
            return;
        }
        BmobUser user=new BmobUser();
        user.setUsername(phone);
        user.setPassword(passwd);
        login_disable();
        //登录成功
        user.login( new SaveListener<BmobUser>() {
            @Override
            public void done(BmobUser bmobUser, BmobException e) {
                if(e==null){
                    //登录成功
                    SPUtils.save(SPUtils.USERNAME,phone);
                    ToastUtils.show("登录成功");
                    //判断用户是否已激活
                    if(SPUtils.isActivate(phone)){
                        //去首页
                        Intent intent=new Intent(UIUtils.getContext(), HomeActivity.class);
                        startActivity(intent);
                    }else{
                        //去激活页面
                        Intent intent=new Intent(UIUtils.getContext(),SelectTdActivity.class);
                        startActivity(intent);
                    }
                    finish();
                }else{
                    switch (e.getErrorCode()){
                        case 101:
                            ToastUtils.show("用户名或密码不正确");
                            break;
                    }
                    login_enable();
                }
            }
        });

    }

    private void login_enable() {
        tv_login.setText(UIUtils.getString(R.string.login));
        tv_login.setTextColor(Color.WHITE);
        tv_login.setBackgroundResource(R.drawable.btn_blue_bg);
        tv_login.setClickable(true);
    }
    private void login_disable() {
        tv_login.setText("登录中...");
        tv_login.setTextColor(Color.BLACK);
        tv_login.setBackgroundResource(R.drawable.btn_gray_bg);
        tv_login.setClickable(false);
    }
}
