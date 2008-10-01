package edu.uab.ssg.model.snp;

import junit.framework.TestCase;
import junit.framework.Assert;
import java.util.Set;

/**
 * @author Jelai Wang
 */

public final class TestPopulation extends TestCase {
	public void testNaively() {
		PopulationBuilder builder = new PopulationBuilder("foo");
		SNP snp1 = SNPFactory.createSNP("snp1", "chrZ", 1);
		SNP snp2 = SNPFactory.createSNP("snp2", "chrZ", 5);
		builder.setGenotype("sample1", snp1, "A", "B", IlluminaStrand.TOP);
		builder.setGenotype("sample1", snp2, "B", "B", IlluminaStrand.BOT);
		Population foo = builder.getInstance();
		Assert.assertEquals("foo", foo.getName());
		Assert.assertEquals(1, foo.getSamples().size());
		Sample sample1 = foo.getSample("sample1");
		Assert.assertEquals("sample1", sample1.getName());
		Assert.assertEquals(2, foo.getSNPs().size());
		Set<SNP> snps = foo.getSNPs();
		Assert.assertTrue(snps.contains(snp1));
		Assert.assertTrue(snps.contains(snp2));
	}

	public void testEquals() {
		Population foo1 = new DefaultPopulation("foo");
		Population foo2 = new DefaultPopulation("foo");
		Assert.assertFalse(foo1.equals(foo2));
	}

	public void testHashCode() {
		Population foo1 = new DefaultPopulation("foo");
		Population foo2 = new DefaultPopulation("foo");
		Assert.assertTrue(foo1.hashCode() != foo2.hashCode());
	}

	public void testBadArgs() {
		try {
			Population foo = new DefaultPopulation(null);
			Assert.fail();
		}
		catch (NullPointerException e) {
			Assert.assertTrue(true);
		}
		// If both alleles are missing, it doesn't make sense to pass strand.
		try {
			new PopulationBuilder("klee").setGenotype("foo", SNPFactory.createSNP("bar", "chr1", 1), null, null, IlluminaStrand.TOP);
			Assert.fail();
		}
		catch (IllegalArgumentException e) {
			Assert.assertTrue(true);
		}
	}
}
