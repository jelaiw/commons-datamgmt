package edu.uab.ssg.model.snp;

import junit.framework.TestCase;
import junit.framework.Assert;

/**
 * @author Jelai Wang
 */

public final class TestDefaultSample extends TestCase {
	private static SNP snp1, snp2, snp3;

	static {
		snp1 = new DefaultSNP("snp1", "chr1", 1);
		snp2 = new DefaultSNP("snp2", "chr1", 2);
		snp3 = new DefaultSNP("snp3", "chr2", 3);
	}

	public void testHeterozygousGenotype() {
		DefaultSample sample1 = new DefaultSample("sample1");
		DefaultSample sample2 = new DefaultSample("sample2");
		// Conceptually, for unphased data, which is what we model here,
		// these two genotypes are equal.
		sample1.setGenotype(snp2, "G", "A", IlluminaStrand.TOP);
		sample2.setGenotype(snp2, "A", "G", IlluminaStrand.TOP);
		Assert.assertEquals(sample1.getGenotype(snp2), sample2.getGenotype(snp2));
	}

	public void testGenotypeCaching() {
		DefaultSample sample1 = new DefaultSample("sample1");
		DefaultSample sample2 = new DefaultSample("sample2");
		// Test simple case, does the caching work?
		sample1.setGenotype(snp1, "A", "G", IlluminaStrand.TOP);
		sample2.setGenotype(snp1, "A", "G", IlluminaStrand.TOP);
		Assert.assertEquals(sample1.getGenotype(snp1), sample2.getGenotype(snp1));
		Assert.assertSame(sample1.getGenotype(snp1), sample2.getGenotype(snp1));
	}

	// Test for various degrees of missing genotype data.
	public void testMissingData() {
		DefaultSample sample1 = new DefaultSample("sample1");
		// Half-null. Does any technology generate this? For now, half-null 
		// is not allowed. This simplifies the implementation somewhat.
		try {
			sample1.setGenotype(snp1, "C", null, IlluminaStrand.TOP);
			Assert.fail();
		}
		catch (IllegalArgumentException e) {
			Assert.assertTrue(true);
		}
		// This is the way we expect client programmers to call
		// setGenotype() with missing data.
		sample1.setGenotype(snp1, null, null, IlluminaStrand.BOT);
		// This is another way, but the previous way has a slight 
		// advantage, so that's what the API supports.
		try {
			sample1.setGenotype(snp3, null, null, null);
			Assert.fail();
		}
		catch (NullPointerException e) {
			Assert.assertTrue(true);
		}

		Assert.assertTrue(sample1.existsGenotype(snp1));
		Assert.assertFalse(sample1.existsGenotype(snp2));

		Sample.Genotype genotype = sample1.getGenotype(snp1);
		Assert.assertEquals(null, genotype.getAllele1());
		Assert.assertEquals(null, genotype.getAllele2());
		Assert.assertEquals(IlluminaStrand.BOT, genotype.getStrand());
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
