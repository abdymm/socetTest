
package test.demo;

import java.util.ArrayList;
import java.util.List;

public class Student {
    public static final int LINE = 6;
    public static final String SEX_BOY = "男";
    public static final String SEX_GIRL = "女";
    public static final String MAJOR_SPLIT = ",";
    public static final int MAJOIR_COUNT = 5;
    private int mId;
    private String mName;
    private int mGrade;
    private int mClassNumber;
    //the six of the student , true is boy, and false is girl.
    private boolean mSex;
    private List<String> mMajors = new ArrayList<String>();

    public int getmId() {
        return mId;
    }

    public boolean getSex() {
        return mSex;
    }

    public void setSex(boolean sex) {
        this.mSex = sex;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public int getGrade() {
        return mGrade;
    }

    public void setGrade(int grade) {
        this.mGrade = grade;
    }

    public int getClassNumber() {
        return mClassNumber;
    }

    public void setClassNumber(int classNumber) {
        this.mClassNumber = classNumber;
    }

    public List<String> getMajors() {
        return mMajors;
    }

    public void addMajor(String major) {
        mMajors.add(major);
    }

    @Override
    public String toString() {
        return "A student with id=" + mId + " mName=" + mName + " mGrade=" + mGrade
                + " mClassNumber=" + mClassNumber + " mMajor=" + mMajors;
    }
}
