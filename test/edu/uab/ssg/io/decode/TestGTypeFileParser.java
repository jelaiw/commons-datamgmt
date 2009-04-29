package edu.uab.ssg.io.decode;

import junit.framework.TestCase;
import junit.framework.Assert;
import java.io.*;

/**
 * @author Jelai Wang
 */

public final class TestGTypeFileParser extends TestCase {
	public void testExampleFile() throws IOException {
		InputStream in = getClass().getClassLoader().getResourceAsStream("edu/uab/ssg/io/decode/gtype.txt");
		GTypeFileParser parser = new GTypeFileParser();
		TestHelper helper = new TestHelper();
		parser.parse(in, helper);
		Assert.assertEquals(7, helper.getNumberOfParsedRecords());
		Assert.assertEquals(0, helper.getNumberOfBadRecords());
	}

	private static final class TestHelper implements GTypeFileParser.RecordListener {
		private int numOfParsedRecords = 0;
		private int numOfBadRecords = 0;

		public void handleParsedRecord(GTypeFileParser.SNPRecord record) {
			// Spot check specific records.
			if (numOfParsedRecords == 0) { // First record.
				Assert.assertEquals("AICDA-007875", record.getName());
				Assert.assertEquals("12", record.getChr());
				Assert.assertEquals(8650011, record.getPosition());
				Assert.assertEquals("BB", record.getGenotype("AVA1001.GType"));
				Assert.assertEquals("BB", record.getGenotype("AVA1002.GType"));
				Assert.assertEquals("BB", record.getGenotype("AVA1007.GType"));
				Assert.assertEquals("BB", record.getGenotype("AVA1011.GType"));
				Assert.assertEquals("BB", record.getGenotype("AVA1017_1.GType"));
				Assert.assertEquals("BB", record.getGenotype("AVA1017.GType"));
				Assert.assertEquals("BB", record.getGenotype("AVA1018.GType"));
			}
			else if (numOfParsedRecords == 2) {
				Assert.assertEquals("BLR1-011551", record.getName());
				Assert.assertEquals("11", record.getChr());
				Assert.assertEquals(118269327, record.getPosition());
				Assert.assertEquals("BB", record.getGenotype("AVA1001.GType"));
				Assert.assertEquals("BB", record.getGenotype("AVA1002.GType"));
				Assert.assertEquals("BB", record.getGenotype("AVA1007.GType"));
				Assert.assertEquals("BB", record.getGenotype("AVA1011.GType"));
				Assert.assertEquals("AB", record.getGenotype("AVA1017_1.GType"));
				Assert.assertEquals("AB", record.getGenotype("AVA1017.GType"));
				Assert.assertEquals("BB", record.getGenotype("AVA1018.GType"));
			}
			else if (numOfParsedRecords == 4) {
				Assert.assertEquals("CCL24-004508", record.getName());
				Assert.assertEquals("7", record.getChr());
				Assert.assertEquals(75278461, record.getPosition());
				Assert.assertEquals("NC", record.getGenotype("AVA1001.GType"));
				Assert.assertEquals("NC", record.getGenotype("AVA1002.GType"));
				Assert.assertEquals("NC", record.getGenotype("AVA1007.GType"));
				Assert.assertEquals("NC", record.getGenotype("AVA1011.GType"));
				Assert.assertEquals("NC", record.getGenotype("AVA1017_1.GType"));
				Assert.assertEquals("NC", record.getGenotype("AVA1017.GType"));
				Assert.assertEquals("NC", record.getGenotype("AVA1018.GType"));
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
