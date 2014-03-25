package test.demo;

public class Staff {
	public static final int LINE = 6;
	public static final String SEX_BOY = "1";
	public static final String SEX_GIRL = "0";
	private int mId;
	private String mName;
	// the six of the student , true is boy, and false is girl.
	private boolean mSex;
	private String mDepartment;

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

	public String getDepartment() {
		return mDepartment;
	}

	public void setDepartment(String department) {
		this.mDepartment = department;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Staff)) {
			return false;
		} else {
			Staff student = (Staff) obj;
			return mName.equals(student.getName()) && mSex == student.getSex()
					&& mDepartment.equals(student.getDepartment());
		}
	}
}
