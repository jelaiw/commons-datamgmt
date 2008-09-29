package edu.uab.ssg.model.snp;

import junit.framework.TestCase;
import junit.framework.Assert;

/**
 * @author Jelai Wang
 */

public final class TestSample extends TestCase {
	public void testMissingData() {
		PopulationBuilder builder = new PopulationBuilder("CEU");
		SNP bar = new DefaultSNP("bar", "chr1", 1);
		SNP baz = new DefaultSNP("baz", "chr2", 3);
		// Test for various degrees of missing genotype data.
		builder.setGenotype("foo", bar, "C", null, IlluminaStrand.TOP);
		builder.setGenotype("foo", baz, null, null, null);
		Population ceu = builder.getInstance();
		Sample foo = ceu.getSample("foo");

		Assert.assertTrue(foo.existsGenotype(bar));
		Sample.Genotype genotype = null;

		genotype = foo.getGenotype(bar);
		Assert.assertEquals("C", genotype.getAllele1());
		Assert.assertEquals(null, genotype.getAllele2());

		genotype = foo.getGenotype(baz);
		Assert.assertEquals(null, genotype.getAllele1());
		Assert.assertEquals(null, genotype.getAllele2());
		Assert.assertEquals(null, genotype.getStrand());
	}

	public void testNaively() {
		PopulationBuilder builder = new PopulationBuilder("CEU");
		SNP bar = new DefaultSNP("bar", "chr1", 1);
		builder.setGenotype("foo", bar, "C", "T", IlluminaStrand.TOP);
		Population ceu = builder.getInstance();
		Sample foo = ceu.getSample("foo");

		Assert.assertEquals("foo", foo.getName());
		Assert.assertSame(ceu, foo.getPopulation());
		Assert.assertEquals(ceu, foo.getPopulation());
		Assert.assertTrue(foo.existsGenotype(bar));
		Sample.Genotype genotype = foo.getGenotype(bar);
		Assert.assertEquals("C", genotype.getAllele1());
		Assert.assertEquals("T", genotype.getAllele2());
		Assert.assertSame(IlluminaStrand.TOP, genotype.getStrand());

		SNP klee = new DefaultSNP("klee", "chr1", 2);
		Assert.assertFalse(foo.existsGenotype(klee));
		try {
			foo.getGenotype(klee);
			Assert.fail();
		}
		catch (IllegalArgumentException e) {
			Assert.assertTrue(true);
		}

		((DefaultSample) foo).setGenotype(klee, "A", "G", IlluminaStrand.BOT);
		Assert.assertTrue(foo.existsGenotype(klee));
		genotype = foo.getGenotype(klee);
		Assert.assertEquals("A", genotype.getAllele1());
		Assert.assertEquals("G", genotype.getAllele2());
		Assert.assertSame(IlluminaStrand.BOT, genotype.getStrand());
	}

	public void testBadArgs() {
		try {
			Sample foo = new DefaultSample(null, null);
			Assert.fail();
		}
		catch (NullPointerException e) {
			Assert.assertTrue(true);
		}

		try {
			Sample foo = new DefaultSample("foo", null);
			Assert.fail();
		}
		catch (NullPointerException e) {
			Assert.assertTrue(true);
		}
	}
}
