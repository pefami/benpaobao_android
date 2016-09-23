package com.example.pefami.benpaob.tool;

import android.text.TextUtils;

import com.example.pefami.benpaob.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/4/20.
 */
public class VerifyUtils {
//    /**
//     * 判断输入密码是否合法
//     *
//     * @param passwd
//     * @return
//     */
//    public static boolean isPasswd(String passwd) {
//        String regex = "^[a-zA-Z0-9]{6}$";
//        if (!TextUtils.isEmpty(passwd)) {
//            Pattern p = Pattern.compile(regex);
//            Matcher m = p.matcher(passwd);
//            if (m.find()) {
//                return true;
//            } else {
//                ToastUtils.show(UIUtils.getContext().getString(R.string.passwd_illegality));
//            }
//        } else {
//            ToastUtils.show(UIUtils.getContext().getString(R.string.passwd_dont_null));
//        }
//        return false;
//    }

    /**
     * 验证手机号码是否合法
     *
     * @param phone
     * @return
     */
    public static boolean isPhone(String phone) {
        String regex = "^(0|86|17951)?(13[0-9]|15[012356789]|17[0678]|18[0-9]|14[57])[0-9]{8}$";
        if (!TextUtils.isEmpty(phone)) {
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(phone);
            if (m.find()) {
                return true;
            } else {
                ToastUtils.show(UIUtils.getContext().getString(R.string.phone_illegality));
            }
        } else {
            ToastUtils.show(UIUtils.getContext().getString(R.string.phone_dont_null));
        }
        return false;
    }
}
