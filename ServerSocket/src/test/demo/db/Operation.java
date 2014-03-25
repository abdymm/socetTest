package test.demo.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public abstract class Operation<T> {
	private DbConnect mDbConnect;
	private Connection mConnection;

	protected Operation() throws ClassNotFoundException, SQLException {
		mDbConnect = DbConnect.getInstance();
		mConnection = mDbConnect.getConnection();
	}

	private PreparedStatement getPreparedStatement(String sql, int returnd)
			throws SQLException {
		// Do not auto commit.
		if (returnd != -1) {
			return mConnection.prepareStatement(sql, returnd);
		}
		return mConnection.prepareStatement(sql);
	}

	protected abstract void bindInsertValue(
			PreparedStatement preparedStatement, T t) throws SQLException;

	protected abstract void bindUpdateValue(
			PreparedStatement preparedStatement, T old, T newO) throws SQLException;

	protected boolean insert(String insertSQL, List<T> insertValues)
			throws SQLException {
		PreparedStatement preparedStatement = null;
		try {
			mConnection.setAutoCommit(false);
			preparedStatement = getPreparedStatement(insertSQL,
					Statement.RETURN_GENERATED_KEYS);
			for (T t : insertValues) {
				bindInsertValue(preparedStatement, t);
			}
			preparedStatement.executeBatch();
			mConnection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				mConnection.rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
				throw new SQLException(e1.getMessage());
			}
			throw new SQLException(e.getMessage());
		} finally {
			mConnection.setAutoCommit(true);
		}
		return true;
	}

	protected void update(String updateSql, T old, T newO)
			throws SQLException {
		PreparedStatement preparedStatement = null;
		preparedStatement = getPreparedStatement(updateSql,
				Statement.RETURN_GENERATED_KEYS);
		bindUpdateValue(preparedStatement, old, newO);
		preparedStatement.executeUpdate();
	}
}
