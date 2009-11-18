package edu.uab.ssg.model.snp;

import junit.framework.TestCase;
import junit.framework.Assert;
import java.util.*;

/**
 * @author Jelai Wang
 */

public final class TestAlleleCounter extends TestCase {
	public void testDefault() {
		AlleleCounter counter = new AlleleCounter();
		Set<String> alleles = null;
		// No alleles.
		Assert.assertFalse(counter.existsMinorAllele());
		Assert.assertEquals(0, counter.getAlleles().size());
		Assert.assertEquals(0, counter.getNumberOfCountedValues());

		// One allele.
		counter.addAllele("foo");
		Assert.assertEquals(1, counter.getFrequency("foo"));
		Assert.assertEquals(1, counter.getNumberOfCountedValues());
		Assert.assertEquals(Double.doubleToLongBits(1.), Double.doubleToLongBits(counter.getRelativeFrequency("foo")));
		Assert.assertFalse(counter.existsMinorAllele()); // Minor allele is undefined in this situation.
		Assert.assertEquals(1, counter.getAlleles().size());
		alleles = counter.getAlleles();
		Assert.assertTrue(alleles.contains("foo"));

		// Two alleles.
		counter.addAllele("bar");
		Assert.assertFalse(counter.existsMinorAllele()); // Minor allele is undefined in this situation, too.
		counter.addAllele("foo");
		counter.addAllele("foo");
		Assert.assertEquals(3, counter.getFrequency("foo"));
		Assert.assertEquals(1, counter.getFrequency("bar"));
		Assert.assertEquals(4, counter.getNumberOfCountedValues());
		Assert.assertTrue(counter.existsMinorAllele());
		Assert.assertEquals(1, counter.getFrequency(counter.getMinorAllele()));
		Assert.assertEquals(Double.doubleToLongBits(0.25), Double.doubleToLongBits(counter.getRelativeFrequency(counter.getMinorAllele())));
		Assert.assertEquals(2, counter.getAlleles().size());
		alleles = counter.getAlleles();
		Assert.assertTrue(alleles.contains("foo"));
		Assert.assertTrue(alleles.contains("bar"));

		// Test missing.
		Assert.assertEquals(0, counter.getNumberOfMissingValues());
		Assert.assertEquals(4, counter.getNumberOfCountedValues());
		counter.addAllele(null);
		Assert.assertEquals(1, counter.getNumberOfMissingValues());
		Assert.assertEquals(4, counter.getNumberOfCountedValues());
	}
}
