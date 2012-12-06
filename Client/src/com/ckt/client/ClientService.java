package com.ckt.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class ClientService extends Service {
	private static final String TAG = "ClientService";
	private static final int SERVER_PORT = 10999;
	private static final String EXIT_COMMAND = "exit";

	private ClientBinder binder = new ClientBinder();
	private ClienServiceListener mListener;
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
		Log.i(TAG, "on ClientServer created");
	}

	public void startConnect(String host) {
		mHost = host;
		thread = new Thread(connect);
		thread.start();
	}

	public void disConnect() {
		sendMessage(EXIT_COMMAND);
		init();
		mListener.onDisconnect();
	}

	Runnable connect = new Runnable() {
		@Override
		public void run() {
			try {
				if (mHost == null)
					return;
				socket = new Socket(mHost, SERVER_PORT);
				in = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
				out = new PrintWriter(new BufferedWriter(
						new OutputStreamWriter(socket.getOutputStream())), true);
				startLisen();
			} catch (IOException e) {
				e.printStackTrace();
				mListener.onConnectFail(e.getMessage());
			}
		}
	};

	public void sendMessage(String message) {
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

	// init all attribute
	private void init() {
		thread = null;
		try {
			if (socket != null) {
				socket.close();
				socket = null;
			}
			if (in != null) {
				in.close();
				in = null;
			}
			if (out != null) {
				out.close();
				out = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, e.getMessage());
		}
	}

	// start lisen to the server for response
	public void startLisen() {
		try {
			while (true) {
				if (socket != null && socket.isConnected()
						&& !socket.isInputShutdown()
						&& (mContent = in.readLine()) != null) {
					System.out.println("yadng   client" + mContent);
					if (mContent.startsWith(Client.CLIENT_CHANGE_POSITION)) {
						String position = mContent.replaceAll(
								Client.CLIENT_CHANGE_POSITION, "");
						mListener.getCommand(position);
					} else {
						if (mContent.startsWith(Client.CLIENT_WELCOME_HEAD)) {
							String index = mContent.replaceAll(
									Client.CLIENT_WELCOME_HEAD, "");
							mListener.onConnectSuccess(index);
						}
					}
				} else {
					mListener.onDisconnect();
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void scanServer() {
		System.out.println(Util.getWifiIP(this));
	}

	// regist listener
	public void rejestListener(ClienServiceListener listener) {
		mListener = listener;
	}

	interface ClienServiceListener {
		public void getCommand(String command);

		public void onDisconnect();

		public void onConnectSuccess(String index);

		public void onConnectFail(String string);
	}

	class ClientBinder extends Binder {
		public ClientService getService() {
			return ClientService.this;
		}
	}
}
