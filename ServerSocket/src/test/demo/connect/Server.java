
package test.demo.connect;

import test.demo.FileReslover;
import test.demo.Student;
import test.demo.db.MajorOperation;
import test.demo.db.StudentOperation;

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
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Server {
    public static final String START_HEAD = "FILE_START";
    public static final String RESULT_ERROR1 = "Reuqest error";
    public static final String RESULT_SEND_SUCCESS = "Save file success";
    public static final String RESULT_CONNECT_SUCCESS = "CONNECT_SUCCESS";
    private static final String FILE_SAVE_PATH = "f://saved_file/";
    private static final int BUFFERED_SIZE = 2048;
    private StudentOperation mStudentOperation;
    private MajorOperation mMajorOperation;

    public static void main(String args[]){
        try {
            new Server().startServer();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    private void startServer() throws IOException {
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
    class Task implements Runnable {

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

                writer = new OutputStreamWriter(socket.getOutputStream(), "UTF-8");
                while (true) {
                    String check = dataInputStream.readUTF();
                    if (START_HEAD.equals(check)) {
                        String filepath = readAndWriteToFile(dataInputStream);
                        FileReslover fileReslover = new FileReslover(filepath);
                        List<Student> students = fileReslover.resolve();
                        for(Student student : students) {
                            mStudentOperation = new StudentOperation();
                            mStudentOperation.insertStudent(student);
                        }
                    } else {
                        writer.write(RESULT_ERROR1);
                        writer.flush();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                dataInputStream.close();
                socket.close();
            }
        }
    }

    private static String readAndWriteToFile(DataInputStream dataInputStream) {
        checkDir();
        File outFile = new File(FILE_SAVE_PATH + System.currentTimeMillis());
        DataOutputStream fileOut = null;
        try {
            fileOut = new DataOutputStream(new BufferedOutputStream(
                    new FileOutputStream(outFile)));
            byte[] buf = new byte[BUFFERED_SIZE];
            long len = dataInputStream.readLong();
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
                if (passedlen == len) {
                    break;
                }
            }
            return outFile.getAbsolutePath();
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        } finally {
            try {
                if (fileOut != null) {
                    fileOut.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}