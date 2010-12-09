package edu.uab.ssg.io.phase;

import junit.framework.TestCase;
import junit.framework.Assert;
import java.util.*;
import java.io.*;
import edu.uab.ssg.model.snp.*;

/**
 * @author Jelai Wang
 */
public final class TestINPWriter extends TestCase {
	public void testSmallPopulation() throws IOException {
		SNP snp1 = new DefaultSNP("snp1", "chr3", 1000);
		SNP snp2 = new DefaultSNP("snp2", "chr3", 2000);
		List<SNP> snps = new ArrayList<SNP>();
		snps.add(snp1);
		snps.add(snp2);

		SampleBuilder b1 = new SampleBuilder("sample1");
		b1.setGenotype(snp1, "A", "A", IlluminaStrand.TOP);
		b1.setGenotype(snp2, "C", "G", IlluminaStrand.TOP);
		SampleBuilder b2 = new SampleBuilder("sample2");
		b2.setGenotype(snp1, null, "A", IlluminaStrand.TOP);
		b2.setGenotype(snp2, "G", "G", IlluminaStrand.TOP);
		SampleBuilder b3 = new SampleBuilder("sample3");
		b3.setGenotype(snp1, "T", "A", IlluminaStrand.TOP);
		List<Sample> samples = new ArrayList<Sample>();
		samples.add(b1.getInstance());
		samples.add(b2.getInstance());
		samples.add(b3.getInstance());

		INPWriter writer = new INPWriter();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		writer.write(samples, snps, out);
		Assert.assertEquals(getExpectedOutput(), out.toString());
	}

	private String getExpectedOutput() throws IOException {
		InputStream in = getClass().getClassLoader().getResourceAsStream("edu/uab/ssg/io/phase/expected.inp");
		StringBuilder builder = new StringBuilder();
		int ch = -1;
		while ((ch = in.read()) != -1) {
			builder.append((char) ch);
		}
		return builder.toString();
	}
}
