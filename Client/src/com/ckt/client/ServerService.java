package com.ckt.client;

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
import android.os.IBinder;

public class ServerService extends Service {
	private static final int PORT = 10999;
	private static final String EXIT_COMMAND = "exit";
	private static final String CLIENT_ERROR_HEAD = "error:";
	private static final String CLIENT_WELCOME_HEAD = "welcome:";
	private static final String CLIENT_SEND_FILE = "file:";
	private static final String CLIENT_CHANGE_POSITION = "position:";
	private static final int MAX_CLIENT = Client.COLORS.length;
	
	private static final String CLIENT_IS_CONNECTED = "Client with ip:%s is connect";

	private List<Client> mClientList = new ArrayList<Client>();
	private ServerSocket mServer = null;
	private ExecutorService mExecutorService = null; // thread pool
	private final ServerBinder binder = new ServerBinder();
	private ListenerList mListeners = new ListenerList();
	private Thread mMainThread;

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		init();
	}
	public void startServer() {
		if(mMainThread == null){
			mMainThread = new Thread(new ClientReciver());
			mMainThread.start();
		}else{
			System.out.println("yadong error !!!! mManiThread != null");
		}
	}
	
	public void init(){
		try {
			for (Client client : mClientList) {
				client.closeClient();
			}
			if(mServer != null){
				mServer.close();
			}
			if(mExecutorService != null){
				mExecutorService.shutdown();
			}
			mMainThread = null;
			mClientList = new ArrayList<Client>();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean isClientConnected(Socket client) {
		for (Client socket : mClientList) {
			if (client.getInetAddress().toString()
					.equals(socket.getmInternetAddress())) {
				String msg = String.format(CLIENT_IS_CONNECTED, client
						.getInetAddress().toString());
				sendErrorMessageAndDisconnect(client, msg);
				System.out.println("yaodong"+msg);
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

	public void registeLisener(ServerServiceListener listener) {
		mListeners.registerListener(listener);
	}
	
	
	private boolean isClientFull(Socket client){
		if(mClientList.size() >= MAX_CLIENT){
			String message = getResources().getString(R.string.client_list_full);
			sendErrorMessageAndDisconnect(client, message);
		}
		return false;
	}
	
	private void sendWelcomeMessage(Client client){
		//send welcome message to client
		StringBuilder sb = new StringBuilder();
		sb.append(CLIENT_WELCOME_HEAD);
		sb.append(client.getmIndex());
		client.sendMessage(sb.toString());
	}
	
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
						//judge the client list is full or the client with a exist ip.
						if (!isClientFull(client) && !isClientConnected(client)) {
							mListeners.onNewClientConnect(client
									.getInetAddress().toString());
							Client clientc = new Client(client,mClientList.size());
							mClientList.add(clientc);
							mExecutorService.execute(new ClientServer(clientc));
							sendWelcomeMessage(clientc);
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
		private Client client;
		private BufferedReader in = null;
		private String msg = "";

		public ClientServer(Client client) {
			this.client = client;
			in = client.getBufferedReader();
			msg = "user" + this.client.getmInternetAddress() + "come toal:"
					+ mClientList.size();
		}

		@Override
		public void run() {
			try {
				while (true) {
					if ((msg = in.readLine()) != null) {
						if (EXIT_COMMAND.equals(msg)) {
							onClientDisconnect(client);
							break;
						} else {
//							TODO get msssage from client just for test
//							for(Client client : mClientList){
//								if(!client.getmInternetAddress().equals(socket.getInetAddress().toString())){
//									sendmsg(client.getmSocket(),msg);
//								}
//							}
							msg = client.getmInternetAddress() + ":" + msg;
							System.out.println("yadong "+msg);
						}
					}else{
						onClientDisconnect(client);
						break;
					}	
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		private void onClientDisconnect(Client clientc) throws IOException{
			mClientList.remove(clientc);
			for(Client client : mClientList){
				if (client.getmInternetAddress().equals(clientc.getmInternetAddress())) {
					mClientList.remove(client);
				}
			}
			mListeners.onClientDisconnect(clientc
					.getmInternetAddress());
			clientc.closeClient();
		}
		
		public void sendmsg(Socket clients,String msg) {
			PrintWriter pout = null;
			try {
				pout = new PrintWriter(new BufferedWriter(
						new OutputStreamWriter(clients.getOutputStream())), true);
				pout.println(msg);
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				pout.close();
			}
			
		}
	}

	interface ServerServiceListener {
		public void onNewClientConnect(String ipAddress);

		public void onClientDisconnect(String ipAddress);

	}

	public class ServerBinder extends Binder {
		public ServerService getService() {
			return ServerService.this;
		}
	}
	
	class ListenerList implements ServerServiceListener{
		private ArrayList<ServerServiceListener> serviceListeners = new ArrayList<ServerServiceListener>(); 
		
		public void registerListener(ServerServiceListener listener){
			serviceListeners.add(listener);
		}
		
		public void unRegisterListener(ServerServiceListener listener){
			serviceListeners.remove(listener);
		}
		
		@Override
		public void onNewClientConnect(String ipAddress) {
			for(ServerServiceListener listener : serviceListeners){
				listener.onNewClientConnect(ipAddress);
			}
		}

		@Override
		public void onClientDisconnect(String ipAddress) {
			for(ServerServiceListener listener : serviceListeners){
				listener.onNewClientConnect(ipAddress);
			}
		}
		
	}


}
