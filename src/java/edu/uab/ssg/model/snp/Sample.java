package edu.uab.ssg.model.snp;

/**
 * @author Jelai Wang
 */

public interface Sample {
	String getName();
	Population getPopulation();
	boolean existsGenotype(SNP snp);
	Genotype getGenotype(SNP snp);

	interface Genotype {
		SNP getSNP();
		String getAllele1();
		String getAllele2();
		Strand getStrand();
	}
}
