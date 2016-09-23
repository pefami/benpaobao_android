package com.example.pefami.benpaob.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/21.
 */
public class CarBrand {
    private String pinyin;

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    private String brand_id;
    private String name;
    private List<CarType> lists=new ArrayList<>();

    public String getBrand_id() {
        return brand_id;
    }

    public void setBrand_id(String brand_id) {
        this.brand_id = brand_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CarType> getLists() {
        return lists;
    }

    public void setLists(List<CarType> lists) {
        this.lists = lists;
    }
}
