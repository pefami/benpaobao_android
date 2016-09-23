package com.example.pefami.benpaob.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/21.
 */
public class Car {
    private List<CarBrandGroup> subBrandList=new ArrayList<>();

    public List<CarBrandGroup> getSubBrandList() {
        return subBrandList;
    }
    public void setSubBrandList(List<CarBrandGroup> subBrandList) {
        this.subBrandList = subBrandList;
    }
}
