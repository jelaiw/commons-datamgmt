package edu.uab.ssg.model.snp;

import junit.framework.TestCase;
import junit.framework.Assert;
import java.util.*;

/**
 * @author Jelai Wang
 */

public final class TestAlleleInfo extends TestCase {
	public void testDefault() {
		DefaultAlleleInfo info = new DefaultAlleleInfo();
		Set<String> alleles = null;
		// No alleles.
		Assert.assertFalse(info.existsMinorAllele());
		Assert.assertEquals(0, info.getAlleles().size());

		// One allele.
		info.addAllele("foo");
		Assert.assertEquals(1, info.getFrequency("foo"));
		Assert.assertEquals(1, info.getTotalNumberOfAlleles());
		Assert.assertEquals(Double.doubleToLongBits(1.), Double.doubleToLongBits(info.getRelativeFrequency("foo")));
		Assert.assertFalse(info.existsMinorAllele()); // Minor allele is undefined in this situation.
		Assert.assertEquals(1, info.getAlleles().size());
		alleles = info.getAlleles();
		Assert.assertTrue(alleles.contains("foo"));

		// Two alleles.
		info.addAllele("bar");
		Assert.assertFalse(info.existsMinorAllele()); // Minor allele is undefined in this situation, too.
		info.addAllele("foo");
		info.addAllele("foo");
		Assert.assertEquals(3, info.getFrequency("foo"));
		Assert.assertEquals(1, info.getFrequency("bar"));
		Assert.assertEquals(4, info.getTotalNumberOfAlleles());
		Assert.assertTrue(info.existsMinorAllele());
		Assert.assertEquals(1, info.getFrequency(info.getMinorAllele()));
		Assert.assertEquals(Double.doubleToLongBits(0.25), Double.doubleToLongBits(info.getRelativeFrequency(info.getMinorAllele())));
		Assert.assertEquals(2, info.getAlleles().size());
		alleles = info.getAlleles();
		Assert.assertTrue(alleles.contains("foo"));
		Assert.assertTrue(alleles.contains("bar"));

		// Test missing.
		Assert.assertEquals(0, info.getNumberOfMissingValues());
		info.addAllele(null);
		Assert.assertEquals(1, info.getNumberOfMissingValues());
	}
}
