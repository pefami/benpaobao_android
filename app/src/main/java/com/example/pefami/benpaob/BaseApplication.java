package com.example.pefami.benpaob;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Process;

/**
 * Created by Administrator on 2016/9/21.
 */
public class BaseApplication extends Application {
    private static  Context context;
    private static Handler handler;
    private static int mainThreadId;
    public static int actNum;


    public static boolean isStart;
    public static String currTd="恒大地产";
    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplicationContext();
        handler=new Handler();
        mainThreadId= Process.myTid();
    }

    public static Context getContext() {
        return context;
    }

    public static Handler getHandler() {
        return handler;
    }

    public static  int getMainThreadId() {
        return mainThreadId;
    }
}
