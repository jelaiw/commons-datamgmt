package edu.uab.ssg.io.haploview;

import junit.framework.TestCase;
import junit.framework.Assert;
import java.util.*;
import java.io.*;
import edu.uab.ssg.model.snp.*;

/**
 * @author Jelai Wang
 */

public final class TestMarkerInfoWriter extends TestCase {
	public void testSmallExample() throws IOException {
		List<SNP> snps = new ArrayList<SNP>();
		snps.add(new DefaultSNP("snp1", "chr5", 1000));
		snps.add(new DefaultSNP("snp2", "chr3", 2000));
		snps.add(new DefaultSNP("snp3", "chrX", 5000));

		MarkerInfoWriter writer = new MarkerInfoWriter();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		writer.write(snps, out);
		Assert.assertEquals(getExpectedOutput(), out.toString());
	}

	private String getExpectedOutput() throws IOException {
		InputStream in = getClass().getClassLoader().getResourceAsStream("edu/uab/ssg/io/haploview/expected.info");
		StringBuilder builder = new StringBuilder();
		int ch = -1;
		while ((ch = in.read()) != -1) {
			builder.append((char) ch);
		}
		return builder.toString();
	}
}
