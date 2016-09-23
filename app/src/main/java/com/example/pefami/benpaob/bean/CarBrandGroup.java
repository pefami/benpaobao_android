package com.example.pefami.benpaob.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/21.
 */
public class CarBrandGroup {
    private String pinyin;
    private List<CarBrand> lists=new ArrayList<>();

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public List<CarBrand> getLists() {
        return lists;
    }

    public void setLists(List<CarBrand> lists) {
        this.lists = lists;
    }
}
