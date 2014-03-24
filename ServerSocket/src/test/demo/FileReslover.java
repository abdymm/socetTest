
package test.demo;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import test.demo.connect.FileResloveErrorException;
import test.demo.connect.Server;

public abstract class FileReslover<T> {
    private String mFileName;
    private int mCurrentLine = 1;

    private Server server;

    public FileReslover(String fileName, Server server) {
        this.mFileName = fileName;
        this.server = server;
    }

    //Read the file and resolve file to Student list.
    public  List<T> resolve() throws ClassNotFoundException, SQLException {
    	ArrayList<T> values = new ArrayList<T>();
        InputStreamReader reader = null;
        BufferedReader bufferedReader = null;
        try {
            reader = new InputStreamReader(new FileInputStream(mFileName), "UTF-8");
            bufferedReader = new BufferedReader(reader);
            while (bufferedReader.ready()) {
                String line = bufferedReader.readLine();
                try {
                    checkLine(line,values);
                } catch (FileResloveErrorException e) {
                    server.writeError(e.getMessage());
                }
                mCurrentLine++;
            }
            System.out.println("mValues = " + values);
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
        return values;
    }

    protected abstract void checkLine(String line,List<T> listValue) throws FileResloveErrorException, ClassNotFoundException, SQLException;
//    {
//        String [] values = line.split(" ");
//        if (values.length != Student.LINE) {
//            throw new FileResloveErrorException("File reslove error, at line " + mCurrentLine
//                    + ",colume must equals " + Student.LINE);
//        }
//        for(String s : values) {
//        	System.out.println("yadong ---" + s);
//        }
//        Student student = new Student();
//        //Id check TODO check in db ,identify column.
//        int id = -1;
//        try {
//            id = Integer.parseInt(values[0].replace(" ", ""));
//        } catch (NumberFormatException e) {
//            throw new FileResloveErrorException("File reslove error, at line " + mCurrentLine
//                    + ", id must be a number values[0]="+values[0]);
//        }
//        student.setId(id);
//
//        //Name
//        student.setName(values[1]);
//
//        //Sex check
//        String sex = values[2];
//        if (!sex.equals(Student.SEX_BOY) && !sex.equals(Student.SEX_GIRL)) {
//            throw new FileResloveErrorException("File reslove error, at line " + mCurrentLine
//                    + ", sex must be " + Student.SEX_BOY + " or " + Student.SEX_GIRL);
//        }
//        student.setSex(sex.equals(Student.SEX_BOY) ? true : false);
//
//        //Grade check
//        int grade;
//        try {
//            grade = Integer.parseInt(values[3]);
//        } catch (NumberFormatException e) {
//            throw new FileResloveErrorException("File reslove error, at line " + mCurrentLine + ", grade must be a number");
//        }
//        student.setGrade(grade);
//
//        //Class check
//        int classNumber;
//        try {
//            classNumber = Integer.parseInt(values[4]);
//        } catch (NumberFormatException e) {
//            throw new FileResloveErrorException("File reslove error, at line " + mCurrentLine + ", classNumber must be a number");
//        }
//        student.setClassNumber(classNumber);
//
//        //Major check 1.Must choose at lest Student.MAJOIR_COUNT major
//        //2. Major id must exist in db
//        String [] majors = values[5].split(Student.MAJOR_SPLIT);
//        if (majors.length < Student.MAJOIR_COUNT) {
//            throw new FileResloveErrorException("File reslove error, at line " + mCurrentLine
//                    + ", a student must have at lest " + Student.MAJOIR_COUNT + " major.");
//        }
//        MajorOperation majorOperation = MajorOperation.getInstance();
//        for(String major : majors) {
//            int majorId = -1;
//            try {
//                majorId = Integer.parseInt(major);
//            } catch (NumberFormatException e) {
//                throw new FileResloveErrorException("File reslove error, at line " + mCurrentLine
//                        + ", must input major id instead the major name");
//            }
//            if (!majorOperation.exist(majorId)) {
//                throw new FileResloveErrorException("File reslove error, at line " + mCurrentLine
//                        + ", major id " + majorId + " Not find");
//            }
//            student.addMajor(majorId);
//        }
//
//        mStudents.add(student);
//    }

    //test code
//    public static void main(String args []) {
//        try {
//            new FileReslover("D://test.txt").resolve();
//        } catch (FileResloveErrorException e) {
//            e.printStackTrace();
//            System.out.println("error:" + e.getMessage());
//        }
//    }
}
