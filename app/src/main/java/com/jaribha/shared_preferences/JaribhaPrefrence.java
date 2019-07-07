package com.jaribha.shared_preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.jaribha.models.GetProjects;

/**
 * Created by Kailash on 16-Oct-15.
 */
public class JaribhaPrefrence {

    public static final String PREFRENCE_NAME = "Jaribha_Prefs";

    public static void setPref(Context c, String pref, String val) {
        SharedPreferences.Editor e = c.getSharedPreferences(PREFRENCE_NAME, Context.MODE_PRIVATE).edit();
        e.putString(pref, val);
        e.commit();
    }

    public static String getPref(Context c, String pref, String val) {
        return c.getSharedPreferences(PREFRENCE_NAME, Context.MODE_PRIVATE).getString(pref, val);
    }

    public static void setPref(Context c, String pref, Integer val) {
        SharedPreferences.Editor e = c.getSharedPreferences(PREFRENCE_NAME, Context.MODE_PRIVATE).edit();
        e.putInt(pref, val);
        e.commit();
    }

    public static Integer getPref(Context c, String pref, Integer val) {
        return c.getSharedPreferences(PREFRENCE_NAME, Context.MODE_PRIVATE).getInt(pref, val);
    }

    public static void setPref(Context c, String pref, Boolean val) {
        SharedPreferences.Editor e = c.getSharedPreferences(PREFRENCE_NAME, Context.MODE_PRIVATE).edit();
        e.putBoolean(pref, val);
        e.commit();
    }

    public static void set_utils(Context c, GetProjects myObject){
        Gson gson = new Gson();
        String json = gson.toJson(myObject);
        SharedPreferences.Editor e = c.getSharedPreferences(PREFRENCE_NAME, Context.MODE_PRIVATE).edit();
        e.putString("MyObject", json);
        e.commit();
    }

    public static GetProjects get_utils(Context c) {
        SharedPreferences e = c.getSharedPreferences(PREFRENCE_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = e.getString("MyObject", "");
        GetProjects obj = gson.fromJson(json, GetProjects.class);
        return obj;
    }

    public static boolean getPref(Context c, String pref, Boolean val) {
        return c.getSharedPreferences(PREFRENCE_NAME, Context.MODE_PRIVATE).getBoolean(pref, val);
    }

    public static void deletePrefs(Context c) {
        c.getSharedPreferences(PREFRENCE_NAME, Context.MODE_PRIVATE).edit().clear().commit();
    }
    public static void deleteKey(Context activity,String key){
        SharedPreferences prefs = activity.getSharedPreferences(PREFRENCE_NAME,
                Context.MODE_PRIVATE);
        prefs.edit().remove(key).commit();
    }


}
