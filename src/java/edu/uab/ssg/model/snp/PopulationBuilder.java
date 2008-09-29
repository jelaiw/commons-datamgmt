package edu.uab.ssg.model.snp;

import java.util.*;

/**
 * @author Jelai Wang
 */

public final class PopulationBuilder {
	private DefaultPopulation population;

	public PopulationBuilder(String name) {
		if (name == null)
			throw new NullPointerException("name");
		this.population = new DefaultPopulation(name);	
	}

	/**
	 * Set the genotype at a SNP for the specified sample.
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
			throw new IllegalArgumentException(String.valueOf(strand));
		DefaultSample sample = (DefaultSample) population.getSample(sampleName);	
		if (sample == null) {
			sample = new DefaultSample(sampleName, population);
			population.addSample(sample);
		}
		sample.setGenotype(snp, a1, a2, strand);
	}

	public Population getInstance() {
		Population tmp = population;
		this.population = null;
		return tmp;
	}
}
