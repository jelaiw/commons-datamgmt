package edu.uab.ssg.io.hapgen;

import edu.uab.ssg.model.snp.*;
import java.io.*;
import java.util.*;

/**
 * A Legend implementation that recodes alleles to 0 or 1 based on the haplotype of a reference sample provided by the client programmer.
 *
 * @author Jelai Wang
 */
public final class ReferenceSampleLegend implements Legend {
	private Sample referenceSample;
	private Map<SNP, AlleleCounter> snp2counter = new LinkedHashMap<SNP, AlleleCounter>();

	/**
	 * Constructs the legend.
	 * @param referenceSample A haplotype of the given reference sample will
	 * be used to establish how the alleles (usually in nucleotide bases)
	 * are recoded to 0 or 1.
	 * @param snps The given samples will be queried at the SNPs provided in
	 * this list to create the legend.
	 * @param samples The set of samples of interest in the study population.
	 */
	public ReferenceSampleLegend(Sample referenceSample, List<SNP> snps, Set<Sample> samples) {
		if (referenceSample == null)
			throw new NullPointerException("referenceSample");
		if (snps == null)
			throw new NullPointerException("snps");
		if (samples == null)
			throw new NullPointerException("samples");
		this.referenceSample = referenceSample;

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
				System.err.println(snp.getName() + "\t" + alleles);
			}
		}
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
