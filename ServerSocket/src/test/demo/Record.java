package test.demo;

public class Record {
	private int mId;
	private int mStaffId;
	private String mStartTime;
	private String mEndTime;
	private String mDate;

	public int getId() {
		return mId;
	}

	public void setId(int id) {
		this.mId = id;
	}

	public int getStaffId() {
		return mStaffId;
	}

	public void setStaffId(int staffId) {
		this.mStaffId = staffId;
	}

	public String getStartTime() {
		return mStartTime;
	}

	public void setStartTime(String startTime) {
		this.mStartTime = startTime;
	}

	public String getEndTime() {
		return mEndTime;
	}

	public void setEndTime(String mEndTime) {
		this.mEndTime = mEndTime;
	}

	public String getDate() {
		return mDate;
	}

	public void setDate(String date) {
		this.mDate = date;
	}

	@Override
	public String toString() {
		return "Record [mId=" + mId + ", mStaffId=" + mStaffId
				+ ", mStartTime=" + mStartTime + ", mEndTime=" + mEndTime
				+ ", mDate=" + mDate + "]";
	}
}
