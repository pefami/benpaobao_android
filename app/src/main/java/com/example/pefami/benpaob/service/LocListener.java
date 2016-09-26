package com.example.pefami.benpaob.service;

import com.baidu.location.BDLocation;

/**
 * Created by Administrator on 2016/9/26.
 */
public interface LocListener {
    void onReceiveLocation(BDLocation location);
}
