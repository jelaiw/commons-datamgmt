package edu.uab.ssg.io.hapgen;

import edu.uab.ssg.model.snp.*;
import java.io.*;
import java.util.*;

/**
 * @author Jelai Wang
 */
/* package private */ final class MinorAlleleLegend implements Legend {
	private Map<SNP, AlleleCounter> snp2counter;

	/* package private */ MinorAlleleLegend(Map<SNP, AlleleCounter> snp2counter) {
		if (snp2counter == null)
			throw new NullPointerException("snp2counter");
		this.snp2counter = new LinkedHashMap<SNP, AlleleCounter>(snp2counter);
	}

	public List<SNP> getSNPs() { return new ArrayList<SNP>(snp2counter.keySet()); }

	public String getAllele0(SNP snp) {
		if (snp == null)
			throw new NullPointerException("snp");
		AlleleCounter counter = snp2counter.get(snp);
		Set<String> alleles = counter.getAlleles();
		if (counter != null) {
			if (counter.existsMinorAllele()) { // The major allele is coded as 0.
				alleles.remove(counter.getMinorAllele());
				if (alleles.size() > 1)
					throw new RuntimeException(counter.getAlleles().toString());
				return alleles.iterator().next();
			}
			else if (!counter.existsMinorAllele() && alleles.size() == 2) { // There is no minor allele because the allele counts are equal. Rare, but it happens. See rs11927748 on chr3 in HapMap phase3 samples across all populations.
				Iterator<String> it = alleles.iterator();
				String firstAllele = it.next();
				String secondAllele = it.next();
				if (firstAllele.compareTo(secondAllele) < 0)
					return firstAllele;
				else if (firstAllele.compareTo(secondAllele) > 0)
					return secondAllele;
				else // This should be impossible.
					throw new RuntimeException(alleles.toString());
			}
			else { // Samples are monomorphic at this SNP, we code the observed allele as 0.
				return alleles.iterator().next();
			}
		}
		return null;
	}

	public String getAllele1(SNP snp) {
		if (snp == null)
			throw new NullPointerException("snp");
		AlleleCounter counter = snp2counter.get(snp);
		Set<String> alleles = counter.getAlleles();
		if (counter != null) {
			if (counter.existsMinorAllele()) { // The minor allele is coded as 1.
				return counter.getMinorAllele();
			}
			else if (!counter.existsMinorAllele() && alleles.size() == 2) { // There is no minor allele because the allele counts are equal. Rare, but it happens. See rs11927748 on chr3 in HapMap phase3 samples across all populations.
				Iterator<String> it = alleles.iterator();
				String firstAllele = it.next();
				String secondAllele = it.next();
				if (firstAllele.compareTo(secondAllele) < 0)
					return secondAllele;
				else if (firstAllele.compareTo(secondAllele) > 0)
					return firstAllele;
				else // This should be impossible.
					throw new RuntimeException(alleles.toString());
			}
			else { // Samples are monomorphic at this SNP, there is no available allele to code as 1 so we return null.
				return null;
			}
		}
		return null;
	}

	public void write(OutputStream out) throws IOException {
		if (out == null)
			throw new NullPointerException("out");
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
		writer.write(toString());
		writer.flush();
		writer.close();
	}

	/**
	 * Returns a tabular string representation of this legend.
	 */
	public String toString() {
		char DELIMITER = ' ';
	   	String EOL = "\n";
	   	String MISSING = "-";

		StringBuilder builder = new StringBuilder();
		// Create header.
		builder.append("rs");
		builder.append(DELIMITER).append("position");
		builder.append(DELIMITER).append("a0");
		builder.append(DELIMITER).append("a1");
		builder.append(EOL);
		// Create the rest.
		List<SNP> snps = getSNPs();
		for (Iterator<SNP> it = snps.iterator(); it.hasNext(); ) {
			SNP snp = it.next();
			String a0 = getAllele0(snp);
			if (a0 == null) a0 = MISSING;
			String a1 = getAllele1(snp);
			if (a1 == null) a1 = MISSING;

			builder.append(snp.getName());
			builder.append(DELIMITER).append(snp.getPosition());
			builder.append(DELIMITER).append(a0);
			builder.append(DELIMITER).append(a1);
			builder.append(EOL);
		}
		return builder.toString();
	}
}
