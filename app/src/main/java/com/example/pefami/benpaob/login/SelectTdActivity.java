package com.example.pefami.benpaob.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.pefami.benpaob.BaseActivity;
import com.example.pefami.benpaob.R;
import com.example.pefami.benpaob.adapter.TdMarketAdapter;

import java.util.ArrayList;

public class SelectTdActivity extends BaseActivity {
    private GridView gv_td_show;
    private ArrayList<String> data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_td);
        gv_td_show= (GridView) findViewById(R.id.gv_td_show);
        gv_td_show.setAdapter(new TdMarketAdapter(this,getData()));
        gv_td_show.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent=new Intent(getApplicationContext(),DealershipActivity.class);
                intent.putExtra("tdInfo",data.get(i));
                startActivity(intent);
            }
        });
    }
    private ArrayList<String> getData(){
        data=new ArrayList<>();
        for(int i=1;i<20;i++){
            data.add("广告"+i);
        }
        return data;
    }
}
