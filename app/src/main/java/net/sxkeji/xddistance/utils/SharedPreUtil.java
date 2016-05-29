package net.sxkeji.xddistance.utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import net.sxkeji.xddistance.BaseApplication;


/**
 * sharedPreferences工具类
 */
public class SharedPreUtil {

    private SharedPreUtil() {}

    public static void writeString(String key, String value) {
        getSharedPreferences().edit().putString(key, value).commit();
    }

    public static void writeInt(String key, int value) {
        getSharedPreferences().edit().putInt(key, value).commit();
    }

    public static void writeFloat(String key, float value) {
        getSharedPreferences().edit().putFloat(key, value).commit();
    }

    public static void writeBoolean(String key, boolean value) {
        getSharedPreferences().edit().putBoolean(key, value).commit();
    }

    public static String readString(String key) {
        return getSharedPreferences().getString(key, "");
    }

    public static String readString(String key, String defaultStr) {
        return getSharedPreferences().getString(key, defaultStr);
    }

    public static int readInt(String key) {
        return getSharedPreferences().getInt(key, 0);
    }

    public static int readInt(String key, int defaultValue) {
        return getSharedPreferences().getInt(key, defaultValue);
    }

    public static float readFloat(String key) {
        return getSharedPreferences().getFloat(key, 0f);
    }

    public static boolean readBoolean(String key) {
        return getSharedPreferences().getBoolean(key, false);
    }

    public static void remove(String key) {
        getSharedPreferences().edit().remove(key).commit();
    }

    public static void removeAll() {
        getSharedPreferences().edit().clear().commit();
    }

    public static SharedPreferences getSharedPreferences() {
        return BaseApplication.getInstance().getApplicationContext().getSharedPreferences(Constant.SP_NAME, Context.MODE_PRIVATE);
    }

}
