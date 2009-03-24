package edu.uab.ssg.io.eigenstrat;

import junit.framework.TestCase;
import junit.framework.Assert;
import java.util.*;
import java.io.*;
import edu.uab.ssg.model.snp.*;

/**
 * @author Jelai Wang
 */

public final class TestGENOWriter extends TestCase {
	public void testSmallPopulation() throws IOException {
		SNP snp1 = SNPFactory.createSNP("snp1", "chr5", 1000);
		SNP snp2 = SNPFactory.createSNP("snp2", "chr3", 2000);

		SampleBuilder b1 = new SampleBuilder("sample1");
		b1.setGenotype(snp1, "A", "A", IlluminaStrand.TOP);
		b1.setGenotype(snp2, "C", "G", IlluminaStrand.TOP);
		SampleBuilder b2 = new SampleBuilder("sample2");
		b2.setGenotype(snp1, null, null, IlluminaStrand.TOP);
		b2.setGenotype(snp2, "G", "G", IlluminaStrand.TOP);
		SampleBuilder b3 = new SampleBuilder("sample3");
		b3.setGenotype(snp1, "T", "A", IlluminaStrand.TOP);

		List<Sample> samples = new ArrayList<Sample>();
		samples.add(b1.getInstance());
		samples.add(b2.getInstance());
		samples.add(b3.getInstance());

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GENOWriter writer = new GENOWriter(out);
		writer.write(snp1, samples);
		writer.write(snp2, samples);
		writer.close();
		Assert.assertEquals(getExpectedOutput(), out.toString());
	}

	private String getExpectedOutput() throws IOException {
		InputStream in = getClass().getClassLoader().getResourceAsStream("edu/uab/ssg/io/eigenstrat/expected.geno");
		StringBuilder builder = new StringBuilder();
		int ch = -1;
		while ((ch = in.read()) != -1) {
			builder.append((char) ch);
		}
		return builder.toString();
	}
}
