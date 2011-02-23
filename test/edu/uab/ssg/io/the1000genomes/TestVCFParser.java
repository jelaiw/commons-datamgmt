package edu.uab.ssg.io.the1000genomes;

import junit.framework.TestCase;
import junit.framework.Assert;
import java.io.*;

/**
 * @author Jelai Wang
 */

public final class TestVCFParser extends TestCase {
	// The test file is a cut down version of the VCF downloaded from ftp://ftp.1000genomes.ebi.ac.uk/vol1/ftp/release/20100804/ALL.2of4intersection.20100804.genotypes.vcf.gz with tabix for just chromosome 10.
	public void testRealFile() throws IOException {
		InputStream in = getClass().getClassLoader().getResourceAsStream("edu/uab/ssg/io/the1000genomes/test.vcf");
		VCFParser parser = new VCFParser();
		TestHelper helper = new TestHelper() {
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
				super.handleParsedRecord(record); // Should increment counter.
			}
		};
		parser.parse(in, helper);
		Assert.assertEquals(11, helper.getNumberOfParsedRecords());
		Assert.assertEquals(0, helper.getNumberOfBadRecords());
	}

	// The example is cut and pasted (with some squeezing of spaces in tr and replacement of spaces with tabs in vim) from the VCF specification at http://www.1000genomes.org/wiki/Analysis/Variant%20Call%20Format/vcf-variant-call-format-version-40. 
	public void testExampleFile() throws IOException {
		InputStream in = getClass().getClassLoader().getResourceAsStream("edu/uab/ssg/io/the1000genomes/example.vcf");
		VCFParser parser = new VCFParser();
		TestHelper helper = new TestHelper() {
			public void handleParsedRecord(VCFParser.VariantRecord record) {
				// Spot check specific records.
				if (numOfParsedRecords == 0) { // In dbSNP.
					Assert.assertEquals("20", record.getChromosome());
					Assert.assertEquals(14370, record.getPosition());
					Assert.assertEquals("rs6054257", record.getID());
					Assert.assertEquals("G", record.getReferenceAllele());
					Assert.assertEquals("A", record.getAlternateAllele());
				}
				else if (numOfParsedRecords == 1) { // Not in dbSNP.
					Assert.assertEquals("20", record.getChromosome());
					Assert.assertEquals(17330, record.getPosition());
					Assert.assertNull(record.getID());
					Assert.assertEquals("T", record.getReferenceAllele());
					Assert.assertEquals("A", record.getAlternateAllele());
				}
				super.handleParsedRecord(record); // Should increment counter.
			}
		};
		parser.parse(in, helper);
		Assert.assertEquals(5, helper.getNumberOfParsedRecords());
		Assert.assertEquals(0, helper.getNumberOfBadRecords());
	}

	private static class TestHelper implements VCFParser.RecordListener {
		protected int numOfParsedRecords = 0;
		protected int numOfBadRecords = 0;

		public void handleParsedRecord(VCFParser.VariantRecord record) {
			numOfParsedRecords++; 
		}

		public void handleBadRecordFormat(String line) {
			numOfBadRecords++;
		}

		private int getNumberOfParsedRecords() { return numOfParsedRecords; }
		private int getNumberOfBadRecords() { return numOfBadRecords; }
	}
}
