package com.jaribha.utility;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.text.format.DateUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.widget.Toast;

import com.jaribha.R;
import com.jaribha.models.GetProjects;
import com.jaribha.shared_preferences.JaribhaPrefrence;

import org.apache.http.conn.util.InetAddressUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    private static Toast myToast;

    /*
     * To hide keyboard when focus on view
    */
    public static void hideKeyBoard(Context c, View v) {
        InputMethodManager imm = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public static Typeface getBoldFont(Context c) {
        try {
            return Typeface.createFromAsset(c.getAssets(), String.format("fonts/%s", "Lato-Bold_0.ttf"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Typeface getRegularFont(Context c) {
        try {
            return Typeface.createFromAsset(c.getAssets(), String.format("fonts/%s", "Lato-Regular.ttf"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getSupportedAmount(String total_support_amount, String goal) {
        double supportedAmount = 0;

        double supported = 0;
        try {
            if (!TextUtils.isNullOrEmpty(total_support_amount))
                supportedAmount = Double.parseDouble(total_support_amount);
            int intGoal = 0;

            if (!TextUtils.isNullOrEmpty(goal))
                intGoal = Integer.parseInt(goal);

            supported = (supportedAmount * 100) / intGoal;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new DecimalFormat("##.##").format(supported);
    }

    /**
     * Hide keyboard.
     *
     * @param ctx the ctx
     */
    public static void hideKeyboard(Activity ctx) {
        if (ctx.getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) ctx
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(ctx.getCurrentFocus().getWindowToken(),
                    0);
        }
    }

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        String result;
        Cursor cursor = context.getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    /*
     * Check Validation on Email address
    */
    public static boolean isValidEmailAddress(String emailAddress) {
        String expression = "[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(emailAddress);
        return matcher.matches();
    }

    @SuppressLint("SimpleDateFormat")
    public static boolean isProjectExpired(String endDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date strDate = null;
        try {
            strDate = sdf.parse(endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date().after(strDate);
    }

    public static void updateFavoriteCount(Context ctx, int count) {
        if (count == 1) {
            int newCount = JaribhaPrefrence.getPref(ctx, Constants.FAV_COUNT, 0) + count;
            JaribhaPrefrence.setPref(ctx, Constants.FAV_COUNT, newCount);
        } else {
            if (JaribhaPrefrence.getPref(ctx, Constants.FAV_COUNT, 0) != 0) {
                int newCount = JaribhaPrefrence.getPref(ctx, Constants.FAV_COUNT, 0) + count;
                JaribhaPrefrence.setPref(ctx, Constants.FAV_COUNT, newCount);
            }
        }
    }

    public static void playVideo(String url, Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.setDataAndType(Uri.parse(url), "video/*");
        context.startActivity(intent);
    }

    public static String getTimeSpan(long timeInMillis) {
        return DateUtils.getRelativeTimeSpanString(timeInMillis).toString();
    }

    public static void setDefaultValues(Context context) {
        JaribhaPrefrence.setPref(context, Constants.FEATURE, context.getString(R.string.recently_launched));
        JaribhaPrefrence.setPref(context, Constants.CATEGORY_NAME, context.getString(R.string.all_cat));
        JaribhaPrefrence.setPref(context, Constants.FEATURE_NAME, context.getString(R.string.all_features));
        JaribhaPrefrence.setPref(context, Constants.COUNTRY_NAME, context.getString(R.string.all_country));
        JaribhaPrefrence.setPref(context, Constants.FEATURE_ID, "recentlylaunched");
    }

    public static void watchYoutubeVideo(Context ctx, String id) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
            ctx.startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v=" + id));
            ctx.startActivity(intent);
        }
    }

    public static void watchVimeoVideo(Context ctx, String id) {
        try {
            Intent browserIntent;
            PackageManager pmi = ctx.getPackageManager();
            browserIntent = pmi.getLaunchIntentForPackage("com.vimeo.android.videoapp");
            browserIntent.setAction(Intent.ACTION_VIEW);
            browserIntent.setData(Uri.parse("http://player.vimeo.com/video/" + id));
            ctx.startActivity(browserIntent);
        } catch (Exception e) {
            // App is not Installed
            //Navigate to Play Store or display message
            ctx.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://player.vimeo.com/video/" + id)));
        }
    }

    /**
     * Method to format date
     *
     * @param context      context
     * @param inputFormat  current date format
     * @param outputFormat required date format
     * @param inputDate    date to be format
     * @return formatted date
     */
    public static String formateDateFromstring(Context context, String inputFormat, String outputFormat, String inputDate) {
        Date parsed;
        String outputDate = "";
        SimpleDateFormat df_input = new SimpleDateFormat(inputFormat.trim(), new Locale(JaribhaPrefrence.getPref(context, Constants.PROJECT_LANGUAGE, "en")));
        SimpleDateFormat df_output = new SimpleDateFormat(outputFormat.trim(), new Locale(JaribhaPrefrence.getPref(context, Constants.PROJECT_LANGUAGE, "en")));

        try {
            parsed = df_input.parse(inputDate);
            outputDate = df_output.format(parsed);
        } catch (ParseException e) {
            Log.d("Utility", "ParseException - dateFormat");
        }
        return outputDate;
    }

    public static String formateDateFromstring(String inputFormat, String outputFormat, String inputDate) {
        Date parsed;
        String outputDate = "";
        SimpleDateFormat df_input = new SimpleDateFormat(inputFormat, java.util.Locale.getDefault());
        SimpleDateFormat df_output = new SimpleDateFormat(outputFormat, java.util.Locale.getDefault());

        try {
            parsed = df_input.parse(inputDate);
            outputDate = df_output.format(parsed);
        } catch (ParseException e) {
            Log.d("Utility", "ParseException - dateFormat");
        }
        return outputDate;
    }

    /**
     * used to get IP address
     *
     * @param useIPv4 true/false
     * @return device IP address
     */
    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress().toUpperCase(Locale.getDefault());
                        boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 port suffix
                                return delim < 0 ? sAddr : sAddr.substring(0, delim);
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } // for now eat exceptions
        return "";
    }

    @SuppressLint("SimpleDateFormat")
    public static int getDateDifference(String startDate) throws ParseException {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date dateFrom = inputFormat.parse(startDate);

        return ((int) ((dateFrom.getTime() / (24 * 60 * 60 * 1000))
                - (int) (new Date().getTime() / (24 * 60 * 60 * 1000))));
    }

    // validating password with retype password
    public static boolean isValidPassword(String pass) {
        return pass != null && pass.length() > 4;
    }

    public static void showDataToast(String data, Context context) {
        if (myToast != null) {
            myToast.cancel();
            myToast = null;
        }
        if (myToast == null) {
            myToast = Toast.makeText(context, data, Toast.LENGTH_SHORT);
        }
        if (myToast != null) {
            myToast.setText(data);
        }
        myToast.show();
    }

    public static void changeLangauge(String langName, Context ctx) {
        Locale locale;
        if (langName.equals("ar")) {

            locale = new Locale("ar");
            Locale.setDefault(locale);
        } else {
            locale = new Locale("en");
            Locale.setDefault(locale);
        }

        Configuration config = new Configuration();
        config.locale = locale;
        ctx.getResources().updateConfiguration(config,
                ctx.getResources().getDisplayMetrics());
    }

    public static boolean isInternetConnected(Context mContext) {
        try {
            ConnectivityManager connect;
            connect = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connect != null) {
                NetworkInfo resultMobile = connect.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                NetworkInfo resultWifi = connect.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                return (resultMobile != null && resultMobile.isConnectedOrConnecting()) || (resultWifi != null && resultWifi.isConnectedOrConnecting());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @SuppressLint("SimpleDateFormat")
    public static String parseDateToMMMyyyy(String time) {
        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String outputPattern = "MMM-yyyy h:mma";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static boolean checkURL(CharSequence input) {
        if (TextUtils.isNullOrEmpty(input.toString())) {
            return false;
        }
        Pattern URL_PATTERN = Patterns.WEB_URL;
        boolean isURL = URL_PATTERN.matcher(input).matches();
        if (!isURL) {
            String urlString = input + "";
            if (URLUtil.isNetworkUrl(urlString)) {
                try {
                    new URL(urlString);
                    isURL = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return isURL;
    }

    public static String bitmapToBase64(String imagePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public static int getDeviceWidth(Context context) {
        try {
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            return metrics.widthPixels;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 480;
    }

    @SuppressLint("SimpleDateFormat")
    public static Date getStringToDate(Context context, String dateStr) {
        Date date = null;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale(JaribhaPrefrence.getPref(context, Constants.PROJECT_LANGUAGE, "en")));
        try {
            date = formatter.parse(dateStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    public static int getDeviceHeight(Context context) {
        try {
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            return metrics.heightPixels;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 480;
    }

    public static void showAlertDialog(Context context, String title, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        if (!TextUtils.isNullOrEmpty(title)) {
            alertDialog.setTitle(title);
        }
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton(context.getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    @SuppressLint("SimpleDateFormat")
    public static String formatToYesterdayOrToday(Context context, String date) {
        Date dateTime = null;
        try {
            dateTime = new SimpleDateFormat("yyyy-MM-dd").parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateTime);
        Calendar today = Calendar.getInstance();
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DATE, -1);
//        DateFormat timeFormatter = new SimpleDateFormat("hh:mma");
        DateFormat timeFormat = new SimpleDateFormat("MMMM dd,yyyy");

        if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
            return context.getString(R.string.today);
        } else if (calendar.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR)) {
            return context.getString(R.string.yesterday);
        } else {
            return timeFormat.format(dateTime);
        }
    }

    public static boolean checkForValidUserImage(Context context, String imagePath) {
        boolean isValidImage = false;
        try {
            // Get file from file name
            File file = new File(imagePath);
            // Get length of file in bytes
            long fileSizeInBytes = file.length();
            // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
            long fileSizeInKB = fileSizeInBytes / 1024;
            // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
            long fileSizeInMB = fileSizeInKB / 1024;

            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 1;
//            options.inJustDecodeBounds = true;
            // Convert imagePath to Bitmap
            Bitmap bMap = BitmapFactory.decodeFile(imagePath, options);
            // Get height from bitmap
            int imageHeight = bMap.getHeight();
            // Get width from bitmap
            int imageWidth = bMap.getWidth();

            if (fileSizeInMB > 1) { // check for valid image size
                isValidImage = false;
                showDataToast(context.getString(R.string.invalid_user_image_size), context);
            } else if (imageWidth < 400 && imageHeight < 400) { // check for valid image Dimension
                isValidImage = false;
                showDataToast(context.getString(R.string.invalid_user_image_dimension), context);
            } else {
                isValidImage = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            showDataToast(context.getString(R.string.invalid_user_image_size), context);
        }
        return isValidImage;
    }

    public static boolean checkForCommunityImage(Context context, String imagePath) {
        boolean isValidImage = false;
        try {
            // Get file from file name
            File file = new File(imagePath);
            // Get length of file in bytes
            long fileSizeInBytes = file.length();
            // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
            long fileSizeInKB = fileSizeInBytes / 1024;
            // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
            long fileSizeInMB = fileSizeInKB / 1024;

            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 1;
//            options.inJustDecodeBounds = true;
            // Convert imagePath to Bitmap
            Bitmap bMap = BitmapFactory.decodeFile(imagePath, options);
            // Get height from bitmap
            int imageHeight = bMap.getHeight();
            // Get width from bitmap
            int imageWidth = bMap.getWidth();

            if (fileSizeInMB > 3) { // check for valid image size
                isValidImage = false;
                showDataToast(context.getString(R.string.invalid_community_image_size), context);
            } else if (imageWidth < 400 && imageHeight < 400) { // check for valid image Dimension
                isValidImage = false;
                showDataToast(context.getString(R.string.invalid_user_image_dimension), context);
            } else {
                isValidImage = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            showDataToast(context.getString(R.string.invalid_community_image_size), context);
        }
        return isValidImage;
    }

    public static boolean checkForValidProjectImage(Context context, String imagePath) {
        boolean isValidImage = false;
        try {
            // Get file from file name
            File file = new File(imagePath);
            // Get length of file in bytes
            long fileSizeInBytes = file.length();
            // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
            long fileSizeInKB = fileSizeInBytes / 1024;
            // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
            long fileSizeInMB = fileSizeInKB / 1024;

            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 1;
            // Convert imagePath to Bitmap
            Bitmap bMap = BitmapFactory.decodeFile(imagePath, options);
            // Get height from bitmap
            int imageHeight = bMap.getHeight();
            // Get width from bitmap
            int imageWidth = bMap.getWidth();

            if (fileSizeInMB > 1) { // check for valid image size
                isValidImage = false;
                showDataToast(context.getString(R.string.invalid_user_image_size), context);
            } else if (imageWidth < 800 && imageHeight < 600) { // check for valid image Dimension
                isValidImage = false;
                showDataToast(context.getString(R.string.invalid_project_image_dimension), context);
            } else {
                isValidImage = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            showDataToast(context.getString(R.string.invalid_user_image_size), context);
        }
        return isValidImage;
    }

    public static String getDaysLeft(GetProjects data) {
        String period = "0";
        if (!TextUtils.isNullOrEmpty(data.enddate)) {
            if (!data.enddate.equals("0000-00-00")) {
                try {
                    int dayLeft = Utils.getDateDifference(data.enddate);
                    if (dayLeft < 0) {
                        period = "0";
                    } else
                        period = String.valueOf(dayLeft);
                } catch (ParseException e) {
                    e.printStackTrace();
                    period = "0";
                }
            } else {
                period = "0";
            }
        }
        return period;
    }

    public static String setProjectStatus(GetProjects data) {
        String status = "";
        if (!TextUtils.isNullOrEmpty(data.total_support_amount) && !TextUtils.isNullOrEmpty(data.goal)) {

            double supportAmt = Double.parseDouble(data.total_support_amount);
            double goalAmt = Double.parseDouble(data.goal);

            if (!TextUtils.isNullOrEmpty(data.enddate)) {
                if (!data.enddate.equals("0000-00-00")) {
                    if ((supportAmt < goalAmt)) {
                        if (Utils.isProjectExpired(data.enddate)) {
                            status = "complete";
                        } else {
                            status = "";
                        }
                    } else if (supportAmt >= goalAmt) {
                        status = "supported";
                    }
                } else {
                    status = "complete";
                }
            }
        }

        return status;
    }

    public static int getCategoryImage(String categoryId, boolean selected) {
        int drawable;
        switch (categoryId) {
            case "1":
                if (selected) {
                    drawable = R.drawable.art_icon_sel;
                } else {
                    drawable = R.drawable.art_icon;
                }
                break;

            case "12":
                if (selected) {
                    drawable = R.drawable.beauty_icon_sel;
                } else {
                    drawable = R.drawable.beauty_icon;
                }
                break;

            case "17":
                if (selected) {
                    drawable = R.drawable.crafts_icon_sel;
                } else {
                    drawable = R.drawable.crafts_icon;
                }
                break;

            case "2":
                if (selected) {
                    drawable = R.drawable.dance_icon_sel;
                } else {
                    drawable = R.drawable.dance_icon;
                }
                break;

            case "3":
                if (selected) {
                    drawable = R.drawable.design_icon_sel;
                } else {
                    drawable = R.drawable.design_icon;
                }
                break;

            case "24":
                if (selected) {
                    drawable = R.drawable.environment_icon_sel;
                } else {
                    drawable = R.drawable.environment_icon;
                }
                break;

            case "4":
                if (selected) {
                    drawable = R.drawable.fashion_icon_sel;
                } else {
                    drawable = R.drawable.fashion_icon;
                }
                break;

            case "18":
                if (selected) {
                    drawable = R.drawable.film_icon_sel;
                } else {
                    drawable = R.drawable.film_icon;
                }
                break;

            case "5":
                if (selected) {
                    drawable = R.drawable.food_icon_sel;
                } else {
                    drawable = R.drawable.food_icon;
                }
                break;

            case "6":
                if (selected) {
                    drawable = R.drawable.games_icon_sel;
                } else {
                    drawable = R.drawable.games_icon;
                }
                break;

            case "22":
                if (selected) {
                    drawable = R.drawable.health_icon_sel;
                } else {
                    drawable = R.drawable.health_icon;
                }
                break;

            case "9":
                if (selected) {
                    drawable = R.drawable.literary_icon_sel;
                } else {
                    drawable = R.drawable.literary_icon;
                }
                break;

            case "7":
                if (selected) {
                    drawable = R.drawable.music_icon_sel;
                } else {
                    drawable = R.drawable.music_icon;
                }
                break;

            case "15":
                if (selected) {
                    drawable = R.drawable.non_profit_icon_sel;
                } else {
                    drawable = R.drawable.non_profit_icon;
                }
                break;

            case "8":
                if (selected) {
                    drawable = R.drawable.photo_icon_sel;
                } else {
                    drawable = R.drawable.photo_icon;
                }
                break;

            case "25":
                if (selected) {
                    drawable = R.drawable.business_icon_sel;
                } else {
                    drawable = R.drawable.business_icon;
                }
                break;

            case "13":
                if (selected) {
                    drawable = R.drawable.sports_icon_sel;
                } else {
                    drawable = R.drawable.sports_icon;
                }
                break;

            case "10":
                if (selected) {
                    drawable = R.drawable.tech_icon_sel;
                } else {
                    drawable = R.drawable.tech_icon;
                }
                break;

            case "11":
                if (selected) {
                    drawable = R.drawable.theater_icon_sel;
                } else {
                    drawable = R.drawable.theater_icon;
                }
                break;

            case "21":
                if (selected) {
                    drawable = R.drawable.animal_icon_sel;
                } else {
                    drawable = R.drawable.animal_icon;
                }
                break;

            case "16":
                if (selected) {
                    drawable = R.drawable.comics_icon_sel;
                } else {
                    drawable = R.drawable.comics_icon;
                }
                break;

            case "23":
                if (selected) {
                    drawable = R.drawable.community_icon_sel;
                } else {
                    drawable = R.drawable.community_icon;
                }
                break;

            case "14":
                if (selected) {
                    drawable = R.drawable.cosmetics_icon_sel;
                } else {
                    drawable = R.drawable.cosmetics_icon;
                }
                break;

//            case "19": // Journalism
//                if (selected) {
//                    drawable = R.drawable.journalism_icon_sel;
//                } else {
//                    drawable = R.drawable.journalism_icon;
//                }
//                break;

            case "20":
                if (selected) {
                    drawable = R.drawable.publishing_icon_sel;
                } else {
                    drawable = R.drawable.publishing_icon;
                }
                break;

            default:
                if (selected) {
                    drawable = R.drawable.default_sel;
                } else {
                    drawable = R.drawable.default_icon;
                }
                break;
        }
        return drawable;
    }

    public static int getCountryImage(String countryId) {
        int drawable;
        switch (countryId) {
            case "3":
                drawable = R.drawable.andorra;
                break;

            case "4":
                drawable = R.drawable.uae;
                break;

            case "5":
                drawable = R.drawable.afghanistan;
                break;

            case "6":
                drawable = R.drawable.antugua;
                break;

            case "8":
                drawable = R.drawable.albania;
                break;

            case "15":
                drawable = R.drawable.american_samoa;
                break;

            case "16":
                drawable = R.drawable.austri;
                break;

            case "23":
                drawable = R.drawable.bangladesh;
                break;

            case "63":
                drawable = R.drawable.algeria;
                break;

            case "107":
                drawable = R.drawable.india;
                break;

            case "110":
                drawable = R.drawable.iran_islamic_rep;
                break;

            case "117":
                drawable = R.drawable.kenya;
                break;

            case "125":
                drawable = R.drawable.kuwait;
                break;

            case "194":
                drawable = R.drawable.soudi_arabia;
                break;

            default:
                drawable = R.drawable.defalt_flage;
                break;
        }
        return drawable;
    }
}
