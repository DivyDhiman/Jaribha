package com.djhs16.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.List;

import org.apache.http.NameValuePair;

import android.content.Context;
import android.text.format.DateFormat;
import android.widget.Toast;

public class Commons {

	public static String serialize(Serializable obj) {
		if (obj == null)
			return "";
		try {
			ByteArrayOutputStream serialObj = new ByteArrayOutputStream();
			ObjectOutputStream objStream = new ObjectOutputStream(serialObj);
			objStream.writeObject(obj);
			objStream.close();
			return encodeBytes(serialObj.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error in Serialization");
		}
		return "";
	}

	public static Object deserialize(String str) {
		if (str == null || str.length() == 0)
			return null;
		try {
			ByteArrayInputStream serialObj = new ByteArrayInputStream(decodeBytes(str));
			ObjectInputStream objStream = new ObjectInputStream(serialObj);
			return objStream.readObject();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error in Deserialization");
		}
		return null;
	}

	public static String encodeBytes(byte[] bytes) {
		StringBuffer strBuf = new StringBuffer();

		for (int i = 0; i < bytes.length; i++) {
			strBuf.append((char) (((bytes[i] >> 4) & 0xF) + ((int) 'a')));
			strBuf.append((char) (((bytes[i]) & 0xF) + ((int) 'a')));
		}

		return strBuf.toString();
	}

	public static byte[] decodeBytes(String str) {
		byte[] bytes = new byte[str.length() / 2];
		for (int i = 0; i < str.length(); i += 2) {
			char c = str.charAt(i);
			bytes[i / 2] = (byte) ((c - 'a') << 4);
			c = str.charAt(i + 1);
			bytes[i / 2] += (c - 'a');
		}
		return bytes;
	}

	public static void toastMessage(Context context, String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
	}

	public static String formatDecimal(double d) {
		DecimalFormat format = new DecimalFormat("#.##");
		return format.format(d);
		// return String.format("%.2f", d);
	}

	public static String getStringMonth(int m) {
		return new String[] { "JAN", "FAB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC" }[m];
	}

	public static String date(final String format) {
		return DateFormat.format(format, System.currentTimeMillis()).toString();
	}

	public static String getUrlEncodedString(List<NameValuePair> nvp) {
		String encodedString = "";
		for (NameValuePair n : nvp) {
			try {
				String name = URLEncoder.encode(n.getName(), "ISO-8859-1");
				String value = URLEncoder.encode(n.getValue(), "ISO-8859-1");
				encodedString += name + "=" + value + "&";
			} catch (UnsupportedEncodingException e) {
				System.out.println("Encoding Error from AppUtils");
				e.printStackTrace();
			}
		}
		return encodedString;
	}

}
