package edu.uab.ssg.io.illumina;

import junit.framework.TestCase;
import junit.framework.Assert;
import java.io.*;

/**
 * @author Jelai Wang
 */

public final class TestGenotypeFileParser extends TestCase {
	public void testExampleFile() throws IOException {
		// This input file contains representative records from the Illumina genotype report in the Limdi warfarin study as well as purposefully spiked-in file format errors.
		InputStream in = getClass().getClassLoader().getResourceAsStream("edu/uab/ssg/io/illumina/genotypes.txt");
		GenotypeFileParser parser = new GenotypeFileParser();
		TestHelper helper = new TestHelper();
		parser.parse(in, helper);
		Assert.assertEquals(12, helper.getNumberOfParsedRecords());
		Assert.assertEquals(2, helper.getNumberOfBadRecords());
	}

	private static final class TestHelper implements GenotypeFileParser.RecordListener {
		private int numOfParsedRecords = 0;
		private int numOfBadRecords = 0;

		public void handleParsedRecord(GenotypeFileParser.GenotypeRecord record) {
			// Spot check specific records.
			if (numOfParsedRecords == 0) { // First record in file.
				Assert.assertEquals("200003", record.getSNPName());
				Assert.assertEquals("A0001", record.getSampleID());
				Assert.assertEquals("G", record.getAllele1Forward());
				Assert.assertEquals("G", record.getAllele2Forward());
			}
			else if (numOfParsedRecords == 11) { // Last record in file.
				Assert.assertEquals("cnvi0048855", record.getSNPName());
				Assert.assertEquals("A0280", record.getSampleID());
				Assert.assertEquals(null, record.getAllele1Forward());
				Assert.assertEquals(null, record.getAllele2Forward());
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
