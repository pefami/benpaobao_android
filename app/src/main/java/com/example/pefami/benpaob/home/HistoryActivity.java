package com.example.pefami.benpaob.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.pefami.benpaob.Constant;
import com.example.pefami.benpaob.R;

import java.util.Calendar;

import cn.aigestudio.datepicker.cons.DPMode;
import cn.aigestudio.datepicker.views.DatePicker;

public class HistoryActivity extends AppCompatActivity {
    private DatePicker dp_history;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        dp_history= (DatePicker) findViewById(R.id.dp_history);
        //获取系统时间
        Calendar calendar=Calendar.getInstance();
        //设置日历显示日期
        dp_history.setDate(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH)+1);
        dp_history.setMode(DPMode.SINGLE);
        //设定选择监听器
        dp_history.setOnDatePickedListener(new DatePicker.OnDatePickedListener() {
            @Override
            public void onDatePicked(String date) {
                Intent intent=new Intent();
                intent.putExtra(Constant.DATE_HISTORY,date);
                setResult(Constant.DATE_RESPONSE,intent);
                finish();
            }
        });
    }
}
