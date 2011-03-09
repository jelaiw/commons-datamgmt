package edu.uab.ssg.io.marchini_gwas;

import junit.framework.TestCase;
import junit.framework.Assert;
import java.util.*;
import java.io.*;
import edu.uab.ssg.model.snp.*;

/**
 * @author Jelai Wang
 */

public final class TestGENSWriter extends TestCase {
	public void testSmallPopulation() throws IOException {
		SNP snp1 = new DefaultSNP("snp1", "chr5", 1000); // Has minor allele.
		SNP snp2 = new DefaultSNP("snp2", "chr10", 5000); // Bi-allelic, no minor allele.
		SNP snp3 = new DefaultSNP("snp3", "chr5", 3000); // Monomorphic.
		SNP snp4 = new DefaultSNP("snp4", "chr5", 4000); // Tri-allelic.
		SNP snp5 = new DefaultSNP("snp5", "chr10", 2000); // Has no call.

		SampleBuilder b1 = new SampleBuilder("sample1");
		b1.setGenotype(snp1, "A", "A", IlluminaStrand.TOP);
		b1.setGenotype(snp2, "A", "A", IlluminaStrand.TOP);
		b1.setGenotype(snp3, "C", "C", IlluminaStrand.TOP);
		b1.setGenotype(snp4, "A", "C", IlluminaStrand.TOP);
		b1.setGenotype(snp5, "G", "C", IlluminaStrand.TOP);
		SampleBuilder b2 = new SampleBuilder("sample2");
		b2.setGenotype(snp1, "A", "T", IlluminaStrand.TOP);
		b2.setGenotype(snp2, "A", "G", IlluminaStrand.TOP);
		b2.setGenotype(snp3, "C", "C", IlluminaStrand.TOP);
		b2.setGenotype(snp4, "G", "C", IlluminaStrand.TOP);
		b2.setGenotype(snp5, "G", "G", IlluminaStrand.TOP);
		SampleBuilder b3 = new SampleBuilder("sample3");
		b3.setGenotype(snp1, "T", "A", IlluminaStrand.TOP);
		b3.setGenotype(snp2, "G", "G", IlluminaStrand.TOP);
		b3.setGenotype(snp3, "C", "C", IlluminaStrand.TOP);
		b3.setGenotype(snp4, "C", "C", IlluminaStrand.TOP);
		List<Sample> samples = new ArrayList<Sample>();
		samples.add(b1.getInstance());
		samples.add(b2.getInstance());
		samples.add(b3.getInstance());

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GENSWriter writer = new GENSWriter(samples, out);
		writer.write(snp1);
		writer.write(snp2);
		writer.write(snp3);
		try {
			writer.write(snp4);
			Assert.fail();
		}
		catch (IllegalArgumentException e) {
			Assert.assertTrue(true);
		}
		writer.write(snp5);
		writer.close();
		Assert.assertEquals(getExpectedOutput(), out.toString());
	}

	private String getExpectedOutput() throws IOException {
		InputStream in = getClass().getClassLoader().getResourceAsStream("edu/uab/ssg/io/marchini_gwas/expected.gens");
		StringBuilder builder = new StringBuilder();
		int ch = -1;
		while ((ch = in.read()) != -1) {
			builder.append((char) ch);
		}
		return builder.toString();
	}
}
