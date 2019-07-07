package com.jaribha.utility;

import android.content.Context;

import com.jaribha.R;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.util.Calendar;
import java.util.Date;

public class TimeConstants {
    public static Date currentDate() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime();
    }

    public static String getJodaTimeAgo(Date date, Context ctx) {
        DateTime myBirthDate = new DateTime(date);
        DateTime now = new DateTime();
        Period period = new Period(myBirthDate, now);
        PeriodFormatter formatter = new PeriodFormatterBuilder()
                .appendYears().appendSuffix(ctx.getString(R.string.years))
                .appendMonths().appendSuffix(ctx.getString(R.string.months))
                .appendWeeks().appendSuffix(ctx.getString(R.string.weeks))
                .appendDays().appendSuffix(ctx.getString(R.string.days1))
                .appendHours().appendSuffix(ctx.getString(R.string.hours))
                .appendMinutes().appendSuffix(ctx.getString(R.string.minutes))
                .appendSeconds().appendSuffix(ctx.getString(R.string.seconds))
                .printZeroNever()
                .toFormatter();

        return formatter.print(period);
    }

    private static int getTimeDistanceInMinutes(long time) {
        long timeDistance = currentDate().getTime() - time;
        return Math.round((Math.abs(timeDistance) / 1000) / 60);
    }

    public static String getRelativeTime(Date millisString) {
        String time = "Just Now";
        long timeMills = millisString.getTime();

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
}
