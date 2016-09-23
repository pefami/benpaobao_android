package com.example.pefami.benpaob.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.pefami.benpaob.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/9/20.
 */
public class TdMarketAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<String> data;

    public TdMarketAdapter(Context mContext, ArrayList<String> data) {
        this.mContext = mContext;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data==null?0:data.size();
    }

    @Override
    public Object getItem(int i) {
        return data==null?null:data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return data==null?0:i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        String name=data.get(i);
        if(convertView==null){
            convertView= LayoutInflater.from(mContext).inflate(R.layout.td_market_item,null);
        }
        TdHolderView holder=TdHolderView.getHolder(convertView);
        holder.tv_td_name.setText(name);
        return convertView;
    }
    public static class TdHolderView{
        public TextView tv_td_name;
        public static TdHolderView getHolder(View view){
            TdHolderView holder= (TdHolderView) view.getTag();
            if(holder==null){
                holder=new TdHolderView();
                holder.tv_td_name= (TextView) view.findViewById(R.id.tv_td_name);
                view.setTag(holder);
            }
            return holder;
        }
    }
}
