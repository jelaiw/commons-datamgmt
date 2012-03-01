package edu.uab.ssg.model.snp;

import java.util.*;

/**
 * A helper class for counting alleles and reporting their frequencies,
 * the name of the minor allele (if it exists), the number of missing
 * values, as well as whether the marker appears to be biallelic, 
 * monomorphic, or from an ambiguous A/T or G/C SNP.
 *
 * @author Jelai Wang
 */
public final class AlleleCounter {
	private Map<String, int[]> map = new HashMap<String, int[]>();
	private int missingCount = 0;

	/**
	 * Constructs an allele counter.
	 */
	public AlleleCounter() {
	}

	/**
	 * Adds an allele to the count.
	 */
	public void addAllele(String allele) {
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
	 * Returns true if the given allele has been counted at least once.
	 */
	public boolean existsAllele(String allele) {
		if (allele == null)
			throw new NullPointerException("allele");
		return map.containsKey(allele);
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
	 * Returns the number of missing allele values.
	 * A value is missing if a sample from the study population was
	 * assessed for genotype at a particular SNP, but, for whatever reason,
	 * the allele(s) could not be determined.
	 */
	public int getNumberOfMissingValues() { return missingCount; }

	/**
	 * Returns the set of counted allele values.
	 * An allele value is counted when the <code>addAllele</code> method is called.
	 */
	public Set<String> getAlleles() { return new HashSet<String>(map.keySet()); }

	/**
	 * Returns the number of counted, non-missing allele values.
	 */
	public int getNumberOfCountedValues() {
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
		return (double) getFrequency(allele) / (double) getNumberOfCountedValues();
	}

	/**
	 * Returns true if counted alleles indicate an ambiguous A/T or G/C SNP.
	 */
	public boolean isAmbiguous() {
		if (isBiallelic()) {
			Set<String> alleles = getAlleles();
			return (alleles.contains("A") && alleles.contains("T")) || (alleles.contains("C") && alleles.contains("G"));
		}
		return false;
	}

	/**
	 * Returns true if the marker is bi-allelic.
	 */
	public boolean isBiallelic() {
		return getAlleles().size() == 2;
	}

	/**
	 * Returns true if the marker is monomorphic.
	 */
	public boolean isMonomorphic() {
		return getAlleles().size() == 1;
	}

	/**
	 * Returns a string representation of this <code>AlleleCounter</code> object.
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		String DELIMITER = "\t", EOL = "\n";
		Set alleles = getAlleles();
		for (Iterator it = alleles.iterator(); it.hasNext(); ) {
			String allele = (String) it.next();
			buffer.append(allele).append(DELIMITER).append(getFrequency(allele)).append(DELIMITER).append(getRelativeFrequency(allele)).append(EOL);
		}
		buffer.append("Number of missing allele values: " + getNumberOfMissingValues()).append(EOL);
		buffer.append("Number of counted allele values: " + getNumberOfCountedValues()).append(EOL);
		if (existsMinorAllele())
			buffer.append("Minor allele (MA): " + getMinorAllele());
		else	
			buffer.append("No minor allele.");
		if (isAmbiguous())
			buffer.append("This is an ambiguous SNP marker.");
		if (isBiallelic())
			buffer.append("This is a bi-allelic marker.");
		if (isMonomorphic())
			buffer.append("This is a monomorphic marker.");
		return buffer.toString();
	}
}
