package edu.uab.ssg.model.snp;

import junit.framework.TestCase;
import junit.framework.Assert;
import java.util.Set;
import java.util.HashSet;

/**
 * @author Jelai Wang
 */

public final class TestChromosome extends TestCase {
	public void testEquals() {
		Chromosome foo = new DefaultChromosome("foo");
		Assert.assertEquals(foo, new DefaultChromosome("foo"));
		Chromosome bar = new DefaultChromosome("bar");
		Assert.assertFalse(foo.equals(bar));
		Assert.assertFalse(foo.equals(null));
	}

	public void testHashCode() {
		Chromosome foo = new DefaultChromosome("foo");
		Chromosome bar = new DefaultChromosome("bar");
		Set<Chromosome> set = new HashSet<Chromosome>();
		set.add(foo);
		Assert.assertTrue(set.contains(foo));
		Assert.assertFalse(set.contains(bar));
		Assert.assertFalse(set.contains(new DefaultChromosome("bar")));
		set.add(bar);
		Assert.assertTrue(set.contains(bar));
		Assert.assertTrue(set.contains(new DefaultChromosome("foo")));
		Assert.assertTrue(set.contains(new DefaultChromosome("bar")));
	}

	public void testBadArgs() {
		try {
			Chromosome foo = new DefaultChromosome(null);
			Assert.fail();
		}
		catch (NullPointerException e) {
			Assert.assertTrue(true);
		}
	}
}
