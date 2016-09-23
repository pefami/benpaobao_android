package com.example.pefami.benpaob.login.car;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.pefami.benpaob.R;
import com.example.pefami.benpaob.bean.CarType;

import java.util.List;

/**
 * Created by Administrator on 2016/9/22.
 */
public class CarTypeAdapter extends BaseAdapter {
    private Context mContext;
    private List<CarType> data;
    private String listName;
    public CarTypeAdapter(Context mContext, List<CarType> data,String listName) {
        this.mContext = mContext;
        this.data = data;
        this.listName=listName;
    }

    public void setData(List<CarType> data,String listName) {
        this.data = data;
        this.listName=listName;
    }

    @Override
    public int getCount() {
        return data==null?0:data.size();
    }

    @Override
    public Object getItem(int position) {
        return data==null?null:data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return data==null?0:position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final CarType carType=data.get(position);
        if(convertView==null){
            convertView= LayoutInflater.from(mContext).inflate(R.layout.city_view_item,null);
        }
        TextView view= (TextView) convertView;
        view.setText(carType.getSeries_name());
        view.setTextColor(0xaa000000);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(mListener!=null){
                   mListener.onItemClickListener(listName,carType.getSeries_name());
               }
            }
        });
        return view;
    }
    private CarOnItemClickListener mListener;
    public void setOnItemClickListener(CarOnItemClickListener mListener){
        this.mListener=mListener;
    }
}
