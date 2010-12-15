package edu.uab.ssg.io.hyperlasso;

import junit.framework.TestCase;
import junit.framework.Assert;
import java.util.*;
import java.io.*;
import edu.uab.ssg.model.snp.*;

/**
 * @author Jelai Wang
 */

public final class TestDATWriter extends TestCase {
	public void testSmallPopulation() throws IOException {
		SNP snp1 = new DefaultSNP("snp1", "chr5", 1000);
		SNP snp2 = new DefaultSNP("snp2", "chr3", 2000);
		SNP snp3 = new DefaultSNP("snp3", "chr3", 3000);
		SNP snp4 = new DefaultSNP("snp4", "chr3", 4000);
		List<SNP> snps = new ArrayList<SNP>();
		snps.add(snp1);
		snps.add(snp2);
		snps.add(snp3);
		snps.add(snp4);

		SampleBuilder b1 = new SampleBuilder("sample1");
		b1.setGenotype(snp1, "A", "A", IlluminaStrand.TOP);
		b1.setGenotype(snp2, "C", "G", IlluminaStrand.TOP);
		// LOOK!! snp3 was not assessed for genotype.
		b1.setGenotype(snp4, "T", "A", IlluminaStrand.TOP);
		SampleBuilder b2 = new SampleBuilder("sample2");
		b2.setGenotype(snp1, "T", "A", IlluminaStrand.TOP);
		b2.setGenotype(snp2, "G", "G", IlluminaStrand.TOP);
		b2.setGenotype(snp3, "C", "C", IlluminaStrand.TOP);
		b2.setGenotype(snp4, "A", "A", IlluminaStrand.TOP);
		SampleBuilder b3 = new SampleBuilder("sample3");
		b3.setGenotype(snp1, "T", "T", IlluminaStrand.TOP);
		b3.setGenotype(snp2, "G", "G", IlluminaStrand.TOP);
		// snp3 was assessed for genotype, but the data were missing
		// for this sample.
		b3.setGenotype(snp3, null, null, IlluminaStrand.TOP);
		b3.setGenotype(snp4, "A", "T", IlluminaStrand.TOP);
		Sample sample1 = b1.getInstance();
		Sample sample2 = b2.getInstance();
		Sample sample3 = b3.getInstance();

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		DATWriter writer = new DATWriter(snps, out);
		writer.writeHeader();
		writer.write(sample1);
		writer.write(sample2);
		writer.write(sample3);
		writer.close();
		Assert.assertEquals(getExpectedOutput(), out.toString());
	}

	private String getExpectedOutput() throws IOException {
		InputStream in = getClass().getClassLoader().getResourceAsStream("edu/uab/ssg/io/hyperlasso/expected.dat");
		StringBuilder builder = new StringBuilder();
		int ch = -1;
		while ((ch = in.read()) != -1) {
			builder.append((char) ch);
		}
		return builder.toString();
	}
}
