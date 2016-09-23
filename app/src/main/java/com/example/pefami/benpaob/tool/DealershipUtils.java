package com.example.pefami.benpaob.tool;

import com.baidu.mapapi.model.LatLng;
import com.example.pefami.benpaob.bean.Dealership;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 2016/9/23.
 */
public class DealershipUtils {

    private static String[] names={"旅行者车行","开拓者车行","驭龙车行","升龙车行","龙轩车行","汇龙车行","信达车行","恒信车行","平安车行"};
    /**
     * 获取附近车行模拟数据
     * @param tdName    广告名
     * @param location  当前所在定位点
     * @return
     */
    public static List<Dealership> getAnalogData(String tdName, LatLng location){
        List<Dealership> list=new ArrayList<>();
        Random random=new Random();
        int count= random.nextInt(10);
        for(int i=0;i<count;i++){
            Dealership dealership=new Dealership();
            dealership.setTdName(tdName);
            //根据定位点随机出一个定位点
            dealership.setLocation(getRandomLocation(location));
            dealership.setTdNum(random.nextInt(1024));
            dealership.setName(names[i]);
            list.add(dealership);
        }
        return list;
    }
    public static LatLng getRandomLocation(LatLng location){
        Random random=new Random();
        double latitude = location.latitude;
        double longitude = location.longitude;
        LatLng ranLat=new LatLng(latitude+random.nextInt(10)/1000.0,longitude+random.nextInt(10)/1000.0);
        return ranLat;
    }
}
