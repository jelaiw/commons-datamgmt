package edu.uab.ssg.model.snp;

import junit.framework.TestCase;
import junit.framework.Assert;
import java.util.*;

/**
 * @author Jelai Wang
 */

public final class TestAlleleStatistics extends TestCase {
	public void testPopulation() {
		// Create test population.
		PopulationBuilder builder = new PopulationBuilder("test");
		SNP snp1 = new DefaultSNP("snp1", "chrZ", 1);
		SNP snp2 = new DefaultSNP("snp2", "chrZ", 5);
		builder.setGenotype("sample1", snp1, "A", "B", IlluminaStrand.TOP);
		builder.setGenotype("sample2", snp1, "B", "B", IlluminaStrand.TOP);
		builder.setGenotype("sample1", snp2, "B", "C", IlluminaStrand.TOP);
		builder.setGenotype("sample2", snp2, "B", null, IlluminaStrand.TOP);
		builder.setGenotype("sample3", snp2, "B", "C", IlluminaStrand.TOP);
		builder.setGenotype("sample4", snp2, null, "C", IlluminaStrand.TOP);
		Population test = builder.getInstance();

		Population.AlleleStatistics statistics = null;
		// Test snp1.
		statistics = test.getStatistics(snp1);
		Assert.assertEquals(1, statistics.getFrequency("A"));
		Assert.assertEquals(3, statistics.getFrequency("B"));
		Assert.assertEquals(4, statistics.getNumberOfMissingValues());
		Assert.assertEquals(Double.doubleToLongBits(0.25), Double.doubleToLongBits(statistics.getRelativeFrequency("A")));
		Assert.assertEquals(Double.doubleToLongBits(0.75), Double.doubleToLongBits(statistics.getRelativeFrequency("B")));
		Assert.assertTrue(statistics.existsMinorAllele());
		Assert.assertEquals(1, statistics.getFrequency(statistics.getMinorAllele()));

		// Test snp2.
		statistics = test.getStatistics(snp2);
		Assert.assertEquals(3, statistics.getFrequency("B"));
		Assert.assertEquals(3, statistics.getFrequency("C"));
		Assert.assertEquals(2, statistics.getNumberOfMissingValues());
		Assert.assertEquals(Double.doubleToLongBits(0.5), Double.doubleToLongBits(statistics.getRelativeFrequency("B")));
		Assert.assertEquals(Double.doubleToLongBits(0.5), Double.doubleToLongBits(statistics.getRelativeFrequency("C")));
		Assert.assertFalse(statistics.existsMinorAllele());
	}

	public void testDefault() {
		DefaultAlleleStatistics statistics = new DefaultAlleleStatistics();
		Set<String> alleles = null;
		// No alleles.
		Assert.assertFalse(statistics.existsMinorAllele());
		Assert.assertEquals(0, statistics.getAlleles().size());

		// One allele.
		statistics.addAllele("foo");
		Assert.assertEquals(1, statistics.getFrequency("foo"));
		Assert.assertEquals(1, statistics.getTotalNumberOfAlleles());
		Assert.assertEquals(Double.doubleToLongBits(1.), Double.doubleToLongBits(statistics.getRelativeFrequency("foo")));
		Assert.assertFalse(statistics.existsMinorAllele()); // Minor allele is undefined in this situation.
		Assert.assertEquals(1, statistics.getAlleles().size());
		alleles = statistics.getAlleles();
		Assert.assertTrue(alleles.contains("foo"));

		// Two alleles.
		statistics.addAllele("bar");
		Assert.assertFalse(statistics.existsMinorAllele()); // Minor allele is undefined in this situation, too.
		statistics.addAllele("foo");
		statistics.addAllele("foo");
		Assert.assertEquals(3, statistics.getFrequency("foo"));
		Assert.assertEquals(1, statistics.getFrequency("bar"));
		Assert.assertEquals(4, statistics.getTotalNumberOfAlleles());
		Assert.assertTrue(statistics.existsMinorAllele());
		Assert.assertEquals(1, statistics.getFrequency(statistics.getMinorAllele()));
		Assert.assertEquals(Double.doubleToLongBits(0.25), Double.doubleToLongBits(statistics.getRelativeFrequency(statistics.getMinorAllele())));
		Assert.assertEquals(2, statistics.getAlleles().size());
		alleles = statistics.getAlleles();
		Assert.assertTrue(alleles.contains("foo"));
		Assert.assertTrue(alleles.contains("bar"));

		// Test missing.
		Assert.assertEquals(0, statistics.getNumberOfMissingValues());
		statistics.addAllele(null);
		Assert.assertEquals(1, statistics.getNumberOfMissingValues());
	}
}
