package edu.uab.ssg.io.eigenstrat;

import edu.uab.ssg.model.snp.*;
import java.util.*;
import java.io.*;

/**
 * @author Jelai Wang
 */
public final class GENOWriter {
	private static final char MISSING_VALUE = '9';
	private static final char EOL = '\n';
	private BufferedWriter writer;
	
	public GENOWriter(OutputStream out) {
		if (out == null)
			throw new NullPointerException("out");
		this.writer = new BufferedWriter(new OutputStreamWriter(out));
	}

	// Returns first non-null allele value or null if none is found.
	private String findReferenceAllele(SNP snp, List<Sample> samples) {
		for (Iterator<Sample> it = samples.iterator(); it.hasNext(); ) {
			Sample sample = it.next();
			if (sample.existsGenotype(snp)) {
				Sample.Genotype genotype = sample.getGenotype(snp);
				String a1 = genotype.getAllele1();
				String a2 = genotype.getAllele2();
				if (a1 != null) return a1;
				if (a2 != null) return a2;
			}
		}
		return null;
	}

	public void close() throws IOException {
		writer.flush();
		writer.close();
	}

	public void write(SNP snp, List<Sample> samples) throws IOException {
		if (snp == null)
			throw new NullPointerException("snp");
		if (samples == null)
			throw new NullPointerException("samples");
		if (samples.size() < 1)
			throw new IllegalArgumentException(String.valueOf(samples.size()));

		String referenceAllele = findReferenceAllele(snp, samples);
		if (referenceAllele == null) { // Data are completely missing.
			System.err.println("Data are missing for " + snp.getName() + ".");
			return; 
		}

		StringBuilder builder = new StringBuilder();
		for (Iterator<Sample> it = samples.iterator(); it.hasNext(); ) {
			Sample sample = it.next();
			if (sample.existsGenotype(snp)) {
				Sample.Genotype genotype = sample.getGenotype(snp);
				String a1 = genotype.getAllele1();
				String a2 = genotype.getAllele2();

				if (a1 == null && a2 == null) { // Data are missing.
					builder.append(MISSING_VALUE);
				}
				else if (a1 != null && a2 != null) {
					int count = 0;
					if (referenceAllele.equals(a1)) count++;
					if (referenceAllele.equals(a2)) count++;
					builder.append(count);
				}
				else { // Half-missing. What to do?
					throw new RuntimeException();
				}
			}
			else { // Data are missing.  Likely not assessed for genotype.
				builder.append(MISSING_VALUE);
			}
		}
		builder.append(EOL);
		writer.write(builder.toString());
	}
}
