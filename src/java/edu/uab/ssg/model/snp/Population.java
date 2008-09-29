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

	AlleleStatistics getStatistics(SNP snp);

	interface AlleleStatistics {
		Set<String> getAlleles();
		int getFrequency(String allele);
		double getRelativeFrequency(String allele);
		boolean existsMinorAllele();
		String getMinorAllele();
		int getNumberOfMissingValues();
	}
}
