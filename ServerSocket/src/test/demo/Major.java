
package test.demo;

public class Major {
    private int id;
    private String teacher;
    private String className;
    private String majorNum;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMajorNum() {
        return majorNum;
    }

    public void setMajorNum(String majorNum) {
        this.majorNum = majorNum;
    }

    @Override
    public String toString() {
        return "Major [id=" + id + ", teacher=" + teacher + ", className=" + className
                + ", majorNum=" + majorNum + "]";
    }
}
