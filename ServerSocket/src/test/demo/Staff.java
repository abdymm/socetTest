
package test.demo;

import java.util.ArrayList;
import java.util.List;

public class Staff {
    public static final int LINE = 6;
    public static final String SEX_BOY = "1";
    public static final String SEX_GIRL = "0";
    public static final String MAJOR_SPLIT = ",";
    public static final int MAJOIR_COUNT = 5;
    private int mId;
    private String mName;
    private int mGrade;
    private int mClassNumber;
    // the six of the student , true is boy, and false is girl.
    private boolean mSex;
    private List<Integer> mMajors = new ArrayList<Integer>();

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
        if (name == null || name.isEmpty()) {
            Log.e("Student's name can not be null");
            this.mName = "";
        }
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

    public List<Integer> getMajors() {
        return mMajors;
    }

    public void addMajor(Integer major) {
        mMajors.add(major);
    }

    @Override
    public String toString() {
        return "A student with id=" + mId + " mName=" + mName + " mGrade=" + mGrade
                + " mClassNumber=" + mClassNumber + " mMajor=" + mMajors;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Staff)) {
            return false;
        }
        else {
            Staff student = (Staff) obj;
            return mName.equals(student.getName()) && mGrade == student.getGrade()
                    && mClassNumber == student.getClassNumber() && mSex == student.getSex();
        }
    }
}
