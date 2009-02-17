package edu.uab.ssg.io.illumina;

import junit.framework.TestCase;
import junit.framework.Assert;
import java.io.*;

/**
 * @author Jelai Wang
 */

public final class TestSNPFileParser extends TestCase {
	public void testExampleFile() throws IOException {
		// This input file contains a "mix" of representative records from the Kaslow anthrax vaccine study and the Limdi warfarin study as well as purposefully spiked-in file format errors.
		InputStream in = getClass().getClassLoader().getResourceAsStream("edu/uab/ssg/io/illumina/snps.txt");
		SNPFileParser parser = new SNPFileParser();
		TestHelper helper = new TestHelper();
		parser.parse(in, helper);
		Assert.assertEquals(21, helper.getNumberOfParsedRecords());
		Assert.assertEquals(3, helper.getNumberOfBadRecords());
	}

	private static final class TestHelper implements SNPFileParser.RecordListener {
		private int numOfParsedRecords = 0;
		private int numOfBadRecords = 0;

		public void handleParsedRecord(SNPFileParser.SNPRecord record) {
			// Spot check specific records.
			if (numOfParsedRecords == 0) { // First record in the file.
				Assert.assertEquals("AICDA-007875", record.getName());
				Assert.assertEquals("12", record.getChromosome());
				Assert.assertEquals(8650011, record.getPosition());
				Assert.assertTrue(Double.compare(0.5980, record.getGenTrainScore()) == 0);
				Assert.assertEquals("[T/C]", record.getSNP());
				Assert.assertEquals("BOT", record.getILMNStrand());
				Assert.assertEquals("TOP", record.getCustomerStrand());
				Assert.assertEquals("0", record.getNormID());
			}
			else if (numOfParsedRecords == 18) {
				Assert.assertEquals("CCL11-004869", record.getName());
				Assert.assertEquals("17", record.getChromosome());
				Assert.assertEquals(29639668, record.getPosition());
				Assert.assertTrue(Double.compare(0.7965, record.getGenTrainScore()) == 0);
				Assert.assertEquals("[A/C]", record.getSNP());
				Assert.assertEquals("TOP", record.getILMNStrand());
				Assert.assertEquals("TOP", record.getCustomerStrand());
				Assert.assertEquals("0", record.getNormID());
			}
			else if (numOfParsedRecords == 19) {
				Assert.assertEquals("200003", record.getName());
				Assert.assertEquals("9", record.getChromosome());
				Assert.assertEquals(139026180, record.getPosition());
				Assert.assertTrue(Double.compare(0.8931, record.getGenTrainScore()) == 0);
				Assert.assertEquals("[T/C]", record.getSNP());
				Assert.assertEquals("BOT", record.getILMNStrand());
				Assert.assertEquals("TOP", record.getCustomerStrand());
				Assert.assertEquals("5", record.getNormID());
			}
			else if (numOfParsedRecords == 20) { // Last record in the file.
				Assert.assertEquals("cnvi0048855", record.getName());
				Assert.assertEquals("4", record.getChromosome());
				Assert.assertEquals(97084207, record.getPosition());
				Assert.assertTrue(Double.compare(0.0000, record.getGenTrainScore()) == 0);
				Assert.assertEquals("[N/A]", record.getSNP());
				Assert.assertEquals("P", record.getILMNStrand());
				Assert.assertEquals("P", record.getCustomerStrand());
				Assert.assertEquals("2", record.getNormID());
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
