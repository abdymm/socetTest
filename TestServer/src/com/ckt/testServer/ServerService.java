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
import android.os.IBinder;

public class ServerService extends Service{
    private static final int PORT = 10999;
    private List<Socket> mList = new ArrayList<Socket>();
    private ServerSocket server = null;
    private ExecutorService mExecutorService = null; //thread pool
    private final MyBinder binder = new MyBinder();
    private ServiceListener mListener;
    
	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}
	
	@Override
	public void onCreate() {
		System.out.println("yadong onservice start");
		Thread thread1 = new Thread(new Thread1());
		thread1.start();
		super.onCreate();
		
	}
	
	class Thread1 implements Runnable{
		@Override
		public void run() {
			 try {
				 	System.out.println("yadong on thread start");
		            server = new ServerSocket(PORT);
		            mExecutorService = Executors.newCachedThreadPool();  //create a thread pool
		            System.out.print("yadong server start ...");
		            Socket client = null;
		            while(true) {
		                client = server.accept();
		                mList.add(client);
		                mListener.onNewClientConnect(client.getInetAddress().toString());
		                mExecutorService.execute(new MyService(client)); //start a new thread to handle the connection
		            }
		        }catch (Exception e) {
		        	 System.out.println("yadong ERROR !" +e.getMessage());
		            e.printStackTrace();
		        }
		}
		
	}
    class MyService implements Runnable {
        private Socket socket;
        private BufferedReader in = null;
        private String msg = "";
        
        public MyService(Socket socket) {
        	
            this.socket = socket;
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                msg = "user" +this.socket.getInetAddress() + "come toal:"
                    +mList.size();
                System.out.println("yadong msg="+msg);
//                this.sendmsg();
            } catch (IOException e) {
                e.printStackTrace();
            }
            
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            try {
                while(true) {
                    if((msg = in.readLine())!= null) {
                        if(msg.equals("exit")) {
                            System.out.println("ssssssss");
                            mList.remove(socket);
                            in.close();
                            msg = "user:" + socket.getInetAddress()
                                + "exit total:" + mList.size();
                            socket.close();
                            this.sendmsg();
                            break;
                        } else {
                            msg = socket.getInetAddress() + ":" + msg;
                            System.out.println("yadong ------ msg = "+msg);
                            this.sendmsg();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
      
       public void sendmsg() {
           System.out.println(msg);
           int num =mList.size();
           for (int index = 0; index < num; index ++) {
               Socket mSocket = mList.get(index);
               PrintWriter pout = null;
               try {
                   pout = new PrintWriter(new BufferedWriter(
                           new OutputStreamWriter(mSocket.getOutputStream())),true);
                   pout.println(msg);
               }catch (IOException e) {
                   e.printStackTrace();
               }
           }
       }
    }  
    
    public void registeLisener(ServiceListener listener){
    	mListener = listener;
    }
    
    interface ServiceListener{
    	public void onNewClientConnect(String ipAddress);
    	public void onClientDisconnect(String ipAddress);
    	
    }
    public class MyBinder extends Binder{
    	public ServerService getService(){
    		return ServerService.this;
    	}
    }
}
