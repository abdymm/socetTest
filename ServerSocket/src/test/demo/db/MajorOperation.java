
package test.demo.db;

import test.demo.Log;
import test.demo.Major;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MajorOperation extends Operation {
    private static final String SELECT_SQL = "SELECT _id,name,major_number,teacher FROM "
            + DbConnect.MAJOR_TABLE_NAME;
    private static final String WHERE_SQL_NAME = " WHERE name=?";
    private static final String INSERT_SQL = "INSERT INTO " + DbConnect.MAJOR_TABLE_NAME
            + " (name,major_number,teacher) VALUES (?,?,?)";

    public MajorOperation() throws ClassNotFoundException, SQLException {
        super();
    }

    public boolean insertMajor(Major major) throws SQLException {
        String name = major.getClassName();
        int id = -1;
        if (getMarjor(name) != null) {
            Log.e("major with name " + major.getClassName() + " exist!!!");
            return false;
        }
        PreparedStatement preparedStatement = null;
        ResultSet result = null;
        preparedStatement = getPreparedStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
        preparedStatement.setString(1, name);
        preparedStatement.setString(2, major.getMajorNum());
        preparedStatement.setString(3, major.getTeacher());
        preparedStatement.executeUpdate();
        result = preparedStatement.getGeneratedKeys();
        if (result.next()) {
            id = result.getInt(1);
        }
        return id != -1;
    }

    public List<Major> getAllMajor() throws SQLException {
        List<Major> out = new ArrayList<Major>();
        PreparedStatement preparedStatement = null;
        ResultSet result = null;
        try {
            preparedStatement = getPreparedStatement(DbUtil.createSql(SELECT_SQL, null,
                    null, null), -1);
            result = preparedStatement.executeQuery();
            while (result.next()) {
                Major major = new Major();
                major.setId(result.getInt(1));
                major.setClassName(result.getString(2));
                major.setMajorNum(result.getString(3));
                major.setTeacher(result.getString(4));
                out.add(major);
            }
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (result != null) {
                result.close();
            }
        }
        return out;
    }

    public Major getMarjor(String majorName) throws SQLException {
        Major out = null;
        PreparedStatement preparedStatement = null;
        ResultSet result = null;
        try {
            preparedStatement = getPreparedStatement(DbUtil.createSql(SELECT_SQL, WHERE_SQL_NAME,
                    null, null), -1);
            preparedStatement.setString(1, majorName);
            result = preparedStatement.executeQuery();
            if (result.next()) {
                out = new Major();
                out.setId(result.getInt(1));
                out.setClassName(result.getString(2));
                out.setMajorNum(result.getString(3));
                out.setTeacher(result.getString(4));
            }
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (result != null) {
                result.close();
            }
        }
        return out;
    }

    // simple test
    public static void main(String args[]) {
        try {
            MajorOperation majorOperation = new MajorOperation();
            Major major = new Major();
            major.setClassName("hightmath");
            major.setMajorNum("2222");
            major.setTeacher("Yadong");
            majorOperation.insertMajor(major);
            System.out.println("====");
            System.out.println(majorOperation.getMarjor("math"));
            System.out.println("====");
            System.out.println(majorOperation.getAllMajor());

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
