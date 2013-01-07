package com.cassidy.wifi_file_sender;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private MyWifiManager mManager;
	private static final int MSG_CONECT_AP_SUCCESS = 1;
	private static final int MSG_CONECT_AP_FAIL = 2;
	
	private String mDevicesId = null;
	private View mDevicesView = null;
	private TextView mShowDevicesId = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mManager = new MyWifiManager(this);
        Button starAp = (Button) findViewById(R.id.start_server);
        mDevicesView = findViewById(R.id.devices_id_parent);
        mShowDevicesId = (TextView) findViewById(R.id.devices_id);
        starAp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mManager.enableAp(true)){
					mDevicesId = mManager.getDevicesId();
					mDevicesView.setVisibility(View.VISIBLE);
					mShowDevicesId.setText(mDevicesId);
					showToast(R.string.ap_start_success);
				}else{
					showToast(R.string.ap_start_fail);
				}
			}
		});
        Button connectAp = (Button) findViewById(R.id.scan_connect_server);
        connectAp.setOnClickListener(new ConnectApListener());
        
        
    }
    private  Handler mHandler = new Handler(){
    	public void handleMessage(Message msg) {
    		switch (msg.what) {
			case MSG_CONECT_AP_SUCCESS:
				showToast(R.string.connect_ap_success);
				break;
			case MSG_CONECT_AP_FAIL:
				showToast(R.string.connect_ap_fail);
				break;
			default:
				break;
			}
    	};
    };
    class ConnectApListener implements OnClickListener,Listener{
    	ProgressDialog dialog = null;
		@Override
		public void onClick(View v) {
			dialog = ProgressDialog.show(MainActivity.this, "Searching", "Searching...");
			mManager.connectAp();
			mManager.setKey("vcp1d");
			mManager.registListener(this);
		}

		@Override
		public void connectAPSuccess(boolean suceess) {
			System.out.println("connectAPSuccess");
			dialog.dismiss();
			if (suceess) {
				mHandler.sendEmptyMessage(MSG_CONECT_AP_SUCCESS);
				
			}else{
				mHandler.sendEmptyMessage(MSG_CONECT_AP_FAIL);
			}
			
		}
    	
    }
    private void showToast(int message){
    	showToast(message, Toast.LENGTH_SHORT);
    }
    private void showToast(int message,int time){
    	Toast.makeText(MainActivity.this,message,time).show();
    }
	
}