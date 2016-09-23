package com.example.pefami.benpaob.tool;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * ShradePreference的工具类
 * Created by pefami on 2015/10/8.
 */
public class SPUtils {
    public static final String USER_ID="user_id";
    public static final String USERNAME="username";
    public static final String ACTIVATE_CODE="activate_code";
    /**
     * 保存参数
     * @param key
     * @param obj
     */
    public static void save(String key,Object obj){
        SharedPreferences sharedPreferences;
        sharedPreferences = UIUtils.getContext().getSharedPreferences("config", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        if(obj instanceof  String){
            editor.putString(key,(String)obj);
        }else if(obj instanceof  Boolean){
            editor.putBoolean(key,(Boolean)obj);
        }else if(obj instanceof Integer){
            editor.putInt(key, (Integer) obj);
        }
        editor.commit();
    }

    /**
     * 获取sharedPreferences
     * @return
     */
    public static  SharedPreferences getSharedPreferences(){
        return UIUtils.getContext().getSharedPreferences("config", Context.MODE_PRIVATE);
    }
    public static void removeKey(String key){
        getSharedPreferences().edit().remove(key).commit();
    }
    public static String getUserId(){
        return getSharedPreferences().getString(USER_ID,"");
    }
    public static String getUserName(){
        return getSharedPreferences().getString(USERNAME,"");
    }
    //用户是否已激活
    public static boolean isActivate(String phone){
        return getSharedPreferences().getString(phone,null)==null?false:true;
    }
    public static boolean isLogin(){
        return !TextUtils.isEmpty(getSharedPreferences().getString(USER_ID,""));
    }
}
