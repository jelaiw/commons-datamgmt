package edu.uab.ssg.model.snp;

import java.util.*;

/**
 * @author Jelai Wang
 */

/* package private */ final class DefaultPopulation implements Population {
	private String name;
	private Map<String, DefaultSample> samples = new LinkedHashMap<String, DefaultSample>();

	/* package private */ DefaultPopulation(String name) {
		if (name == null)
			throw new NullPointerException("name");
		this.name = name;	
	}

	/* package private */ void addSample(DefaultSample sample) {
		if (sample == null)
			throw new NullPointerException("sample");
		if (!sample.getPopulation().equals(this))	
			throw new IllegalArgumentException();
		samples.put(sample.getName(), sample);	
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
		return samples.get(sampleName);	
	}

	// Return the union set of all SNPs from all samples.
	public Set<SNP> getSNPs() { 
		Set<SNP> set = new LinkedHashSet<SNP>();
		for (Iterator<DefaultSample> it = samples.values().iterator(); it.hasNext(); ) {
			DefaultSample sample = it.next();
			set.addAll(sample.getSNPs());
		}
		return set;
	}

	public AlleleStatistics getStatistics(SNP snp) {
		if (snp == null)
			throw new NullPointerException("snp");
		DefaultAlleleStatistics statistics = new DefaultAlleleStatistics();
		for (Iterator<DefaultSample> it = samples.values().iterator(); it.hasNext(); ) {
			DefaultSample sample = it.next();
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

	public String toString() { return name + " " + samples.size() + " " + getSNPs().size(); }
}
