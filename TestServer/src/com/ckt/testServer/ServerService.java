package com.ckt.testServer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

public class ServerService extends Service {
	private static final int PORT = 10999;
	private static final String EXIT_COMMAND = "exit";
	private static final String CLIENT_ERROR_HEAD = "error:";
	private static final String CLIENT_IS_CONNECTED = "Client with ip:%s is connect";

	private List<Client> mClientList = new ArrayList<Client>();
	private ServerSocket mServer = null;
	private ExecutorService mExecutorService = null; // thread pool
	private final MyBinder binder = new MyBinder();
	private ServiceListener mListener;

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	@Override
	public void onCreate() {
		System.out.println("yadong onservice start");
		Thread thread1 = new Thread(new ClientReciver());
		thread1.start();
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			for (Client client : mClientList) {
				client.closeSocket();
			}
			mServer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean isClientConnected(Socket client) {
		for (Client socket : mClientList) {
			if (client.getInetAddress().toString()
					.equals(socket.getmSocket().getInetAddress().toString())) {
				String msg = String.format(CLIENT_IS_CONNECTED, client
						.getInetAddress().toString());
				sendErrorMessageAndDisconnect(client, msg);
				return true;
			}
		}
		return false;
	}

	private void sendErrorMessageAndDisconnect(Socket client,
			String errorMessage) {
		PrintWriter pout = null;
		try {
			pout = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
					client.getOutputStream())), true);
			pout.print(CLIENT_ERROR_HEAD + errorMessage);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				pout.close();
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public void registeLisener(ServiceListener listener) {
		mListener = listener;
	}
	
	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			
		};
	};
	
	private class ClientReciver implements Runnable {
		@Override
		public void run() {
			synchronized (mClientList) {
				try {
					mServer = new ServerSocket(PORT);
					mExecutorService = Executors.newCachedThreadPool();
					System.out.print("yadong server start ...");
					Socket client = null;
					while (true) {
						client = mServer.accept();
						if (!isClientConnected(client)) {
							mClientList.add(new Client(client,mClientList.size()));
							mListener.onNewClientConnect(client
									.getInetAddress().toString());
							mExecutorService.execute(new ClientServer(client));
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						mServer.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	class ClientServer implements Runnable {
		private Socket socket;
		private BufferedReader in = null;
		private String msg = "";

		public ClientServer(Socket socket) {
			this.socket = socket;
			try {
				in = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
				msg = "user" + this.socket.getInetAddress() + "come toal:"
						+ mClientList.size();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		@Override
		public void run() {
			try {
				while (true) {
					if ((msg = in.readLine()) != null) {
						if (EXIT_COMMAND.equals(msg)) {
							mClientList.remove(socket);
							mListener.onClientDisconnect(socket
									.getInetAddress().toString());
							in.close();
							socket.close();
							break;
						} else {
							msg = socket.getInetAddress() + ":" + msg;
							System.out.println("yadong ------ msg = " + msg);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void sendmsg() {
			PrintWriter pout = null;
			try {
				pout = new PrintWriter(new BufferedWriter(
						new OutputStreamWriter(socket.getOutputStream())), true);
				pout.println(msg);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	interface ServiceListener {
		public void onNewClientConnect(String ipAddress);

		public void onClientDisconnect(String ipAddress);

	}

	public class MyBinder extends Binder {
		public ServerService getService() {
			return ServerService.this;
		}
	}
}
