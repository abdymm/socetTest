package test.demo.connect;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import test.demo.FileReslover;
import test.demo.Record;
import test.demo.db.RecordOperation;

public class Server {
	public static final String START_HEAD = "FILE_START";
	public static final String RESULT_ERROR1 = "Reuqest error";
	public static final String RESULT_SEND_SUCCESS = "Save file success";
	public static final String RESULT_CONNECT_SUCCESS = "CONNECT_SUCCESS";
	public static final String RESULT_PROCESS_CHANGE_PREFIX = "process:";
	public static final String RESULT_WARNING_PREFIX = "warning:    ";
	public static final String RESULT_ERROR_PREFIX = "error:    ";
	public static final String RESULT_INFO_PREFIX = "info:    ";

	private static final String FILE_SAVE_PATH = "f://saved_file/";
	private static final int BUFFERED_SIZE = 2048;
	private DataOutputStream mWriter = null;

	public static void main(String args[]) {
		try {
			new Server().startServer();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void startServer() throws IOException {
		int port = 8899;
		ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 10, 1L,
				TimeUnit.HOURS, new LinkedBlockingDeque<Runnable>());
		ServerSocket server = new ServerSocket(port);
		while (true) {
			Socket socket = server.accept();
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
			try {
				dataInputStream = new DataInputStream(new BufferedInputStream(
						socket.getInputStream()));

				mWriter = new DataOutputStream(socket.getOutputStream());
				while (true) {
					String check = dataInputStream.readUTF();
					if (START_HEAD.equals(check)) {
						// Start loading
						writeInfo("Start uploding...");
						changeProcess(0);
						String filepath = readAndWriteToFile(dataInputStream);
						changeProcess(20);
						// Start resolve
						writeInfo("Start reslove...");
						// TODO error.....here
						FileReslover fileReslover = new FileReslover(filepath,
								Server.this);
						List<Record> records = null;
						try {
							records = fileReslover.resolve();
						} catch (test.demo.connect.FileResloveErrorException e) {
							writeAndFlush(e.getMessage());
						}
						changeProcess(40);
						if (records != null && records.size() != 0) {
							RecordOperation operation = RecordOperation
									.getInstance();
							operation.insertRecord(records);
							changeProcess(100);
						}
					} else {
						writeError(RESULT_ERROR1);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				writeError(e.getMessage());
			} finally {
				dataInputStream.close();
				socket.close();
			}
		}
	}

	public void writeWarning(String warning) throws IOException {
		writeAndFlush(RESULT_WARNING_PREFIX + warning);
	}

	public void writeError(String error) throws IOException {
		writeAndFlush(RESULT_ERROR_PREFIX + error);
	}

	public void writeInfo(String info) throws IOException {
		writeAndFlush(RESULT_INFO_PREFIX + info);
	}

	public void changeProcess(int process) throws IOException {
		writeAndFlush(RESULT_PROCESS_CHANGE_PREFIX + process);
	}

	private void writeAndFlush(String message) throws IOException {
		if (mWriter != null) {
			mWriter.writeUTF(message);
			mWriter.flush();
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