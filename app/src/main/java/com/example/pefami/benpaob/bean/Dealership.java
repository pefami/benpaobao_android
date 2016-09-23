package com.example.pefami.benpaob.bean;

import com.baidu.mapapi.model.LatLng;

/**
 * Created by Administrator on 2016/9/23.
 */
public class Dealership {
    private String name;
    private String tdName;
    private LatLng location;
    private int tdNum;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTdName() {
        return tdName;
    }

    public void setTdName(String tdName) {
        this.tdName = tdName;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public int getTdNum() {
        return tdNum;
    }

    public void setTdNum(int tdNum) {
        this.tdNum = tdNum;
    }
}
