package edu.uab.ssg.model.snp;

import java.util.*;

/**
 * A builder for a sample from a study population.
 *
 * @author Jelai Wang
 */

public final class SampleBuilder {
	private DefaultSample sample;

	/**
	 * Constructs a builder for a sample with the given name.
	 */
	public SampleBuilder(String name) {
		if (name == null)
			throw new NullPointerException("name");
		this.sample = new DefaultSample(name);
	}

	/**
	 * Returns the sample name.
	 */
	public String getSampleName() {
		return sample.getName();
	}

	/**
	 * Sets the genotype at a SNP for a particular sample.
	 * Alleles can be null to indicate that the sample was assessed for
	 * genotype at this SNP, but data are missing.
	 * @param a1 The first allele.
	 * @param a2 The second allele.
	 * @param strand The strand cannot be null if one or both alleles are given.
	 */
	public void setGenotype(SNP snp, String a1, String a2, Strand strand) {
		if (snp == null)
			throw new NullPointerException("snp");
		if ((a1 != null || a2 != null) && strand == null)
			throw new NullPointerException("strand");
		sample.setGenotype(snp, a1, a2, strand);
	}

	/**
	 * Returns the sample and disables the builder.
	 * In other words, after getInstance is called, setGenotype doesn't work.
	 */
	public Sample getInstance() {
		Sample tmp = sample;
		this.sample = null;
		return tmp;
	}
}
