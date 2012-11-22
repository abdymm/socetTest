package com.ckt.testServer;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.ckt.testServer.ServerService.ServiceListener;

public class MainActivity extends Activity implements ServiceListener{
	private static final String TAG = "MainActivityYadong";
	private static final int MSG_CLIENT_CHANGE = 1;
	private ArrayList<String> mClientList;
	private Handler mHandler;
	private TextView textView;
	private ServerService serverService;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		textView = (TextView) findViewById(R.id.textview);
		mClientList = new ArrayList<String>();
	
		Intent intent = new Intent(MainActivity.this, ServerService.class);
		bindService(intent, new ServiceConnection() {
			@Override
			public void onServiceDisconnected(ComponentName name) {
				
			}
			
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				serverService = ((ServerService.MyBinder)service).getService();
				serverService.registeLisener(MainActivity.this);
			}
		},Context.BIND_AUTO_CREATE);
		mHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				StringBuffer sb = new StringBuffer();
				for(String string:mClientList){
					string = string.replaceAll("/", "");
					sb.append(string+"\n");
				}
				textView.setText(sb.toString());
			}
		};
		
	}

	@Override
	public void onNewClientConnect(String ipAddress) {
		Log.i(TAG, "ipAddress " + ipAddress + "connect!!");
		if (mClientList.indexOf(ipAddress) != -1) {
			Log.e(TAG, "ip address is exist abort");
			return;
		}
		mHandler.sendEmptyMessage(MSG_CLIENT_CHANGE);
		mClientList.add(ipAddress);
	}

	@Override
	public void onClientDisconnect(String ipAddress) {
		mClientList.remove(mClientList.indexOf(ipAddress));
		mHandler.sendEmptyMessage(MSG_CLIENT_CHANGE);
	}
}
