package com.example.pefami.benpaob.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.pefami.benpaob.BaseActivity;
import com.example.pefami.benpaob.BaseApplication;
import com.example.pefami.benpaob.R;
import com.example.pefami.benpaob.login.DealershipActivity;
import com.example.pefami.benpaob.login.SelectTdActivity;
import com.example.pefami.benpaob.tool.UIUtils;

public class HomeActivity extends BaseActivity implements View.OnClickListener {
    private TextView tv_nearby_dealership;
    private TextView tv_td_market;
    private TextView tv_show_map;
    private TextView tv_selected_td;
    private TextView tv_start_run;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        tv_nearby_dealership= (TextView) findViewById(R.id.tv_nearby_dealership);
        tv_td_market= (TextView) findViewById(R.id.tv_td_market);
        tv_show_map= (TextView) findViewById(R.id.tv_show_map);
        tv_selected_td= (TextView) findViewById(R.id.tv_selected_td);
        tv_start_run= (TextView) findViewById(R.id.tv_start_run);
        tv_nearby_dealership.setOnClickListener(this);
        tv_td_market.setOnClickListener(this);
        tv_show_map.setOnClickListener(this);
        tv_start_run.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_nearby_dealership:
                Intent intent=new Intent(this, DealershipActivity.class);
                intent.putExtra("tdInfo",tv_selected_td.getText().toString());
                startActivity(intent);
                break;
            case R.id.tv_td_market:
                startActivity(new Intent(this, SelectTdActivity.class));
                break;
            case R.id.tv_show_map:
                startActivity(new Intent(this,RunMapActivity.class));
                finish();
                break;
            case R.id.tv_start_run:
                Intent mapIntent=new Intent(this,RunMapActivity.class);
                if(BaseApplication.isStart){
                    BaseApplication.isStart=false;
                }else{
                    BaseApplication.isStart=true;
                }
                startActivity(mapIntent);
                finish();
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        changeStartButton();
    }

    public void changeStartButton(){
        if(BaseApplication.isStart){
            tv_start_run.setText(UIUtils.getString(R.string.stop_run));
        }else{
            tv_start_run.setText(UIUtils.getString(R.string.start_run));
        }
    }
}
