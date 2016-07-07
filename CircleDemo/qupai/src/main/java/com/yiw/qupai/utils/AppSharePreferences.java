package com.yiw.qupai.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * Created by yiwei on 16/4/24.
 */
public class AppSharePreferences {

    private static AppSharePreferences instance;
    private Context context;
    private SharedPreferences sp;

    private AppSharePreferences(Context context){
        this.context = context;
        sp = context.getSharedPreferences("qupai_sp", Context.MODE_PRIVATE);

    }
    public static AppSharePreferences getInstance(Context context){
        if(instance == null){
            synchronized (AppSharePreferences.class){
                if (instance == null){
                    instance = new AppSharePreferences(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    /**
     * 按Key存储配置信息
     *
     * @param key
     * @param value
     */
    public void set(String key, String value) {
        if (null != value) {
            sp.edit().putString(key, value).commit();
        }
    }

    /**
     * 按Key存储配置信息
     *
     * @param key
     * @param value
     */
    public void set(String key, boolean value) {
        sp.edit().putBoolean(key, value);
    }

    /**
     * 根据Key获取配置信息
     *
     * @param key
     * @return
     */
    public String get(String key) {
        String value = sp.getString(key, "");
        if (TextUtils.isEmpty(value)) {
            return "";
        }
        return value;
    }

    /**
     * 根据Key获取配置信息
     *
     * @param key
     * @return
     */
    public boolean get(String key, boolean defaultValue) {
        return sp.getBoolean(key, defaultValue);
    }


}
