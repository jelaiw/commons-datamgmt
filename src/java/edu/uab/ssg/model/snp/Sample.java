package edu.uab.ssg.model.snp;

/**
 * A sample from a study population.
 *
 * @author Jelai Wang
 */

public interface Sample {
	/**
	 * Returns the name of the sample.
	 */
	String getName();

	/**
	 * Returns the study population to which this sample belongs.
	 */
	Population getPopulation();

	/**
	 * Returns true if genotype for the given SNP was assessed in this sample.
	 */
	boolean existsGenotype(SNP snp);

	/**
	 * Returns the genotype of the sample for the given SNP.
	 */
	Genotype getGenotype(SNP snp);

	/**
	 * A genotype for a sample at a particular SNP.
	 */
	interface Genotype {
		/**
		 * Returns the SNP for this genotype.
		 */
		SNP getSNP();

		/**
		 * Returns the first allele of this genotype.
		 */
		String getAllele1();

		/**
		 * Returns the second allele of this genotype.
		 */
		String getAllele2();

		/**
		 * Returns the strand of the alleles of this genotype.
		 */
		Strand getStrand();
	}
}
