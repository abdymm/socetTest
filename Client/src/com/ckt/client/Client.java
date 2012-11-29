package com.ckt.client;

import java.io.IOException;
import java.net.Socket;

import android.util.Log;

/**
 * Client class. 
 * @author Administrator
 *
 */
public class Client {
	public static final int NEIGHBOUR_LEFT = 0;
	public static final int NEIGHBOUR_TOP = 1;
	public static final int NEIGHBOUR_RIGHT = 2;
	public static final int NEIGHBOUR_BOTTOM = 3;
	private static final String TAG  = "Server:Client";
	
	//client index in server.
	private int mIndex;
	private Socket mSocket;
	private float mScreenWidth = -1;
	private float mScreenHight = -1;
	private String[] mNeighbours = new String[4];
	private String mInternetAddress;
	
	@SuppressWarnings("unused")
	private Client(){}
	
	public Client(Socket socket,int index) throws Exception{
		if(socket == null){
			Log.e(TAG, "socket can not be null");
			throw new Exception("socket can not be null");
		}
		mSocket = socket;
		mIndex = index;
		mInternetAddress = socket.getInetAddress().toString();
	}
	
	public String getNeighbour(int direction){
		if(direction < NEIGHBOUR_LEFT || direction > NEIGHBOUR_BOTTOM){
			Log.e(TAG, "direction error");
			return null;
		}
		return mNeighbours[direction];
	}
	
	public String getmInternetAddress() {
		return mInternetAddress;
	}
	
	public int getmIndex() {
		return mIndex;
	}
	public void setScreenSize(float width,float hight){
		mScreenHight = hight;
		mScreenWidth = width;
	}
	public float getmScreenHight() {
		return mScreenHight;
	}
	public float getmScreenWidth() {
		return mScreenWidth;
	}
	public Socket getmSocket() {
		return mSocket;
	}

	public void addNeighbour(String neighbour, int direction) {
		if(direction < NEIGHBOUR_LEFT || direction > NEIGHBOUR_BOTTOM){
			Log.e(TAG, "direction error");
		}
		mNeighbours[direction] = neighbour;
	}
	
	public void closeSocket() throws IOException{
		if (null != mSocket) {
			mSocket.close();
		}
	}
	

}
