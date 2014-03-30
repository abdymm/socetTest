package test.demo.db;

import java.sql.*;

public class DbConnect {
	public static final String RECORD_TABLE_NAME = "record";
	public static final String STAFF_TABLE_NAME = "staff";

	private static final String URL = "jdbc:mysql://127.0.0.1:3306/servertest";
	private static final String USER = "root";
	private static final String PASSWORD = "admintest";
	private static DbConnect sInstance;
	private Connection mConnection;

	private DbConnect() throws ClassNotFoundException, SQLException {
		String driver = "com.mysql.jdbc.Driver";
		Class.forName(driver);
		mConnection = DriverManager.getConnection(URL, USER, PASSWORD);
	}

	public synchronized static DbConnect getInstance()
			throws ClassNotFoundException, SQLException {
		if (sInstance == null) {
			sInstance = new DbConnect();
		}
		return sInstance;
	}

	public void disconnect() throws SQLException {
		if (mConnection != null) {
			mConnection.close();
		}
	}

	public Connection getConnection() {
		return mConnection;
	}
}
