package edu.uab.ssg.io.phase;

import edu.uab.ssg.model.snp.*;
import java.io.*;
import java.util.*;

/**
 * A writer for the PHASE INP input file format.
 *
 * This implementation uses the space character as the field delimiter, the
 * '?' character to represent missing SNP values, and Unix-style line ending.
 *
 * Here is an excerpt from chapter 3 of <a href="doc-files/instruct2.1.pdf">the PHASE version 2.1 manual</a> specifying the file format:
 * <p><tt>
 * NumberOfIndividuals<br/>
 * NumberOfLoci<br/>
 * P Position(1) Position(2) Position(NumberOfLoci)<br/>
 * LocusType(1) LocusType(2) ... LocusType(NumberOfLoci)<br/>
 * ID(1)<br/>
 * Genotype(1)<br/>
 * ID(2)<br/>
 * Genotype(2)<br/>
 * .<br/>
 * .<br/>
 * .<br/>
 * ID(NumberOfIndividuals)]<br/>
 * Genotype(NumberOfIndividuals)<br/>
 * </tt></p>
 *
 * <p>The example <a href="doc-files/test.inp">test.inp</a> file from the PHASE binary package may also be helpful.</p>
 *
 * @author Jelai Wang
 */
public final class INPWriter {
	private static final String EOL = "\n";
	private static final String MISSING_SNP_ALLELE = "?";

	/**
	 * Constructs the writer.
	 */
	public INPWriter() {
	}

	/**
	 * Writes the study population to the output stream in PHASE INP format.
	 */
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
