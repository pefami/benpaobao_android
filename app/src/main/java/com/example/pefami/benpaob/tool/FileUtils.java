package com.example.pefami.benpaob.tool;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by Administrator on 2016/4/15.
 */
public class FileUtils {
    /**
     * 读取Assests的txt文件为字符串
     * @param name
     * @return
     */
    public static String readAssetsTxt(String name){
        InputStream is = null;
        try {
            is = UIUtils.getContext().getAssets().open(name);
            InputStreamReader reader = new InputStreamReader(is);
            BufferedReader bufferedReader = new BufferedReader(reader);
            StringBuffer buffer = new StringBuffer("");
            String str;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            return buffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(is!=null)
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    /**
     * 保存对像
     * @param context
     * @param name
     * @param obj
     */
    public static void saveObject(Context context, String name, Object obj) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = context.openFileOutput(name, Context.MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(obj);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    // fos流关闭异常
                    e.printStackTrace();
                }
            }
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    // oos流关闭异常
                    e.printStackTrace();
                }
            }
        }
    }
    /**
     * 读取对象
     * @param context
     * @param name
     * @return
     */
    public static Object getObject(Context context, String name) {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = context.openFileInput(name);
            ois = new ObjectInputStream(fis);
            return ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    // fis流关闭异常
                    e.printStackTrace();
                }
            }
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    // ois流关闭异常
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 清除对象
     */
    public static void removeObject(String name){
        File file=new File(name);
        if(file.exists()){
            file.delete();
        }
    }
}
