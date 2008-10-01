package edu.uab.ssg.model.snp;

/**
 * @author Jelai Wang
 */

/* package private */ final class DefaultChromosome implements Chromosome {
	private String name;

	/* package private */ DefaultChromosome(String name) {
		if (name == null)
			throw new NullPointerException("name");
		this.name = name;	
	}

	/**
	 * Returns true if the object argument is a chromosome of the same name.
	 */
	public boolean equals(Object o) {
		if (o instanceof DefaultChromosome) {
			DefaultChromosome tmp = (DefaultChromosome) o;
			return tmp.name.equals(name);
		}
		return false;
	}

	public int hashCode() {
		int tmp = 17;
		tmp = 37 * tmp + name.hashCode();
		return tmp;
	}

	public String getName() { 
		return name; 
	}

	public String toString() {
		return name;
	}
}
