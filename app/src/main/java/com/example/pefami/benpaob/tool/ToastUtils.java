package com.example.pefami.benpaob.tool;

import android.widget.Toast;

/**
 * Created by Administrator on 2016/3/16.
 */
public class ToastUtils {
    private static Toast mToast;
    public static void show(String text){
        if(mToast==null){
            mToast=Toast.makeText(UIUtils.getContext(),text,Toast.LENGTH_SHORT);
        }else{
            mToast.setText(text);
        }
        mToast.show();
    }
}
