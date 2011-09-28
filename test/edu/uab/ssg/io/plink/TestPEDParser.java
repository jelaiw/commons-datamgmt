package edu.uab.ssg.io.plink;

import junit.framework.TestCase;
import junit.framework.Assert;
import java.util.*;
import java.io.*;
import edu.uab.ssg.model.snp.*;

/**
 * @author Jelai Wang
 */

public final class TestPEDParser extends TestCase {
	public void testNoMissingRecord() {
		PEDParser.SampleRecord record = new PEDParser.ParsedSampleRecord("sample1 1 2 3 1 2 A A C G");
		Assert.assertEquals("sample1", record.getFID());
		Assert.assertEquals("1", record.getIID());
		Assert.assertEquals("2", record.getPaternalID());
		Assert.assertEquals("3", record.getMaternalID());
		Assert.assertEquals(Sex.MALE, record.getSex());
		Assert.assertEquals("2", record.getPhenotype());
		Assert.assertEquals(2, record.getNumberOfAvailableGenotypeCalls());
		Assert.assertEquals("A", record.getAllele1(0));
		Assert.assertEquals("A", record.getAllele2(0));
		Assert.assertEquals("C", record.getAllele1(1));
		Assert.assertEquals("G", record.getAllele2(1));
	}

	public void testSomeMissingRecord() {
		PEDParser.SampleRecord record = new PEDParser.ParsedSampleRecord("sample1 1 0 4 0 22.0 0 0 T G");
		Assert.assertEquals("sample1", record.getFID());
		Assert.assertEquals("1", record.getIID());
		Assert.assertNull(record.getPaternalID());
		Assert.assertEquals("4", record.getMaternalID());
		Assert.assertNull(record.getSex());
		Assert.assertEquals("22.0", record.getPhenotype());
		Assert.assertEquals(2, record.getNumberOfAvailableGenotypeCalls());
		Assert.assertNull(record.getAllele1(0));
		Assert.assertNull(record.getAllele2(0));
		Assert.assertEquals("T", record.getAllele1(1));
		Assert.assertEquals("G", record.getAllele2(1));
	}

	// Needs more work.
	public void testTestFile() throws IOException {
		PEDParser parser = new PEDParser();
		InputStream in = getClass().getClassLoader().getResourceAsStream("edu/uab/ssg/io/plink/test.ped");
		parser.parse(in, new PEDParser.RecordListener() {
			public void handleParsedRecord(PEDParser.SampleRecord record) {
			}

			public void handleBadRecordFormat(String line) {
				System.err.println(line);
			}
		});
	}
}
