package edu.uab.ssg.io.hapgen;

import edu.uab.ssg.model.snp.*;
import java.util.*;
import java.io.*;

/**
 * @author Jelai Wang
 */
public final class HAPSWriter {
	private static final char DELIMITER = ' ';
	private static final char MISSING = '-';
	private static final String EOL = "\n";

	public HAPSWriter() {
	}

	public void write(Set<Sample> samples, Legend legend, OutputStream out) throws IOException {
		if (samples == null)
			throw new NullPointerException("samples");
		if (legend == null)
			throw new NullPointerException("legend");
		if (out == null)
			throw new NullPointerException("out");
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
		List<SNP> snps = legend.getSNPs();
		for (Iterator<Sample> it1 = samples.iterator(); it1.hasNext(); ) {
			Sample sample = it1.next();
			StringBuilder haplotype1 = new StringBuilder();
			StringBuilder haplotype2 = new StringBuilder();

			for (Iterator<SNP> it2 = snps.iterator(); it2.hasNext(); ) {
				SNP snp = it2.next();
				if (sample.existsGenotype(snp)) {
					Sample.Genotype genotype = sample.getGenotype(snp);
					String a1 = recode(legend, snp, genotype.getAllele1());
					String a2 = recode(legend, snp, genotype.getAllele2());
					haplotype1.append(DELIMITER).append(a1);
					haplotype2.append(DELIMITER).append(a2);
				}
				else {
					haplotype1.append(DELIMITER).append(MISSING);
					haplotype2.append(DELIMITER).append(MISSING);
				}
			}

			writer.write(haplotype1.toString().trim()); writer.write(EOL);
			writer.write(haplotype2.toString().trim()); writer.write(EOL);
		}
		writer.flush();
		writer.close();
	}

	// Recode allele to 0 or 1 using legend.
	private String recode(Legend legend, SNP snp, String allele) {
		if (allele == null)
			return String.valueOf(MISSING);
		else if (legend.getAllele0(snp).equals(allele))
			return "0";
		else if (legend.getAllele1(snp).equals(allele))
			return "1";
		else
			throw new RuntimeException(snp + "\t" + allele);
	}
}
