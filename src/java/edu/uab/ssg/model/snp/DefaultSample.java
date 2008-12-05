package edu.uab.ssg.model.snp;

import java.util.*;

/**
 * @author Jelai Wang
 */

/* package private */ final class DefaultSample implements Sample {
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
		genotypes.put(snp, new DefaultGenotype(snp, a1, a2, strand));
	}

	private static final class DefaultGenotype implements Sample.Genotype {
		private SNP snp;
		private String a1, a2;
		private Strand strand;

		// Missing genotype data is indicated by a null value for a1 or a2.
		private DefaultGenotype(SNP snp, String a1, String a2, Strand strand) {
			if (snp == null)
				throw new NullPointerException("snp");
			this.snp = snp;
			this.a1 = a1;
			this.a2 = a2;
			this.strand = strand;
		}

		public SNP getSNP() { return snp; }
		public String getAllele1() { return a1; }
		public String getAllele2() { return a2; }
		public Strand getStrand() { return strand; }
		public String toString() { return snp.getName() + " " + a1 + " " + a2 + " " + strand; }
	}
}
