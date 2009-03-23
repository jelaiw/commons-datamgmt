package edu.uab.ssg.io.limdi;

import junit.framework.TestCase;
import junit.framework.Assert;
import java.io.*;
import java.util.*;

/**
 * @author Jelai Wang
 */
public final class TestCovariateTabParser extends TestCase {
	public void testExampleFile() throws IOException {
		InputStream in = getClass().getClassLoader().getResourceAsStream("edu/uab/ssg/io/limdi/test_covariate.tab");
		CovariateTabParser parser = new CovariateTabParser();
		TestHelper helper = new TestHelper();
		parser.parse(in, helper);
		Assert.assertEquals(3, helper.getNumberOfParsedRecords());
		Assert.assertEquals(0, helper.getNumberOfBadRecords());
	}

	private static final class TestHelper implements CovariateTabParser.RecordListener {
		private int numOfParsedRecords = 0;
		private int numOfBadRecords = 0;

		public void handleParsedRecord(CovariateTabParser.SampleRecord record) {
			// Spot check specific records.
			if (numOfParsedRecords == 0) { // First record in file.
				Assert.assertEquals("A0001", record.getIDNum());
				Assert.assertEquals("48", record.getAge());
				Assert.assertEquals("M", record.getGender());
				Assert.assertEquals("B", record.getRace());
				Assert.assertEquals("70", record.getHeight());
				Assert.assertEquals("152", record.getWeight());
				Assert.assertEquals("21.80999947", record.getBMI());
				Assert.assertEquals("11", record.getCYP2C9());
				Assert.assertEquals("CT", record.getVKOR());
				Assert.assertEquals("1", record.getrfCHF());
				Assert.assertEquals("0", record.getStatin());
				Assert.assertEquals("0", record.getAmiod());
				Assert.assertEquals("5.048701308", record.getAvgMaint());
				Assert.assertEquals("5.048701308", record.getAvg1Maint());
				Assert.assertEquals("1.333333333", record.getAvgOfK());
				Assert.assertEquals("5", record.getDose_Target());
				Assert.assertNull(record.getDose_Stable());
				Assert.assertEquals("1", record.getalcohol());
				Assert.assertEquals("1", record.getsmoke());
				Assert.assertEquals("0", record.getcyp2c9v());
				Assert.assertEquals("1", record.getvkorv());
				Assert.assertEquals("anormal", record.getCKD());
				Assert.assertEquals("0", record.getCKD1());
				Assert.assertEquals("4.8", record.getaged());
				Assert.assertEquals("1.619131043", record.getlogdose());
				Assert.assertEquals("2.246931532", record.getsqdose());
			}
			else if (numOfParsedRecords == 1) { // Next record in file.
				Assert.assertEquals("A0171", record.getIDNum());
				Assert.assertEquals("79", record.getAge());
				Assert.assertEquals("F", record.getGender());
				Assert.assertEquals("B", record.getRace());
				Assert.assertEquals("69", record.getHeight());
				Assert.assertEquals("130", record.getWeight());
				Assert.assertEquals("19.20000076", record.getBMI());
				Assert.assertEquals("11", record.getCYP2C9());
				Assert.assertEquals("CC", record.getVKOR());
				Assert.assertEquals("0", record.getrfCHF());
				Assert.assertEquals("0", record.getStatin());
				Assert.assertEquals("0", record.getAmiod());
				Assert.assertEquals("6.090656632", record.getAvgMaint());
				Assert.assertEquals("6.090656632", record.getAvg1Maint());
				Assert.assertEquals("1.87804878", record.getAvgOfK());
				Assert.assertEquals("5.714285851", record.getDose_Target());
				Assert.assertEquals("5.714285851", record.getDose_Stable());
				Assert.assertEquals("0", record.getalcohol());
				Assert.assertEquals("0", record.getsmoke());
				Assert.assertEquals("0", record.getcyp2c9v());
				Assert.assertEquals("0", record.getvkorv());
				Assert.assertEquals("anormal", record.getCKD());
				Assert.assertEquals("0", record.getCKD1());
				Assert.assertEquals("7.9", record.getaged());
				Assert.assertEquals("1.806755897", record.getlogdose());
				Assert.assertEquals("2.467925573", record.getsqdose());
			}
			else if (numOfParsedRecords == 2) { // Last record in file.
				Assert.assertEquals("B0036", record.getIDNum());
				Assert.assertEquals("78", record.getAge());
				Assert.assertEquals("F", record.getGender());
				Assert.assertEquals("B", record.getRace());
				Assert.assertEquals("0", record.getHeight());
				Assert.assertEquals("0", record.getWeight());
				Assert.assertEquals("0", record.getBMI());
				Assert.assertNull(record.getCYP2C9());
				Assert.assertNull(record.getVKOR());
				Assert.assertEquals("0", record.getrfCHF());
				Assert.assertEquals("0", record.getStatin());
				Assert.assertEquals("0", record.getAmiod());
				Assert.assertNull(record.getAvgMaint());
				Assert.assertNull(record.getAvg1Maint());
				Assert.assertEquals("0", record.getAvgOfK());
				Assert.assertNull(record.getDose_Target());
				Assert.assertNull(record.getDose_Stable());
				Assert.assertEquals("0", record.getalcohol());
				Assert.assertEquals("0", record.getsmoke());
				Assert.assertNull(record.getcyp2c9v());
				Assert.assertNull(record.getvkorv());
				Assert.assertEquals("moderat", record.getCKD());
				Assert.assertEquals("1", record.getCKD1());
				Assert.assertEquals("7.8", record.getaged());
				Assert.assertNull(record.getlogdose());
				Assert.assertNull(record.getsqdose());
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
