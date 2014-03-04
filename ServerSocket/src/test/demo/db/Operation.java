
package test.demo.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Operation {
    private DbConnect mDbConnect;
    private Connection mConnection;

    public Operation() throws ClassNotFoundException, SQLException {
        mDbConnect = DbConnect.getInstance();
        mConnection = mDbConnect.getConnection();
    }

    protected PreparedStatement getPreparedStatement(String sql, int returnd) throws SQLException {
        if (returnd != -1) {
            return mConnection.prepareStatement(sql, returnd);
        }
        return mConnection.prepareStatement(sql);
    }
}
