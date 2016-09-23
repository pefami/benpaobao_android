package com.example.pefami.benpaob.login.city;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bigkoo.quicksidebar.QuickSideBarTipsView;
import com.bigkoo.quicksidebar.QuickSideBarView;
import com.bigkoo.quicksidebar.listener.OnQuickSideBarTouchListener;
import com.example.pefami.benpaob.BaseActivity;
import com.example.pefami.benpaob.Constant;
import com.example.pefami.benpaob.R;
import com.example.pefami.benpaob.bean.City;
import com.example.pefami.benpaob.tool.FileUtils;
import com.example.pefami.benpaob.tool.GsonService;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CityActivity extends BaseActivity  implements OnQuickSideBarTouchListener {
    //热门城市
    RecyclerView recyclerView;
    HashMap<String,Integer> letters = new HashMap<>();
    private QuickSideBarView quickSideBarView;
    private QuickSideBarTipsView quickSideBarTipsView;
    //共多少个城市
    private List<City.CityListBean.ListsBean> citys=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        quickSideBarView = (QuickSideBarView) findViewById(R.id.quickSideBarView);
        quickSideBarTipsView = (QuickSideBarTipsView) findViewById(R.id.quickSideBarTipsView);
        //设置监听
        quickSideBarView.setOnQuickSideBarTouchListener(this);
        //设置列表数据和浮动header
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        // Add the sticky headers decoration
        CityListWithHeadersAdapter adapter = new CityListWithHeadersAdapter();

        //获取城市的City json
        String json= FileUtils.readAssetsTxt("city.txt");
        City city = GsonService.parseJson(json, City.class);
        //有多少个分组
        List<City.CityListBean> cityList = city.getCityList();
        //分组城市列表
        int position = 0;
        ArrayList<String> customLetters = new ArrayList<>();
        for(City.CityListBean cityListBean:city.getCityList()){
            String letter = cityListBean.getPinyin();
            //将字母加入index集合
            if(!letters.containsKey(letter)){
                letters.put(letter,position);
                customLetters.add(letter);
            }
            //获取每个城市数据
            for(City.CityListBean.ListsBean listsBean:cityListBean.getLists()){
                citys.add(listsBean);
                position++;
            }
        }
        //设置快速查找字母
        quickSideBarView.setLetters(customLetters);
        adapter.addAll(citys);
        recyclerView.setAdapter(adapter);
        final StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(adapter);
        recyclerView.addItemDecoration(headersDecor);
        // Add decoration for dividers between list items
        recyclerView.addItemDecoration(new DividerDecoration(this));
        adapter.setCityOnItemClickListener(new CityOnItemClickListener() {
            @Override
            public void onItemClickListener(View v, int position) {
                Intent intent=new Intent();
                intent.putExtra("city",citys.get(position).getName());
                intent.putExtra("carCode",citys.get(position).getCar_prefix());
                //将选定的城市返回
                setResult(Constant.CITY_RESPONSE,intent);
                finish();
            }
        });
    }

    @Override
    public void onLetterChanged(String letter, int position, float y) {
        quickSideBarTipsView.setText(letter, position, y);
        //有此key则获取位置并滚动到该位置
        if(letters.containsKey(letter)) {
            recyclerView.scrollToPosition(letters.get(letter));
        }
    }

    @Override
    public void onLetterTouching(boolean touching) {
        //可以自己加入动画效果渐显渐隐
        quickSideBarTipsView.setVisibility(touching? View.VISIBLE:View.INVISIBLE);
    }
}
