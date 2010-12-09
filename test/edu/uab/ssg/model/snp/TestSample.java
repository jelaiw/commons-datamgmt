package edu.uab.ssg.model.snp;

import junit.framework.TestCase;
import junit.framework.Assert;

/**
 * @author Jelai Wang
 */

public final class TestSample extends TestCase {
	private static SNP snp1, snp2, snp3;

	static {
		snp1 = new DefaultSNP("snp1", "chr1", 1);
		snp2 = new DefaultSNP("snp2", "chr1", 2);
		snp3 = new DefaultSNP("snp3", "chr2", 3);
	}

	public void testMissingData() {
		// Test for various degrees of missing genotype data.
		DefaultSample sample1 = new DefaultSample("sample1");
		sample1.setGenotype(snp1, "C", null, IlluminaStrand.TOP);
		sample1.setGenotype(snp3, null, null, null);

		Assert.assertTrue(sample1.existsGenotype(snp1));
		Sample.Genotype genotype = null;

		genotype = sample1.getGenotype(snp1);
		Assert.assertEquals("C", genotype.getAllele1());
		Assert.assertEquals(null, genotype.getAllele2());

		genotype = sample1.getGenotype(snp3);
		Assert.assertEquals(null, genotype.getAllele1());
		Assert.assertEquals(null, genotype.getAllele2());
		Assert.assertEquals(null, genotype.getStrand());
	}

	public void testNaively() {
		DefaultSample sample1 = new DefaultSample("sample1");
		sample1.setGenotype(snp1, "C", "T", IlluminaStrand.TOP);

		Assert.assertEquals("sample1", sample1.getName());
		Assert.assertTrue(sample1.existsGenotype(snp1));
		Sample.Genotype genotype = sample1.getGenotype(snp1);
		Assert.assertEquals("C", genotype.getAllele1());
		Assert.assertEquals("T", genotype.getAllele2());
		Assert.assertSame(IlluminaStrand.TOP, genotype.getStrand());

		Assert.assertFalse(sample1.existsGenotype(snp2));
		try {
			sample1.getGenotype(snp2);
			Assert.fail();
		}
		catch (IllegalArgumentException e) {
			Assert.assertTrue(true);
		}

		sample1.setGenotype(snp2, "A", "G", IlluminaStrand.BOT);
		Assert.assertTrue(sample1.existsGenotype(snp2));
		genotype = sample1.getGenotype(snp2);
		Assert.assertEquals("A", genotype.getAllele1());
		Assert.assertEquals("G", genotype.getAllele2());
		Assert.assertSame(IlluminaStrand.BOT, genotype.getStrand());
	}
}
