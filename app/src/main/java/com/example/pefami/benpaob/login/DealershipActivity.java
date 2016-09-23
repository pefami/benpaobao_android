package com.example.pefami.benpaob.login;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.example.pefami.benpaob.BaseActivity;
import com.example.pefami.benpaob.BaseApplication;
import com.example.pefami.benpaob.R;
import com.example.pefami.benpaob.adapter.DealershipAdapter;
import com.example.pefami.benpaob.bean.Dealership;
import com.example.pefami.benpaob.tool.DealershipUtils;
import com.example.pefami.benpaob.tool.SPUtils;
import com.example.pefami.benpaob.tool.ToastUtils;
import com.example.pefami.benpaob.tool.UIUtils;
import com.example.pefami.benpaob.track.OverlayUtils;

import java.util.List;

public class DealershipActivity extends BaseActivity {
    private MapView mapView;
    private BaiduMap baiduMap;
    private ListView lv_detail;

    private LocationClient mLocationClient;//定位客户端
    public MyLocationListener mMyLocationListener;//定位的监听器
    private boolean isFristLocation = true;
    private String tdInfo;
    private OverlayUtils overlayUtils;
    private List<Dealership> analogData;
    private List<Marker> markers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_dealership);
        mapView = (MapView) findViewById(R.id.mv_dealership_map);
        baiduMap = mapView.getMap();
        lv_detail = (ListView) findViewById(R.id.lv_detail);
        Intent intent = getIntent();
        tdInfo = intent.getStringExtra("tdInfo");
//        Toast.makeText(this,"您查看的是："+ tdInfo,Toast.LENGTH_SHORT).show();
        initMyLocation();
        overlayUtils = new OverlayUtils(getApplicationContext());
        lv_detail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String phone= SPUtils.getUserName();
                if(!TextUtils.isEmpty(phone)&&!SPUtils.isActivate(phone)){
                    Intent intent=new Intent(getApplicationContext(),ActivateActivity.class);
                    intent.putExtra("phone",phone);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

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
        option.setScanSpan(0);
        option.setIsNeedLocationDescribe(true);
        mLocationClient.setLocOption(option);
    }

    /**
     * 实现实位回调监听
     */
    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {

            // map view 销毁后不在处理新接收的位置
            if (location == null || mapView == null)
                return;
            // 构造定位数据
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            // 设置定位数据
            baiduMap.setMyLocationData(locData);

            final LatLng ll = new LatLng(location.getLatitude(),
                    location.getLongitude());
            // 第一次定位时，将地图位置移动到当前位置
            if (isFristLocation) {
                //根据定位坐标和所需广告，获取附近车行的坐标及广告牌数量
                setMarker(ll);
//              Toast.makeText(getApplicationContext(),location.getCity()+":"+location.getAddrStr()+":"+location.getCityCode(),Toast.LENGTH_SHORT).show();
                isFristLocation = false;
                baiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().zoom(15).build()));
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
                baiduMap.animateMapStatus(u);
            }
        }

    }

    private DealershipAdapter dealershipAdapter;

    private void setMarker(final LatLng ll) {
        new AsyncTask<String, Integer, String>() {
            @Override
            protected String doInBackground(String... params) {
                analogData = DealershipUtils.getAnalogData(tdInfo == null ? BaseApplication.currTd : tdInfo, ll);
                //添加marker
                markers = overlayUtils.addMarker(analogData, baiduMap);
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if(analogData.size()==0){
                    ToastUtils.show("附近没有车行");
                }
                //设置详细列表
                dealershipAdapter = new DealershipAdapter(getApplicationContext(), analogData);
                lv_detail.setAdapter(dealershipAdapter);
            }
        }.execute();

        baiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                if (markers != null && markers.size() > 0) {
                    //获取标记的位置
                    final int index = markers.indexOf(marker);
                    new AsyncTask<String, Integer, String>() {
                        @Override
                        protected String doInBackground(String... params) {
                            for(int i=0;i<markers.size();i++){
                                markers.get(i).setIcon(BitmapDescriptorFactory
                                        .fromResource(UIUtils.getResource("b_poi_"+(i+1))));
                            }
                            //将当前market更改为高亮
                            marker.setIcon(BitmapDescriptorFactory
                                    .fromResource(UIUtils.getResource("b_poi_"+(index+1)+"_hl")));
                            return null;
                        }
                    }.execute();
                    //设置listView位置
                    lv_detail.setSelection(index);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onStart() {
        // 开启图层定位
        baiduMap.setMyLocationEnabled(true);
        if (!mLocationClient.isStarted()) {
            mLocationClient.start();
        }
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}
