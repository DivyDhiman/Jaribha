package com.djhs16.net;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

public class NetworkStatus {

	/**
	 * permissions - READ_PHONE_STATE
	 */
	public static String getIMEI(Context c) {
		TelephonyManager tm = (TelephonyManager) c.getSystemService(android.content.Context.TELEPHONY_SERVICE);
		String IMEIorMEID = tm.getDeviceId();
		return IMEIorMEID;
	}

	/**
	 * permissions - READ_PHONE_STATE
	 */
	public static String getEmail(Context c) {
		String emailId = null;
		AccountManager accountManager = AccountManager.get(c.getApplicationContext());
		Account[] accounts = accountManager.getAccountsByType("com.google");
		if (accounts.length > 0) {
			emailId = accounts[0].name;
		} else {
		}
		return emailId;
	}

	/**
	 * permissions - ACCESS_NETWORK_STATE, INTERNET
	 */
	public static boolean isConnected(Context c) {
		ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo nInfo = cm.getActiveNetworkInfo();
		return nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
	}

	/**
	 * permissions - ACCESS_NETWORK_STATE, INTERNET
	 */
	public static boolean isConnecting(Context c) {
		ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo nInfo[] = cm.getAllNetworkInfo();
		if (nInfo != null) {
			for (NetworkInfo info : nInfo) {
				if (info.isConnectedOrConnecting()) {
					return true;
				}
			}
		}
		return false;
	}
}
