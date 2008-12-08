package edu.uab.ssg.io.plink;

import junit.framework.TestCase;
import junit.framework.Assert;
import java.util.*;
import java.io.*;
import edu.uab.ssg.model.snp.*;

/**
 * @author Jelai Wang
 */

public final class TestPEDWriter extends TestCase {
	public void testSmallPopulation() throws IOException {
		SNP snp1 = SNPFactory.createSNP("snp1", "chr5", 1000);
		SNP snp2 = SNPFactory.createSNP("snp2", "chr3", 2000);
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
		Sample sample1 = b1.getInstance();
		Sample sample2 = b2.getInstance();
		Sample sample3 = b3.getInstance();

		PEDWriter.MetaData metaData = new PEDWriter.MetaData() {
			public String getSex(String sampleName) {
				if ("sample2".equals(sampleName))
					return "2";
				else if ("sample3".equals(sampleName))
					return "1";
				return null;
			}

			public String getPhenotype(String sampleName) {
				if ("sample1".equals(sampleName))
					return "100";
				else if ("sample3".equals(sampleName))
					return "10000";
				return null;
			}
		};
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PEDWriter writer = new PEDWriter(snps, metaData, out);
		writer.write(sample1);
		writer.write(sample2);
		writer.write(sample3);
		writer.close();
		Assert.assertEquals(getExpectedOutput(), out.toString());
	}

	private String getExpectedOutput() throws IOException {
		InputStream in = getClass().getClassLoader().getResourceAsStream("edu/uab/ssg/io/plink/expected.ped");
		StringBuilder builder = new StringBuilder();
		int ch = -1;
		while ((ch = in.read()) != -1) {
			builder.append((char) ch);
		}
		return builder.toString();
	}
}
