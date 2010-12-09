package edu.uab.ssg.io.fastphase;

import junit.framework.TestCase;
import junit.framework.Assert;
import java.util.*;
import java.io.*;
import edu.uab.ssg.model.snp.*;

/**
 * @author Jelai Wang
 */

public final class TestOUTWriter extends TestCase {
	public void testSmallPopulation() throws IOException {
		SNP snp1 = new DefaultSNP("snp1", "chr5", 1000);
		SNP snp2 = new DefaultSNP("snp2", "chr3", 2000);
		List<SNP> snps = new ArrayList<SNP>();
		snps.add(snp1);
		snps.add(snp2);

		SampleBuilder b1 = new SampleBuilder("sample1");
		b1.setGenotype(snp1, "0", "0", IlluminaStrand.TOP);
		b1.setGenotype(snp2, "0", "1", IlluminaStrand.TOP);
		SampleBuilder b2 = new SampleBuilder("sample2");
		b2.setGenotype(snp1, "0", "1", IlluminaStrand.TOP);
		b2.setGenotype(snp2, "1", "1", IlluminaStrand.TOP);
		SampleBuilder b3 = new SampleBuilder("sample3");
		b3.setGenotype(snp1, "0", "0", IlluminaStrand.TOP);
		b3.setGenotype(snp2, "1", "1", IlluminaStrand.TOP);
		Sample sample1 = b1.getInstance();
		Sample sample2 = b2.getInstance();
		Sample sample3 = b3.getInstance();

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		OUTWriter writer = new OUTWriter(snps, out);
		writer.write(sample1, "1");
		writer.write(sample2, "2");
		writer.write(sample3, "1");
		writer.close();
//		System.out.println(out.toString());
		Assert.assertEquals(getExpectedOutput(), out.toString());
	}

	private String getExpectedOutput() throws IOException {
		InputStream in = getClass().getClassLoader().getResourceAsStream("edu/uab/ssg/io/fastphase/expected.out");
		StringBuilder builder = new StringBuilder();
		int ch = -1;
		while ((ch = in.read()) != -1) {
			builder.append((char) ch);
		}
		return builder.toString();
	}
}
