package edu.uab.ssg.io.hapgen;

import junit.framework.TestCase;
import junit.framework.Assert;
import java.util.*;
import java.io.*;
import edu.uab.ssg.model.snp.*;

/**
 * @author Jelai Wang
 */

public final class TestLegendFile extends TestCase {
	public void testSmallExample() throws IOException {
		Legend legend = new LegendFile(getClass().getClassLoader().getResourceAsStream("edu/uab/ssg/io/hapgen/minor_allele.leg"));
		List<SNP> snps = legend.getSNPs();
		Assert.assertEquals(2, snps.size());
		SNP snp1 = snps.get(0);
		Assert.assertEquals("A", legend.getAllele0(snp1));
		Assert.assertNull(legend.getAllele1(snp1));
		SNP snp2 = snps.get(1);
		Assert.assertEquals("A", legend.getAllele0(snp2));
		Assert.assertEquals("G", legend.getAllele1(snp2));

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		legend.write(out);
		Assert.assertEquals(getExpectedOutput(), out.toString());
	}

	private String getExpectedOutput() throws IOException {
		InputStream in = getClass().getClassLoader().getResourceAsStream("edu/uab/ssg/io/hapgen/minor_allele.leg");
		StringBuilder builder = new StringBuilder();
		int ch = -1;
		while ((ch = in.read()) != -1) {
			builder.append((char) ch);
		}
		return builder.toString();
	}
}
