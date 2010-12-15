package edu.uab.ssg.model.snp;

import junit.framework.TestCase;
import junit.framework.Assert;
import java.util.*;

/**
 * @author Jelai Wang
 */

public final class TestDefaultSNP extends TestCase {
	public void testFoo() {
		SNP foo = new DefaultSNP("foo", "chr1", 1);
		Assert.assertEquals("foo", foo.getName());
		Assert.assertEquals("chr1", foo.getChromosome());
		Assert.assertEquals(1, foo.getPosition());
	}

	public void testEquals() {
		SNP foo = new DefaultSNP("foo", "chr1", 1);
		SNP bar = new DefaultSNP("bar", "chr1", 1);
		SNP baz = new DefaultSNP("baz", "chr1", 10);
		Assert.assertEquals(foo, bar); // Same SNP, different name.
		Assert.assertFalse(foo.equals(baz));
		Assert.assertFalse(bar.equals(baz));
		Assert.assertTrue(bar.equals(foo));
		SNP klee = new DefaultSNP("foo", "chr2", 1);
		Assert.assertFalse(klee.equals(foo));
	}

	public void testHashCode() {
		SNP foo = new DefaultSNP("foo", "chr1", 1);
		SNP bar = new DefaultSNP("bar", "chr1", 1);
		SNP baz = new DefaultSNP("baz", "chr2", 10);
		SNP klee = new DefaultSNP("foo", "chr2", 1);
		Set<SNP> set = new HashSet<SNP>();
		set.add(foo);
		Assert.assertTrue(set.contains(foo));
		Assert.assertTrue(set.contains(bar));
		Assert.assertFalse(set.contains(baz));
		Assert.assertTrue(set.contains(new DefaultSNP("foo clone", "chr1", 1)));
		set.add(baz);
		Assert.assertTrue(set.contains(baz));
		Assert.assertTrue(set.contains(new DefaultSNP("baz clone", "chr1", 1)));
		set.add(bar);
		Assert.assertEquals(2, set.size());
		set.add(klee);
		Assert.assertEquals(3, set.size());
	}
}
