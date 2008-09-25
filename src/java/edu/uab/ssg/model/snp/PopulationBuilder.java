package edu.uab.ssg.model.snp;

import java.util.*;

/**
 * @author Jelai Wang
 */

public final class PopulationBuilder {
	private DefaultPopulation population;
	private Set<SNP> snps = new LinkedHashSet<SNP>();

	public PopulationBuilder(String name) {
		if (name == null)
			throw new NullPointerException("name");
		this.population = new DefaultPopulation(name);	
	}

	// The alleles a1 and a2 can be null to indicate missing genotype data according to our stated missing data handling policy.
	public void addGenotype(String sampleName, SNP snp, String a1, String a2, Strand strand) {
		if (sampleName == null)
			throw new NullPointerException("sampleName");
		if (snp == null)
			throw new NullPointerException("snp");
		if (strand == null)
			throw new NullPointerException("strand");
		DefaultSample sample = (DefaultSample) population.getSample(sampleName);	
		if (sample == null) {
			sample = new DefaultSample(sampleName, population);
			population.addSample(sample);
		}
		sample.addGenotype(snp, a1, a2, strand);
		snps.add(snp); // Cache SNPs that have been added.
	}

	public Population getInstance() {
		// Optimization that involves caching SNPs that were added.
		population.setSnps(snps); 
		Population tmp = population;
		this.population = null;
		return tmp;
	}
}
