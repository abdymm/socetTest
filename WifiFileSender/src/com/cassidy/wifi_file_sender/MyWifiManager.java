package com.cassidy.wifi_file_sender;

import java.lang.reflect.Method;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.cassidy.wifi_file_sender.WifiConnect.WifiCipherType;

public class MyWifiManager {
	public static final String AP_NAME = "WifiFileSender";
	//TODO generate password from Util
	private static final String AP_PASSWORD = "abcdefgh";
	private static final int MAX_TRY_CONNECT_TIME = 2;
	private static final int DELY_FOR_RESULT = 10000;
	public boolean mApStart = false;
	public boolean mConnectedServer = false;
	private Context mContext;
	private WifiManager mWifiManager; 
	private Listener mListener;
	private String mDevicesId;
	private String mKey;
	
	public void setKey(String key) {
		mKey = key;
	}
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
			mDevicesId = Util.createRandomId();
			WifiConfiguration wifiConfiguration = new WifiConfiguration();
			wifiConfiguration.SSID = AP_NAME;
			wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
			wifiConfiguration.preSharedKey = AP_PASSWORD+mDevicesId;
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
	
	public String getDevicesId(){
		return mDevicesId;
	}
	
	public void connectAp(){
		final WifiReceiver wifiReceiver = new WifiReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		mContext.registerReceiver(wifiReceiver, filter);
		new Thread(new Runnable() {
			@Override
			public void run() {
				startScan();
				for (int time = 0;time <= MAX_TRY_CONNECT_TIME ;time++){
					try {
						Thread.sleep(DELY_FOR_RESULT);
					} catch (InterruptedException e) {}
					if(mConnectedServer){
						mContext.unregisterReceiver(wifiReceiver);
						mListener.connectAPSuccess(true);
						break;
					}else if(time == MAX_TRY_CONNECT_TIME){
						mContext.unregisterReceiver(wifiReceiver);
						mListener.connectAPSuccess(false);
					}
				}
			}
		}).start();
	
	}
	
	class WifiReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			System.out.println("yadong Actiong = "+action);
			if(action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)){
				if (!mConnectedServer) {
					for (ScanResult result : mWifiManager.getScanResults()) {
						if (result.SSID.equals(AP_NAME)) {
							WifiConnect connect = new WifiConnect(mWifiManager);
							String password = AP_PASSWORD + mKey;
							if (connect.connect(AP_NAME, password,
									WifiCipherType.WIFICIPHER_WPA)) {
								mConnectedServer = true;
							}
						}
					}
				}
			}else if(action.equals(ConnectivityManager.CONNECTIVITY_ACTION)){
				NetworkInfo info = intent
						.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
				System.out.println("yadong"+info.getState());
			}

		}
		
	}
	
	public void startScan(){
		int wifiState = mWifiManager.getWifiState();
		if (wifiState != WifiManager.WIFI_STATE_ENABLED && wifiState != WifiManager.WIFI_STATE_ENABLING ) {
			mWifiManager.setWifiEnabled(true);
			System.out.println("yadong Thread sleep");
			//dely 10s for open the wifi
			try {
				Thread.sleep(DELY_FOR_RESULT);
			} catch (InterruptedException e) {}
		}
		System.out.println("yadong code start");
		mWifiManager.startScan();
	}
}
interface Listener{
	public void connectAPSuccess(boolean suceess);
}

