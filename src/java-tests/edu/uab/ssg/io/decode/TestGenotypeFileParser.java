package edu.uab.ssg.io.decode;

import junit.framework.TestCase;
import junit.framework.Assert;
import java.io.*;

/**
 * @author Jelai Wang
 */

public final class TestGenotypeFileParser extends TestCase {
	public void testExampleFile() throws IOException {
		InputStream in = getClass().getClassLoader().getResourceAsStream("edu/uab/ssg/io/decode/genotypes.txt");
		GenotypeFileParser parser = new GenotypeFileParser();
		TestHelper helper = new TestHelper();
		parser.parse(in, helper);
		Assert.assertEquals(10, helper.getNumberOfRecords());
	}

	private static final class TestHelper implements GenotypeFileParser.RecordListener {
		private int numOfRecords = 0;

		public void handleParsedRecord(GenotypeFileParser.GenotypeRecord record) {
			// Spot check specific records.
			if (numOfRecords == 0) { // First record.
				Assert.assertEquals("AICDA-007875", record.getSNPName());
				Assert.assertEquals("AVA1001", record.getSampleID());
				Assert.assertEquals("G", record.getAllele1Top());
				Assert.assertEquals("G", record.getAllele2Top());
				Assert.assertEquals("C", record.getAllele1Forward());
				Assert.assertEquals("C", record.getAllele2Forward());
				Assert.assertTrue(Double.compare(0.4495, record.getGCScore()) == 0);
			}
			else if (numOfRecords == 9) { // Last record.
				Assert.assertEquals("CASP12-001606", record.getSNPName());
				Assert.assertEquals("G", record.getAllele1Top());
				Assert.assertEquals("G", record.getAllele2Top());
				Assert.assertEquals("G", record.getAllele1Forward());
				Assert.assertEquals("G", record.getAllele2Forward());
				Assert.assertTrue(Double.compare(0.7598, record.getGCScore()) == 0);
			}
			// Increment total number of handled records.
			numOfRecords++;
		}

		public void handleBadRecordFormat(String line) {
			Assert.fail();
		}

		private int getNumberOfRecords() { return numOfRecords; }
	}
}
