package com.example.xyy.DanmuPlayer.utils.database;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by xyy on 2017/12/24.
 */

public class SharedPreferencesHelper {
    private static final String saveData = "saveData";
    private static SharedPreferencesHelper instance;
    private static SharedPreferences mSharedPreferences;

    private SharedPreferencesHelper(Context context){
        mSharedPreferences = context.getSharedPreferences(saveData,Context.MODE_PRIVATE);
    }

    public static synchronized void init(Context context){
        if (instance == null){
            instance = new SharedPreferencesHelper(context);
        }
    }

    public static SharedPreferencesHelper getInstance(){
        if (instance == null){
            throw new RuntimeException("class should init !");
        }
        return instance;
    }

    /**
     * 保存int数据
     * @param key 键
     * @param value 值
     */
    public void saveInteger(String key,int value){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    /**
     * 获取int数据
     * @param key 键
     * @return int
     */
    public int getInteger(String key){
        return mSharedPreferences.getInt(key,0);
    }

    /**
     * 保存String数据
     * @param key 键
     * @param value 值
     */
    public void saveString(String key,String value){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * 获取String数据
     * @param key 键
     * @return String
     */
    public String getString(String key,String defValue){
        return mSharedPreferences.getString(key,defValue);
    }
}
