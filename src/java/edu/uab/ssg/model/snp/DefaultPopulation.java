package edu.uab.ssg.model.snp;

import java.util.*;

/**
 * @author Jelai Wang
 */

/* package private */ final class DefaultPopulation implements Population {
	private String name;
	private Map<String, Sample> samples = new LinkedHashMap<String, Sample>();
	private Set<SNP> snps;

	/* package private */ DefaultPopulation(String name) {
		if (name == null)
			throw new NullPointerException("name");
		this.name = name;	
	}

	// See the PopulationBuilder class to coordinate changes.
	/* package private */ void addSample(Sample sample) {
		if (sample == null)
			throw new NullPointerException("sample");
		if (!sample.getPopulation().equals(this))	
			throw new IllegalArgumentException();
		samples.put(sample.getName(), sample);	
	}

	// See the PopulationBuilder class to coordinate changes.
	/* package private */ void setSnps(Set<SNP> snps) {
		this.snps = new LinkedHashSet<SNP>(snps);
	}

	public String getName() { 
		return name; 
	}

	public Set<Sample> getSamples() { 
		return new LinkedHashSet<Sample>(samples.values()); 
	}

	public Sample getSample(String sampleName) {
		if (sampleName == null)
			throw new NullPointerException("sampleName");
		return (Sample) samples.get(sampleName);	
	}

	public Set<SNP> getSNPs() { return new LinkedHashSet<SNP>(snps); }

	public AlleleStatistics getStatistics(SNP snp) {
		if (snp == null)
			throw new NullPointerException("snp");
		DefaultAlleleStatistics statistics = new DefaultAlleleStatistics();
		for (Iterator<Sample> it = samples.values().iterator(); it.hasNext(); ) {
			Sample sample = it.next();
			if (sample.existsGenotype(snp)) {
				Sample.Genotype genotype = sample.getGenotype(snp);
				statistics.addAllele(genotype.getAllele1());
				statistics.addAllele(genotype.getAllele2());
			}
			else { // LOOK!!
				statistics.addAllele(null);
				statistics.addAllele(null);
			}
		}
		return statistics;
	}

	public String toString() { return name + " " + samples.size() + " " + snps.size(); }
}
