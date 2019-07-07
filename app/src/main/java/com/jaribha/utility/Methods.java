package com.jaribha.utility;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;
import android.provider.Settings;

import org.apache.http.conn.util.InetAddressUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Methods {

    private final static double AVERAGE_MILLIS_PER_MONTH = 365.24 * 24 * 60 * 60 * 1000 / 12;

    /**
     * Convert byte array to hex string
     *
     * @param bytes
     * @return
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuilder sbuf = new StringBuilder();
        for (int idx = 0; idx < bytes.length; idx++) {
            int intVal = bytes[idx] & 0xff;
            if (intVal < 0x10) sbuf.append("0");
            sbuf.append(Integer.toHexString(intVal).toUpperCase(Locale.getDefault()));
        }
        return sbuf.toString();
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @SuppressLint("NewApi")
    public static void initThreadPolicy() {
        // TODO Auto-generated method stub
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD) {
            StrictMode.ThreadPolicy tp = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(tp);
        }
    }

    //distance in miles
    public static double distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        float dist = (float) (earthRadius * c);

        return dist * 0.000621371;
    }

    public static String getCurrentDateAndTime() {
        DateFormat df = new SimpleDateFormat("MMM dd,yyyy, HH:mm");
        String date = df.format(Calendar.getInstance().getTime());
        return date;
    }

    public static int minutesDiff(Date earlierDate) {
        long MINUTE_MILLIS = 1000 * 60;

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy, HH:mm");
        Date laterDate = null;
        try {
            laterDate = sdf.parse(sdf.format(new Date()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (earlierDate == null || laterDate == null) return 0;

        //Log.e("earlier date",sdf.format(earlierDate)+">>>"+sdf.format(new Date()));
        return (int) ((System.currentTimeMillis() / MINUTE_MILLIS) - (earlierDate.getTime() / MINUTE_MILLIS));
    }

    //giving date difference in days
    public static int getDateDifference(Date startDate, Date endDate) {
        int difference =
                ((int) ((startDate.getTime() / (24 * 60 * 60 * 1000))
                        - (int) (endDate.getTime() / (24 * 60 * 60 * 1000))));
        return difference;
    }

    //giving date difference in months
    public static double monthsBetween(Date d1, Date d2) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy, HH:mm");
        Date currentDate = null;
        try {
            currentDate = sdf.parse(sdf.format(d2));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return (currentDate.getTime() - d1.getTime()) / AVERAGE_MILLIS_PER_MONTH;
    }


    //giving date difference in months
    public static final int getMonthsDifference(Date date1, Date date2) {
        int m1 = date1.getYear() * 12 + date1.getMonth();
        int m2 = date2.getYear() * 12 + date2.getMonth();
        return m2 - m1 + 1;
    }

    //check weather month is increased or not
    public static boolean isMonthIncreased(Date givenDate) {

        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yy");

        Date currentDate = null;

        try {
            currentDate = sdf.parse(sdf.format(new Date()));
            // Log.e("dates 1",(givenDate.getMonth())+">>"+(currentDate.getMonth()));
            //Log.e("dates 2",(givenDate.getMonth()+2)+">>"+(currentDate.getMonth()+1));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if ((givenDate.getMonth() + 1) == (currentDate.getMonth() + 1)) {
            // Log.e("dates",givenDate.getMonth()+">>"+currentDate.getMonth());
            return false;
        }


        return true;
    }

    public static String getDeviceId(Context ctx) {

        String device_uuid = Settings.Secure.getString(ctx.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        //Log.e("device id",device_uuid);
        return device_uuid;
    }


    //Give Current date
    public static Date getCurrentDate() {

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy, HH:mm");

        Date currentDate = null;

        try {
            currentDate = sdf.parse(sdf.format(new Date()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return currentDate;
    }

    public static boolean isMonthEquals(Date givenDate) {

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy, HH:mm");
        Date currentDate = null;
        try {
            currentDate = sdf.parse(sdf.format(new Date()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //Log.e("dates",givenDate.toString()+">>"+currentDate.toString());

        if (givenDate.getMonth() == currentDate.getMonth()) {
            return true;
        }

        return false;
    }

    public static String giveAbbreviations(double value) {

        String newValue = "";

        if (Math.floor(value) < 99999) {

            if (Math.floor(value) < 1000) {

            } else {
                newValue = "K";
            }
        } else {

            //value = value / 1000;
            newValue = "M";
        }

        return newValue;
    }

    public static String givePoints(double value) {

        String newValue = "";

        if (Math.floor(value) < 99999) {

            if (Math.floor(value) < 1000) {
                newValue = String.valueOf(Math.floor(value));
            } else {
                value = value / 1000;
                newValue = String.valueOf(value);
            }
        } else {

            value = value / 1000;
            newValue = String.valueOf(value);
        }

        return newValue;
    }


    /**
     * Get utf8 byte array.
     *
     * @param str
     * @return array of NULL if error was found
     */
    public static byte[] getUTF8Bytes(String str) {
        try {
            return str.getBytes("UTF-8");
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Load UTF8withBOM or any ansi text file.
     *
     * @param filename
     * @return
     * @throws IOException
     */
    public static String loadFileAsString(String filename) throws IOException {
        final int BUFLEN = 1024;
        BufferedInputStream is = new BufferedInputStream(new FileInputStream(filename), BUFLEN);
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(BUFLEN);
            byte[] bytes = new byte[BUFLEN];
            boolean isUTF8 = false;
            int read, count = 0;
            while ((read = is.read(bytes)) != -1) {
                if (count == 0 && bytes[0] == (byte) 0xEF && bytes[1] == (byte) 0xBB && bytes[2] == (byte) 0xBF) {
                    isUTF8 = true;
                    baos.write(bytes, 3, read - 3); // drop UTF8 bom marker
                } else {
                    baos.write(bytes, 0, read);
                }
                count += read;
            }
            return isUTF8 ? new String(baos.toByteArray(), "UTF-8") : new String(baos.toByteArray());
        } finally {
            try {
                is.close();
            } catch (Exception ex) {
            }
        }
    }

    /**
     * Returns MAC address of the given interface name.
     *
     * @param interfaceName eth0, wlan0 or NULL=use first interface
     * @return mac address or empty string
     */
    public static String getMACAddress(String interfaceName) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (interfaceName != null) {
                    if (!intf.getName().equalsIgnoreCase(interfaceName)) continue;
                }
                byte[] mac = intf.getHardwareAddress();
                if (mac == null) return "";
                StringBuilder buf = new StringBuilder();
                for (int idx = 0; idx < mac.length; idx++)
                    buf.append(String.format("%02X:", mac[idx]));
                if (buf.length() > 0) buf.deleteCharAt(buf.length() - 1);
                return buf.toString();
            }
        } catch (Exception ex) {
        } // for now eat exceptions
        return "";
        /*try {
            // this is so Linux hack
            return loadFileAsString("/sys/class/net/" +interfaceName + "/address").toUpperCase().trim();
        } catch (IOException ex) {
            return null;
        }*/
    }

    /**
     * Get IP address from first non-localhost interface
     * true=return ipv4, false=return ipv6
     *
     * @return address or empty string
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
        } // for now eat exceptions
        return "";
    }

}
