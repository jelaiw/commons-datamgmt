package edu.uab.ssg.model.snp;

/**
 * @author Jelai Wang
 */

/* package private */ final class DefaultSNP implements SNP {
	private String name;
	private Chromosome chromosome;
	private int position;

	/* package private */ DefaultSNP(String name, Chromosome chromosome, int position) {
		if (name == null)
			throw new NullPointerException("name");
		if (chromosome == null)
			throw new NullPointerException("chromosome");
		if (position < 0)
			throw new IllegalArgumentException(String.valueOf(position));
		this.name = name;	
		this.chromosome = chromosome;	
		this.position = position;	
	}

	public String getName() { return name; }
	public Chromosome getChromosome() { return chromosome; }
	public int getPosition() { return position; }

	/**
	 * Returns true if the user-supplied object is a SNP at the same chromosome
	 * position.
	 */
	public boolean equals(Object o) {
		if (o instanceof DefaultSNP) {
			DefaultSNP tmp = (DefaultSNP) o;
			return chromosome.equals(tmp.chromosome) && position == tmp.position;
		}
		return false;
	}

	public int hashCode() {
		int tmp = 17;
		tmp = 37 * tmp + chromosome.hashCode();
		tmp = 37 * tmp + position;
		return tmp;
	}

	public String toString() { return name + " " + chromosome + " " + position; }
}
