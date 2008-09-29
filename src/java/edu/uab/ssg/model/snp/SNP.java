package edu.uab.ssg.model.snp;

/**
 * A single nucleotide polymorphism (SNP).
 *
 * @author Jelai Wang
 */

public interface SNP {
	/**
	 * Returns the name of the SNP.
	 */
	String getName();

	/**
	 * Returns the chromosome where the SNP is located.
	 */
	Chromosome getChromosome();

	/**
	 * Returns the position on the chromosome where the SNP is located.
	 */
	int getPosition();
}
