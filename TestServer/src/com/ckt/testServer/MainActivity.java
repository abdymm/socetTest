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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.ckt.testServer.ServerService.ServiceListener;

public class MainActivity extends Activity implements ServiceListener{
	private static final String TAG = "MainActivityYadong";
	private static final int MSG_SERVICE_CONNECT = 1;
	private static final int MSG_SERVICE_DISCONNECT = 2;
	
	private ArrayList<String> mClientList;
	private ListView mClientListView;
	private TextView mNoClientView;
	private ServerService serverService;
	private ArrayAdapter<String> mArrayAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mNoClientView = (TextView) findViewById(R.id.no_client);
		mClientListView = (ListView) findViewById(R.id.clients);
		mClientList = new ArrayList<String>();
		
		mArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, mClientList);
		mClientListView.setAdapter(mArrayAdapter);
		Intent intent = new Intent(MainActivity.this, ServerService.class);
		bindService(intent,new ServerServiceConnection(),Context.BIND_AUTO_CREATE);
	}
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			Log.v(TAG, "onHandleMessage msg = "+msg.what);
			switch (msg.what) {
			case MSG_SERVICE_CONNECT:
				onNewClientConnectOnUI();
				break;
			case MSG_SERVICE_DISCONNECT:
				onClientDisconnectOnUI();
			default:
				break;
			}
		}
	};
	
	//ServiceConnection handler
	class ServerServiceConnection implements ServiceConnection {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			serverService = ((ServerService.MyBinder)service).getService();
			serverService.registeLisener(MainActivity.this);
		}
		@Override
		public void onServiceDisconnected(ComponentName name) {
		}
		
	}
	private void onNewClientConnectOnUI(){
		mNoClientView.setVisibility(View.GONE);
		mClientListView.setVisibility(View.VISIBLE);
		mArrayAdapter.notifyDataSetChanged();
		mClientListView.setAdapter(mArrayAdapter);
	}
	private void onClientDisconnectOnUI(){
		if (mClientList.size() <= 0) {
			mNoClientView.setVisibility(View.VISIBLE);
			mClientListView.setVisibility(View.GONE);
		}else{
			mArrayAdapter.notifyDataSetChanged();
			mClientListView.setAdapter(mArrayAdapter);
		}
	}
	
	@Override
	public void onNewClientConnect(String ipAddress) {
		Log.i(TAG, "ipAddress " + ipAddress + "connect!!");
		if (mClientList.indexOf(ipAddress) != -1) {
			Log.e(TAG, "ip address is exist abort");
			return;
		}
		mHandler.sendEmptyMessage(MSG_SERVICE_CONNECT);
		mClientList.add(ipAddress);
	}

	@Override
	public void onClientDisconnect(String ipAddress) {
		Log.i(TAG, "ipAddress " + ipAddress + "connect!!");
		mClientList.remove(mClientList.indexOf(ipAddress));
		mHandler.sendEmptyMessage(MSG_SERVICE_DISCONNECT);
	}
}
