package com.cassidy.wifi_file_sender;

import java.lang.reflect.Method;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiManager;
import android.util.Log;

public class MyWifiManager {
	public static final String AP_NAME = "WifiFileSender";
	//TODO generate password from Util
	private static final String AP_PASSWORD = "abcdefgh";
	private static final int SLEEP_TIME = 30000;
	public boolean mApStart = false;
	public boolean mConnectedServer = false;
	private Context mContext;
	private WifiManager mWifiManager; 
	private Listener mListener;
	
	public MyWifiManager(Context context){
		mContext = context;
		mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
	}
	
	public void registListener(Listener listener){
		this.mListener =listener;
	}
	public boolean enableAp(boolean state){
		if (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED && state) {
			mWifiManager.setWifiEnabled(false);
		}
		try {
			WifiConfiguration wifiConfiguration = new WifiConfiguration();
			wifiConfiguration.SSID = AP_NAME;
			wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
			wifiConfiguration.preSharedKey = AP_PASSWORD;
			
			Method method = mWifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
			if((Boolean) method.invoke(mWifiManager, wifiConfiguration,state)){
				mApStart = !mApStart;
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public void connectAp(){
		startScan();
		final WifiReceiver receiver = new WifiReceiver();
		mContext.registerReceiver(receiver, new IntentFilter(
				WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(SLEEP_TIME);
				} catch (InterruptedException e) {
					Log.e(this.getClass().getSimpleName(),"yadong"+e.getMessage());
					e.printStackTrace();
				}finally{
					mContext.unregisterReceiver(receiver);
					mListener.connectAPSuccess(false);
				}
			}
		}).start();
	
	}
	
	class WifiReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("onReceive");
			WifiConfiguration wifiConfiguration = null;
			

			for(ScanResult result : mWifiManager.getScanResults()){
				System.out.println("yadong -- "+result.toString());
				if(result.SSID.equals(AP_NAME)){
					WifiConfiguration config = new WifiConfiguration();
					config.SSID = "\"" + AP_NAME + "\"";
					config.preSharedKey = "\"" + AP_PASSWORD + "\"";
					config.BSSID = result.BSSID;
					config.allowedKeyManagement.set(KeyMgmt.NONE);
					mWifiManager.c
					mListener.connectAPSuccess(true);
				}
			}

		}
		
	}
	
	public void startScan(){
		int wifiState = mWifiManager.getWifiState();
		if (wifiState != WifiManager.WIFI_STATE_ENABLED && wifiState != WifiManager.WIFI_STATE_ENABLING ) {
			mWifiManager.setWifiEnabled(true);
		}
		mWifiManager.startScan();
	}
}
interface Listener{
	public void connectAPSuccess(boolean suceess);
}

