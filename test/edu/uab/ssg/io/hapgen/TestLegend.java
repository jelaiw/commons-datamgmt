package edu.uab.ssg.io.hapgen;

import junit.framework.TestCase;
import junit.framework.Assert;
import java.util.*;
import java.io.*;
import edu.uab.ssg.model.snp.*;

/**
 * @author Jelai Wang
 */

public final class TestLegend extends TestCase {
	public void testSmallExample() throws IOException {
		// Set up markers.
		SNP snp1 = SNPFactory.createSNP("snp1", "chr2", 1000);
		SNP snp2 = SNPFactory.createSNP("snp2", "chr3", 2000);
		SNP snp3 = SNPFactory.createSNP("snp3", "chr4", 3000);
		// Set up samples so that snp1 is monomorphic, snp2 is bi-allelic,
		// snp3 is tri-allelic.
		SampleBuilder builder1 = new SampleBuilder("sample1");
		builder1.setGenotype(snp1, "A", "A", IlluminaStrand.TOP);
		builder1.setGenotype(snp2, "G", "A", IlluminaStrand.TOP);
		builder1.setGenotype(snp3, "C", "A", IlluminaStrand.TOP);
		Sample sample1 = builder1.getInstance();

		SampleBuilder builder2 = new SampleBuilder("sample2");
		builder2.setGenotype(snp1, "A", "A", IlluminaStrand.TOP);
		builder2.setGenotype(snp2, "A", "A", IlluminaStrand.TOP);
		builder2.setGenotype(snp3, "G", "A", IlluminaStrand.TOP);
		Sample sample2 = builder2.getInstance();

		SampleBuilder builder3 = new SampleBuilder("sample3");
		builder3.setGenotype(snp1, "A", "A", IlluminaStrand.TOP);
		builder3.setGenotype(snp2, "A", "A", IlluminaStrand.TOP);
		builder3.setGenotype(snp3, "C", "A", IlluminaStrand.TOP);
		Sample sample3 = builder3.getInstance();

		// Assemble input data structures.
		List<SNP> snps = new ArrayList<SNP>();
		snps.add(snp1);
		snps.add(snp2);
		snps.add(snp3);

		Set<Sample> samples = new LinkedHashSet<Sample>();
		samples.add(sample1);
		samples.add(sample2);
		samples.add(sample3);

		Legend legend = new MinorAlleleLegend(snps, samples);
		Assert.assertEquals(2, legend.getSNPs().size());
		Assert.assertNull(legend.getAllele0(snp3));
		Assert.assertNull(legend.getAllele1(snp3));
//		System.out.println(legend);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		legend.write(out);
		Assert.assertEquals(getExpectedOutput(), out.toString());
	}

	private String getExpectedOutput() throws IOException {
		InputStream in = getClass().getClassLoader().getResourceAsStream("edu/uab/ssg/io/hapgen/expected.leg");
		StringBuilder builder = new StringBuilder();
		int ch = -1;
		while ((ch = in.read()) != -1) {
			builder.append((char) ch);
		}
		return builder.toString();
	}
}
