package com.ckt.client;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public final class Util {

	public static String getWifiIP(Context context) {
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ipAddress = wifiInfo.getIpAddress();

		StringBuffer sb = new StringBuffer("");
		sb.append(String.valueOf((ipAddress & 0x000000FF)));
		sb.append(".");
		sb.append(String.valueOf((ipAddress & 0x0000FFFF) >>> 8));
		sb.append(".");
		sb.append(String.valueOf((ipAddress & 0x00FFFFFF) >>> 16));
		sb.append(".");
		sb.append(String.valueOf((ipAddress >>> 24)));
		return sb.toString();

	}

}
