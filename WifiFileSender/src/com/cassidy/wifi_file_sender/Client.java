package com.cassidy.wifi_file_sender;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import android.graphics.Color;
import android.util.Log;

/**
 * Client class.
 * 
 * @author Administrator
 * 
 */
public class Client {
	public static final int NEIGHBOUR_LEFT = 0;
	public static final int NEIGHBOUR_TOP = 1;
	public static final int NEIGHBOUR_RIGHT = 2;
	public static final int NEIGHBOUR_BOTTOM = 3;
	public static final int[] COLORS = new int[] { Color.RED, Color.BLUE,
			Color.GREEN, Color.YELLOW, Color.BLACK, Color.GRAY };
	public static final String CLIENT_ERROR_HEAD = "error:";
	public static final String CLIENT_WELCOME_HEAD = "welcome:";
	public static final String CLIENT_SEND_FILE = "file:";
	public static final String CLIENT_CHANGE_POSITION = "position:";
	public static final String SERVER_SCREEN_SIZE = "screensize:";

	private static final String TAG = "Server:Client";
	// client index in server.
	private int mIndex;
	private Socket mSocket;
	private int mScreenWidth = -1;
	private int mScreenHight = -1;
	private String mInternetAddress;
	private PrintWriter mPout = null;
	private BufferedReader mReader = null;

	@SuppressWarnings("unused")
	private Client() {
	}
	
	public Client(Socket socket, int index) throws Exception {
		if (socket == null) {
			Log.e(TAG, "socket can not be null");
			throw new Exception("socket can not be null");
		}
		mSocket = socket;
		mIndex = index;
		mInternetAddress = socket.getInetAddress().toString();
		mPout = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
				mSocket.getOutputStream())), true);
		mReader = new BufferedReader(new InputStreamReader(
				socket.getInputStream()));
	}
	public int getColor(){
		return COLORS[mIndex];
	}

	public String getmInternetAddress() {
		return mInternetAddress;
	}

	public int getmIndex() {
		return mIndex;
	}

	public void setScreenSize(int width, int hight) {
		mScreenHight = hight;
		mScreenWidth = width;
	}

	public int getScreenHight() {
		return mScreenHight;
	}

	public int getScreenWidth() {
		return mScreenWidth;
	}

	public Socket getSocket() {
		return mSocket;
	}

	public void sendMessage(String messageString) {
		System.out.println("yadng" + (mPout != null) + "  " + messageString);
		if (mPout != null) {
			mPout.println(messageString);
		}
		mPout.flush();
	}

	public BufferedReader getBufferedReader() {
		return mReader;
	}

	public void closeClient() throws IOException {
		if (mPout != null)
			mPout.close();
		if (mSocket != null)
			mSocket.close();
		if (mReader != null)
			mReader.close();
	}

}
