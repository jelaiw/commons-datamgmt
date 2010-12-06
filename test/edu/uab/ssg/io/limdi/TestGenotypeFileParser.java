package edu.uab.ssg.io.illumina;

import junit.framework.TestCase;
import junit.framework.Assert;
import java.io.*;

/**
 * @author Jelai Wang
 */

public final class TestGenotypeFileParser extends TestCase {
	public void testExampleFile() throws IOException {
		// This input file contains representative records from the Illumina genotype report for an ImmunoChip experiment from Molly Bray's lab.
		InputStream in = getClass().getClassLoader().getResourceAsStream("edu/uab/ssg/io/illumina/test100.txt");
		GenotypeFileParser parser = new GenotypeFileParser();
		TestHelper helper = new TestHelper();
		parser.parse(in, helper);
		Assert.assertEquals(90, helper.getNumberOfParsedRecords());
		Assert.assertEquals(0, helper.getNumberOfBadRecords());
	}

	private static final class TestHelper implements GenotypeFileParser.RecordListener {
		private int numOfParsedRecords = 0;
		private int numOfBadRecords = 0;

		public void handleParsedRecord(GenotypeFileParser.GenotypeRecord record) {
			// Spot check specific records.
			if (numOfParsedRecords == 0) { // First record in file.
				Assert.assertEquals("1-159076491-G-DELETION", record.getValue("SNP Name"));
				Assert.assertEquals("AVA1001a", record.getValue("Sample ID"));
				Assert.assertEquals("I", record.getValue("Allele1 - Top"));
				Assert.assertEquals("I", record.getValue("Allele2 - Top"));
				Assert.assertEquals("B", record.getValue("Allele1 - AB"));
				Assert.assertEquals("B", record.getValue("Allele2 - AB"));
				Assert.assertEquals("1", record.getValue("Chr"));
				Assert.assertEquals("159076491", record.getValue("Position"));
			}
			else if (numOfParsedRecords == 90) { // Last record in file.
				Assert.assertEquals("1_211076006", record.getValue("SNP Name"));
				Assert.assertEquals("AVA1001a", record.getValue("Sample ID"));
				Assert.assertEquals("G", record.getValue("Allele1 - Top"));
				Assert.assertEquals("G", record.getValue("Allele2 - Top"));
				Assert.assertEquals("B", record.getValue("Allele1 - AB"));
				Assert.assertEquals("B", record.getValue("Allele2 - AB"));
				Assert.assertEquals("1", record.getValue("Chr"));
				Assert.assertEquals("211076006", record.getValue("Position"));
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
