package test.demo.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import test.demo.Record;

public class RecordOperation extends Operation<Record> {
	private static final String INSERT_SQL = "INSERT INTO "
			+ DbConnect.RECORD_TABLE_NAME
			+ "(staff_id, start_time, end_time, date) VALUES(?,?,?,?)";
	private static final String QUERY_ALL_SQL = "SELECT _id,staff_id,start_time,end_time,date FROM "
			+ DbConnect.RECORD_TABLE_NAME;
	private static final String QUERY_FROM_DATE = QUERY_ALL_SQL
			+ " WHERE date = ?";
	private static RecordOperation sRecordOperation = null;

	protected RecordOperation() throws ClassNotFoundException, SQLException {
		super();
	}

	public static RecordOperation getInstance() throws ClassNotFoundException,
			SQLException {
		if (sRecordOperation == null) {
			sRecordOperation = new RecordOperation();
		}
		return sRecordOperation;
	}

	public List<Record> queryAll() throws SQLException {
		ResultSet resultSet = null;
		List<Record> records = new ArrayList<Record>();
		try {
			resultSet = query(QUERY_ALL_SQL);
			while (resultSet.next()) {
				records.add(buildFromResult(resultSet));
			}
		} finally {
			if (resultSet != null) {
				resultSet.close();
			}
		}
		return records;
	}

	public List<Record> queryFromDate(String date) throws SQLException {
		ResultSet resultSet = null;
		List<Record> records = new ArrayList<Record>();
		try {
			resultSet = query(QUERY_FROM_DATE, date);
			while (resultSet.next()) {
				records.add(buildFromResult(resultSet));
			}
		} finally {
			if (resultSet != null) {
				resultSet.close();
			}
		}
		return records;
	}

	private Record buildFromResult(ResultSet resultSet) throws SQLException {
		Record record = new Record();
		record.setId(resultSet.getInt(1));
		record.setStaffId(resultSet.getInt(2));
		record.setStartTime(resultSet.getString(3));
		record.setEndTime(resultSet.getString(4));
		record.setDate(resultSet.getString(5));
		return record;
	}

	public boolean insertRecord(List<Record> records) throws SQLException {
		return insert(INSERT_SQL, records);
	}

	protected void bindInsertValue(PreparedStatement preparedStatement,
			Record record) throws SQLException {
		preparedStatement.setInt(1, record.getStaffId());
		preparedStatement.setString(2, record.getStartTime());
		preparedStatement.setString(3, record.getEndTime());
		preparedStatement.setString(4, record.getDate());
	}

	@Override
	protected void bindUpdateValue(PreparedStatement preparedStatement,
			Record old, Record newO) throws SQLException {
		//Do nothing. no update method.
	}

	// //Simple test code
	public static void main(String args[]) throws ClassNotFoundException,
			SQLException {
//		 RecordOperation operation = RecordOperation.getInstance();
//		 Record record = new Record();
//		 record.setStaffId(1);
//		 record.setStartTime("22:22");
//		 record.setEndTime("22:23");
//		 record.setDate("2014-03-25");
//		 Record record2 = new Record();
//		 record2.setStaffId(1);
//		 record2.setStartTime("22:22");
//		 record2.setEndTime("22:23");
//		 record2.setDate("2014-03-26");
//		 List<Record> list = new ArrayList<Record>();
//		 list.add(record);
		// System.out.println(operation.queryAll());
		// System.out.println(operation.queryFromDate("2014-03-26"));
	}

}
