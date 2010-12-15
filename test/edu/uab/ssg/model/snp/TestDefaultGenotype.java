package edu.uab.ssg.model.snp;

import junit.framework.TestCase;
import junit.framework.Assert;
import java.util.*;

/**
 * @author Jelai Wang
 */

public final class TestDefaultGenotype extends TestCase {
	private static final SNP foo = new DefaultSNP("foo", "chr1", 1);

	public void testNaive() {
		DefaultGenotype genotype = new DefaultGenotype(foo, "C", "G", IlluminaStrand.TOP);
		Assert.assertEquals(foo, genotype.getSNP());
		Assert.assertEquals("C", genotype.getAllele1());
		Assert.assertEquals("G", genotype.getAllele2());
		Assert.assertEquals(IlluminaStrand.TOP, genotype.getStrand());
	}

	public void testEquals() {
		DefaultGenotype g1 = new DefaultGenotype(foo, "C", "C", IlluminaStrand.TOP);
		DefaultGenotype g2 = new DefaultGenotype(foo, "C", "G", IlluminaStrand.TOP);
		DefaultGenotype g3 = new DefaultGenotype(foo, "G", "G", IlluminaStrand.TOP);
		// Test homozygous.
		Assert.assertEquals(g1, new DefaultGenotype(foo, "C", "C", IlluminaStrand.TOP));
		Assert.assertEquals(g3, new DefaultGenotype(foo, "G", "G", IlluminaStrand.TOP));
		Assert.assertFalse(g1.equals(new DefaultGenotype(foo, "C", "C", IlluminaStrand.BOT))); // LOOK!!
		// Test heterozygous.
		Assert.assertEquals(g2, new DefaultGenotype(foo, "G", "C", IlluminaStrand.TOP));
		Assert.assertFalse(g2.equals(new DefaultGenotype(foo, "G", "C", IlluminaStrand.BOT))); // LOOK!!
	}

	public void testHashCode() {
		DefaultGenotype g1 = new DefaultGenotype(foo, "C", "C", IlluminaStrand.TOP);
		DefaultGenotype g2 = new DefaultGenotype(foo, "C", "G", IlluminaStrand.TOP);
		DefaultGenotype g3 = new DefaultGenotype(foo, "G", "G", IlluminaStrand.TOP);

		Set<DefaultGenotype> set = new HashSet<DefaultGenotype>();
		set.add(g1);
		// Test homozygous.
		Assert.assertTrue(set.contains(g1));
		Assert.assertTrue(set.contains(new DefaultGenotype(foo, "C", "C", IlluminaStrand.TOP)));
		set.add(new DefaultGenotype(foo, "C", "C", IlluminaStrand.TOP));
		Assert.assertEquals(1, set.size());

		set.add(g2);
		set.add(g3);

		Assert.assertEquals(3, set.size());
		// Test heterozygous.
		Assert.assertTrue(set.contains(new DefaultGenotype(foo, "G", "C", IlluminaStrand.TOP)));
	}
}
