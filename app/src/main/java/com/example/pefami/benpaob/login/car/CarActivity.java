package com.example.pefami.benpaob.login.car;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.bigkoo.quicksidebar.QuickSideBarTipsView;
import com.bigkoo.quicksidebar.QuickSideBarView;
import com.bigkoo.quicksidebar.listener.OnQuickSideBarTouchListener;
import com.example.pefami.benpaob.Constant;
import com.example.pefami.benpaob.R;
import com.example.pefami.benpaob.bean.Car;
import com.example.pefami.benpaob.bean.CarBrand;
import com.example.pefami.benpaob.bean.CarBrandGroup;
import com.example.pefami.benpaob.bean.CarType;
import com.example.pefami.benpaob.login.city.CityOnItemClickListener;
import com.example.pefami.benpaob.login.city.DividerDecoration;
import com.example.pefami.benpaob.tool.FileUtils;
import com.example.pefami.benpaob.tool.GsonService;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CarActivity extends AppCompatActivity implements OnQuickSideBarTouchListener {
    private LayoutInflater layoutInflater;
    RecyclerView recyclerView;
    HashMap<String, Integer> letters = new HashMap<>();
    private QuickSideBarView quickSideBarView;
    private QuickSideBarTipsView quickSideBarTipsView;
    //共多少个车种
    private List<CarBrand> carBrands = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car);
        layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        quickSideBarView = (QuickSideBarView) findViewById(R.id.quickSideBarView);
        quickSideBarTipsView = (QuickSideBarTipsView) findViewById(R.id.quickSideBarTipsView);
        //设置监听
        quickSideBarView.setOnQuickSideBarTouchListener(this);
        //设置列表数据和浮动header
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        CarListWithHeadersAdapter adapter = new CarListWithHeadersAdapter();

        //获取车型json
        String json = FileUtils.readAssetsTxt("car.txt");
        Car car = GsonService.parseJson(json, Car.class);
        //有多少个分组
        List<CarBrandGroup> cityList = car.getSubBrandList();
        //车品牌列表
        int position = 0;
        ArrayList<String> customLetters = new ArrayList<>();
        for (CarBrandGroup carBrandGroup : cityList) {
            String letter = carBrandGroup.getPinyin();
            //将字母加入index集合
            if (!letters.containsKey(letter)) {
                letters.put(letter, position);
            }
            customLetters.add(letter);
            //获取每个品牌数据
            for (CarBrand carBrand : carBrandGroup.getLists()) {
                carBrand.setPinyin(carBrandGroup.getPinyin());
                carBrands.add(carBrand);
                position++;
            }
        }
        //设置快速查找字母
        quickSideBarView.setLetters(customLetters);
        adapter.addAll(carBrands);
        recyclerView.setAdapter(adapter);
        final StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(adapter);
        recyclerView.addItemDecoration(headersDecor);
        // Add decoration for dividers between list items
        recyclerView.addItemDecoration(new DividerDecoration(this));
        adapter.setCityOnItemClickListener(new CityOnItemClickListener() {
            @Override
            public void onItemClickListener(View v, int position) {
                showCarType(carBrands.get(position).getName(), carBrands.get(position).getLists());
            }
        });
    }

    //车型悬浮界面
    private PopupWindow carTypePopup;
    private View popupView;
    private ListView listView;

    //list适配器
    private CarTypeAdapter carTypeAdapter;

    /**
     * 展示车型
     * @param name
     * @param lists
     */
    private void showCarType(final String name, final List<CarType> lists) {
        if (carTypePopup == null) {
            popupView = layoutInflater.inflate(R.layout.car_type_pop, null);
            listView = (ListView) popupView.findViewById(R.id.listview);
            popupView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (carTypePopup.isShowing()) {
                        carTypePopup.dismiss();
                    }
                }
            });
            //创建PopupWindow
            carTypePopup = new PopupWindow(popupView, LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, true);
            carTypePopup.setBackgroundDrawable(new ColorDrawable());
            carTypePopup.setFocusable(true);
        }
        if (carTypeAdapter == null) {
            carTypeAdapter = new CarTypeAdapter(getApplicationContext(), lists,name);
            carTypeAdapter.setOnItemClickListener(new CarOnItemClickListener() {
                @Override
                public void onItemClickListener(String name, String carType) {
                    Intent intent = new Intent();
                    intent.putExtra(Constant.CAR_BRAND,name);
                    intent.putExtra(Constant.CAR_TYPE,carType);
                    //将选定的车型返回
                    setResult(Constant.CAR_RESPONSE, intent);
                    if (carTypePopup.isShowing()) {
                        carTypePopup.dismiss();
                    }
                    finish();
                }
            });
            listView.setAdapter(carTypeAdapter);
        } else {
            carTypeAdapter.setData(lists,name);
            carTypeAdapter.notifyDataSetChanged();
        }
        carTypePopup.showAtLocation(getWindow().getDecorView(), Gravity.TOP, 0, 0);
    }


    @Override
    public void onLetterChanged(String letter, int position, float y) {
        quickSideBarTipsView.setText(letter, position, y);
        //有此key则获取位置并滚动到该位置
        if (letters.containsKey(letter)) {
            recyclerView.scrollToPosition(letters.get(letter));
        }
    }

    @Override
    public void onLetterTouching(boolean touching) {
        //可以自己加入动画效果渐显渐隐
        quickSideBarTipsView.setVisibility(touching ? View.VISIBLE : View.INVISIBLE);
    }

}
