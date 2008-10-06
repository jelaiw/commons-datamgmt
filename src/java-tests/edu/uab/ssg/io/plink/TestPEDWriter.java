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
		PopulationBuilder builder = new PopulationBuilder("foo");
		builder.setGenotype("sample1", snp1, "A", "A", IlluminaStrand.TOP);
		builder.setGenotype("sample1", snp2, "C", "G", IlluminaStrand.TOP);
		builder.setGenotype("sample2", snp1, null, "A", IlluminaStrand.TOP);
		builder.setGenotype("sample2", snp2, "G", "G", IlluminaStrand.TOP);
		builder.setGenotype("sample3", snp1, "T", "A", IlluminaStrand.TOP);
		Population foo = builder.getInstance();

		PEDWriter writer = new PEDWriter();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		writer.write(foo, out);
		Assert.assertEquals(getExpectedOutput(), out.toString());
	}

	private String getExpectedOutput() throws IOException {
		InputStream in = getClass().getClassLoader().getResourceAsStream("edu/uab/ssg/io/plink/expected_PED_file.txt");
		StringBuilder builder = new StringBuilder();
		int ch = -1;
		while ((ch = in.read()) != -1) {
			builder.append((char) ch);
		}
		return builder.toString();
	}
}
