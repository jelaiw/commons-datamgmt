package edu.uab.ssg.io.mach;

import junit.framework.TestCase;
import junit.framework.Assert;
import java.io.*;
import java.util.*;

/**
 * @author Jelai Wang
 */

public final class TestGENOParser extends TestCase {
	public void testExampleFile() throws IOException {
		InputStream in = getClass().getClassLoader().getResourceAsStream("edu/uab/ssg/io/mach/mach1.out.geno");
		GENOParser parser = new GENOParser();
		TestHelper helper = new TestHelper();
		parser.parse(in, helper);
		Assert.assertEquals(7, helper.getNumberOfParsedRecords());
		Assert.assertEquals(3, helper.getNumberOfBadRecords());
	}

	private static final class TestHelper implements GENOParser.RecordListener {
		private int numOfParsedRecords = 0;
		private int numOfBadRecords = 0;

		public void handleParsedRecord(GENOParser.IndividualRecord record) {
			// Spot check specific records.
			if (numOfParsedRecords == 0) { // First record.
				Assert.assertEquals("FAM1", record.getFamilyID());
				Assert.assertEquals("IND1", record.getIndividualID());
				List<String> genotypes = record.getGenotypes();
				Assert.assertEquals(7, genotypes.size());
				Assert.assertEquals("A/A", genotypes.get(0));
				Assert.assertEquals("A/G", genotypes.get(6));
			}
			else if (numOfParsedRecords == 9) { // Last record.
				Assert.assertEquals("FAM10", record.getFamilyID());
				Assert.assertEquals("IND1", record.getIndividualID());
				List<String> genotypes = record.getGenotypes();
				Assert.assertEquals(7, genotypes.size());
				Assert.assertEquals("G/A", genotypes.get(1));
				Assert.assertEquals("A/G", genotypes.get(6));
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
