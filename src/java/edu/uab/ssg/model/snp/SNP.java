package edu.uab.ssg.model.snp;

/**
 * A single nucleotide polymorphism (SNP).
 *
 * @author Jelai Wang
 */

public interface SNP {
	/**
	 * Return the name of the SNP.
	 */
	String getName();

	/**
	 * Return the chromosome where the SNP is located.
	 */
	Chromosome getChromosome();

	/**
	 * Return the position on the chromosome where the SNP is located.
	 */
	int getPosition();
}
