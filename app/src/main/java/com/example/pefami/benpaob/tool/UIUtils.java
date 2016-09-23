package com.example.pefami.benpaob.tool;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Process;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.example.pefami.benpaob.BaseApplication;

import java.util.Locale;

/**
 * UI界面工具类
 *
 * @author pefami
 */
public class UIUtils {
    //////////////获取BaseApplication成员的相关方法

    /**
     * 获取图片名称获取图片的资源id的方法
     *
     * @param imageName
     * @return
     */
    public static int getResource(String imageName) {
        Context ctx = UIUtils.getContext();
        int resId = ctx.getResources().getIdentifier(imageName, "drawable", ctx.getPackageName());
        return resId;
    }
    public static String getDistaceUnit(long distace){
        if(distace>1000){
            return String.format("%.2f", distace/1000.0)+"公里";
        }else{
            return distace+" 米";
        }
    }
    /**
     * 修改控件的高
     *
     * @param view
     * @param percent
     */
    public static void changeViewHeight(View view, float percent) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int) (UIUtils.getScreenHeight() * percent);
        view.setLayoutParams(layoutParams);
    }

    /**
     * 修改控件的宽
     *
     * @param view
     * @param percent
     */
    public static void changeViewWidth(View view, float percent) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = (int) (UIUtils.getScreenWidth() * percent);
        view.setLayoutParams(layoutParams);
    }

    /**
     * 修改控件的宽
     *
     * @param view
     * @param percent
     */
    public static void changeViewWidth(View view, float percent, int total) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = (int) (total * percent);
        view.setLayoutParams(layoutParams);
    }


    public static void setLanguage(Locale locale) {
        Resources resources = getContext().getResources();
        Configuration config = resources.getConfiguration();
        config.locale = locale;
        DisplayMetrics dm = resources.getDisplayMetrics();
        resources.updateConfiguration(config, dm);
    }

    /**
     * 获取最后刷新的时间
     *
     * @return
     */
    public static String getLastDate() {
        return DateUtils.formatDateTime(
                getContext(),
                System.currentTimeMillis(),
                DateUtils.FORMAT_SHOW_TIME
                        | DateUtils.FORMAT_SHOW_DATE
                        | DateUtils.FORMAT_ABBREV_ALL);
    }

    //获取应用上下文
    public static Context getContext() {
        return BaseApplication.getContext();
    }

    //获取全局的Handler对象
    public static Handler getHandler() {
        return BaseApplication.getHandler();
    }

    //获取UI线程的ID
    public static int getMainThreadId() {
        return BaseApplication.getMainThreadId();
    }

    //////////////获取UI界面所需要的资源的相关方法

    //获取字符串资源
    public static String getString(int id) {
        return getContext().getResources().getString(id);
    }

    //获取字符串数组资源
    public static String[] getStringArray(int id) {
        return getContext().getResources().getStringArray(id);
    }

    //获取图片资源
    public static Drawable getDrawable(int id) {
        return getContext().getResources().getDrawable(id);
    }

    //获取颜色资源
    public static int getColor(int id) {
        return getContext().getResources().getColor(id);
    }

    //获取颜色状态集资源
    public static ColorStateList getColorStateList(int id) {
        return getContext().getResources().getColorStateList(id);
    }

    //获取尺寸
    public static int getDimen(int id) {
        return getContext().getResources().getDimensionPixelSize(id);
    }

    //获取布局对象,无父布局
    public static View inflate(int id) {
        return LayoutInflater.from(getContext()).inflate(id, null);
    }

    //获取布局对象,有父布局
    public static View inflate(int id, ViewGroup parent) {
        return LayoutInflater.from(getContext()).inflate(id, parent, false);
    }

    ///////////////////尺寸单位px与dip 相互转换

    //将dip转为px
    public static int dip2px(float dip) {
        //获取屏幕密度
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (dip * density + 0.5f);
    }

    //将px转换为dip
    public static float px2dip(int px) {
        //获取屏幕密度
        float density = getContext().getResources().getDisplayMetrics().density;
        return px / density;
    }

    //获取屏幕宽度
    public static int getScreenWidth() {
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display defaultDisplay = windowManager.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        defaultDisplay.getMetrics(metrics);
        return metrics.widthPixels;
    }

    //获取屏幕高度
    public static int getScreenHeight() {
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display defaultDisplay = windowManager.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        defaultDisplay.getMetrics(metrics);
        return metrics.heightPixels;
    }
    //计算图片宽高比
    //////////////////////UI界面业务方法

    //判断当前线程是否为UI线程
    public static boolean currentThreadIsUI() {
        return Process.myTid() == getMainThreadId();
    }

    //将可执行程序运行在主线程
    public static void runingOnUiThread(Runnable r) {
        //判断当前线程是否为主线程
        if (currentThreadIsUI()) {
            r.run();
        } else {
            getHandler().post(r);
        }
    }

    /**
     * 将字符串全角化
     *
     * @param input
     * @return
     */
    public static String toDBC(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }
            if (c[i] > 65280 && c[i] < 65375)
                c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }
}
