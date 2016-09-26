package com.example.pefami.benpaob.track;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.district.DistrictResult;
import com.baidu.mapapi.search.district.DistrictSearch;
import com.baidu.mapapi.search.district.DistrictSearchOption;
import com.baidu.mapapi.search.district.OnGetDistricSearchResultListener;
import com.baidu.mapapi.utils.DistanceUtil;
import com.example.pefami.benpaob.bean.Dealership;
import com.example.pefami.benpaob.bean.TrackPoint;
import com.example.pefami.benpaob.dao.TrackDao;
import com.example.pefami.benpaob.tool.ToastUtils;
import com.example.pefami.benpaob.tool.UIUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pefami on 2016/9/16.
 */
public class OverlayUtils {
    private Context mContext;
    public OverlayUtils(Context context){
        mContext=context;
    }

    private List<Marker> dealershipMarker=new ArrayList<>();
    /**
     * 添加标记物
     * @param analogData
     */
    public List<Marker> addMarker(List<Dealership> analogData,BaiduMap baiduMap) {
        if(baiduMap==null){
            return dealershipMarker;
        }
        baiduMap.clear();
        dealershipMarker.clear();
        for(int i=0;i<analogData.size();i++){
            Dealership dealership=analogData.get(i);
            OverlayOptions markerOptions = new MarkerOptions().flat(true).anchor(0.5f, 0.5f).icon(BitmapDescriptorFactory
                    .fromResource(UIUtils.getResource("b_poi_"+(i+1)))).position(dealership.getLocation());
            Marker marker = (Marker) baiduMap.addOverlay(markerOptions);
            dealershipMarker.add(marker);
        }
        return dealershipMarker;
    }


    private  List<Overlay> boundaryLine = new ArrayList<>();
    //显示区域边界
    public  void showBoundary(final BaiduMap baiduMap, String city, String district) {
        DistrictSearch districtSearch = DistrictSearch.newInstance();
        districtSearch.searchDistrict(new DistrictSearchOption().cityName(city).districtName(district));
        //反回结果监听
        districtSearch.setOnDistrictSearchListener(new OnGetDistricSearchResultListener() {
            @Override
            public void onGetDistrictResult(DistrictResult districtResult) {
                //获取边界坐标
                List<List<LatLng>> polylines = districtResult.getPolylines();
                for (List<LatLng> polyline : polylines) {
                    OverlayOptions polylineOptions = new PolylineOptions().points(polyline).width(10).color(Color.BLUE);
                    Overlay overlay = baiduMap.addOverlay(polylineOptions);
                    boundaryLine.add(overlay);
                }
            }
        });
    }
    //隐藏边界
    public  void hideBoundary(){
        for (Overlay overlay:boundaryLine){
            overlay.remove();
        }
        boundaryLine.clear();
    }
    private List<Overlay> historyTrack=new ArrayList<>();
    //有效路径
    private List<Overlay> validTrack=new ArrayList<>();
    private double minSpeed=20;
    private double maxSpeed=80;
    private void showValidTrack(List<TrackPoint> trackPoints, BaiduMap baiduMap){
        //有效定位点集合
        Map<Integer,List<LatLng>> validMap=new HashMap<>();
        int trackID=0;
        boolean isVaild=false;
        for(TrackPoint point:trackPoints){
            double speed=point.getSpeed();
            if(speed>=minSpeed&&speed<=maxSpeed){
                //是有效路径的定位点
                if(isVaild){
                    //说明是该路径的中间点
                    List<LatLng> latLngs = validMap.get(trackID);
                    LatLng newLatlng=new LatLng(point.getLantitude(),point.getLongitude());
                    LatLng oldLatLng = latLngs.get(latLngs.size() - 1);
                    //如果和上一点距离很小，丢弃该点
                    double distance = DistanceUtil.getDistance(oldLatLng, newLatlng);
                    if(distance>10){
                        latLngs.add(newLatlng);
                    }
                }else{
                    //说明是该路径的起点
                    isVaild=true;
                    List<LatLng> latLngs=new ArrayList<>();
                    latLngs.add(new LatLng(point.getLantitude(),point.getLongitude()));
                    validMap.put(trackID,latLngs);
                }

            }else{
                //不是有效路径的定位点
                isVaild=false;
                trackID++;
            }
        }
        //显示轨迹
        Collection<List<LatLng>> values = validMap.values();
        for(List<LatLng> polyline:values){
            if(polyline.size()>1&&baiduMap!=null){
                Log.e("Line",polyline.toString());
                OverlayOptions polylineOptions = new PolylineOptions().points(polyline).width(10).color(Color.GREEN);
                Overlay overlay = baiduMap.addOverlay(polylineOptions);
                validTrack.add(overlay);
            }
        }
    }
    //显示历史轨迹
    public void showHistoryTrack(TrackDao trackDao, BaiduMap baiduMap,long startTime){
        List<TrackPoint> trackPoints = trackDao.queryTrack(startTime);
        Map<String,List<LatLng>> polylines=new HashMap<>();
        //数据处理
        for(TrackPoint point:trackPoints){
            //该轨迹已存在
            if(polylines.containsKey(point.getTrackid())){
                List<LatLng> latLngs = polylines.get(point.getTrackid());
                LatLng newLatlng=new LatLng(point.getLantitude(),point.getLongitude());
                LatLng oldLatLng = latLngs.get(latLngs.size() - 1);
                //如果和上一点距离很小，丢弃该点
                double distance = DistanceUtil.getDistance(oldLatLng, newLatlng);
                if(distance>10){
                    latLngs.add(newLatlng);
                }
            }else{
                //该轨迹不存在
                List<LatLng> latLngs=new ArrayList<>();
                LatLng newLatlng=new LatLng(point.getLantitude(),point.getLongitude());
                latLngs.add(newLatlng);
                polylines.put(point.getTrackid(),latLngs);
            }
        }
        //显示轨迹
        Collection<List<LatLng>> values = polylines.values();
        for(List<LatLng> polyline:values){
            if(polyline.size()>1&&baiduMap!=null){
                OverlayOptions polylineOptions = new PolylineOptions().points(polyline).width(10).color(Color.BLUE);
                Overlay overlay = baiduMap.addOverlay(polylineOptions);
                historyTrack.add(overlay);
            }
        }
        //显示有效轨迹
//        showValidTrack(trackPoints,baiduMap);
        if(historyTrack.size()==0) {
            UIUtils.runingOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtils.show("该日没有行程记录");
            }
        });
    }
    }
    //隐藏历史轨迹
    public  void hideHistoryTrack(){
        for (Overlay overlay:historyTrack){
            overlay.remove();
        }
        historyTrack.clear();
        for (Overlay overlay:validTrack){
            overlay.remove();
        }
        validTrack.clear();
    }

}
