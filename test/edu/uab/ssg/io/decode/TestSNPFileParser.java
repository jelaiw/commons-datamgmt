package edu.uab.ssg.io.decode;

import junit.framework.TestCase;
import junit.framework.Assert;
import java.io.*;

/**
 * @author Jelai Wang
 */

public final class TestSNPFileParser extends TestCase {
	public void testExampleFile() throws IOException {
		InputStream in = getClass().getClassLoader().getResourceAsStream("edu/uab/ssg/io/decode/JHY_SNP.txt");
		SNPFileParser parser = new SNPFileParser();
		TestHelper helper = new TestHelper();
		parser.parse(in, helper);
		Assert.assertEquals(9, helper.getNumberOfParsedRecords());
		Assert.assertEquals(0, helper.getNumberOfBadRecords());
	}

	private static final class TestHelper implements SNPFileParser.RecordListener {
		private int numOfParsedRecords = 0;
		private int numOfBadRecords = 0;

		public void handleParsedRecord(SNPFileParser.SNPRecord record) {
			// Spot check specific records.
			if (numOfParsedRecords == 0) { // First record in the file.
				Assert.assertEquals("AICDA-007875", record.getName());
				Assert.assertEquals("12", record.getChr());
				Assert.assertEquals(8650011, record.getPosition());
				Assert.assertEquals("BOT", record.getILMNStrand());
				Assert.assertEquals("TOP", record.getCustomerStrand());
				Assert.assertEquals("[T/C]", record.getSNP());
			}
			else if (numOfParsedRecords == 2) {
				Assert.assertEquals("CD19-002620", record.getName());
				Assert.assertEquals("16", record.getChr());
				Assert.assertEquals(28851704, record.getPosition());
				Assert.assertEquals("BOT", record.getILMNStrand());
				Assert.assertEquals("TOP", record.getCustomerStrand());
				Assert.assertEquals("[G/C]", record.getSNP());
			}
			else if (numOfParsedRecords == 5) {
				Assert.assertEquals("rs10030951", record.getName());
				Assert.assertEquals("4", record.getChr());
				Assert.assertEquals(118777850, record.getPosition());
				Assert.assertEquals("BOT", record.getILMNStrand());
				Assert.assertEquals("BOT", record.getCustomerStrand());
				Assert.assertEquals("[T/G]", record.getSNP());
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
