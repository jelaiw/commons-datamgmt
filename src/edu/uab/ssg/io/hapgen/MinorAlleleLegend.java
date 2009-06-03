package edu.uab.ssg.io.hapgen;

import edu.uab.ssg.model.snp.*;
import java.io.*;
import java.util.*;

/**
 * A Legend implementation that recodes to allele 0 or 1 based on the minor allele at a SNP in samples provided by the client programmer.
 *
 * @author Jelai Wang
 */
public final class MinorAlleleLegend implements Legend {
	private Map<SNP, AlleleCounter> snp2counter = new LinkedHashMap<SNP, AlleleCounter>();

	/**
	 * Constructs the legend.
	 * @param snps The given samples will be queried at the SNPs provided in
	 * this list to establish how the alleles (usually in nucleotide bases)
	 * are recoded as allele 0 or 1.
	 * @param samples The set of samples of interest in the study population.
	 */
	public MinorAlleleLegend(List<SNP> snps, Set<Sample> samples) {
		if (snps == null)
			throw new NullPointerException("snps");
		if (samples == null)
			throw new NullPointerException("samples");
		// Figure out the minor allele for the SNPs of interest
		// amongst the given samples; this information will be
		// used to establish allele0 and allele1.
		for (Iterator<SNP> it1 = snps.iterator(); it1.hasNext(); ) {
			SNP snp = it1.next();
			AlleleCounter counter = new AlleleCounter();

			for (Iterator<Sample> it2 = samples.iterator(); it2.hasNext(); ) {
				Sample sample = it2.next();
				if (sample.existsGenotype(snp)) {
					Sample.Genotype genotype = sample.getGenotype(snp);
					counter.addAllele(genotype.getAllele1());
					counter.addAllele(genotype.getAllele2());
				}
			}
			// The 0 and 1 allele encodings are only defined for biallelic SNPs.
			Set<String> alleles = counter.getAlleles();
			if (alleles.size() > 0 && alleles.size() <= 2) {
				snp2counter.put(snp, counter);
			}
			else if (alleles.size() > 2) { // We don't handle this, but log it.
				System.err.println(alleles);
			}
		}
	}

	public List<SNP> getSNPs() { return new ArrayList<SNP>(snp2counter.keySet()); }

	public String getAllele0(SNP snp) {
		if (snp == null)
			throw new NullPointerException("snp");
		AlleleCounter counter = snp2counter.get(snp);
		if (counter != null) {
			if (counter.existsMinorAllele()) { // The major allele is coded as 0.
				Set<String> alleles = counter.getAlleles();
				alleles.remove(counter.getMinorAllele());
				if (alleles.size() > 1)
					throw new RuntimeException(counter.getAlleles().toString());
				return alleles.iterator().next();
			}
			else { // Samples are monomorphic at this SNP, we code the observed allele as 0.
				Set<String> alleles = counter.getAlleles();
				return alleles.iterator().next();
			}
		}
		return null;
	}

	public String getAllele1(SNP snp) {
		if (snp == null)
			throw new NullPointerException("snp");
		AlleleCounter counter = snp2counter.get(snp);
		if (counter != null) {
			if (counter.existsMinorAllele()) { // The minor allele is coded as 1.
				return counter.getMinorAllele();
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
