
package test.demo;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class FileReslover {
    private String mFileName;
    private List<Student> mStudents;
    private int mCurrentLine = 1;

    public FileReslover(String fileName) {
        this.mFileName = fileName;
        this.mStudents = new ArrayList<Student>();
    }

    //Read the file and resolve file to Student list.
    public  List<Student> resolve() throws FileResloveErrorException {
        InputStreamReader reader = null;
        BufferedReader bufferedReader = null;
        try {
            reader = new InputStreamReader(new FileInputStream(mFileName), "UTF-8");
            bufferedReader = new BufferedReader(reader);
            while (bufferedReader.ready()) {
                String line = bufferedReader.readLine();
                System.out.println(line);
                checkLine(line);
                mCurrentLine++;
            }
            System.out.println("mStudents = " + mStudents);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return mStudents;
    }

    private void checkLine(String line) throws FileResloveErrorException {
        String [] values = line.split(" ");
        if (values.length != Student.LINE) {
            throw new FileResloveErrorException("File reslove error, at line " + mCurrentLine
                    + ",colume must equals " + Student.LINE);
        }
        Student student = new Student();
        //Id check TODO check in db ,identify column.
        int id = -1;
        try {
            id = Integer.parseInt(values[0]);
        } catch (NumberFormatException e) {
            throw new FileResloveErrorException("File reslove error, at line " + mCurrentLine
                    + ", id must be a number");
        }
        student.setId(id);

        //Name
        student.setName(values[1]);

        //Sex check
        String sex = values[2];
        if (!sex.equals(Student.SEX_BOY) && !sex.equals(Student.SEX_GIRL)) {
            throw new FileResloveErrorException("File reslove error, at line " + mCurrentLine
                    + ", sex must be " + Student.SEX_BOY + " or " + Student.SEX_GIRL);
        }
        student.setSex(sex.equals(Student.SEX_BOY) ? true : false);

        //Grade check
        int grade;
        try {
            grade = Integer.parseInt(values[3]);
        } catch (NumberFormatException e) {
            throw new FileResloveErrorException("File reslove error, at line " + mCurrentLine + ", grade must be a number");
        }
        student.setGrade(grade);

        //Class check
        int classNumber;
        try {
            classNumber = Integer.parseInt(values[4]);
        } catch (NumberFormatException e) {
            throw new FileResloveErrorException("File reslove error, at line " + mCurrentLine + ", classNumber must be a number");
        }
        student.setClassNumber(classNumber);

        //Major check TODO check in db.
        String [] majors = values[5].split(Student.MAJOR_SPLIT);
        if (majors.length < Student.MAJOIR_COUNT) {
            throw new FileResloveErrorException("File reslove error, at line " + mCurrentLine
                    + ", a student must have at lest " + Student.MAJOIR_COUNT + " major.");
        }
        for(String major : majors) {
            student.addMajor(major);
        }

        mStudents.add(student);
    }

    //test code
    public static void main(String args []) {
        try {
            new FileReslover("D://test.txt").resolve();
        } catch (FileResloveErrorException e) {
            e.printStackTrace();
            System.out.println("error:" + e.getMessage());
        }
    }
}

class FileResloveErrorException extends Exception {
    private static final long serialVersionUID = 1L;

    public FileResloveErrorException(String msg) {
        super(msg);
    }
}