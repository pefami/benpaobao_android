package com.example.pefami.benpaob.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.pefami.benpaob.R;
import com.example.pefami.benpaob.bean.Dealership;
import com.example.pefami.benpaob.tool.UIUtils;

import java.util.List;

/**
 * Created by Administrator on 2016/9/23.
 */
public class DealershipAdapter extends MyBaseAdapter<Dealership> {
    public DealershipAdapter(Context context, List<Dealership> data) {
        this.context = context;
        this.data = data;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Dealership dealership=data.get(position);
        if(convertView==null){
            convertView= UIUtils.inflate(R.layout.dealership_detail_item,parent);
        }
        DealershipHolder holder=DealershipHolder.getHolder(convertView);
        holder.tv_deal_name.setText((position+1)+"."+dealership.getName());
        holder.tv_deal_detail.setText(dealership.getTdName()+"广告牌剩余："+dealership.getTdNum()+" 张");
        return convertView;
    }
    private static class DealershipHolder{
        public TextView tv_deal_name;
        public TextView tv_deal_detail;
        public static DealershipHolder getHolder(View view){
            DealershipHolder holder= (DealershipHolder) view.getTag();
            if(holder==null){
                holder=new DealershipHolder();
                holder.tv_deal_detail= (TextView) view.findViewById(R.id.tv_deal_detail);
                holder.tv_deal_name= (TextView) view.findViewById(R.id.tv_deal_name);
                view.setTag(holder);
            }
            return holder;
        }
    }
}
