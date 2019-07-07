package com.jaribha.shared_preferences;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class JaribhaSharedPrefrences {

    public static final String PREFRENCE_NAME = "JaribhaAppPreferences";

    public static String getSharedPrefData(Context activity, String key) {
        SharedPreferences prefs = activity.getSharedPreferences(PREFRENCE_NAME,
                Context.MODE_PRIVATE);
        String value = null;
        if (prefs != null && prefs.contains(key)) {
            value = prefs.getString(key, "null");
        }
        return value;
    }

    public static void setDataInSharedPrefrence(Context activity, String key,
                                                String value) {
        SharedPreferences prefs = activity.getSharedPreferences(PREFRENCE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static void setBooleanInSharedPrefrence(Context context, String key, boolean value) {
        SharedPreferences prefs = context.getSharedPreferences(PREFRENCE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static boolean getBooleanPrefData(Context activity, String key) {
        SharedPreferences prefs = activity.getSharedPreferences(PREFRENCE_NAME,
                Context.MODE_PRIVATE);
        boolean value = false;
        if (prefs != null && prefs.contains(key)) {
            value = prefs.getBoolean(key, false);
        }
        return value;
    }

    public static void deletePrefrenceData(Activity activity) {
        SharedPreferences prefs = activity.getSharedPreferences(PREFRENCE_NAME,
                Context.MODE_PRIVATE);
        prefs.edit().clear().commit();
    }

    public static void deleteKey(Context activity, String key) {
        SharedPreferences prefs = activity.getSharedPreferences(PREFRENCE_NAME,
                Context.MODE_PRIVATE);
        prefs.edit().remove(key).commit();
    }
}
