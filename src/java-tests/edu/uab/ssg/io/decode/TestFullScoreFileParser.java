package edu.uab.ssg.io.decode;

import junit.framework.TestCase;
import junit.framework.Assert;
import java.io.*;

/**
 * @author Jelai Wang
 */

public final class TestFullScoreFileParser extends TestCase {
	public void testExampleFile() throws IOException {
		InputStream in = getClass().getClassLoader().getResourceAsStream("edu/uab/ssg/io/decode/fullscore.txt");
		FullScoreFileParser parser = new FullScoreFileParser();
		TestHelper helper = new TestHelper();
		parser.parse(in, helper);
		Assert.assertEquals(15, helper.getNumberOfParsedRecords());
		Assert.assertEquals(0, helper.getNumberOfBadRecords());
	}

	private static final class TestHelper implements FullScoreFileParser.RecordListener {
		private int numOfParsedRecords = 0;
		private int numOfBadRecords = 0;

		public void handleParsedRecord(FullScoreFileParser.SNPRecord record) {
			// Spot check specific records.
			if (numOfParsedRecords == 0) { // First record.
				Assert.assertEquals("rs714630", record.getSNPName());
				Assert.assertEquals("12", record.getChr());
				Assert.assertEquals(8657652, record.getCoordinate());
				Assert.assertTrue(record.isInclude());
				Assert.assertFalse(record.isMHC());
				Assert.assertFalse(record.isAIM());
				Assert.assertFalse(record.isCNV());
				Assert.assertFalse(record.isHarley());
				Assert.assertEquals("AICDA", record.getHugoSymbol());
				Assert.assertEquals("NA", record.getFunc());
			}
			else if (numOfParsedRecords == 10) { // MHC record.
				Assert.assertEquals("rs592625", record.getSNPName());
				Assert.assertEquals("6", record.getChr());
				Assert.assertEquals(33080668, record.getCoordinate());
				Assert.assertTrue(record.isInclude());
				Assert.assertTrue(record.isMHC());
				Assert.assertFalse(record.isAIM());
				Assert.assertFalse(record.isCNV());
				Assert.assertFalse(record.isHarley());
				Assert.assertEquals("HLA-DOA", record.getHugoSymbol());
				Assert.assertEquals("untranslated", record.getFunc());
			}
			else if (numOfParsedRecords == 11) { // AIM record.
				Assert.assertEquals("rs10313", record.getSNPName());
				Assert.assertEquals("5", record.getChr());
				Assert.assertEquals(180615011, record.getCoordinate());
				Assert.assertFalse(record.isInclude());
				Assert.assertFalse(record.isMHC());
				Assert.assertTrue(record.isAIM());
				Assert.assertFalse(record.isCNV());
				Assert.assertFalse(record.isHarley());
				Assert.assertEquals("NA", record.getHugoSymbol());
				Assert.assertEquals("NA", record.getFunc());
			}
			else if (numOfParsedRecords == 12) { // CNV record.
				Assert.assertEquals("CNV_cnv11927p2", record.getSNPName());
				Assert.assertEquals("8", record.getChr());
				Assert.assertEquals(6727787, record.getCoordinate());
				Assert.assertFalse(record.isInclude());
				Assert.assertFalse(record.isMHC());
				Assert.assertFalse(record.isAIM());
				Assert.assertTrue(record.isCNV());
				Assert.assertFalse(record.isHarley());
				Assert.assertEquals("NA", record.getHugoSymbol());
				Assert.assertEquals("NA", record.getFunc());
			}
			else if (numOfParsedRecords == 13) { // Harley record.
				Assert.assertEquals("rs2280789", record.getSNPName());
				Assert.assertEquals("17", record.getChr());
				Assert.assertEquals(31231116, record.getCoordinate());
				Assert.assertTrue(record.isInclude());
				Assert.assertFalse(record.isMHC());
				Assert.assertFalse(record.isAIM());
				Assert.assertFalse(record.isCNV());
				Assert.assertTrue(record.isHarley());
				Assert.assertEquals("CCL5", record.getHugoSymbol());
				Assert.assertEquals("NA", record.getFunc());
			}
			else if (numOfParsedRecords == 14) { // Not included, Harley record.
				Assert.assertEquals("rs10002897", record.getSNPName());
				Assert.assertEquals("4", record.getChr());
				Assert.assertEquals(74489288, record.getCoordinate());
				Assert.assertFalse(record.isInclude());
				Assert.assertFalse(record.isMHC());
				Assert.assertFalse(record.isAIM());
				Assert.assertFalse(record.isCNV());
				Assert.assertTrue(record.isHarley());
				Assert.assertEquals("NA", record.getHugoSymbol());
				Assert.assertEquals("NA", record.getFunc());
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
