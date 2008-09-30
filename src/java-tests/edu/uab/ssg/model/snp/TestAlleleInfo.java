package edu.uab.ssg.model.snp;

import junit.framework.TestCase;
import junit.framework.Assert;
import java.util.*;

/**
 * @author Jelai Wang
 */

public final class TestAlleleInfo extends TestCase {
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

		Population.AlleleInfo info = null;
		// Test snp1.
		info = test.getAlleleInfo(snp1);
		Assert.assertEquals(1, info.getFrequency("A"));
		Assert.assertEquals(3, info.getFrequency("B"));
		Assert.assertEquals(4, info.getNumberOfMissingValues());
		Assert.assertEquals(Double.doubleToLongBits(0.25), Double.doubleToLongBits(info.getRelativeFrequency("A")));
		Assert.assertEquals(Double.doubleToLongBits(0.75), Double.doubleToLongBits(info.getRelativeFrequency("B")));
		Assert.assertTrue(info.existsMinorAllele());
		Assert.assertEquals(1, info.getFrequency(info.getMinorAllele()));

		// Test snp2.
		info = test.getAlleleInfo(snp2);
		Assert.assertEquals(3, info.getFrequency("B"));
		Assert.assertEquals(3, info.getFrequency("C"));
		Assert.assertEquals(2, info.getNumberOfMissingValues());
		Assert.assertEquals(Double.doubleToLongBits(0.5), Double.doubleToLongBits(info.getRelativeFrequency("B")));
		Assert.assertEquals(Double.doubleToLongBits(0.5), Double.doubleToLongBits(info.getRelativeFrequency("C")));
		Assert.assertFalse(info.existsMinorAllele());
	}

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
