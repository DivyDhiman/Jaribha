package com.djhs16.utils;

import android.text.format.DateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    public static String getCurrentAge(String yyyy_MM_dd) {
        String age = "";
        try {
            Date dateOfBirth = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(yyyy_MM_dd);

            Calendar calendar = Calendar.getInstance();
            int nowYear = calendar.get(Calendar.YEAR);

            calendar.setTime(dateOfBirth);
            int dobYear = calendar.get(Calendar.YEAR);

            age = (nowYear - dobYear) + "";
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return age;
    }

    public static String getRelativeTime(String millisString) {
        String time = "Just Now";
        long timeMills = Long.parseLong(millisString) * 1000l;

        Calendar nowTime = Calendar.getInstance();
        Calendar postTime = Calendar.getInstance();
        postTime.setTimeInMillis(timeMills);

        int year = nowTime.get(Calendar.YEAR) - postTime.get(Calendar.YEAR);
        int month = nowTime.get(Calendar.MONTH) - postTime.get(Calendar.MONTH);
        int day = nowTime.get(Calendar.DAY_OF_MONTH) - postTime.get(Calendar.DAY_OF_MONTH);
        int hours = nowTime.get(Calendar.HOUR_OF_DAY) - postTime.get(Calendar.HOUR_OF_DAY);
        int mins = nowTime.get(Calendar.MINUTE) - postTime.get(Calendar.MINUTE);

        if (year > 0) {
            time = year + " year ago";
        } else if (month > 0) {
            time = month + " month ago";
        } else if (day > 0) {
            time = day + " day ago";
        } else if (hours > 0) {
            time = hours + " hour ago";
        } else if (mins > 0) {
            time = mins + " minute ago";
        }

        return time;
    }

    public static String formatDate(Date d) {
        return DateFormat.format("dd-MM-yyyy kk:mm AA", d).toString();
    }
}
