package edu.uab.ssg.io.limdi;

import junit.framework.TestCase;
import junit.framework.Assert;
import java.io.*;
import java.util.*;

/**
 * @author Jelai Wang
 */
public final class TestTabParser extends TestCase {
	public void testExampleFile() throws IOException {
		InputStream in = getClass().getClassLoader().getResourceAsStream("edu/uab/ssg/io/limdi/test.tab");
		TabParser parser = new TabParser();
		TestHelper helper = new TestHelper();
		parser.parse(in, helper);
		Assert.assertEquals(7, helper.getNumberOfParsedRecords());
		Assert.assertEquals(0, helper.getNumberOfBadRecords());
	}

	private static final class TestHelper implements TabParser.RecordListener {
		private int numOfParsedRecords = 0;
		private int numOfBadRecords = 0;

		public void handleParsedRecord(TabParser.SampleRecord record) {
			// Spot check specific records.
			if (numOfParsedRecords == 0) { // First record in file.
				Assert.assertEquals("A0001", record.getIDNum());
				Assert.assertEquals("M", record.getGender());
				Assert.assertEquals("5.048701308", record.getAvgDose());
				Assert.assertNull(record.getStabledose());
			}
			else if (numOfParsedRecords == 1) {
				Assert.assertEquals("A0003", record.getIDNum());
				Assert.assertEquals("M", record.getGender());
				Assert.assertEquals("6.061213134", record.getAvgDose());
				Assert.assertEquals("5.714285851", record.getStabledose());
			}
			else if (numOfParsedRecords == 5) {
				Assert.assertEquals("B0109", record.getIDNum());
				Assert.assertEquals("M", record.getGender());
				Assert.assertEquals("4.563492094", record.getAvgDose());
				Assert.assertNull(record.getStabledose());
			}
			else if (numOfParsedRecords == 6) {
				Assert.assertEquals("A0073", record.getIDNum());
				Assert.assertEquals("F", record.getGender());
				Assert.assertNull(record.getAvgDose());
				Assert.assertNull(record.getStabledose());
			}
			// Increment total number of handled records.
			numOfParsedRecords++;
		}

		public void handleBadRecordFormat(String line) {
			numOfBadRecords++;
		}

		private int getNumberOfParsedRecords() { return numOfParsedRecords; }
		private int getNumberOfBadRecords() { return numOfBadRecords; }
	}
}
