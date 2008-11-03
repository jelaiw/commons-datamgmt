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
		Assert.assertEquals(3, helper.getNumberOfParsedRecords());
		Assert.assertEquals(1, helper.getNumberOfBadRecords());
	}

	private static final class TestHelper implements TabParser.RecordListener {
		private int numOfParsedRecords = 0;
		private int numOfBadRecords = 0;

		public void handleParsedRecord(TabParser.IndividualRecord record) {
			// Spot check specific records.
			if (numOfParsedRecords == 0) { // First record.
				Assert.assertEquals("560000", record.getOMRFBarCode());
				Assert.assertEquals("N/A", record.getWRAMCBarCode());
				Assert.assertEquals("None", record.getGroup());
				Assert.assertEquals("M", record.getSex());
				Assert.assertEquals("PI", record.getRace());
				Assert.assertEquals("22", record.getAge());
				Assert.assertEquals("1/31/2005", record.getDateReceived());
				Assert.assertEquals("11/03", record.getDateLastVacc());
				Assert.assertEquals("1.25", record.getYearsPostVacc());
				Assert.assertEquals("5", record.getNumberOfVacc());
				Assert.assertEquals("1000", record.getPAEndTiter());
				Assert.assertEquals("0", record.getLFEndTiter());
				Assert.assertEquals("0", record.getEFEndTiter());
				Assert.assertEquals("57", record.getViability());
				Assert.assertEquals("high", record.getNeutralizationGroup());
			}
			else if (numOfParsedRecords == 2) { // Last record.
				Assert.assertEquals("561000", record.getOMRFBarCode());
				Assert.assertEquals("B4247", record.getWRAMCBarCode());
				Assert.assertEquals("4", record.getGroup());
				Assert.assertEquals("M", record.getSex());
				Assert.assertEquals("H", record.getRace());
				Assert.assertEquals("25", record.getAge());
				Assert.assertEquals("12/7/2007", record.getDateReceived());
				Assert.assertEquals("9/19/2007", record.getDateLastVacc());
				Assert.assertEquals("0.25", record.getYearsPostVacc());
				Assert.assertEquals("3", record.getNumberOfVacc());
				Assert.assertEquals("100", record.getPAEndTiter());
				Assert.assertEquals("10", record.getLFEndTiter());
				Assert.assertEquals("10", record.getEFEndTiter());
				Assert.assertEquals("", record.getViability()); // LOOK!!
				Assert.assertEquals("neg", record.getNeutralizationGroup());
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
