package com.example.pefami.benpaob.service;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.pefami.benpaob.BaseApplication;
import com.example.pefami.benpaob.dao.TrackDao;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Administrator on 2016/9/26.
 */
public class LocService extends Service implements  ILocationManger{
    public class LocBinder extends Binder implements ILocationManger{
        @Override
        public void startLocation() {
            LocService.this.startLocation();
        }
        @Override
        public void stopLocation() {
            LocService.this.stopLocation();
        }
        @Override
        public boolean isStartLocation() {
            return LocService.this.isStartLocation();
        }

        @Override
        public void startRun() {
            LocService.this.startRun();
        }

        @Override
        public void stopRun() {
            LocService.this.stopRun();
        }
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initMyLocation();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new LocBinder();
    }

    private LocationClient mLocationClient;
    private MyLocationListener mMyLocationListener;

    /**
     * 初始化定位相关代码
     */
    private void initMyLocation() {
        // 定位初始化
        mLocationClient = new LocationClient(getApplicationContext());
        mMyLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mMyLocationListener);
        // 设置定位的相关配置
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setIsNeedAddress(true);
        option.setScanSpan(2000);
        option.setIsNeedLocationDescribe(true);
        mLocationClient.setLocOption(option);
    }

    /**
     * 开启定位服务
     */
    @Override
    public void startLocation() {
        if(!mLocationClient.isStarted()){
            mLocationClient.start();
        }
    }

    /**
     * 停止定位服务
     */
    @Override
    public void stopLocation() {
        if(mLocationClient.isStarted()){
            mLocationClient.stop();
        }
    }

    @Override
    public boolean isStartLocation() {
        return mLocationClient.isStarted();
    }

    private boolean isRun;
    @Override
    public void startRun() {
        isRun=true;
        trackid= UUID.randomUUID().toString();
    }

    @Override
    public void stopRun() {
        isRun=false;
        trackid=null;
    }

    /**
     * 实现定位回调监听
     */
    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null)
                return;
            for(LocListener listener:locListeners){
                listener.onReceiveLocation(location);
            }
            if(isRun)
            addLocationToDB(location);
        }
    }
    private static List<LocListener> locListeners=new ArrayList<>();
    public static void addLocListener(LocListener listener){
        locListeners.add(listener);
    }

    private TrackDao trackDao=new TrackDao(BaseApplication.getContext());
    private String trackid;
    //将坐标保存到数据库中
    private void addLocationToDB(BDLocation location) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TrackDao.TRACKID, trackid);
        contentValues.put(TrackDao.LANTITUDE, location.getLatitude());
        contentValues.put(TrackDao.LONGITUDE, location.getLongitude());
        contentValues.put(TrackDao.SPEED, location.getSpeed());
        contentValues.put(TrackDao.TIME, System.currentTimeMillis());
        trackDao.addTrackPoint(contentValues);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLocation();
    }

}
