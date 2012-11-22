package com.ckt.client;

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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.ckt.client.ClientService.ClienServiceListener;
import com.ckt.client.ClientService.ClientBinder;

public class ClientActivity extends Activity implements ClienServiceListener,OnClickListener{
	private static final int MSG_CONECT_SUCCESS = 1;
	private static final int MSG_DISCONECT_SUCCESS = 2;
	private Button mConnectBtn,mDisconnectBtn;
	private EditText mHostIp;
	private ClientService clientService;
	private LinearLayout layout;
	
	
	public TextWatcher watcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {}
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {}
		@Override
		public void afterTextChanged(Editable s) {
			String sarr [] = s.toString().split("\\.");
			if (sarr.length == 4) {
				mConnectBtn.setEnabled(true);
			}else{
				mConnectBtn.setEnabled(false);
			}
		}
	};
	
	public ServiceConnection connection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {	}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			clientService = ((ClientBinder) service).getService();
			clientService.rejestListener(ClientActivity.this);
		}
	};
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set no title and fullscreen
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.main);
		mConnectBtn = (Button) findViewById(R.id.connetct);
		mDisconnectBtn = (Button) findViewById(R.id.disconnetct);
		mConnectBtn.setOnClickListener(this);
		mDisconnectBtn.setOnClickListener(this);
		mHostIp = (EditText) findViewById(R.id.hostinput);
		layout = (LinearLayout) findViewById(R.id.parent);
		layout.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
		mHostIp.addTextChangedListener(watcher);
		Intent intent = new Intent(this,ClientService.class);
		bindService(intent,connection,Context.BIND_AUTO_CREATE);
    }
    
    public Handler mHandler = new Handler(){
    	public void handleMessage(Message msg) {
    		switch (msg.what) {
			case MSG_CONECT_SUCCESS:
				onConnectSuccessOnUI();
				break;
			case MSG_DISCONECT_SUCCESS:
				onDisConnectOnUI();
				break;
			default:
				break;
			}
    	};
    };
    
    @Override
    protected void onStop() {
    	super.onStop();
    	unbindService(connection);
    }


	@Override
	public void onClick(View v) {
		if (v.equals(mConnectBtn)) {
			clientService.startConnect(mHostIp.getText().toString());
		}else if(v.equals(mDisconnectBtn)){
			clientService.disConnect();
		}
		
	}
	
	@Override
	public void getCommand(String command) {
		// TODO Auto-generated method stub
		
	}
	public void onDisConnectOnUI(){
		mHostIp.setVisibility(View.VISIBLE);
		mHostIp.setText(null);
		mConnectBtn.setVisibility(View.VISIBLE);
		mDisconnectBtn.setEnabled(false);
		layout.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
	}
	
	@Override
	public void onDisconnect() {
		Message message = new Message();
		message.what = MSG_DISCONECT_SUCCESS;
		mHandler.sendMessage(message);
	}
	
	private void onConnectSuccessOnUI(){
		mHostIp.setVisibility(View.GONE);
		mConnectBtn.setVisibility(View.GONE);
		mDisconnectBtn.setEnabled(true);
		layout.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
	}
	@Override
	public void onConnectSuccess() {
		Message message = new Message();
		message.what = MSG_CONECT_SUCCESS;
		mHandler.sendMessage(message);
	}

	@Override
	public void onConnectFail(String string) {
		// TODO Auto-generated method stub
		
	}



}