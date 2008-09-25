package edu.uab.ssg.model.snp;

import java.util.Set;

/**
 * @author Jelai Wang
 */

public interface Population {
	String getName();
	Set<Sample> getSamples();
	Sample getSample(String sampleName);
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
