package com.example.pefami.benpaob.login.city;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.pefami.benpaob.R;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.security.SecureRandom;

public class CityListWithHeadersAdapter extends CityListAdapter<RecyclerView.ViewHolder>
        implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.city_view_item, parent, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }
    private CityOnItemClickListener cityOnItemClickListener;

    public void setCityOnItemClickListener(CityOnItemClickListener cityOnItemClickListener) {
        this.cityOnItemClickListener = cityOnItemClickListener;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final TextView textView = (TextView) holder.itemView;
        textView.setText(getItem(position).getName());
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cityOnItemClickListener!=null){
                    cityOnItemClickListener.onItemClickListener(textView,position);
                }
            }
        });
    }

    @Override
    public long getHeaderId(int position) {
        return getItem(position).getPinyin().charAt(0);
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.city_view_header, parent, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        TextView textView = (TextView) holder.itemView;
        textView.setText(String.valueOf(getItem(position).getPinyin()));
        holder.itemView.setBackgroundColor(getRandomColor());
    }

    private int getRandomColor() {
        SecureRandom rgen = new SecureRandom();
        return Color.HSVToColor(150, new float[]{
                rgen.nextInt(359), 1, 1
        });
    }
}
