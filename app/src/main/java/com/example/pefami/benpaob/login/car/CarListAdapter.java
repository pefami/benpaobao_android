package com.example.pefami.benpaob.login.car;

import android.support.v7.widget.RecyclerView;

import com.example.pefami.benpaob.bean.CarBrand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;


/**
 * Adapter holding a list of animal names of type String. Note that each item must be unique.
 */
public abstract class CarListAdapter<VH extends RecyclerView.ViewHolder>
    extends RecyclerView.Adapter<VH> {
  private ArrayList<CarBrand> items = new ArrayList<>();

  public CarListAdapter() {
    setHasStableIds(true);
  }

  public void add(CarBrand object) {
    items.add(object);
    notifyDataSetChanged();
  }

  public void add(int index,CarBrand object) {
    items.add(index, object);
    notifyDataSetChanged();
  }

  public void addAll(Collection<? extends CarBrand> collection) {
    if (collection != null) {
      items.addAll(collection);
      notifyDataSetChanged();
    }
  }

  public void addAll(CarBrand... items) {
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

  public CarBrand getItem(int position) {
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
