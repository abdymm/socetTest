package com.ckt.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class ClientService extends Service{
	private static final String TAG = "ClientService";
	private static final int SERVER_PORT = 10999;
	private static final String EXIT_COMMAND = "exit";
	private static final String CLIENT_ERROR_HEAD = "error:";
	
	private ClientBinder binder = new ClientBinder();
	private List<ClienServiceListener> mListeners;
	private Socket socket;
    private BufferedReader in = null;
    private PrintWriter out = null;
    private String mContent = "";
    private String mHost = "";
    private Thread thread;
	
	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(TAG,"on ClientServer created");
		mListeners = new ArrayList<ClientService.ClienServiceListener>();
	}
	public void startConnect(String host){
		mHost = host;
		thread = new Thread(connect);
		thread.start();
	}
	
	public void disConnect(){
		sendMessage(EXIT_COMMAND);
		init();
		for(ClienServiceListener listener:mListeners){
			listener.onDisconnect();
		}
	}
	
	Runnable connect = new Runnable() {
		@Override
		public void run() {
			try {
				if(mHost == null)
					return;
				socket = new Socket(mHost, SERVER_PORT);
				in = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
				out = new PrintWriter(new BufferedWriter(
						new OutputStreamWriter(socket.getOutputStream())), true);
				for(ClienServiceListener listener:mListeners){
					listener.onConnectSuccess();
				}
				startLisen();
			} catch (IOException e) {
				e.printStackTrace();
				for(ClienServiceListener listener:mListeners){
					listener.onConnectFail(e.getMessage());
				}
			}
		}
	};
	
	public void sendMessage(String message){
		if (socket != null && socket.isConnected()) {
            if (!socket.isOutputShutdown()) {
                out.println(message);
            }
        }
	}
	
	public void onDestroy() {
		super.onDestroy();
		init();
	};
	
	//init all attribute
	private void init(){
		thread = null;
		try {
			if(socket != null){
				socket.close();
				socket = null;
			}
			if(in != null){
				in.close();
				in = null;
			}
			if(out != null){
				out.close();
				out = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, e.getMessage());
		}
	}
	
	//start lisen to the server for response
	public void startLisen() {
		try {
			while (true) {
				if (socket.isConnected() && !socket.isInputShutdown()
						&& (mContent = in.readLine()) != null) {
					if(mContent.startsWith(CLIENT_ERROR_HEAD)){
						Log.e(TAG, mContent);
						//TODO server return error.
					}
					for(ClienServiceListener listener:mListeners){
						listener.getCommand(mContent);
					}
				}else if(!socket.isConnected() || socket.isInputShutdown()){
					for(ClienServiceListener listener:mListeners){
						listener.onDisconnect();
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//regist listener
	public void rejestListener(ClienServiceListener listener){
		mListeners.add(listener);
	}
	
	interface ClienServiceListener{
		public void getCommand(String command);
		public void onDisconnect();
		public void onConnectSuccess();
		public void onConnectFail(String string);
	}
	
	class ClientBinder extends Binder{
		public ClientService getService(){
			return ClientService.this;
		}
	}
}
