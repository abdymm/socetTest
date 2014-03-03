package test.demo.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import test.demo.Student;

public class StudentOperation {
	private DbConnect mDbConnect;
	private Connection mConnection;
	private static final String INSERT_SQL = "INSERT INTO "
			+ DbConnect.STUDENT_TABLE_NAME
			+ "(name, grade, className, sex) VALUES (?,?,?,?)";

	public StudentOperation() {
		try {
			mDbConnect = DbConnect.getInstance();
			mConnection = mDbConnect.getConnection();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean insertStudent(Student student) {
		int createdId = -1;
		ResultSet result = null;
		try {
			PreparedStatement preparedStatement = mConnection.prepareStatement(
					INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
			preparedStatement.setString(1, student.getName());
			preparedStatement.setInt(2, student.getGrade());
			preparedStatement.setInt(3, student.getClassNumber());
			// 1 is a boy 0 is a girl.
			preparedStatement.setInt(4, student.getSex() ? 1 : 0);
			preparedStatement.executeUpdate();
			result = preparedStatement.getGeneratedKeys();
			if (result.next()) {
				createdId = result.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (result != null) {
				try {
					result.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println(createdId);
		return createdId != -1 ? true : false;
	}

	//simple test code
	public static void main(String args []) {
		Student student = new Student();
		student.setName("Huang");
		student.setGrade(1);
		student.setClassNumber(1);
		student.setSex(true);
		new StudentOperation().insertStudent(student);
	}

}
