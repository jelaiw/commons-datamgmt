package edu.uab.ssg.io.omrf;

import junit.framework.TestCase;
import junit.framework.Assert;
import java.io.*;
import java.util.*;

/**
 * @author Jelai Wang
 */

public final class TestTabParser extends TestCase {
	public void testExampleFile() throws IOException {
		InputStream in = getClass().getClassLoader().getResourceAsStream("edu/uab/ssg/io/omrf/test.tab");
		TabParser parser = new TabParser();
		TestHelper helper = new TestHelper();
		parser.parse(in, helper);
		Assert.assertEquals(4, helper.getNumberOfParsedRecords());
		Assert.assertEquals(0, helper.getNumberOfBadRecords());
	}

	private static final class TestHelper implements TabParser.RecordListener {
		private int numOfParsedRecords = 0;
		private int numOfBadRecords = 0;

		public void handleParsedRecord(TabParser.SampleRecord record) {
			// Spot check specific records.
			if (numOfParsedRecords == 0) { // First record.
				Assert.assertEquals("560001", record.getOMRFBarCode());
				Assert.assertEquals("M", record.getSex());
				Assert.assertEquals("C", record.getRace());
				Assert.assertEquals("37", record.getAge());
				Assert.assertEquals("2/1/2005", record.getDateReceived());
				Assert.assertEquals("6/99", record.getDateLastVacc());
				Assert.assertEquals("6", record.getNumberOfVacc());
				Assert.assertEquals("10", record.getPAEndTiter());
				Assert.assertEquals("100", record.getLFEndTiter());
				Assert.assertEquals("0", record.getEFEndTiter());
				Assert.assertEquals("7", record.getViability());
				Assert.assertEquals("neg", record.getNeutralizationGroup());
			}
			else if (numOfParsedRecords == 3) { // Last record.
				Assert.assertEquals("561035", record.getOMRFBarCode());
				Assert.assertEquals("m", record.getSex());
				Assert.assertEquals("C", record.getRace());
				Assert.assertEquals("27", record.getAge());
				Assert.assertEquals("2/13/2008", record.getDateReceived());
				Assert.assertEquals("6/8/2007", record.getDateLastVacc());
				Assert.assertEquals("6", record.getNumberOfVacc());
				Assert.assertEquals("1000", record.getPAEndTiter());
				Assert.assertEquals("0", record.getLFEndTiter());
				Assert.assertEquals("0", record.getEFEndTiter());
				Assert.assertEquals("68", record.getViability());
				Assert.assertEquals("high", record.getNeutralizationGroup());
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
