package com.example.pefami.benpaob.adapter;

import android.content.Context;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by Administrator on 2016/9/23.
 */
public abstract class MyBaseAdapter<T> extends BaseAdapter {
    protected Context context;
    protected List<T> data;
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
}
