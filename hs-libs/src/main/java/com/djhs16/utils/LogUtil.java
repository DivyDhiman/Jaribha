package com.djhs16.utils;

import android.util.Log;

import com.djhs16.hslibs.BuildConfig;

/**
 * Created by Himanshu on 6/18/2015.
 */
public class LogUtil {

    public static void e(String tag, String text) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, text);
        }
    }

    public static void e(Exception e) {
        if (BuildConfig.DEBUG) {
            e.printStackTrace();
        }
    }

    public static void d(String tag, String text) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, text);
        }
    }

    public static void e(String text) {
        if (BuildConfig.DEBUG) {
            Log.e("BrickPage", text);
        }
    }

    public static void d(String text) {
        if (BuildConfig.DEBUG) {
            Log.d("BrickPage", text);
        }
    }
}
