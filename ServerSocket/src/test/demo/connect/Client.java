
package test.demo.connect;

import test.demo.Log;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    private Socket socket = null;
    private String ip = null;
    private int port = 8080;
    private ClientConnectListener mListener;
    private ClientFileSendListener mFileSendListener;
    private static final int BUFFERED_SIZE = 2048;
    private boolean connected = false;

    public Client(ClientConnectListener listener) {
        mListener = listener;
    }

    public void setListener(ClientConnectListener listener) {
        mListener = listener;
    }

    public void setFileSendLitener(ClientFileSendListener clientFileSendListener) {
        this.mFileSendListener = clientFileSendListener;
    }

    public void connect(final String address) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ip = address.substring(0, address.indexOf(":"));
                    port = Integer.parseInt(address.substring(address.indexOf(":") + 1));
                    socket = new Socket(ip, port);
                    connected = true;
                    DataInputStream dataInputStream;
                    dataInputStream = new DataInputStream(socket.getInputStream());
                    mListener.onConnectSuccess(true);
                    while (true) {
                        String returnStr = dataInputStream.readUTF();
                        Log.D("Get return code:" + returnStr);
                        if (Server.RESULT_CONNECT_SUCCESS.equals(returnStr)) {
                            mListener.onConnectSuccess(true);
                        } else if (Server.RESULT_STATE_RESOLVE.equals(returnStr)
                                || Server.RESULT_STATE_UPLOADING.equals(returnStr)
                                || Server.RESULT_STATE_SUCCESS.equals(returnStr)) {
                            mFileSendListener.onProcess(returnStr);
                        } else {
                            mFileSendListener.onError(returnStr);
                        }
                    }
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    mListener.onConnectError(e.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                    mListener.onConnectError(e.getMessage());
                }
            }
        }).start();
    }

    public void disConnect() {
        if (connected) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        connected = false;
    }

    public void sendFile(final String filePath) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DataOutputStream dos = null;
                DataInputStream dis = null;
                try {
                    File file = new File(filePath);
                    dos = new DataOutputStream(socket.getOutputStream());
                    dis = new DataInputStream(
                            new BufferedInputStream(new FileInputStream(filePath)));
                    byte[] bufArray = new byte[BUFFERED_SIZE];
                    dos.writeUTF(Server.START_HEAD);
                    dos.flush();
                    dos.writeLong(file.length());
                    dos.flush();
                    while (true) {
                        int read = 0;
                        if (dis != null) {
                            read = dis.read(bufArray);
                        }
                        if (read == -1) {
                            break;
                        }
                        dos.write(bufArray, 0, read);
                    }
                    dos.flush();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        dis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (NullPointerException e2) {
                    }
                }
            }

        }).start();
    }
}
