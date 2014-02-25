
package test.demo.connect;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Server {
    public static final String START_HEAD = "FILE_START";
    public static final String RESULT_ERROR1 = "Reuqest error";
    public static final String RESULT_SEND_SUCCESS = "Save file success";
    private static final String FILE_SAVE_PATH = "f://saved_file/";
    private static final int BUFFERED_SIZE = 2048;

    public static void main(String args[]) throws IOException {
        // 为了简单起见，所有的异常信息都往外抛
        int port = 8899;
        // 定义一个ServerSocket监听在端口8899上
        ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 10, 1L, TimeUnit.HOURS,
                new LinkedBlockingDeque<Runnable>());
        ServerSocket server = new ServerSocket(port);
        while (true) {
            // server尝试接收其他Socket的连接请求，server的accept方法是阻塞式的
            Socket socket = server.accept();
            // 每接收到一个Socket就建立一个新的线程来处理它
            Task task = new Task(socket);
            executor.execute(task);
        }
    }

    private static void checkDir() {
        File savePath = new File(FILE_SAVE_PATH);
        if (savePath.exists() && !savePath.isDirectory()) {
            savePath.delete();
        } else if (!savePath.exists()) {
            savePath.mkdirs();
        }
    }

    /**
     * 用来处理Socket请求的
     */
    static class Task implements Runnable {

        private Socket socket;

        public Task(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                handleSocket();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * 跟客户端Socket进行通信
         * 
         * @throws Exception
         */
        private void handleSocket() throws Exception {
            DataInputStream dataInputStream = null;
            Writer writer = null;
            try {
                dataInputStream = new DataInputStream(new BufferedInputStream(
                        socket.getInputStream()));
                String check = dataInputStream.readUTF();
                writer = new OutputStreamWriter(socket.getOutputStream(), "UTF-8");
                if (START_HEAD.equals(check)) {
                    // 检查存储位置是否准备好
                    checkDir();
                    String temFile = System.currentTimeMillis() + "";
                    File outFile = new File(FILE_SAVE_PATH + temFile);
                    DataOutputStream fileOut = new DataOutputStream(new BufferedOutputStream(
                            new FileOutputStream(outFile)));
                    try {
                        byte[] buf = new byte[BUFFERED_SIZE];
                        // 文件总大小
                        long len = dataInputStream.readLong();
                        // 目前接受大小
                        int passedlen = 0;
                        while (true) {
                            int read = 0;
                            if (dataInputStream != null) {
                                read = dataInputStream.read(buf);
                            }
                            passedlen += read;
                            if (read == -1) {
                                break;
                            }
                            fileOut.write(buf, 0, read);
                            System.out.println("recevied:" + (passedlen * 100 / len) + "%");
                        }
                        writer.write(RESULT_SEND_SUCCESS);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        fileOut.close();
                    }
                } else {
                    writer.write(RESULT_ERROR1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                dataInputStream.close();
                socket.close();
            }
        }
    }
}
