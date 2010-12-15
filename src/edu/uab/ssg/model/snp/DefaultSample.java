package edu.uab.ssg.model.snp;

import java.util.*;

/**
 * @author Jelai Wang
 */

/* package private */ final class DefaultSample implements Sample {
	private static final Map<SNP, List<Sample.Genotype>> genotypeCache = new LinkedHashMap<SNP, List<Sample.Genotype>>();

	private String name;
	private Map<SNP, Sample.Genotype> genotypes = new LinkedHashMap<SNP, Sample.Genotype>();

	/* package private */ DefaultSample(String name) {
		if (name == null)
			throw new NullPointerException("name");
		this.name = name;
	}

	public String getName() { return name; }
	public String toString() { return name; }

	public boolean existsGenotype(SNP snp) {
		if (snp == null)
			throw new NullPointerException("snp");
		return genotypes.containsKey(snp);	
	}

	public Sample.Genotype getGenotype(SNP snp) {
		if (snp == null)
			throw new NullPointerException("snp");
		if (!existsGenotype(snp))
			throw new IllegalArgumentException(snp.getName());
		return (Sample.Genotype) genotypes.get(snp);	
	}

	/* package private */ Set<SNP> getSNPs() {
		return new LinkedHashSet<SNP>(genotypes.keySet());
	}

	/* package private */ void setGenotype(SNP snp, String a1, String a2, Strand strand) {
		if (snp == null)
			throw new NullPointerException("snp");
		if (strand == null)
			throw new NullPointerException("strand");
		if ((a1 == null && a2 != null) || (a1 != null && a2 == null))
			throw new IllegalArgumentException("half-null unsupported");
		genotypes.put(snp, getGenotype(snp, a1, a2, strand));
	}

	/* package private */ Sample.Genotype getGenotype(SNP snp, String a1, String a2, Strand strand) {
		if (snp == null)
			throw new NullPointerException("snp");
		List<Sample.Genotype> list = genotypeCache.get(snp);
		if (list == null) {
			list = new ArrayList<Sample.Genotype>();
			genotypeCache.put(snp, list);
		}
		Sample.Genotype genotype = new DefaultGenotype(snp, a1, a2, strand);
		if (!list.contains(genotype)) {
			list.add(genotype); // Cache this genotype for later.
			return genotype;
		}
		else { // Return cached copy of this genotype.
			return list.get(list.indexOf(genotype));
		}
	}
}
