package com.example.pefami.benpaob.home;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.example.pefami.benpaob.BaseActivity;
import com.example.pefami.benpaob.BaseApplication;
import com.example.pefami.benpaob.Constant;
import com.example.pefami.benpaob.R;
import com.example.pefami.benpaob.dao.TrackDao;
import com.example.pefami.benpaob.login.DealershipActivity;
import com.example.pefami.benpaob.login.SelectTdActivity;
import com.example.pefami.benpaob.service.LocListener;
import com.example.pefami.benpaob.service.LocService;
import com.example.pefami.benpaob.tool.UIUtils;
import com.example.pefami.benpaob.track.OverlayUtils;
import com.example.pefami.benpaob.track.TrackDraw;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RunMapActivity extends BaseActivity implements View.OnClickListener {
    private MapView mapView;
    private BaiduMap baiduMap;
    private TextView tv_start_run;
    private TextView tv_totaldis;
    private TextView tv_speed;
    private ImageView iv_map;
    private TextView tv_pause_run;
    private LinearLayout mLlMenu;
    private Toolbar mTbHeadBar;
    private DrawerLayout mMyDrawable;
    private ActionBarDrawerToggle mToggle;

    private boolean isFristLocation = true;
    private boolean isRun;
    private TrackDao trackDao;
    private double mCurrentLantitude;
    private double mCurrentLongitude;
    private boolean isStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_run_map);
        trackDao = new TrackDao(getApplicationContext());
        mapView = (MapView) findViewById(R.id.mv_run_map);
        baiduMap = mapView.getMap();
        tv_start_run = (TextView) findViewById(R.id.tv_start_run);
        tv_totaldis = (TextView) findViewById(R.id.tv_totaldis);
        tv_speed = (TextView) findViewById(R.id.tv_speed);
        iv_map = (ImageView) findViewById(R.id.iv_map);
        tv_pause_run = (TextView) findViewById(R.id.tv_pause_run);
        tv_start_run.setOnClickListener(this);
        iv_map.setOnClickListener(this);
        tv_pause_run.setOnClickListener(this);
        initSlidingMenu();
        //开启定位服务
        startService();
    }

    private void startService() {
        Intent locService=new Intent(getApplicationContext(), LocService.class);
        //启用全局服务，完全后台进行
        startService(locService);
        //启用绑定服务，操作服务流程
        bindService(locService,conn,BIND_AUTO_CREATE);
        //设置定位回调监听
        LocService.addLocListener(locListener);
    }
    //百度定位回调监听器
    private LocListener locListener=new LocListener() {
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
            mCurrentLantitude = location.getLatitude();
            mCurrentLongitude = location.getLongitude();
//            // 设置自定义图标
//            BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory
//                    .fromResource(R.drawable.navi_map_gps_locked);
//            MyLocationConfigeration config = new MyLocationConfigeration(
//                    mCurrentMode, true, mCurrentMarker);
//            mBaiduMap.setMyLocationConfigeration(config);
            LatLng ll = new LatLng(location.getLatitude(),
                    location.getLongitude());
            //获取当前速度
            float speed = location.getSpeed();
            tv_speed.setText("速度：" + speed + " km/h");
//            if (trackDraw != null&&isRun)
//                trackDraw.addLatLng(ll);

            //获取当前计算的行程
            if (trackDraw != null) {
                String distance = "当前行程：" + UIUtils.getDistaceUnit((long) trackDraw.distanceTotal());
                tv_totaldis.setText(distance);
            }
            // 第一次定位时，将地图位置移动到当前位置
            if (isFristLocation) {
                isFristLocation = false;
                baiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().zoom(15).build()));
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
                baiduMap.animateMapStatus(u);
                if ((BaseApplication.isStart && !isRun) || (!BaseApplication.isStart && isRun)) {
                    clickRun();
                }
            }
        }
    };
    private LocService.LocBinder locBinder;
    private ServiceConnection conn=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if(service!=null)
            locBinder= (LocService.LocBinder) service;
            startLocation();
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };
    private boolean isShowMap = true;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_start_run:
                clickRun();
                break;
            case R.id.tv_nearby_dealership:
                Intent dealIntent = new Intent(getApplicationContext(), DealershipActivity.class);
                startActivity(dealIntent);
                mMyDrawable.closeDrawers();
                break;
            case R.id.tv_td_market:
                Intent tdIntent = new Intent(getApplicationContext(), SelectTdActivity.class);
                startActivity(tdIntent);
                mMyDrawable.closeDrawers();
                break;
            case R.id.tv_home:
                Intent homeIntent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(homeIntent);
                mMyDrawable.closeDrawers();
                break;
            case R.id.iv_map:
                if (isShowMap) {
                    //隐藏地图
                    isShowMap = false;
                    mapView.setVisibility(View.GONE);
                    isFristLocation = false;
                } else {
                    isShowMap = true;
                    isFristLocation = true;
                    mapView.setVisibility(View.VISIBLE);
                }
                baiduMap.setMyLocationEnabled(isShowMap);
                break;
            case R.id.tv_history:
                clickShowHistory();
                break;
            case R.id.tv_pause_run:
                clickPause();
                break;
        }
    }

    private boolean isPause;

    private void clickPause() {
        if (!isPause) {
            isPause = true;
        } else {
            isPause = false;
        }
        tv_pause_run.setText(!isPause?UIUtils.getString(R.string.pasue_run):UIUtils.getString(R.string.resume_run));
        trackDraw.setIsPause(isPause);
    }

    private TrackDraw trackDraw;
    /**
     * 点击开启奔跑执行方法
     */
    private void clickRun() {
        if(locBinder==null){
            return;
        }
        if (!isRun) {
            isRun = true;
            BaseApplication.isStart = true;
            if (isShowHistory) {
                //当在显示历史轨迹时，先关掉历史轨迹
                clickShowHistory();
            }
            tv_start_run.setText(UIUtils.getString(R.string.stop_run));
            if (trackDraw == null) {
                trackDraw = new TrackDraw();
            }
            trackDraw.initRoadData(mapView, mCurrentLantitude, mCurrentLongitude);
            trackDraw.moveLooper();
            showPause(true);
            locBinder.startRun();
        } else {
            isRun = false;
            BaseApplication.isStart = false;
            tv_start_run.setText(UIUtils.getString(R.string.start_run));
            trackDraw.stopMoveLooper();
            showPause(false);
            locBinder.stopRun();
        }
    }

    private void showPause(boolean b) {
        tv_pause_run.setVisibility(b?View.VISIBLE:View.GONE);
        tv_pause_run.setText(b?UIUtils.getString(R.string.pasue_run):UIUtils.getString(R.string.resume_run));
        isPause=!b;
        trackDraw.setIsPause(isPause);
    }

    private boolean isShowHistory;
    private OverlayUtils overlayUtils;

    /**
     * 点击显示或隐藏历史轨迹
     */
    public void clickShowHistory() {
        if (!isShowHistory) {
            Intent intent = new Intent(getApplicationContext(), HistoryActivity.class);
            startActivityForResult(intent, Constant.SELECT_DATE);
            mMyDrawable.closeDrawers();
        } else {
            isShowHistory = false;
            if (overlayUtils != null) {
                overlayUtils.hideHistoryTrack();
            }
            tv_history.setText(UIUtils.getString(R.string.show_history_track));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.SELECT_DATE && resultCode == Constant.DATE_RESPONSE && data != null) {
            String time = data.getStringExtra(Constant.DATE_HISTORY);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
            //将字符串转为时间
            try {
                Date parse = sdf.parse(time);
                final long start = parse.getTime();
                if (overlayUtils == null) {
                    overlayUtils = new OverlayUtils(getApplicationContext());
                }
                new Thread() {
                    @Override
                    public void run() {
                        overlayUtils.showHistoryTrack(trackDao, baiduMap, start);
                    }
                }.start();
                isShowHistory = true;
                tv_history.setText(UIUtils.getString(R.string.hide_history_track));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private TextView tv_nearby_dealership;
    private TextView tv_td_market;
    private TextView tv_home;
    private TextView tv_history;

    private void initSlidingMenu() {
        mLlMenu = (LinearLayout) findViewById(R.id.llMenu);
        mTbHeadBar = (Toolbar) findViewById(R.id.tbHeadBar);
        mMyDrawable = (DrawerLayout) findViewById(R.id.dlMenu);
        tv_nearby_dealership = (TextView) findViewById(R.id.tv_nearby_dealership);
        tv_td_market = (TextView) findViewById(R.id.tv_td_market);
        tv_home = (TextView) findViewById(R.id.tv_home);
        tv_history = (TextView) findViewById(R.id.tv_history);
        tv_nearby_dealership.setOnClickListener(this);
        tv_td_market.setOnClickListener(this);
        tv_home.setOnClickListener(this);
        tv_history.setOnClickListener(this);
        initToolBarAndDrawableLayout();
    }

    private void initToolBarAndDrawableLayout() {
        setSupportActionBar(mTbHeadBar);
        /*以下俩方法设置返回键可用*/
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        /*设置标题文字不可显示*/
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mToggle = new ActionBarDrawerToggle(this, mMyDrawable, mTbHeadBar, R.string.open, R.string.close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mMyDrawable.addDrawerListener(mToggle);
        mToggle.syncState();/*同步状态*/
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    private void startLocation(){
        if(locBinder!=null&&!locBinder.isStartLocation()){
            locBinder.startLocation();
        }
    }
    @Override
    protected void onStart() {
        // 开启图层定位
        baiduMap.setMyLocationEnabled(isShowMap);
        startLocation();
        if (mCurrentLantitude != 0 || mCurrentLongitude != 0) {
            if ((BaseApplication.isStart && !isRun) || (!BaseApplication.isStart && isRun)) {
                clickRun();
            }
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
