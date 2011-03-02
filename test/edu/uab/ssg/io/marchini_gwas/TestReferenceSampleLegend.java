package edu.uab.ssg.io.marchini_gwas;

import junit.framework.TestCase;
import junit.framework.Assert;
import java.util.*;
import java.io.*;
import edu.uab.ssg.model.snp.*;

/**
 * @author Jelai Wang
 */

public final class TestReferenceSampleLegend extends TestCase {
	public void testSmallExample() throws IOException {
		// Set up markers.
		SNP snp1 = new DefaultSNP("snp1", "chr2", 1000);
		SNP snp2 = new DefaultSNP("snp2", "chr3", 2000);
		SNP snp3 = new DefaultSNP("snp3", "chr4", 3000);
		// Set up reference sample.
		SampleBuilder builder1 = new SampleBuilder("sample1");
		builder1.setGenotype(snp1, "A", "A", IlluminaStrand.TOP);
		builder1.setGenotype(snp2, "G", "A", IlluminaStrand.TOP);
		builder1.setGenotype(snp3, "C", "A", IlluminaStrand.TOP);
		Sample sample1 = builder1.getInstance();

		LegendBuilder builder = new LegendBuilder();
		// snp1 is monomorphic.
		builder.countAllele(snp1, "A");
		builder.countAllele(snp1, "A");
		builder.countAllele(snp1, "A");
		builder.countAllele(snp1, "A");
		// snp2 is biallelic.
		builder.countAllele(snp2, "G");
		builder.countAllele(snp2, "A");
		builder.countAllele(snp2, "A");
		builder.countAllele(snp2, "A");
		// snp3 is triallelic.
		builder.countAllele(snp3, "G");
		builder.countAllele(snp3, "A");
		builder.countAllele(snp3, "C");
		builder.countAllele(snp3, "A");

		Legend legend = builder.createReferenceSampleLegend(sample1);
		Assert.assertEquals(2, legend.getSNPs().size());
		Assert.assertEquals(1, builder.getBadSNPs().size());
		Assert.assertEquals(3, builder.getAllelesForBadSNP(snp3).size());

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		legend.write(out);
		Assert.assertEquals(getExpectedOutput(), out.toString());
	}

	private String getExpectedOutput() throws IOException {
		InputStream in = getClass().getClassLoader().getResourceAsStream("edu/uab/ssg/io/marchini_gwas/reference_sample.leg");
		StringBuilder builder = new StringBuilder();
		int ch = -1;
		while ((ch = in.read()) != -1) {
			builder.append((char) ch);
		}
		return builder.toString();
	}
}
