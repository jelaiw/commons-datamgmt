package edu.uab.ssg.io.decode;

import junit.framework.TestCase;
import junit.framework.Assert;
import java.io.*;

/**
 * @author Jelai Wang
 */

public final class TestGenotypeFileParser extends TestCase {
	public void testExampleFile() throws IOException {
		// This input file contains representative records from the Illumina genotype report in the Kaslow anthrax vaccine study as well as purposefully spiked-in file format errors.
		InputStream in = getClass().getClassLoader().getResourceAsStream("edu/uab/ssg/io/decode/genotypes.txt");
		GenotypeFileParser parser = new GenotypeFileParser();
		TestHelper helper = new TestHelper();
		parser.parse(in, helper);
		Assert.assertEquals(12, helper.getNumberOfParsedRecords());
		Assert.assertEquals(3, helper.getNumberOfBadRecords());
	}

	private static final class TestHelper implements GenotypeFileParser.RecordListener {
		private int numOfParsedRecords = 0;
		private int numOfBadRecords = 0;

		public void handleParsedRecord(GenotypeFileParser.GenotypeRecord record) {
			// Spot check specific records.
			if (numOfParsedRecords == 0) { // First record.
				Assert.assertEquals("AICDA-007875", record.getSNPName());
				Assert.assertEquals("AVA1001", record.getSampleID());
				Assert.assertEquals("G", record.getAllele1Top());
				Assert.assertEquals("G", record.getAllele2Top());
				Assert.assertEquals("C", record.getAllele1Forward());
				Assert.assertEquals("C", record.getAllele2Forward());
				Assert.assertTrue(Double.compare(0.4495, record.getGCScore()) == 0);
			}
			else if (numOfParsedRecords == 9) { // Record with missing data.
				Assert.assertEquals("rs10814262", record.getSNPName());
				Assert.assertEquals("AVA1001", record.getSampleID());
				Assert.assertEquals(null, record.getAllele1Top());
				Assert.assertEquals(null, record.getAllele2Top());
				Assert.assertEquals(null, record.getAllele1Forward());
				Assert.assertEquals(null, record.getAllele2Forward());
				Assert.assertTrue(Double.compare(0.0000, record.getGCScore()) == 0);
			}
			else if (numOfParsedRecords == 10) { // Next to last record.
				Assert.assertEquals("CASP12-001606", record.getSNPName());
				Assert.assertEquals("AVA1001", record.getSampleID());
				Assert.assertEquals("G", record.getAllele1Top());
				Assert.assertEquals("G", record.getAllele2Top());
				Assert.assertEquals("G", record.getAllele1Forward());
				Assert.assertEquals("G", record.getAllele2Forward());
				Assert.assertTrue(Double.compare(0.7598, record.getGCScore()) == 0);
			}
			else if (numOfParsedRecords == 11) { // Last record.
				Assert.assertEquals("TNFSF7-006847", record.getSNPName());
				Assert.assertEquals("deCODE control", record.getSampleID());
				Assert.assertEquals("A", record.getAllele1Top());
				Assert.assertEquals("A", record.getAllele2Top());
				Assert.assertEquals("T", record.getAllele1Forward());
				Assert.assertEquals("T", record.getAllele2Forward());
				Assert.assertTrue(Double.compare(0.3556, record.getGCScore()) == 0);
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
