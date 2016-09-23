package com.example.pefami.benpaob.login.city;

import android.support.v7.widget.RecyclerView;

import com.example.pefami.benpaob.bean.City;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;


/**
 * Adapter holding a list of animal names of type String. Note that each item must be unique.
 */
public abstract class CityListAdapter<VH extends RecyclerView.ViewHolder>
    extends RecyclerView.Adapter<VH> {
  private ArrayList<City.CityListBean.ListsBean> items = new ArrayList<>();

  public CityListAdapter() {
    setHasStableIds(true);
  }

  public void add(City.CityListBean.ListsBean object) {
    items.add(object);
    notifyDataSetChanged();
  }

  public void add(int index, City.CityListBean.ListsBean object) {
    items.add(index, object);
    notifyDataSetChanged();
  }

  public void addAll(Collection<? extends City.CityListBean.ListsBean> collection) {
    if (collection != null) {
      items.addAll(collection);
      notifyDataSetChanged();
    }
  }

  public void addAll(City.CityListBean.ListsBean... items) {
    addAll(Arrays.asList(items));
  }

  public void clear() {
    items.clear();
    notifyDataSetChanged();
  }

  public void remove(String object) {
    items.remove(object);
    notifyDataSetChanged();
  }

  public City.CityListBean.ListsBean getItem(int position) {
    return items.get(position);
  }

  @Override
  public long getItemId(int position) {
    return getItem(position).hashCode();
  }

  @Override
  public int getItemCount() {
    return items.size();
  }
}
