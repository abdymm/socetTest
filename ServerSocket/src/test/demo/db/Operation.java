
package test.demo.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Operation {
    private DbConnect mDbConnect;
    protected Connection mConnection;

    protected Operation() throws ClassNotFoundException, SQLException {
        mDbConnect = DbConnect.getInstance();
        mConnection = mDbConnect.getConnection();
    }

    protected PreparedStatement getPreparedStatement(String sql, int returnd) throws SQLException {
    	//Do not auto commit.
    	mConnection.setAutoCommit(false);
        if (returnd != -1) {
            return mConnection.prepareStatement(sql, returnd);
        }
        return mConnection.prepareStatement(sql);
    }
}
