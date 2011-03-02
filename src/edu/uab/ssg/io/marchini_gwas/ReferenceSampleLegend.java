package edu.uab.ssg.io.marchini_gwas;

import edu.uab.ssg.model.snp.*;
import java.io.*;
import java.util.*;

/**
 * @author Jelai Wang
 */
/* package private */ final class ReferenceSampleLegend implements Legend {
	private Sample referenceSample;
	private Map<SNP, AlleleCounter> snp2counter = new LinkedHashMap<SNP, AlleleCounter>();

	/* package private */ ReferenceSampleLegend(Sample referenceSample, Map<SNP, AlleleCounter> snp2counter) {
		if (referenceSample == null)
			throw new NullPointerException("referenceSample");
		if (snp2counter == null)
			throw new NullPointerException("snp2counter");
		this.referenceSample = referenceSample;
		this.snp2counter = new LinkedHashMap<SNP, AlleleCounter>(snp2counter);
	}

	public List<SNP> getSNPs() { return new ArrayList<SNP>(snp2counter.keySet()); }

	public String getAllele0(SNP snp) {
		if (snp == null)
			throw new NullPointerException("snp");
		AlleleCounter counter = snp2counter.get(snp);
		if (counter != null && referenceSample.existsGenotype(snp)) {
			Sample.Genotype genotype = referenceSample.getGenotype(snp);
			String a1 = genotype.getAllele1();
			Set<String> alleles = counter.getAlleles();
			if (alleles.size() == 2) { // biallelic SNP.
				alleles.remove(a1);
				return alleles.iterator().next();
			}
			else if (alleles.size() == 1) { // monomorphic SNP.
				return null;
			}
			else {
				throw new RuntimeException(counter.getAlleles().toString());
			}
		}
		return null;
	}

	public String getAllele1(SNP snp) {
		if (snp == null)
			throw new NullPointerException("snp");
		if (snp2counter.containsKey(snp) && referenceSample.existsGenotype(snp)) {
			Sample.Genotype genotype = referenceSample.getGenotype(snp);
			// We assume the genotype data are phased and use the first
			// haplotype to establish the reference allele (a1).
			return genotype.getAllele1();
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
