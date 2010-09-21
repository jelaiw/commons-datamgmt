package edu.uab.ssg.io.hapmap;

import junit.framework.TestCase;
import junit.framework.Assert;
import java.io.*;

/**
 * @author Jelai Wang
 */

public final class TestGenotypeFileParser extends TestCase {
	public void testExampleFile() throws IOException {
		InputStream in = getClass().getClassLoader().getResourceAsStream("edu/uab/ssg/io/hapmap/genotypes_chr16_CHB_r28_nr.b36_fwd.txt");
		GenotypeFileParser parser = new GenotypeFileParser();
		TestHelper helper = new TestHelper();
		parser.parse(in, helper);
		Assert.assertEquals(139 * 3, helper.getNumberOfParsedRecords()); // Number of samples per line times the number of lines.
		Assert.assertEquals(0, helper.getNumberOfBadRecords());
	}

	private static final class TestHelper implements GenotypeFileParser.RecordListener {
		private int numOfParsedRecords = 0;
		private int numOfBadRecords = 0;

		public void handleParsedRecord(GenotypeFileParser.GenotypeRecord record) {
			String sampleID = record.getSampleID();
			String snp = record.getSNPName();
			if ("NA18524".equals(sampleID) && "rs3743872".equals(snp)) {
				Assert.assertEquals("chr16", record.getChromosome());
				Assert.assertEquals(24045, record.getPosition());
				Assert.assertEquals("A/G", record.getAlleles());
				Assert.assertEquals("+", record.getStrand());
				Assert.assertEquals("ncbi_b36", record.getAssemblyVersion());
				Assert.assertEquals("G", record.getAllele1());
				Assert.assertEquals("G", record.getAllele2());
			}
			else if ("NA18532".equals(sampleID) && "rs3743872".equals(snp)) {
				Assert.assertEquals("chr16", record.getChromosome());
				Assert.assertEquals(24045, record.getPosition());
				Assert.assertEquals("A/G", record.getAlleles());
				Assert.assertEquals("+", record.getStrand());
				Assert.assertEquals("ncbi_b36", record.getAssemblyVersion());
				Assert.assertEquals("A", record.getAllele1());
				Assert.assertEquals("G", record.getAllele2());
			}
			else if ("NA18524".equals(sampleID) && "rs2562132".equals(snp)) {
				Assert.assertEquals("chr16", record.getChromosome());
				Assert.assertEquals(24170, record.getPosition());
				Assert.assertEquals("C/T", record.getAlleles());
				Assert.assertEquals("+", record.getStrand());
				Assert.assertEquals("ncbi_b36", record.getAssemblyVersion());
				Assert.assertEquals("C", record.getAllele1());
				Assert.assertEquals("T", record.getAllele2());
			}
			else if ("NA18525".equals(sampleID) && "rs2562132".equals(snp)) {
				Assert.assertEquals("chr16", record.getChromosome());
				Assert.assertEquals(24170, record.getPosition());
				Assert.assertEquals("C/T", record.getAlleles());
				Assert.assertEquals("+", record.getStrand());
				Assert.assertEquals("ncbi_b36", record.getAssemblyVersion());
				Assert.assertNull(record.getAllele1());
				Assert.assertNull(record.getAllele2());
			}
			else if ("NA18524".equals(sampleID) && "rs9673344".equals(snp)) {
				Assert.assertEquals("chr16", record.getChromosome());
				Assert.assertEquals(88801983, record.getPosition());
				Assert.assertEquals("C/T", record.getAlleles());
				Assert.assertEquals("+", record.getStrand());
				Assert.assertEquals("ncbi_b36", record.getAssemblyVersion());
				Assert.assertEquals("C", record.getAllele1());
				Assert.assertEquals("C", record.getAllele2());
			}
			else if ("NA18798".equals(sampleID) && "rs9673344".equals(snp)) {
				Assert.assertEquals("chr16", record.getChromosome());
				Assert.assertEquals(88801983, record.getPosition());
				Assert.assertEquals("C/T", record.getAlleles());
				Assert.assertEquals("+", record.getStrand());
				Assert.assertEquals("ncbi_b36", record.getAssemblyVersion());
				Assert.assertNull(record.getAllele1());
				Assert.assertNull(record.getAllele2());
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
