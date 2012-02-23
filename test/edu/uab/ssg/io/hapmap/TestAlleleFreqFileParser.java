package edu.uab.ssg.io.hapmap;

import junit.framework.TestCase;
import junit.framework.Assert;
import java.io.*;
import java.util.zip.GZIPInputStream;

/**
 * @author Jelai Wang
 */

public final class TestAlleleFreqFileParser extends TestCase {
	public void testExampleFile() throws IOException {
		InputStream in = getClass().getClassLoader().getResourceAsStream("edu/uab/ssg/io/hapmap/allele_freqs_chr19_CEU_r24_nr.b36_fwd.txt.gz");
		AlleleFreqFileParser parser = new AlleleFreqFileParser();
		TestHelper helper = new TestHelper();
		parser.parse(new GZIPInputStream(in), helper);
		Assert.assertEquals(56606, helper.getNumberOfParsedRecords());
		Assert.assertEquals(0, helper.getNumberOfBadRecords());
	}

	private static final class TestHelper implements AlleleFreqFileParser.RecordListener {
		private int numOfParsedRecords = 0;
		private int numOfBadRecords = 0;

		public void handleParsedRecord(AlleleFreqFileParser.AlleleFreqRecord record) {
			String rsnum = record.getRsnum();
			if ("rs2261761".equals(rsnum)) {
				Assert.assertEquals("chr19", record.getChrom());
				Assert.assertEquals(40310, record.getPos());
				Assert.assertEquals("+", record.getStrand());
				Assert.assertEquals("ncbi_b36", record.getBuild());
				Assert.assertEquals("perlegen", record.getCenter());
				Assert.assertEquals("urn:lsid:perlegen.hapmap.org:Protocol:Genotyping_1.0.0:2", record.getProtLSID());
				Assert.assertEquals("urn:lsid:perlegen.hapmap.org:Assay:25761.7226392:1", record.getAssayLSID());
				Assert.assertEquals("urn:lsid:dcc.hapmap.org:Panel:CEPH-30-trios:1", record.getPanelLSID());
				Assert.assertEquals("QC+", record.getQC_code());
				Assert.assertEquals("G", record.getRefAllele());
				Assert.assertEquals(1.0, record.getRefAlleleFreq());
				Assert.assertEquals(118, record.getRefAlleleCount());
				Assert.assertEquals("A", record.getOtherAllele());
				Assert.assertEquals(0., record.getOtherAlleleFreq());
				Assert.assertEquals(0, record.getOtherAlleleCount());
				Assert.assertEquals(118, record.getTotalCount());
			}
			else if ("rs7247199".equals(rsnum)) {
				Assert.assertEquals("chr19", record.getChrom());
				Assert.assertEquals(204938, record.getPos());
				Assert.assertEquals("+", record.getStrand());
				Assert.assertEquals("ncbi_b36", record.getBuild());
				Assert.assertEquals("imsut-riken", record.getCenter());
				Assert.assertEquals("urn:lsid:imsut-riken.hapmap.org:Protocol:genotyping:1", record.getProtLSID());
				Assert.assertEquals("urn:lsid:imsut-riken.hapmap.org:Assay:7247199:2", record.getAssayLSID());
				Assert.assertEquals("urn:lsid:dcc.hapmap.org:Panel:CEPH-30-trios:1", record.getPanelLSID());
				Assert.assertEquals("QC+", record.getQC_code());
				Assert.assertEquals("A", record.getRefAllele());
				Assert.assertEquals(0.4, record.getRefAlleleFreq());
				Assert.assertEquals(48, record.getRefAlleleCount());
				Assert.assertEquals("G", record.getOtherAllele());
				Assert.assertEquals(0.6, record.getOtherAlleleFreq());
				Assert.assertEquals(72, record.getOtherAlleleCount());
				Assert.assertEquals(120, record.getTotalCount());
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
