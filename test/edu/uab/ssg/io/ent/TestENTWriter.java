package edu.uab.ssg.io.ent;

import junit.framework.TestCase;
import junit.framework.Assert;
import java.util.*;
import java.io.*;
import edu.uab.ssg.model.snp.*;

/**
 * @author Jelai Wang
 */
public final class TestENTWriter extends TestCase {
	public void testSmallPopulation() throws IOException {
		SNP snp1 = SNPFactory.createSNP("snp1", "chr3", 1000);
		SNP snp2 = SNPFactory.createSNP("snp2", "chr3", 2000);
		List<SNP> snps = new ArrayList<SNP>();
		snps.add(snp1);
		snps.add(snp2);

		SampleBuilder b1 = new SampleBuilder("sample1");
		b1.setGenotype(snp1, "A", "A", IlluminaStrand.TOP);
		b1.setGenotype(snp2, "C", "G", IlluminaStrand.TOP);
		SampleBuilder b2 = new SampleBuilder("sample2");
		b2.setGenotype(snp1, "A", "A", IlluminaStrand.TOP);
		b2.setGenotype(snp2, "G", "G", IlluminaStrand.TOP);
		SampleBuilder b3 = new SampleBuilder("sample3");
		b3.setGenotype(snp1, "T", "A", IlluminaStrand.TOP);
		SampleBuilder b4 = new SampleBuilder("sample4");
		b4.setGenotype(snp1, "T", "T", IlluminaStrand.TOP);
		b4.setGenotype(snp2, "C", "C", IlluminaStrand.TOP);
		List<Sample> samples = new ArrayList<Sample>();
		samples.add(b1.getInstance());
		samples.add(b2.getInstance());
		samples.add(b3.getInstance());
		samples.add(b4.getInstance());

		ENTWriter writer = new ENTWriter();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		writer.write(samples, snps, out);
		Assert.assertEquals(getExpectedOutput(), out.toString());
	}

	private String getExpectedOutput() throws IOException {
		InputStream in = getClass().getClassLoader().getResourceAsStream("edu/uab/ssg/io/ent/expected.ent_gen");
		StringBuilder builder = new StringBuilder();
		int ch = -1;
		while ((ch = in.read()) != -1) {
			builder.append((char) ch);
		}
		return builder.toString();
	}
}
