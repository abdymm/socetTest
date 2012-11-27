package com.ckt.testServer;

import java.io.IOException;
import java.net.Socket;

import android.util.Log;

public class Client {
	public static final int NEIGHBOUR_LEFT = 0;
	public static final int NEIGHBOUR_TOP = 1;
	public static final int NEIGHBOUR_RIGHT = 2;
	public static final int NEIGHBOUR_BOTTOM = 3;
	private static final String TAG = "Server_Client";
	
	private int mIndex;
	private Socket mSocket;
	private float mScreenWidth = -1;
	private float mScreenHight = -1;
	private Socket[] mNeighbours = new Socket[4];
	
	@SuppressWarnings("unused")
	private Client(){}
	
	public Client(Socket socket,int index){
		mSocket = socket;
		mIndex = index;
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
	
	public void addNeighbourLeft(Socket neighbour){
		addNeighbour(neighbour,NEIGHBOUR_LEFT);
	}
	public void addNeighbourRight(Socket neighbour){
		addNeighbour(neighbour,NEIGHBOUR_RIGHT);
	}
	public void addNeighbourTop(Socket neighbour){
		addNeighbour(neighbour,NEIGHBOUR_TOP);
	}
	public void addNeighbourBottom(Socket neighbour){
		addNeighbour(neighbour,NEIGHBOUR_BOTTOM);
	}
	
	public void addNeighbour(Socket neighbour,int direction){
		if(direction > NEIGHBOUR_BOTTOM || direction < NEIGHBOUR_LEFT){
			Log.e(TAG, "have no this direction");
		}else{
			mNeighbours[direction] = neighbour;
		}
	}
	
	public void closeSocket() throws IOException{
		if (null != mSocket) {
			mSocket.close();
		}
	}
	

}
