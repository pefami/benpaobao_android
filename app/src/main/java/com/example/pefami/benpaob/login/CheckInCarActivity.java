package com.example.pefami.benpaob.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.example.pefami.benpaob.BaseActivity;
import com.example.pefami.benpaob.Constant;
import com.example.pefami.benpaob.R;
import com.example.pefami.benpaob.bean.City;
import com.example.pefami.benpaob.login.car.CarActivity;
import com.example.pefami.benpaob.login.city.CityActivity;
import com.example.pefami.benpaob.tool.FileUtils;
import com.example.pefami.benpaob.tool.GsonService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class CheckInCarActivity extends BaseActivity implements View.OnClickListener {
    private TextView tv_checkin_next;
    private TextView tv_change_city;
    private Spinner sp_province;
    private EditText et_input_car_number;
    private TextView tv_car_type;
    private EditText et_car_owner;
    private TextView et_car_date;
    private EditText et_car_owner_name;
    private EditText et_car_owner_idcard;
    private TextView et_licence_date;
    //时间选择器
    private TimePickerView carPickTime;
    private TimePickerView licencePickTime;

    private String currentCarPrefix;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkin_car);
        tv_checkin_next= (TextView) findViewById(R.id.tv_checkin_next);
        tv_change_city= (TextView) findViewById(R.id.tv_change_city);
        sp_province= (Spinner) findViewById(R.id.sp_province);
        et_input_car_number= (EditText) findViewById(R.id.et_input_car_number);
        tv_car_type= (TextView) findViewById(R.id.tv_car_type);
        et_car_date= (TextView) findViewById(R.id.et_car_date);
        et_car_owner_name= (EditText) findViewById(R.id.et_car_owner_name);
        et_car_owner_idcard= (EditText) findViewById(R.id.et_car_owner_idcard);
        et_licence_date= (TextView) findViewById(R.id.et_licence_date);

        tv_checkin_next.setOnClickListener(this);
        tv_change_city.setOnClickListener(this);
        et_car_date.setOnClickListener(this);
        et_licence_date.setOnClickListener(this);
        tv_car_type.setOnClickListener(this);
        //设置车牌号前缀
        setCarPrefix();
        //时间选择器
        setCarDate();
    }

    //用于判断哪个控件调出时间选择
    private boolean isCarData;
    private void setCarDate() {
        carPickTime = new TimePickerView(this, TimePickerView.Type.YEAR_MONTH_DAY);
        carPickTime.setTime(new Date());
        carPickTime.setCyclic(false);
        carPickTime.setCancelable(true);
        //时间选择后回调
        carPickTime.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date) {
                if(isCarData){
                    et_car_date.setText(getTime(date));
                }else{
                    et_licence_date.setText(getTime(date));
                }

            }
        });

    }

    private HashMap<String,String> city_car=new HashMap<>();
    private ArrayList<String> carPrefixList=new ArrayList<>();
    private ArrayAdapter<String> arr_adapter;
    /**
     *  //设置车牌号前缀
     */
    private void setCarPrefix() {
        //获取城市的City json
        String json= FileUtils.readAssetsTxt("city.txt");
        City city = GsonService.parseJson(json, City.class);
        //有多少个分组
        List<City.CityListBean> cityList = city.getCityList();
        for(City.CityListBean cityListBean:city.getCityList()){
            //获取每个城市数据
            for(City.CityListBean.ListsBean listsBean:cityListBean.getLists()){
                String car_prefix = listsBean.getCar_prefix();
                String name=listsBean.getName();
                //将字母加入index集合
                if(!carPrefixList.contains(car_prefix)){
                    carPrefixList.add(car_prefix);
                }
                if(!city_car.containsKey(name)){
                    city_car.put(name,car_prefix);
                }
            }
        }
        //给下拉菜单设置数据
        //适配器
        arr_adapter= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, carPrefixList);
        //设置样式
        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        sp_province.setAdapter(arr_adapter);

        String cityName=tv_change_city.getText().toString().trim();
        String value = city_car.get(cityName);
        int index=0;
        if((index=carPrefixList.indexOf(value))!=-1){
            sp_province.setSelection(index);
        }
    }

    //格式化时间
    public static String getTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }
    // 回调方法，从第二个页面回来的时候会执行这个方法
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 根据上面发送过去的请求吗来区别
        if(requestCode==Constant.SELECT_CITY&&resultCode==Constant.CITY_RESPONSE){
            String city = data.getStringExtra("city");
            String carCode=data.getStringExtra("carCode");
            tv_change_city.setText(city);
            int indexOf = carPrefixList.indexOf(carCode);
            if(indexOf>-1){
                sp_province.setSelection(indexOf);
            }
        }else if(requestCode==Constant.SELECT_CAR&&resultCode==Constant.CAR_RESPONSE){
            String brand=data.getStringExtra(Constant.CAR_BRAND);
            String carType=data.getStringExtra(Constant.CAR_TYPE);
            tv_car_type.setText(brand+" "+carType);
        }
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_checkin_next:
                Intent intent=new Intent(this,SelectTdActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.tv_change_city:
                Intent cityIntent=new Intent(this, CityActivity.class);
                startActivityForResult(cityIntent, Constant.SELECT_CITY);
                break;
            case R.id.et_car_date:
                isCarData=true;
                carPickTime.show();
                break;
            case R.id.et_licence_date:
                isCarData=false;
                carPickTime.show();
                break;
            case R.id.tv_car_type:
                Intent carIntent=new Intent(this, CarActivity.class);
                startActivityForResult(carIntent, Constant.SELECT_CAR);
                break;
        }
    }
}
