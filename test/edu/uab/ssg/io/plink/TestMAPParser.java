package edu.uab.ssg.io.plink;

import junit.framework.TestCase;
import junit.framework.Assert;
import java.util.*;
import java.io.*;
import edu.uab.ssg.model.snp.*;

/**
 * @author Jelai Wang
 */

public final class TestMAPParser extends TestCase {
	public void testSmallExample() throws IOException {
		MAPParser parser = new MAPParser();
		InputStream in = getClass().getClassLoader().getResourceAsStream("edu/uab/ssg/io/plink/test.map");
		List<SNP> snps = parser.parse(in, new MAPParser.BadRecordFormatListener() {
			public void handleBadRecordFormat(String record) {
				Assert.fail();
			}
		});
		Assert.assertEquals(3, snps.size());
		// Spot check.
		SNP snp1 = snps.get(0);
		Assert.assertEquals("snp1", snp1.getName());
		Assert.assertEquals("chr5", snp1.getChromosome());
		Assert.assertEquals(1000, snp1.getPosition());
		SNP snp3 = snps.get(2);
		Assert.assertEquals("snp3", snp3.getName());
		Assert.assertEquals("chrX", snp3.getChromosome());
		Assert.assertEquals(5000, snp3.getPosition());
	}
}
