package test.demo.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import test.demo.Record;

public class RecordOperation extends Operation<Record> {
	private static final String INSERT_SQL = "INSERT INTO "
			+ DbConnect.RECORD_TABLE_NAME
			+ "(staff_id, start_time, end_time, date) VALUES(?,?,?,?)";
	private static RecordOperation sRecordOperation = null;

	protected RecordOperation() throws ClassNotFoundException, SQLException {
		super();
	}

	public static RecordOperation getInstance() throws ClassNotFoundException, SQLException {
		if (sRecordOperation == null) {
			sRecordOperation = new RecordOperation();
		}
		return sRecordOperation;
	}

	public boolean insertRecord(List<Record> records) throws SQLException {
		return insert(INSERT_SQL, records);
	}

	protected void bindInsertValue(PreparedStatement preparedStatement, Record record) throws SQLException {
		preparedStatement.setInt(1, record.getStaffId());
		preparedStatement.setString(2, record.getStartTime());
		preparedStatement.setString(3, record.getEndTime());
		preparedStatement.setString(4, record.getEndTime());
		preparedStatement.addBatch();
	}

	@Override
	protected void bindUpdateValue(PreparedStatement preparedStatement,
			Record old, Record newO) throws SQLException {
		// TODO Auto-generated method stub
		
	}


	//Simple test code
	public static void main(String args []) throws ClassNotFoundException, SQLException {
		RecordOperation operation = RecordOperation.getInstance();
		Record record = new Record();
		record.setStaffId(1);
		record.setStartTime("22:22");
		record.setEndTime("22:23");
		record.setDate("2014-03-25");
		Record record2 = new Record();
		record2.setStaffId(1);
		record2.setStartTime("22:22");
		record2.setEndTime("22:23");
		record2.setDate("2014-03-26");
		List<Record> list = new ArrayList<Record>();
		list.add(record);
		list.add(record2);
		operation.insertRecord(list);
	}

}
