package edu.uab.ssg.io.ucsc;

import junit.framework.TestCase;
import junit.framework.Assert;
import java.io.*;

/**
 * @author Jelai Wang
 */

public final class TestSNPAnnotationParser extends TestCase {
	public void testExampleFile() throws IOException {
		InputStream in = getClass().getClassLoader().getResourceAsStream("edu/uab/ssg/io/ucsc/test.txt");
		SNPAnnotationParser parser = new SNPAnnotationParser();
		TestHelper helper = new TestHelper();
		parser.parse(in, helper);
		Assert.assertEquals(10, helper.getNumberOfParsedRecords());
		Assert.assertEquals(0, helper.getNumberOfBadRecords());
	}

	private static final class TestHelper implements SNPAnnotationParser.RecordListener {
		private int numOfParsedRecords = 0;
		private int numOfBadRecords = 0;

		public void handleParsedRecord(SNPAnnotationParser.SNPRecord record) {
			// Spot check specific records.
			if (numOfParsedRecords == 0) { // First record.
				Assert.assertEquals("chr1", record.getChrom());
				Assert.assertEquals(10433, record.getChromStart());
				Assert.assertEquals(10433, record.getChromEnd());
				Assert.assertEquals("rs56289060", record.getName());
				Assert.assertEquals("+", record.getStrand());
				Assert.assertEquals("-/C", record.getObserved());
				Assert.assertEquals("genomic", record.getMolType());
				Assert.assertEquals("insertion", record.getSNPClass());
				Assert.assertEquals("unknown", record.getValid());
				Assert.assertEquals("near-gene-5", record.getFunc());
				Assert.assertEquals("between", record.getLocType());
			}
			else if (numOfParsedRecords == 9) { // Last record.
				Assert.assertEquals("chrY", record.getChrom());
				Assert.assertEquals(59362673, record.getChromStart());
				Assert.assertEquals(59362674, record.getChromEnd());
				Assert.assertEquals("rs56053134", record.getName());
				Assert.assertEquals("+", record.getStrand());
				Assert.assertEquals("A/G", record.getObserved());
				Assert.assertEquals("genomic", record.getMolType());
				Assert.assertEquals("single", record.getSNPClass());
				Assert.assertEquals("unknown", record.getValid());
				Assert.assertEquals("unknown", record.getFunc());
				Assert.assertEquals("exact", record.getLocType());
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
