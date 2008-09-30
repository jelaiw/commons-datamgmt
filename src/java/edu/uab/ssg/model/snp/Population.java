package edu.uab.ssg.model.snp;

import java.util.Set;

/**
 * A study population of samples.
 *
 * @author Jelai Wang
 */

public interface Population {
	/**
	 * Returns the name of the population.
	 */
	String getName();

	/**
	 * Returns the samples that comprise this population.
	 */
	Set<Sample> getSamples();

	/**
	 * Returns a particular sample by name.
	 */
	Sample getSample(String sampleName);

	/**
	 * Returns the SNPs where genotype is available for at least one sample.
	 * In other words, the union set of SNPs with genotypes from all samples.
	 */
	Set<SNP> getSNPs();

	/**
	 * Returns allele info for a particular SNP.
	 */
	AlleleInfo getAlleleInfo(SNP snp);

	/**
	 * Information about the alleles at a particular SNP, including names, 
	 * frequencies, and number of missing values.
	 */
	interface AlleleInfo {
		/**
		 * Returns the alleles.
		 */
		Set<String> getAlleles();

		/**
		 * Returns the frequency, or count, of a particular allele within
		 * the study population.
		 */
		int getFrequency(String allele);

		/**
		 * Returns the relative frequency of a particular allele within
		 * the study population.
		 */
		double getRelativeFrequency(String allele);

		/**
		 * Returns true if the minor allele exists.
		 * The minor allele is the less frequent allele and is defined only if 
		 * there is more than one allele at this SNP in the study population.
		 */
		boolean existsMinorAllele();

		/**
		 * Returns the minor allele.
		 * If the minor allele does not exist, a runtime exception is thrown.
		 */
		String getMinorAllele();

		/**
		 * Returns the number of missing (allele) values.
		 * A value is missing if a sample from the study population was
		 * assessed for genotype at a particular SNP, but, for whatever reason,
		 * the allele(s) could not be determined.
		 */
		int getNumberOfMissingValues();
	}
}
