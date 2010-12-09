package edu.uab.ssg.model.snp;

/**
 * A default SNP implementation that defines equals() and hashCode() in terms of chromosome location, not SNP name.
 *
 * @author Jelai Wang
 */

public final class DefaultSNP implements SNP {
	private String name, chromosome;
	private int position;

	/**
	 * Constructs a SNP.
	 */
	public DefaultSNP(String name, String chromosome, int position) {
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
	public String getChromosome() { return chromosome; }
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

	/**
	 * Returns the hash code.
	 */
	public int hashCode() {
		int tmp = 17;
		tmp = 37 * tmp + chromosome.hashCode();
		tmp = 37 * tmp + position;
		return tmp;
	}

	/**
	 * Returns a string representation.
	 */
	public String toString() { 
		String DELIMITER = " ";
		return name + DELIMITER + chromosome + DELIMITER + position; 
	}
}
