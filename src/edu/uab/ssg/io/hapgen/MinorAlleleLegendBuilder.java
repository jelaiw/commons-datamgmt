package edu.uab.ssg.io.hapgen;

import edu.uab.ssg.model.snp.*;
import java.util.*;

/**
 * @author Jelai Wang
 */

public final class MinorAlleleLegendBuilder {
	private Map<SNP, AlleleCounter> snp2counter = new LinkedHashMap<SNP, AlleleCounter>();
	private Map<SNP, AlleleCounter> badsnps = new LinkedHashMap<SNP, AlleleCounter>();

	public MinorAlleleLegendBuilder() {
	}

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

	public Legend getInstance() {
		return new MinorAlleleLegend(snp2counter);
	}

	public List<SNP> getBadSNPs() { return new ArrayList<SNP>(badsnps.keySet()); }

	public Set<String> getAllelesForBadSNP(SNP badsnp) {
		if (badsnp == null)
			throw new NullPointerException("badsnp");
		if (!badsnps.containsKey(badsnp))
			throw new IllegalArgumentException(badsnp.toString());
		AlleleCounter counter = badsnps.get(badsnp);
		return counter.getAlleles();
	}
}
