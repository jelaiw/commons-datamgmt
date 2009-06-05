package edu.uab.ssg.io.hapgen;

import junit.framework.TestCase;
import junit.framework.Assert;
import java.util.*;
import java.io.*;
import edu.uab.ssg.model.snp.*;

/**
 * @author Jelai Wang
 */

public final class TestHAPSWriter extends TestCase {
	public void testSmallExample() throws IOException {
		// Set up markers.
		SNP snp1 = SNPFactory.createSNP("snp1", "chr2", 1000);
		SNP snp2 = SNPFactory.createSNP("snp2", "chr3", 2000);
		SNP snp3 = SNPFactory.createSNP("snp3", "chr4", 3000);
		// Sample with complete data.
		SampleBuilder builder1 = new SampleBuilder("sample1");
		builder1.setGenotype(snp1, "A", "A", IlluminaStrand.TOP);
		builder1.setGenotype(snp2, "C", "A", IlluminaStrand.TOP);
		builder1.setGenotype(snp3, "C", "G", IlluminaStrand.TOP);
		Sample sample1 = builder1.getInstance();
		// Sample with a missing haplotype.
		SampleBuilder builder2 = new SampleBuilder("sample2");
		builder2.setGenotype(snp1, "A", null, IlluminaStrand.TOP);
		builder2.setGenotype(snp2, "C", null, IlluminaStrand.TOP);
		builder2.setGenotype(snp3, "C", null, IlluminaStrand.TOP);
		Sample sample2 = builder2.getInstance();
		// Sample with missing data for a SNP.
		SampleBuilder builder3 = new SampleBuilder("sample3");
		builder3.setGenotype(snp1, "A", "A", IlluminaStrand.TOP);
		builder3.setGenotype(snp2, "C", "A", IlluminaStrand.TOP);
		Sample sample3 = builder3.getInstance();
		// Create a legend.
		Legend legend = createLegend(new SNP[] { snp1, snp2, snp3 }, new Sample[] { sample1, sample2, sample3 });
		
		Set<Sample> samples = new LinkedHashSet<Sample>();
		samples.add(sample1);
		samples.add(sample2);
		samples.add(sample3);
		HAPSWriter writer = new HAPSWriter(legend, samples);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		writer.write(out);
		Assert.assertEquals(getExpectedOutput(), out.toString());
	}

	private Legend createLegend(SNP[] snps, Sample[] samples) {
		LegendBuilder builder = new LegendBuilder();
		for (int i = 0; i < samples.length; i++) {
			Sample sample = samples[i];
			for (int j = 0; j < snps.length; j++) {
				SNP snp = snps[j];
				if (sample.existsGenotype(snp)) {
					Sample.Genotype genotype = sample.getGenotype(snp);
					builder.countAllele(snp, genotype.getAllele1());
					builder.countAllele(snp, genotype.getAllele2());
				}
			}
		}
		return builder.createMinorAlleleLegend();
	}

	private String getExpectedOutput() throws IOException {
		InputStream in = getClass().getClassLoader().getResourceAsStream("edu/uab/ssg/io/hapgen/expected.haps");
		StringBuilder builder = new StringBuilder();
		int ch = -1;
		while ((ch = in.read()) != -1) {
			builder.append((char) ch);
		}
		return builder.toString();
	}
}
