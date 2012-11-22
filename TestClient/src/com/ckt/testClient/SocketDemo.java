package com.ckt.testClient;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.testclient.R;

public class SocketDemo extends Activity{
    private TextView tv_msg = null;
    private EditText ed_msg = null;
    private Button btn_send = null;
    private Button btn_connect = null;
    private Button btn_disconnect = null;
//    private Button btn_login = null;
    private String content = "";
    private ClientService clientService;
    private boolean mIsServiceConnected = false;
    private static final int SEND_MESSAGE = 1;
    private static final int MSG_GET_RESPONSE = 2;
    private int trytime = 0;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_msg = (TextView) findViewById(R.id.TextView);
        ed_msg = (EditText) findViewById(R.id.EditText01);
        btn_connect = (Button) findViewById(R.id.connect);
        btn_disconnect = (Button) findViewById(R.id.disconnect);
//        btn_login = (Button) findViewById(R.id.Button01);
        btn_send = (Button) findViewById(R.id.Button02);
        btn_connect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				 Intent intent = new Intent(SocketDemo.this, ClientService.class);
				 bindService(intent,new ServiceConnection() {
						@Override
						public void onServiceDisconnected(ComponentName name) {
							mIsServiceConnected = false;
							clientService = null;
						}
						
						@Override
						public void onServiceConnected(ComponentName arg0, IBinder arg1) {
							mIsServiceConnected = true;
							clientService = ((ClientService.LocalBinder) arg1).returnService();
						}
					}, Context.BIND_AUTO_CREATE);
			}
		});
       
        
        
        btn_send.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandler.sendEmptyMessage(SEND_MESSAGE);
            }
        });
    }


    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
        	switch (msg.what) {
			case SEND_MESSAGE:
				String message = ed_msg.getText().toString();
				if(mIsServiceConnected){
					clientService.sendMessage(message);
				}else{
					if (trytime < 5) {
						mHandler.sendEmptyMessageDelayed(SEND_MESSAGE, 1000);
						trytime++;
					}
				}
				break;
			case MSG_GET_RESPONSE:
				if(!(msg.obj instanceof String)){
					break;
				}
				tv_msg.setTag(msg.obj);
			default:
				break;
			}
            super.handleMessage(msg);
            tv_msg.setText(tv_msg.getText().toString() + content);
        }
    };
    
}