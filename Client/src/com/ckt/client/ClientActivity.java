package com.ckt.client;

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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ckt.client.ClientService.ClienServiceListener;
import com.ckt.client.ClientService.ClientBinder;
import com.ckt.client.ServerService.ServerBinder;
import com.ckt.client.ServerService.ServerServiceListener;

public class ClientActivity extends Activity implements ClienServiceListener,
		OnClickListener, ServerServiceListener {
	private static final int MSG_CONNECT_SUCCESS = 1;
	private static final int MSG_DISCONECT_SUCCESS = 2;
	private static final int MSG_GET_COMMAND = 3;
	private static final int MSG_CONNECT_FAIL = 4;
	private static final int MSG_SERVER_STARTED = 5;
	private static final int MSG_SERVER_STOPED = 6;
	private static final int MSG_CLIENT_CONNECT = 7;
	private static final int MSG_CLIENT_DISCONNECT = 8;

	private static final String ITEM_IMAGE = "image";
	private static final String ITEM_NAME = "name";

	private static final String TAG = "ClientActivity";
	private static final String LOCAL_IP = "127.0.0.1";

	private Button mConnectBtn;
	private EditText mHostIp;
	private ClientService mClientService;
	private ServerService mServerService;
	private ImageView mImage;
	private TextView mTextView;
	private ListView mAllClientList;
	private TextView mAllClientText;
	private LinearLayout mLinearLayout;
	// mark the client service is connect .
	private boolean mIsClientServiceConnect;
	// mark the server is started . not the server service.
	private boolean mIsServerServiceStarted;
	private ArrayList<String> mClientList = new ArrayList<String>();

	public TextWatcher watcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			String sarr[] = s.toString().split("\\.");
			if (sarr.length == 4) {
				mConnectBtn.setEnabled(true);
			} else {
				mConnectBtn.setEnabled(false);
			}
		}
	};

	public ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			if (ClientService.class.getName().equals(name.getClassName())) {
				mClientService = ((ClientBinder) service).getService();
				mClientService.rejestListener(ClientActivity.this);
			} else if (ServerService.class.getName()
					.equals(name.getClassName())) {
				mServerService = ((ServerBinder) service).getService();
				mServerService.registeLisener(ClientActivity.this);
			}
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// set no title and fullscreen
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.main);
		mConnectBtn = (Button) findViewById(R.id.connetct);
		mConnectBtn.setOnClickListener(this);
		mHostIp = (EditText) findViewById(R.id.hostinput);
		mHostIp.addTextChangedListener(watcher);
		mImage = (ImageView) findViewById(R.id.imageView);
		mImage.setOnTouchListener(new ImageTouchListener());
		mLinearLayout = (LinearLayout) findViewById(R.id.client_list);
		mAllClientList = (ListView) findViewById(R.id.all_clients);
		mAllClientText = (TextView) findViewById(R.id.all_client_message);
		mTextView = (TextView) findViewById(R.id.client_welcome);
		Intent intent = new Intent(this, ClientService.class);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
		Intent serverIntent = new Intent(ClientActivity.this,
				ServerService.class);
		bindService(serverIntent, mConnection, Context.BIND_AUTO_CREATE);
	}

	public Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_CONNECT_SUCCESS:
				onConnectSuccessOnUI((String) msg.obj);
				break;
			case MSG_DISCONECT_SUCCESS:
				onDisConnectOnUI();
				break;
			case MSG_GET_COMMAND:
				int positions[] = (int[]) msg.obj;
				changePosition(positions);
				break;
			case MSG_CONNECT_FAIL:
				String message = (String) msg.obj;
				String errorFormat = getResources().getString(
						R.string.connect_fail);
				errorFormat = String.format(errorFormat, mHostIp.getText()
						.toString());
				Log.e(TAG, message);
				Toast.makeText(ClientActivity.this, errorFormat,
						Toast.LENGTH_SHORT).show();
				break;
			case MSG_SERVER_STARTED:
				mLinearLayout.setVisibility(View.VISIBLE);
				break;
			case MSG_SERVER_STOPED:
				mLinearLayout.setVisibility(View.GONE);
				break;
			case MSG_CLIENT_CONNECT:
			case MSG_CLIENT_DISCONNECT:
				String format = getResources().getString(
						R.string.client_list_message);
				String currentId  = Util.getWifiIP(ClientActivity.this);
				String text = String.format(format, mClientList.size(),currentId);
				mAllClientText.setText(text);
				
				break;
			default:
				break;
			}
		};
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.client_activity_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.menu_disconnect).setVisible(
				mIsClientServiceConnect && !mIsServerServiceStarted);
		// if server is started , we should not show the start server item.
		menu.findItem(R.id.menu_start_server).setVisible(
				!mIsServerServiceStarted && !mIsClientServiceConnect);
		// server is not started , need not show the end item.
		menu.findItem(R.id.menu_end_server).setVisible(mIsServerServiceStarted);

		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void onClick(View v) {
		if (v.equals(mConnectBtn) && null != mClientService) {
			// start client service connect.
			mClientService.startConnect(mHostIp.getText().toString());
		}

	}

	public void onItemClick(MenuItem item) {
		Log.d(TAG, "on menu item click item = " + item.getItemId());
		switch (item.getItemId()) {
		case R.id.menu_disconnect:
			if (null != mClientService) {
				// disconnect the client service connection.
				mClientService.disConnect();
			}
			break;
		case R.id.menu_client_config:
			// TODO .. config client position.

			break;
		case R.id.menu_start_server:
			// start server. when server service is binding.
			mServerService.startServer();
			Toast.makeText(ClientActivity.this, R.string.server_started,
					Toast.LENGTH_SHORT).show();
			mIsServerServiceStarted = true;
			mClientService.startConnect(LOCAL_IP);
			mHandler.sendEmptyMessage(MSG_SERVER_STARTED);
			break;
		case R.id.menu_end_server:
			// end the server
			mServerService.init();
			mIsServerServiceStarted = false;
			mHandler.sendEmptyMessage(MSG_SERVER_STOPED);
			mClientList = new ArrayList<String>();
			break;
		default:
			Log.e(TAG, "have no this item id!!!");
			break;
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(mConnection);
	}

	@Override
	public void getCommand(String command) {
		// handle message
		String[] positionStr = command.split("\\|");
		int[] positions = new int[positionStr.length];
		for (int index = 0; index < positionStr.length; index++) {
			positions[index] = Integer.parseInt(positionStr[index]);
		}
		Message message = new Message();
		message.what = MSG_GET_COMMAND;
		message.obj = positions;
		mHandler.sendMessage(message);
	}

	public void onDisConnectOnUI() {
		mHostIp.setVisibility(View.VISIBLE);
		mHostIp.setText(null);
		mConnectBtn.setVisibility(View.VISIBLE);
		mImage.setVisibility(View.GONE);
		mTextView.setText("");
		mTextView.setVisibility(View.GONE);
	}

	@Override
	public void onDisconnect() {
		Message message = new Message();
		message.what = MSG_DISCONECT_SUCCESS;
		mHandler.sendMessage(message);
		mIsClientServiceConnect = false;
	}

	private void onConnectSuccessOnUI(String index) {
		mHostIp.setVisibility(View.GONE);
		mConnectBtn.setVisibility(View.GONE);
		mImage.setVisibility(View.VISIBLE);
		mTextView.setBackgroundColor(Client.COLORS[Integer.parseInt(index)]);
		mTextView.getBackground().setAlpha(100);
		mTextView.setVisibility(View.VISIBLE);
		mTextView.setText("Welcom. client:" + index);
		//hide the inputMethod
		final InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);       
		imm.hideSoftInputFromWindow(mHostIp.getWindowToken(), 0); 
	}
		
	@Override
	public void onConnectSuccess(String index) {
		Message message = new Message();
		message.what = MSG_CONNECT_SUCCESS;
		message.obj = index;
		mHandler.sendMessage(message);
		mIsClientServiceConnect = true;
		mClientService.sendMessage(Client.SERVER_SCREEN_SIZE+getScreenSize());
	}
	private String getScreenSize(){
		WindowManager manager = getWindowManager();
		int width = manager.getDefaultDisplay().getWidth();
		int height = manager.getDefaultDisplay().getHeight();
		return Util.changeToString(width, height);
	}

	@Override
	public void onConnectFail(String string) {
		Message message = new Message();
		message.what = MSG_CONNECT_FAIL;
		message.obj = string;
		mHandler.sendMessage(message);

	}

	public void changePosition(int positions[]) {
		mImage.layout(positions[0], positions[1], positions[2], positions[3]);
	}

	class ImageTouchListener implements OnTouchListener {
		private float startX = 0;
		private float startY = 0;

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN: {
				startX = event.getRawX();
				startY = event.getRawY();
				break;
			}
			case MotionEvent.ACTION_MOVE: {
				// 计算偏移量
				int dx = (int) (event.getRawX() - startX);
				int dy = (int) (event.getRawY() - startY);
				System.out.println("onTouched dx = " + dx + " dy = " + dy);
				// 计算控件的区域
				int left = v.getLeft() + dx;
				int right = v.getRight() + dx;
				int top = v.getTop() + dy;
				int bottom = v.getBottom() + dy;
				v.layout(left, top, right, bottom);
				startX = event.getRawX();
				startY = event.getRawY();
				mClientService.sendMessage(left + "|" + top + "|" + right + "|"
						+ bottom);
				System.out.println("yadong left = " + left + " top = " + top
						+ " right = " + right + " bottom=" + bottom);
				break;
			}
			}
			return false;
		}

	}

	@Override
	public void onNewClientConnect(String ipAddress) {
		System.out.println("yadong onNewClientConnect ="+ipAddress);
		mClientList.add(ipAddress);
		mHandler.sendEmptyMessage(MSG_CLIENT_CONNECT);
	}

	@Override
	public void onClientDisconnect(String ipAddress) {
		System.out.println("yadong onClientDisconnect ="+ipAddress);
		mClientList.remove(ipAddress);
		mHandler.sendEmptyMessage(MSG_CLIENT_DISCONNECT);
	}

}