/**
 * 
 */
package com.cassidy.wifi_file_sender;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TableLayout.LayoutParams;

import com.cassidy.wifi_file_sender.ClientService.ClienServiceListener;
import com.cassidy.wifi_file_sender.ClientService.ClientBinder;
import com.cassidy.wifi_file_sender.ServerService.ServerBinder;
import com.cassidy.wifi_file_sender.ServerService.ServerServiceListener;

/**
 * @author Administrator
 * 
 */
public class MainActivity extends Activity implements ClienServiceListener,
		ServerServiceListener {
	private static final int MSG_CONNECT_SUCCESS = 1;
	private static final int MSG_DISCONNECTED = 2;
	private static final int MSG_GET_COMMAND = 3;
	private static final int MSG_CONNECT_FAIL = 4;
	private static final int MSG_NEW_CLIENT = 5;
	private static final int MSG_CLIENT_DISCONNECTED = 6;

	private static final String LOCAL_IP = "127.0.0.1";

	private String mKey = null;
	private boolean mIsHost = false;
	private String mHostIp = null;
	private boolean mIsHostStart = false;
	private boolean mIsConnectedToHost = false;
	private List<Client> mClientList = new ArrayList<Client>();

	private ClientService mClientService;
	private ServerService mServerService;

	private TextView mClientId;
	private View mHostShow;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mClientId = (TextView) findViewById(R.id.client_show);
		mHostShow = findViewById(R.id.host_show);
		if (getIntent() != null) {
			mKey = getIntent().getExtras().getString(WelcomActivity.EXTRA_KEY,
					null);
			mHostIp = getIntent().getExtras().getString(
					WelcomActivity.EXTRA_HOST_IP, null);
		}
		if (null != mKey && !mKey.isEmpty()) {
			mIsHost = true;
		}
		
		// If the devices is host,start Server service
		if (mIsHost) {
			mHostIp = LOCAL_IP;
			bindService(ServerService.class);
		}
		// All devices need bind Client service;
		bindService(ClientService.class);

	}

	@Override
	protected void onResume() {
		super.onResume();
		setupHostView();
		setupVisibility();
	}

	private void bindService(Class<?> cls) {
		Intent intent = new Intent();
		intent.setClass(this, cls);
		bindService(intent, new Connection(), BIND_AUTO_CREATE);
	}

	private void setupVisibility() {

		if (mIsHost) {
			mClientId.setVisibility(View.GONE);
			mHostShow.setVisibility(View.VISIBLE);
		} else {
			mClientId.setVisibility(View.VISIBLE);
			mHostShow.setVisibility(View.GONE);
		}
	}

	private void setupHostView() {
		StringBuilder hostStr = new StringBuilder();
		hostStr.append(getStringFromResources(R.string.is_host));
		if (mIsHost) {
			hostStr.append(getStringFromResources(R.string.host));
			hostStr.append(getStringFromResources(R.string.device_key_is));
			hostStr.append(mKey);
		} else {
			hostStr.append(getStringFromResources(R.string.client));
		}
		((TextView) findViewById(R.id.host_view)).setText(hostStr.toString());
	}

	private String getStringFromResources(int id) {
		return getResources().getString(id);
	}

	private void updateClientListOnUI(){
		LinearLayout clientList = (LinearLayout) findViewById(R.id.client_list);
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		for(Client client : mClientList){
			View item = inflater.inflate(R.layout.client_item,null);
			TextView idView = (TextView) item.findViewById(R.id.client_id_view);
			idView.setBackgroundColor(client.getColor());
			idView.setText(client.getmIndex());
			TextView ipView = (TextView) item.findViewById(R.id.client_ip_view);
			ipView.setText(client.getmInternetAddress());
			clientList.addView(item, new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT));
		}
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_CONNECT_SUCCESS:
				String index = (String) msg.obj;
				String clientIndex = String.format(
						getStringFromResources(R.string.client_id), index);
				mClientId.setText(clientIndex);
				mIsConnectedToHost = true;
				break;
			case MSG_NEW_CLIENT:
				String clientIp = (String) msg.obj;
				String text = String.format(
						getResources().getString(
								R.string.new_client_connect_message), clientIp);
				Toast.makeText(MainActivity.this, text,Toast.LENGTH_SHORT).show();
				updateClientListOnUI();
				break;
			case MSG_CLIENT_DISCONNECTED:
				String clientIp2 = (String) msg.obj;
				String text2 = String.format(
						getResources().getString(
								R.string.client_disconnect_message), clientIp2);
				Toast.makeText(MainActivity.this, text2,Toast.LENGTH_SHORT).show();
				updateClientListOnUI();
				break;
			default:
				break;
			}
		};
	};

	/**
	 * On server service or client service connected
	 * 
	 * @author Administrator
	 * 
	 */
	private class Connection implements ServiceConnection {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			if (name.getClassName().equals(ServerService.class.getName())) {
				mServerService = ((ServerBinder) service).getService();
				mServerService.startServer();
				mServerService.registeLisener(MainActivity.this);
				mIsHostStart = true;
			} else if (name.getClassName()
					.equals(ClientService.class.getName())) {
				if (mHostIp != null && !mHostIp.isEmpty()) {
					mClientService = ((ClientBinder) service).getService();
					mClientService.startConnect(mHostIp);
					mClientService.rejestListener(MainActivity.this);
				}

			}

		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Util.logv("ManiActivity onServiceDisconnected "
					+ name.getClassName());
		
		}

	}

	/**
	 * Override from ClientService start
	 */
	@Override
	public void getCommand(String command) {
		Util.logv("MainActivity getCommand " + command);

	}

	@Override
	public void onDisconnect() {
		Util.logv("MainActivity onDisconnect ");

	}

	@Override
	public void onConnectSuccess(String index) {
		Message message = new Message();
		message.what = MSG_CONNECT_SUCCESS;
		message.obj = index;
		mHandler.sendMessage(message);
	}

	@Override
	public void onConnectFail(String string) {
		Util.logv("MainActivity onConnectFail " + string);
	}

	/**
	 * Override from ClientService end
	 */

	/**
	 * Override from ServerService start
	 */
	@Override
	public void onNewClientConnect(String ipAddress) {
		mClientList = mServerService.getClientList();
		Message message = new Message();
		message.what = MSG_NEW_CLIENT;
		message.obj = ipAddress;
		mHandler.sendMessage(message);
	}

	@Override
	public void onClientDisconnect(String ipAddress) {
		mClientList = mServerService.getClientList();
		Message message = new Message();
		message.what = MSG_CLIENT_DISCONNECTED;
		message.obj = ipAddress;
		mHandler.sendMessage(message);

	}
	/**
	 * Override from ServerService end
	 */

}
