
package test.demo.db;

import test.demo.Log;
import test.demo.Student;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class StudentOperation extends Operation {
    private static final String INSERT_SQL = "INSERT INTO "
            + DbConnect.STUDENT_TABLE_NAME
            + "(name, grade, className, sex) VALUES (?,?,?,?)";
    private static final String SELECT_SQL = "select _id,name,grade,className,sex from "
            + DbConnect.STUDENT_TABLE_NAME;
    private static final String SELECTION_SQL_NAME = "WHERE name=?";
    private static StudentOperation sStudentOperation = null;

    private StudentOperation() throws ClassNotFoundException, SQLException {
        super();
    }

    public static StudentOperation getInstance() throws ClassNotFoundException, SQLException {
        if (sStudentOperation == null) {
            sStudentOperation = new StudentOperation();
        }
        return sStudentOperation;
    }

    private boolean chechStudentExist(Student student) throws SQLException {
        List<Student> students = getStudent(student.getName());
        for (Student s : students) {
            if (s.equals(student)) {
                return true;
            }
        }
        return false;
    }

    public boolean insertStudent(Student student) throws SQLException, ClassNotFoundException {
        if (chechStudentExist(student)) {
            Log.e("Student " + student + " have exist!!");
            return false;
        }
        int createdId = -1;
        PreparedStatement preparedStatement = null;
        ResultSet result = null;
        try {
            preparedStatement = getPreparedStatement(
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
            //put marjor info.
            if (createdId != -1) {
                MajorOperation majorOperation = MajorOperation.getInstance();
                majorOperation.addMajor(createdId, parseIntegerArr(student.getMajors()));
            }
        } finally {
            if (result != null) {
                result.close();
                preparedStatement.close();
            }
        }
        System.out.println(createdId);
        return createdId != -1;
    }

    private int[] parseIntegerArr(List<Integer> input) {
        int [] output = new int [input.size()];
        for (int index = 0;index < output.length;index++) {
            output[index] = input.get(index);
        }
        return output;
    }

    public List<Student> getStudent(String name) throws SQLException {
        List<Student> out = new ArrayList<Student>();
        PreparedStatement preparedStatement = null;
        ResultSet result = null;
        try {
            preparedStatement = getPreparedStatement(
                    DbUtil.createSql(SELECT_SQL, SELECTION_SQL_NAME, null, null), -1);
            preparedStatement.setString(1, name);
            result = preparedStatement.executeQuery();
            while (result.next()) {
                Student student = new Student();
                student.setId(result.getInt(1));
                student.setName(result.getString(2));
                student.setGrade(result.getInt(3));
                student.setClassNumber(result.getInt(4));
                student.setSex(result.getInt(5) == 1 ? true : false);
                out.add(student);
            }
        } finally {
            if (result != null) {
                result.close();
                preparedStatement.close();
            }
        }
        System.out.println(out);
        return out;
    }

//    // simple test code
//    public static void main(String args[]) {
//        Student student = new Student();
//        student.setName("Huang");
//        student.setGrade(12);
//        student.setClassNumber(1);
//        student.setSex(true);
//        try {
//            new StudentOperation().insertStudent(student);
//        } catch (SQLException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }

}
