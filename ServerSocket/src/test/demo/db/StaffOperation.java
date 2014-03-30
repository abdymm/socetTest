package test.demo.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import test.demo.Staff;

public class StaffOperation extends Operation<Staff> {
	private static final String INSERT_SQL = "INSERT INTO "
			+ DbConnect.STAFF_TABLE_NAME
			+ "(name,sex, department) VALUES(?,?,?)";
	private static final String QUERY_ALL_SQL = "SELECT _id,name,sex,department FROM "
			+ DbConnect.STAFF_TABLE_NAME;
	private static final String QUERY_DEPARTMENT_SQL = QUERY_ALL_SQL
			+ " WHERE department=?";
	private static final String QUERY_ID_SQL = QUERY_ALL_SQL + " WHERE _id=?";
	private static final String QUERY_NAME_SQL = QUERY_ALL_SQL
			+ " WHERE name=?";
	private static StaffOperation sStaffOperation;

	protected StaffOperation() throws ClassNotFoundException, SQLException {
		super();
	}

	public static StaffOperation getInstance() throws ClassNotFoundException,
			SQLException {
		if (sStaffOperation == null) {
			sStaffOperation = new StaffOperation();
		}
		return sStaffOperation;
	}

	public boolean insert(List<Staff> staffs) throws SQLException {
		return insert(INSERT_SQL, staffs);
	}

	private List<Staff> queryFromSelection(String sql,String ... selections) throws SQLException {
		ResultSet resultSet = null;
		List<Staff> staffs = new ArrayList<Staff>();
		try {
			resultSet = query(sql, selections);
			while (resultSet.next()) {
				staffs.add(buildFromResult(resultSet));
			}
		} finally {
			if (resultSet != null) {
				resultSet.close();
			}
		}
		return staffs;
	}

	public List<Staff> queryAll() throws SQLException {
		return queryFromSelection(QUERY_ALL_SQL);
	}

	public List<Staff> queryFromDepartment(String department)
			throws SQLException {
		return queryFromSelection(QUERY_DEPARTMENT_SQL, department);
	}

	public List<Staff> queryFromName(String name) throws SQLException {
		return queryFromSelection(QUERY_NAME_SQL, name);
	}

	public Staff queryFromId(int id) throws SQLException {
		ResultSet resultSet = null;
		Staff staff = null;
		try {
			resultSet = query(QUERY_ID_SQL, id + "");
			while (resultSet.next()) {
				staff = buildFromResult(resultSet);
			}
		} finally {
			if (resultSet != null) {
				resultSet.close();
			}
		}
		return staff;
	}

	private Staff buildFromResult(ResultSet resultSet) throws SQLException {
		Staff staff = new Staff();
		staff.setId(resultSet.getInt(1));
		staff.setName(resultSet.getString(2));
		staff.setSex(resultSet.getInt(3) == 1);
		staff.setDepartment(resultSet.getString(4));
		return staff;
	}

	@Override
	protected void bindInsertValue(PreparedStatement preparedStatement, Staff t)
			throws SQLException {
		System.out.println("binInserValue staff = " + t);
		preparedStatement.setString(1, t.getName());
		preparedStatement.setInt(2, t.getSex() ? 1 : 0);
		preparedStatement.setString(3, t.getDepartment());
	}

	@Override
	protected void bindUpdateValue(PreparedStatement preparedStatement,
			Staff old, Staff newO) throws SQLException {
	}

	// Simple test code
	public static void main(String args[]) throws ClassNotFoundException,
			SQLException {
		StaffOperation operation = StaffOperation.getInstance();
		// System.out.println(operation.getAll());
		// System.out.println(operation.getFromDepartment("smart phone"));
		// System.out.println(operation.getFromId(1));
		List<Staff> staffs = new ArrayList<Staff>();
		Staff staff = new Staff();
		staff.setDepartment("featurephone");
		staff.setName("test");
		staff.setSex(false);
		staffs.add(staff);
		Staff staff2 = new Staff();
		staff2.setDepartment("test");
		staff2.setName("zhongyan");
		staff2.setSex(false);
		staffs.add(staff2);
		operation.insert(staffs);
	}

}
