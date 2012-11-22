package com.ckt.testClient;

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

public class ClientService extends Service{
    private static final String HOST = "192.168.1.131";
    private static final int PORT = 9999;
	private Socket socket;
    private BufferedReader in = null;
    private PrintWriter out = null;
    private String content = "";
    private IBinder binder=new ClientService.LocalBinder();
    
	
	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();	
		System.out.println("debug service oncreate");
		Thread thread = new Thread(runnable);
		thread.start();
	}
	
	Runnable runnable = new Runnable() {
		public void run() {
			try {
				socket = new Socket(HOST, PORT);
				in = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
				out = new PrintWriter(new BufferedWriter(
						new OutputStreamWriter(socket.getOutputStream())), true);
				startLisen();
			} catch (IOException e) {
				e.printStackTrace();
				init();
				showToast(e.getMessage());
			}
		}
	};
	
	public void startLisen(){
		 try {
			while (true) {
			     if (socket.isConnected()) {
			         if (!socket.isInputShutdown()) {
			             if ((content = in.readLine()) != null) {
			                 content += "\n";
			                 System.out.println("content = "+content);
			             } else {

			             }
			         }
			     }
			 }
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendMessage(String message){
		if (socket != null && socket.isConnected()) {
            if (!socket.isOutputShutdown()) {
                out.println(message);
            }
        }
	}
	
	private void showToast(String message){	
		System.out.println("yadong message!!!"+message);
	}
	
	
	private void init(){
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
			showToast(e.getMessage());
		}
	}
	
	public class LocalBinder  extends Binder{
		public ClientService returnService(){
			return ClientService.this;
		}
	}

    public void run() {
        try {
           
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
