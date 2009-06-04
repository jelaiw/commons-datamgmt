package edu.uab.ssg.io.hapgen;

import edu.uab.ssg.model.snp.*;
import java.util.*;

/**
 * A builder for Legend implementations.
 *
 * @author Jelai Wang
 */

public final class LegendBuilder {
	private Map<SNP, AlleleCounter> snp2counter = new LinkedHashMap<SNP, AlleleCounter>();
	private Map<SNP, AlleleCounter> badsnps = new LinkedHashMap<SNP, AlleleCounter>();

	/**
	 * Constructs the builder.
	 */
	public LegendBuilder() {
	}

	/**
	 * Counts an allele at a SNP.
	 * The set of alleles observed at a SNP determines how the alleles
	 * are recoded to 0 or 1 in the legend.
	 */
	public void countAllele(SNP snp, String allele) {
		if (snp == null)
			throw new NullPointerException("snp");
		if (allele == null)
			throw new NullPointerException("allele");
		
		if (badsnps.containsKey(snp)) {
			AlleleCounter counter = badsnps.get(snp);
			counter.addAllele(allele);
		}
		else {
			AlleleCounter counter = snp2counter.get(snp);
			if (counter == null) {
				counter = new AlleleCounter();
				snp2counter.put(snp, counter);
			}
			counter.addAllele(allele);
			// Check if this SNP is no longer biallelic.
			if (counter.getAlleles().size() > 2) {
				badsnps.put(snp, counter);
				snp2counter.remove(snp);
			}
		}
	}

	/**
	 * Creates legend that recodes alleles to 0 or 1 based on the minor allele.
	 */
	public Legend createMinorAlleleLegend() {
		return new MinorAlleleLegend(snp2counter);
	}

	/**
	 * Creates legend that recodes alleles to 0 or 1 based on the haplotype of a given reference sample.
	 */
	public Legend createReferenceSampleLegend(Sample referenceSample) {
		return new ReferenceSampleLegend(referenceSample, snp2counter);
	}

	/**
	 * Returns a list of SNPs that are not monomorphic or biallelic.
	 * These SNPs are not part of the legend because the recoding of alleles
	 * to 0 or 1 is only defined for monomorphic or biallelic SNPs.
	 */
	public List<SNP> getBadSNPs() { return new ArrayList<SNP>(badsnps.keySet()); }

	/**
	 * For a SNP that is not monomorphic or biallelic, return the set of observed alleles for troubleshooting purposes.
	 */
	public Set<String> getAllelesForBadSNP(SNP badsnp) {
		if (badsnp == null)
			throw new NullPointerException("badsnp");
		if (!badsnps.containsKey(badsnp))
			throw new IllegalArgumentException(badsnp.toString());
		AlleleCounter counter = badsnps.get(badsnp);
		return counter.getAlleles();
	}
}
