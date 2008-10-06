package edu.uab.ssg.io.plink;

import junit.framework.TestCase;
import junit.framework.Assert;
import java.util.*;
import java.io.*;
import edu.uab.ssg.model.snp.*;

/**
 * @author Jelai Wang
 */

public final class TestMAPWriter extends TestCase {
	public void testSmallPopulation() throws IOException {
		List<SNP> snps = new ArrayList<SNP>();
		snps.add(SNPFactory.createSNP("snp1", "chr5", 1000));
		snps.add(SNPFactory.createSNP("snp2", "chr3", 2000));
		snps.add(SNPFactory.createSNP("snp3", "chrX", 5000));

		MAPWriter writer = new MAPWriter();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		writer.write(snps, out);
		Assert.assertEquals(getExpectedOutput(), out.toString());
	}

	private String getExpectedOutput() throws IOException {
		InputStream in = getClass().getClassLoader().getResourceAsStream("edu/uab/ssg/io/plink/expected_MAP_file.txt");
		StringBuilder builder = new StringBuilder();
		int ch = -1;
		while ((ch = in.read()) != -1) {
			builder.append((char) ch);
		}
		return builder.toString();
	}
}
