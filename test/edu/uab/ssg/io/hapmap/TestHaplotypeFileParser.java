package edu.uab.ssg.io.hapmap;

import junit.framework.TestCase;
import junit.framework.Assert;
import java.io.*;

/**
 * @author Jelai Wang
 */

public final class TestHaplotypeFileParser extends TestCase {
	public void testExampleFile() throws IOException {
		InputStream in = getClass().getClassLoader().getResourceAsStream("edu/uab/ssg/io/hapmap/hapmap3_r2_b36_fwd.consensus.qc.poly.chr1_asw.unr.phased");
		HaplotypeFileParser parser = new HaplotypeFileParser();
		TestHelper helper = new TestHelper();
		parser.parse(in, helper);
		Assert.assertEquals(5, helper.getNumberOfParsedRecords());
		Assert.assertEquals(0, helper.getNumberOfBadRecords());
	}

	private static final class TestHelper implements HaplotypeFileParser.RecordListener {
		private int numOfParsedRecords = 0;
		private int numOfBadRecords = 0;

		public void handleParsedRecord(HaplotypeFileParser.SNPRecord record) {
			// Spot check specific records.
			if (numOfParsedRecords == 0) { // First record.
				Assert.assertEquals("rs10458597", record.getName());
				Assert.assertEquals(554484, record.getPosition());
				Assert.assertEquals(13, record.getSampleNames().size());
				Assert.assertEquals("C", record.getAlleleA("NA19904"));
				Assert.assertEquals("C", record.getAlleleB("NA20341"));
			}
			else if (numOfParsedRecords == 1) {
				Assert.assertEquals("rs11240767", record.getName());
				Assert.assertEquals(718814, record.getPosition());
				Assert.assertEquals(13, record.getSampleNames().size());
				Assert.assertEquals("T", record.getAlleleB("NA20281"));
				Assert.assertEquals("C", record.getAlleleB("NA20341"));
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
