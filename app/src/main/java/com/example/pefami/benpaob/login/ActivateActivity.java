package com.example.pefami.benpaob.login;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.example.pefami.benpaob.BaseActivity;
import com.example.pefami.benpaob.R;
import com.example.pefami.benpaob.home.HomeActivity;
import com.example.pefami.benpaob.tool.SPUtils;
import com.example.pefami.benpaob.tool.ToastUtils;

public class ActivateActivity extends BaseActivity implements View.OnClickListener {
    private CheckBox cb_agreement;
    private TextView tv_next;
    private EditText et_activate_code;
    private String phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activate);
        cb_agreement= (CheckBox) findViewById(R.id.cb_agreement);
        tv_next= (TextView) findViewById(R.id.tv_next);
        et_activate_code= (EditText) findViewById(R.id.et_activate_code);
        tv_next.setOnClickListener(this);
        cb_agreement.setOnCheckedChangeListener(checkedChangeListener);
        tv_next.setClickable(cb_agreement.isChecked());
        Intent intent=getIntent();
        phone=intent.getStringExtra("phone");
        if(TextUtils.isEmpty(phone)){
            ToastUtils.show("非法进入");
            startActivity(new Intent(this,LoginActivity.class));
            finish();
        }
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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_next:
                activate();
                break;
        }
    }

    //激活码验证
    private void activate() {
        String code=et_activate_code.getText().toString().trim();
        if(TextUtils.isEmpty(code)){
            ToastUtils.show("激活码不能为空");
            return;
        }
        if(code.length()!=6){
            ToastUtils.show("激活码错误");
            return;
        }
        //激活码正确
        ToastUtils.show("激活成功");
        //保存激活信息
        SPUtils.save(phone,code);
        Intent intent=new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
