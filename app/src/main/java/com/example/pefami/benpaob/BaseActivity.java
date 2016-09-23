package com.example.pefami.benpaob;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.example.pefami.benpaob.tool.ToastUtils;

/**
 * Created by Administrator on 2016/9/20.
 */
public class BaseActivity extends AppCompatActivity {
    private FragmentManager fm;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BaseApplication.actNum++;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BaseApplication.actNum--;
    }
    private Fragment mContent;
    private Fragment preContent;
    public void switchContent(int id, Fragment from, Fragment to) {
        if (mContent != to) {
            mContent = to;
            preContent=from;
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.setCustomAnimations(android.R.anim.fade_in, R.anim.slide_out);
            if (!to.isAdded()) {    // 先判断是否被add过
                transaction.hide(from).add(id,to).commitAllowingStateLoss(); // 隐藏当前的fragment，add下一个到Activity中
            } else {
                transaction.hide(from).show(to).commitAllowingStateLoss(); // 隐藏当前的fragment，显示下一个
            }
        }
    }

    private long  preTime;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(BaseApplication.actNum==1&&keyCode==KeyEvent.KEYCODE_BACK){
            long curTime=System.currentTimeMillis();
            if(curTime-preTime>2000){
                ToastUtils.show("再按一次退出应用");
                preTime=curTime;
            }else{
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    // 获取点击事件
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            // 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
            View view = getCurrentFocus();
            if (isHideInput(view, ev)) {
                hideSoftInput(view);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    // 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
    private boolean isHideInput(View v, MotionEvent ev) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (ev.getX() > left && ev.getX() < right && ev.getY() > top
                    && ev.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
        return false;
    }

    // 隐藏软键盘
    private void hideSoftInput(View view) {
        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (manager != null) {
            manager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
