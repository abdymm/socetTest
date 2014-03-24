package test.demo.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import test.demo.Record;

public class RecordOperation extends Operation {
	private static final String INSERT_SQL = "INSERT INTO "
			+ DbConnect.RECORD_TABLE_NAME
			+ "(staff_id, start_time, end_time, date) VALUES(?,?,?,?)";

	protected RecordOperation() throws ClassNotFoundException, SQLException {
		super();
	}

	private void bindRecord(PreparedStatement preparedStatement, Record record)
			throws SQLException {
		preparedStatement.setInt(1, record.getStaffId());
		preparedStatement.setString(2, record.getStartTime());
		preparedStatement.setString(3, record.getEndTime());
		preparedStatement.setString(4, record.getEndTime());
		preparedStatement.addBatch();
	}

	public boolean insertRecords(List<Record> records) throws SQLException {
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = getPreparedStatement(INSERT_SQL,
					Statement.RETURN_GENERATED_KEYS);
			for (Record record : records) {
				bindRecord(preparedStatement, record);
			}
			preparedStatement.executeBatch();
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				mConnection.rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
				throw new SQLException(e1.getMessage());
			}
			throw new SQLException(e.getMessage());
		}
		return true;
	}

}
