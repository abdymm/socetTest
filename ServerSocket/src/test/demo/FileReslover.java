package test.demo;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import test.demo.connect.FileResloveErrorException;
import test.demo.connect.Server;
import test.demo.db.StaffOperation;

public class FileReslover {
	private String mFileName;
	private int mCurrentLine = 1;
	private Server server;

	public FileReslover(String fileName, Server server) {
		this.mFileName = fileName;
		this.server = server;
	}

	// Read the file and resolve file to Student list.
	public List<Record> resolve() throws ClassNotFoundException, SQLException {
		ArrayList<Record> values = new ArrayList<Record>();
		InputStreamReader reader = null;
		BufferedReader bufferedReader = null;
		try {
			reader = new InputStreamReader(new FileInputStream(mFileName),
					"UTF-8");
			bufferedReader = new BufferedReader(reader);
			while (bufferedReader.ready()) {
				String line = bufferedReader.readLine();
				try {
					checkLine(line, values);
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

	private void checkLine(String line, List<Record> listValue)
			throws FileResloveErrorException, ClassNotFoundException,
			SQLException {
		String[] values = line.split(" ");
		if (values.length != Record.LINE) {
			throw new FileResloveErrorException("File reslove error, at line "
					+ mCurrentLine + ",colume must equals " + Record.LINE);
		}
		Record record = new Record();
		// Name check
		String name = values[0];
		StaffOperation staffOperation = StaffOperation.getInstance();
		List<Staff> staffs = staffOperation.queryFromName(name);
		if (staffs.size() < 1) {
			throw new FileResloveErrorException("File reslove error, at line "
					+ mCurrentLine + ",staff with name=" + name
					+ " doesn't exist");
		}
		record.setStaffId(staffs.get(0).getId());
		// Date check
		String date = values[1];
		SimpleDateFormat dateFormat = new SimpleDateFormat();
		// Don't allow some date string like "2014-13-20" or "25:01"
		dateFormat.setLenient(false);
		try {
			dateFormat.applyPattern("yyyy-MM-dd");
			dateFormat.parse(date);
		} catch (ParseException e) {
			throw new FileResloveErrorException("File reslove error, at line "
					+ mCurrentLine
					+ ",date format error, date format just like yyyy-MM-dd");
		}
		record.setDate(date);
		// Start time check
		String startTime = values[2];
		try {
			dateFormat.applyPattern("HH:mm");
			dateFormat.parse(startTime);
		} catch (ParseException e) {
			throw new FileResloveErrorException("File reslove error, at line "
					+ mCurrentLine
					+ ",start time format error, date format just like HH:mm");
		}
		record.setStartTime(startTime);
		// End time check
		String endTime = values[3];
		try {
			dateFormat.parse(endTime);
		} catch (ParseException e) {
			throw new FileResloveErrorException("File reslove error, at line "
					+ mCurrentLine
					+ ",end time format error, date format just like HH:mm");
		}
		record.setEndTime(endTime);
		listValue.add(record);
	}

	// test code
	public static void main(String args[]) throws ClassNotFoundException,
			SQLException {
		try {
			new FileReslover("D://test.txt", null).resolve();
		} catch (FileResloveErrorException e) {
			e.printStackTrace();
			System.out.println("error:" + e.getMessage());
		}
	}
}
