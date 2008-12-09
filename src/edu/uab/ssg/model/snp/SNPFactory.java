package edu.uab.ssg.model.snp;

/**
 * A factory for objects in the SNP model.
 *
 * @author Jelai Wang
 */
public final class SNPFactory {
	private SNPFactory() {
	}

	/**
	 * Constructs a chromosome given its name.
	 */
	public static Chromosome createChromosome(String name) {
		if (name == null)
			throw new NullPointerException("name");
		return new DefaultChromosome(name);
	}

	/**
	 * Creates a SNP given its name, the chromosome where it is located, 
	 * and its position on the chromosome.
	 */
	public static SNP createSNP(String name, Chromosome chromosome, int position) {
		if (name == null)
			throw new NullPointerException("name");
		if (chromosome == null)
			throw new NullPointerException("chromosome");
		if (position < 0)
			throw new IllegalArgumentException(String.valueOf(position));
		return new DefaultSNP(name, chromosome, position);
	}

	/**
	 * Creates a SNP given its name, the chromosome where it is located, 
	 * and its position on the chromosome.
	 * This factory method may be slightly more convenient for some
	 * client programmers.
	 */
	public static SNP createSNP(String name, String chromosomeName, int position) {
		return createSNP(name, createChromosome(chromosomeName), position);
	}
}
