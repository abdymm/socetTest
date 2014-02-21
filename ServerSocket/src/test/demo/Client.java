
package test.demo;

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
    public static void main(String args []) {
        new Client().sendFile("d://NewVirtualDisk1.vhd");
    }

    private void sendFile(String filePath) {
        String host = "127.0.0.1"; // 要连接的服务端IP地址
        int port = 8899; // 要连接的服务端对应的监听端口
        DataOutputStream dos = null;
        DataInputStream dis = null;
        Socket socket = null;
        try {
            socket = new Socket(host, port);
            File file = new File(filePath);
            dos = new DataOutputStream(socket.getOutputStream());
            dis = new DataInputStream(new BufferedInputStream(new FileInputStream(filePath)));
            int buffferSize = 2048;
            byte[] bufArray = new byte[buffferSize];
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
                dos.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException e2) {

            }
        }
    }
}
