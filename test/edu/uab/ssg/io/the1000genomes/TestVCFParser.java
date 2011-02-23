package edu.uab.ssg.io.the1000genomes;

import junit.framework.TestCase;
import junit.framework.Assert;
import java.io.*;

/**
 * @author Jelai Wang
 */

public final class TestVCFParser extends TestCase {
	public void testExampleFile() throws IOException {
		InputStream in = getClass().getClassLoader().getResourceAsStream("edu/uab/ssg/io/the1000genomes/test.vcf");
		VCFParser parser = new VCFParser();
		TestHelper helper = new TestHelper();
		parser.parse(in, helper);
		Assert.assertEquals(11, helper.getNumberOfParsedRecords());
		Assert.assertEquals(0, helper.getNumberOfBadRecords());
	}

	private static final class TestHelper implements VCFParser.RecordListener {
		private int numOfParsedRecords = 0;
		private int numOfBadRecords = 0;

		public void handleParsedRecord(VCFParser.VariantRecord record) {
			// Spot check specific records.
			if (numOfParsedRecords == 0) { // First record.
				Assert.assertEquals("10", record.getChromosome());
				Assert.assertEquals(60523, record.getPosition());
				Assert.assertNull(record.getID());
				Assert.assertEquals("T", record.getReferenceAllele());
				Assert.assertEquals("G", record.getAlternateAllele());
			}
			else if (numOfParsedRecords == 1) {
				Assert.assertEquals("10", record.getChromosome());
				Assert.assertEquals(61020, record.getPosition());
				Assert.assertEquals("rs115033199", record.getID());
				Assert.assertEquals("G", record.getReferenceAllele());
				Assert.assertEquals("C", record.getAlternateAllele());
			}
			else if (numOfParsedRecords == 10) {
				Assert.assertEquals("10", record.getChromosome());
				Assert.assertEquals(135524737, record.getPosition());
				Assert.assertNull(record.getID());
				Assert.assertEquals("T", record.getReferenceAllele());
				Assert.assertEquals("G", record.getAlternateAllele());
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
