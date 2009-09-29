package edu.uab.ssg.io.fastphase;

import junit.framework.TestCase;
import junit.framework.Assert;
import java.io.*;
import java.util.*;

/**
 * @author Jelai Wang
 */
public final class TestOUTParser extends TestCase {
	public void testExampleFile() throws IOException {
		InputStream in = getClass().getClassLoader().getResourceAsStream("edu/uab/ssg/io/fastphase/test.out");
		OUTParser parser = new OUTParser();
		TestHelper helper = new TestHelper();
		parser.parse(in, helper);
		Assert.assertEquals(26, helper.getNumberOfParsedRecords());
		Assert.assertEquals(0, helper.getNumberOfBadRecords());
	}

	private static final class TestHelper implements OUTParser.RecordListener {
		private int numOfParsedRecords = 0;
		private int numOfBadRecords = 0;

		public void handleParsedRecord(OUTParser.SampleRecord record) {
			// Spot check specific records.
			if (numOfParsedRecords == 0) { // First record in file.
				Assert.assertEquals("HGDP00001", record.getSampleID());
				Assert.assertEquals("1", record.getSubpopLabel());
				Assert.assertEquals(19632, record.getNumberOfSNPs());
				Assert.assertEquals("0", record.getAllele1At(0));
				Assert.assertEquals("0", record.getAllele2At(0));
				Assert.assertEquals("1", record.getAllele1At(19631));
				Assert.assertEquals("1", record.getAllele2At(19631));
			}
			else if (numOfParsedRecords == 1) { // Next record in file.
				Assert.assertEquals("HGDP00003", record.getSampleID());
				Assert.assertEquals("2", record.getSubpopLabel());
				Assert.assertEquals(19632, record.getNumberOfSNPs());
				Assert.assertEquals("1", record.getAllele1At(0));
				Assert.assertEquals("0", record.getAllele2At(0));
				Assert.assertEquals("1", record.getAllele1At(19631));
				Assert.assertEquals("1", record.getAllele2At(19631));
			}
			else if (numOfParsedRecords == 25) { // Last record in file.
				Assert.assertEquals("HGDP00052", record.getSampleID());
				Assert.assertEquals("foo", record.getSubpopLabel());
				Assert.assertEquals(19632, record.getNumberOfSNPs());
				Assert.assertEquals("0", record.getAllele1At(0));
				Assert.assertEquals("1", record.getAllele2At(0));
				Assert.assertEquals("1", record.getAllele1At(19631));
				Assert.assertEquals("0", record.getAllele2At(19631));
			}
			// Increment total number of handled records.
			numOfParsedRecords++;
		}

		public void handleBadRecordFormat(String sampleLine, String haplotype1Line, String haplotype2Line) {
			numOfBadRecords++;
		}

		private int getNumberOfParsedRecords() { return numOfParsedRecords; }
		private int getNumberOfBadRecords() { return numOfBadRecords; }
	}
}
