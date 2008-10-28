package edu.uab.ssg.io.phase;

import edu.uab.ssg.model.snp.*;
import java.io.*;
import java.util.*;

/**
 * @author Jelai Wang
 */
public final class INPWriter {
	private static final String EOL = "\n";
	private static final String MISSING_SNP_ALLELE = "?";

	public void write(Population population, OutputStream out) throws IOException {
		if (population == null)
			throw new NullPointerException("population");
		if (out == null)
			throw new NullPointerException("out");
		Set<Sample> samples = population.getSamples();
		Set<SNP> snps = population.getSNPs();

		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
		// Number of "individuals".
		writer.write(String.valueOf(samples.size()));
		writer.write(EOL);
		// Number of "loci".
		writer.write(String.valueOf(snps.size()));
		writer.write(EOL);
		// Optional line of SNP positions in base-pairs.
		writer.write(createPositionLine(snps));
		writer.write(EOL);
		// Locus types.
		writer.write(createLocusTypeLine(snps));
		writer.write(EOL);

		for (Iterator<Sample> it1 = samples.iterator(); it1.hasNext(); ) {
			Sample sample = it1.next();
			// This algorithm produces an unnecessary leading space
			// that is later trimmed before being written out.
			StringBuilder firstRow = new StringBuilder();
			StringBuilder secondRow = new StringBuilder();
			for (Iterator<SNP> it2 = snps.iterator(); it2.hasNext(); ) {
				SNP snp = it2.next();
				if (sample.existsGenotype(snp)) {
					Sample.Genotype genotype = sample.getGenotype(snp);
					String a1 = genotype.getAllele1();
					String a2 = genotype.getAllele2();
					firstRow.append(' ').append(a1 != null ? a1 : MISSING_SNP_ALLELE);
					secondRow.append(' ').append(a2 != null ? a2 : MISSING_SNP_ALLELE);
				}
				else { // Untyped.
					firstRow.append(' ').append(MISSING_SNP_ALLELE);
					secondRow.append(' ').append(MISSING_SNP_ALLELE);
				}
			}

			// Three lines are written per sample.
			writer.write(sample.getName());
			writer.write(EOL);
			writer.write(firstRow.toString().trim());
			writer.write(EOL);
			writer.write(secondRow.toString().trim());
			writer.write(EOL);
		}

		writer.flush();
		writer.close();
	}

	private String createPositionLine(Set<SNP> snps) {
		StringBuilder builder = new StringBuilder();
		builder.append("P");
		for (Iterator<SNP> it = snps.iterator(); it.hasNext(); ) {
			SNP snp = it.next();
			builder.append(' ').append(snp.getPosition());
		}
		return builder.toString();
	}

	private String createLocusTypeLine(Set<SNP> snps) {
		StringBuilder builder = new StringBuilder();
		for (Iterator<SNP> it = snps.iterator(); it.hasNext(); ) {
			SNP snp = it.next();
			builder.append('S');
		}
		return builder.toString();
	}
}
