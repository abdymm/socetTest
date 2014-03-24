package test.demo;

import java.sql.SQLException;
import java.util.List;

import test.demo.connect.FileResloveErrorException;
import test.demo.connect.Server;

public class PunchRecordsReslover extends FileReslover<Record>{

	public PunchRecordsReslover(String fileName, Server server) {
		super(fileName, server);
	}

	@Override
	protected void checkLine(String line, List<Record> listValue)
			throws FileResloveErrorException, ClassNotFoundException,
			SQLException {
		
	}

}
