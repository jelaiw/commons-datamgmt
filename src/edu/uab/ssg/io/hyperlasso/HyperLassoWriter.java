package edu.uab.ssg.io.hyperlasso;

import edu.uab.ssg.model.snp.*;
import java.io.*;
import java.util.*;

/**
 * A writer for the HyperLasso DAT file format described in the <a href="http://www.ebi.ac.uk/projects/BARGEN/download/HyperLasso/readme.txt">README</a>.
 *
 * @author Jelai Wang
 */
public final class HyperLassoWriter {
	private static final String FIELD_DELIMITER = "\t";
	private static final String EOL = "\n";
	private static final String MISSING_VALUE = "NA"; // Can also be 9 or -1.

	private List<SNP> snps;
	private Writer writer;
	// Map from SNP to allele of interest (for genotype encoding purposes).
	private Map<SNP, String> snp2allele = new LinkedHashMap<SNP, String>();

	/**
	 * Constructs a writer.
	 */
	public HyperLassoWriter(List<SNP> snps, OutputStream out) {
		if (snps == null)
			throw new NullPointerException("snps");
		if (out == null)
			throw new NullPointerException("out");
		this.snps = new ArrayList<SNP>(snps);
		this.writer = new BufferedWriter(new OutputStreamWriter(out));
	}

	/**
	 * Writes the header (first line, of SNP names) according to the
	 * HyperLasso DAT file format.
	 */
	public void writeHeader() throws IOException {
		StringBuilder builder = new StringBuilder();
		for (Iterator<SNP> it = snps.iterator(); it.hasNext(); ) {
			SNP snp = it.next();
			builder.append(FIELD_DELIMITER).append(snp.getName());
		}
		writer.write(builder.toString().trim()); // LOOK!!
		writer.write(EOL);
	}

	/**
	 * Writes the genotypes for the given sample to the output stream 
	 * in HyperLasso DAT file format.
	 */
	public void write(Sample sample) throws IOException {
		if (sample == null)
			throw new NullPointerException("sample");

		StringBuilder builder = new StringBuilder();
		for (Iterator<SNP> it = snps.iterator(); it.hasNext(); ) {
			SNP snp = it.next();
			if (sample.existsGenotype(snp)) {
				Sample.Genotype genotype = sample.getGenotype(snp);
				String allele = snp2allele.get(snp);
				if (allele == null) {
					allele = genotype.getAllele1();
					snp2allele.put(snp, allele);
				}
				String a1 = genotype.getAllele1();
				String a2 = genotype.getAllele2();
				if (a1 == null && a2 == null) { // Missing data.
					builder.append(FIELD_DELIMITER).append(MISSING_VALUE);
				}
				else {
					// Count the number of occurences of the allele of interest.
					int count = 0;
					if (allele.equals(a1)) count++;
					if (allele.equals(a2)) count++;
					// This count is the genotype coding for the additive model.
					builder.append(FIELD_DELIMITER).append(count);
				}
			}
			else { // Missing data.
				builder.append(FIELD_DELIMITER).append(MISSING_VALUE);
			}
		}
		// The above string building algorithm results in a leading
		// tab that needs to be trimmed before use.
		writer.write(builder.toString().trim());
		writer.write(EOL);
	}

	/**
	 * Closes the writer.
	 */
	public void close() throws IOException {
		writer.flush();
		writer.close();
	}
}
