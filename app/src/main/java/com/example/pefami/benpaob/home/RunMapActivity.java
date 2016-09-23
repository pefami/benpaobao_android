package com.example.pefami.benpaob.home;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
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
import com.example.pefami.benpaob.tool.UIUtils;
import com.example.pefami.benpaob.track.OverlayUtils;
import com.example.pefami.benpaob.track.TrackDraw;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

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

    private LocationClient mLocationClient;//定位客户端
    public MyLocationListener mMyLocationListener;//定位的监听器
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
        tv_pause_run= (TextView) findViewById(R.id.tv_pause_run);
        tv_start_run.setOnClickListener(this);
        iv_map.setOnClickListener(this);
        tv_pause_run.setOnClickListener(this);
        initSlidingMenu();
        initMyLocation();
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
  /*mMyDrawable.setDrawerListener(mToggle);不推荐*/
        mMyDrawable.addDrawerListener(mToggle);
        mToggle.syncState();/*同步状态*/
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
        option.setScanSpan(2000);
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
                String distance = "当前行程：" +UIUtils.getDistaceUnit ((long) trackDraw.distanceTotal());
                tv_totaldis.setText(distance);
            }
            if (trackid != null && isRun)
                addLocationToDB(location);
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
    }

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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

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
        if(!isPause){
            tv_pause_run.setText(UIUtils.getString(R.string.resume_run));
            isPause=true;
        }else{
            tv_pause_run.setText(UIUtils.getString(R.string.pasue_run));
            isPause=false;
        }
        trackDraw.setIsPause(isPause);
    }

    private TrackDraw trackDraw;
    private String trackid;

    /**
     * 点击开启奔跑执行方法
     */
    private void clickRun() {
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
            //生成当前轨迹ID
            trackid = UUID.randomUUID().toString();
            tv_pause_run.setVisibility(View.VISIBLE);
        } else {
            isRun = false;
            BaseApplication.isStart = false;
            tv_start_run.setText(UIUtils.getString(R.string.start_run));
            trackDraw.stopMoveLooper();
            trackid = null;
            tv_pause_run.setVisibility(View.GONE);
        }
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
                        overlayUtils.showHistoryTrack(trackDao, baiduMap,start);
                    }
                }.start();
                isShowHistory = true;
                tv_history.setText(UIUtils.getString(R.string.hide_history_track));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onStart() {
        // 开启图层定位
        baiduMap.setMyLocationEnabled(isShowMap);
        if (!mLocationClient.isStarted()) {
            mLocationClient.start();
        }
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
