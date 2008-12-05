package edu.uab.ssg.model.snp;

import java.util.*;

/**
 * Information about the alleles at a particular SNP, including names, 
 * frequencies, and number of missing values.
 *
 * @author Jelai Wang
 */

/* package private */ final class DefaultAlleleInfo {
	private Map<String, int[]> map = new HashMap<String, int[]>();
	private int missingCount = 0;

	/* package private */ DefaultAlleleInfo() {
	}

	/* package private */ void addAllele(String allele) {
		if (allele == null) {
			missingCount++;
			return;
		}
		int[] frequency = (int[]) map.get(allele);
		if (frequency == null) {
			frequency = new int[1];
			frequency[0] = 0;
			map.put(allele, frequency);
		}
		frequency[0]++;
	}

	/**
	 * Returns true if the minor allele exists.
	 * The minor allele is the less frequent allele and is defined only if 
	 * there is more than one allele at this SNP in the study population.
	 */
	public boolean existsMinorAllele() {
		if (map.size() > 1) {
			// This can probably be simplified by a more clever programmer.
			int minimum = Integer.MAX_VALUE;
			for (Iterator<String> it = map.keySet().iterator(); it.hasNext(); ) {
				String allele = it.next();
				int frequency = getFrequency(allele);
				if (frequency < minimum) {
					minimum = frequency;
				}
				else if (frequency == minimum && frequency != Integer.MAX_VALUE) {
					return false;
				}
			}
			return true;
		}
		return false;	
	}

	/**
	 * Returns the minor allele.
	 * If the minor allele does not exist, a runtime exception is thrown.
	 */
	public String getMinorAllele() {
		if (!existsMinorAllele())
			throw new IllegalStateException();
		int minimum = Integer.MAX_VALUE;
		String minorAllele = null;
		for (Iterator<String> it = map.keySet().iterator(); it.hasNext(); ) {
			String allele = it.next();
			int frequency = getFrequency(allele);
			if (frequency < minimum) {
				minimum = frequency;
				minorAllele = allele;
			}
		}
		return minorAllele;
	}

	/**
	 * Returns the number of missing (allele) values.
	 * A value is missing if a sample from the study population was
	 * assessed for genotype at a particular SNP, but, for whatever reason,
	 * the allele(s) could not be determined.
	 */
	public int getNumberOfMissingValues() { return missingCount; }

	/**
	 * Returns the alleles.
	 */
	public Set<String> getAlleles() { return new HashSet<String>(map.keySet()); }

	/* package private */ int getTotalNumberOfAlleles() {
		int total = 0;
		for (Iterator<String> it = getAlleles().iterator(); it.hasNext(); ) {
			String allele = it.next();
			total += getFrequency(allele);
		}
		return total;
	}

	/**
	 * Returns the frequency, or count, of a particular allele within
	 * the study population.
	 */
	public int getFrequency(String allele) {
		if (allele == null)
			throw new IllegalArgumentException("allele");
		int[] frequency = map.get(allele);
		if (frequency == null)
			throw new IllegalArgumentException();
		if (frequency[0] < 1)
			throw new IllegalStateException();
		return frequency[0];
	}

	/**
	 * Returns the relative frequency of a particular allele within
	 * the study population.
	 */
	public double getRelativeFrequency(String allele) {
		if (allele == null)
			throw new NullPointerException("allele");
		return (double) getFrequency(allele) / (double) getTotalNumberOfAlleles();
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		Set alleles = getAlleles();
		for (Iterator it = alleles.iterator(); it.hasNext(); ) {
			String allele = (String) it.next();
			buffer.append(allele).append('\t').append(getFrequency(allele)).append('\t').append(getRelativeFrequency(allele)).append('\n');
		}
		buffer.append("Number of missing values: " + getNumberOfMissingValues()).append('\n');
		buffer.append("Total number of alleles: " + getTotalNumberOfAlleles()).append('\n');
		if (existsMinorAllele())
			buffer.append("Minor allele (MA): " + getMinorAllele());
		else	
			buffer.append("No minor allele.");
		return buffer.toString();
	}
}
