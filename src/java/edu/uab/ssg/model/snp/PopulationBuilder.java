package edu.uab.ssg.model.snp;

import java.util.*;

/**
 * A builder for a study population.
 *
 * @author Jelai Wang
 */

public final class PopulationBuilder {
	private DefaultPopulation population;

	/**
	 * Constructs a builder for a study population with the given name.
	 */
	public PopulationBuilder(String name) {
		if (name == null)
			throw new NullPointerException("name");
		this.population = new DefaultPopulation(name);	
	}

	/**
	 * Sets the genotype at a SNP for a particular sample.
	 * Alleles can be null to indicate that the sample was assessed for
	 * genotype at this SNP, but data are missing.
	 * @param a1 The first allele.
	 * @param a2 The second allele.
	 * @param strand The strand cannot be null if one or both alleles are given.
	 */
	public void setGenotype(String sampleName, SNP snp, String a1, String a2, Strand strand) {
		if (sampleName == null)
			throw new NullPointerException("sampleName");
		if (snp == null)
			throw new NullPointerException("snp");
		// If one or both alleles are not missing, strand cannot be null.
		if ((a1 != null || a2 != null) && strand == null)
			throw new NullPointerException("strand");
		// If both alleles are missing, it doesn't make sense to pass strand.
		if (a1 == null && a2 == null && strand != null)
			throw new IllegalArgumentException(a1 + " " + a2 + " " + strand);
		DefaultSample sample = (DefaultSample) population.getSample(sampleName);	
		if (sample == null) {
			sample = new DefaultSample(sampleName, population);
			population.addSample(sample);
		}
		sample.setGenotype(snp, a1, a2, strand);
	}

	/**
	 * Returns the study population and disables the builder.
	 * In other words, after getInstance is called, setGenotype doesn't work.
	 */
	public Population getInstance() {
		Population tmp = population;
		this.population = null;
		return tmp;
	}
}
