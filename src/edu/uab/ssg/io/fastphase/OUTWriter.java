package edu.uab.ssg.io.fastphase;

import edu.uab.ssg.model.snp.*;
import java.io.*;
import java.util.*;

/**
 * A writer for the fastPHASE inferred haplotypes output file format.
 *
 * This implementation writes placeholders for the expected sections before BEGIN GENOTYPES, omits the "(internally X)" comment in the sample lines, and will throw an exception for missing values in the user-supplied Sample objects.
 *
 * @author Jelai Wang
 */
public final class OUTWriter {
	private static final char DELIMITER = ' ';
	private static final char EOL = '\n';

	private List<SNP> snps;
	private BufferedWriter writer;
	private boolean isBoilerplateWritten = false;

	/**
	 * Constructs the writer.
	 * @param snps The desired sequence of SNPs in the haplotype.
	 */
	public OUTWriter(List<SNP> snps, OutputStream out) {
		if (snps == null)
			throw new NullPointerException("snps");
		if (out == null)
			throw new NullPointerException("out");
		this.snps = new ArrayList<SNP>(snps);
		this.writer = new BufferedWriter(new OutputStreamWriter(out));
	}

	/**
	 * Closes the writer.
	 */
	public void close() throws IOException {
		writer.write("END GENOTYPES");
		writer.write(EOL);
		writer.flush();
		writer.close();
	}

	/**
	 * Writes the haplotypes for a given sample to the output stream.
	 * @param subpop The subpop label for the sample.
	 */
	public void write(Sample sample, String subpop) throws IOException {
		if (sample == null)
			throw new NullPointerException("sample");
		if (subpop == null)
			throw new NullPointerException("subpop");

		if (!isBoilerplateWritten) {
			StringBuilder builder = new StringBuilder();
			builder.append("BEGIN COMMAND_LINE").append(EOL);
			builder.append("END COMMAND_LINE").append(EOL);
			builder.append(EOL);
			builder.append("BEGIN COMMAND_EXPLAIN").append(EOL);
			builder.append("END COMMAND_EXPLAIN").append(EOL);
			builder.append(EOL);
			builder.append("BEGIN DESCRIBE_TASKS").append(EOL);
			builder.append("END DESCRIBE_TASKS").append(EOL);
			builder.append(EOL);
			builder.append("BEGIN GENOTYPES").append(EOL);
			writer.write(builder.toString());
			isBoilerplateWritten = true;
		}

		StringBuilder sampleLineBuilder = new StringBuilder();
		sampleLineBuilder.append(sample.getName());
		sampleLineBuilder.append(" # subpop. label: ");
		sampleLineBuilder.append(subpop);
		sampleLineBuilder.append(EOL);

		StringBuilder haplotype1LineBuilder = new StringBuilder();
		StringBuilder haplotype2LineBuilder = new StringBuilder();
		for (Iterator<SNP> it = snps.iterator(); it.hasNext(); ) {
			SNP snp = it.next();
			if (sample.existsGenotype(snp)) {
				Sample.Genotype genotype = sample.getGenotype(snp);
				String a1 = genotype.getAllele1();
				String a2 = genotype.getAllele2();
				if (a1 == null || a2 == null) // fastPHASE output is expected to have imputed missing genotype data so haplotypes won't have missing values.
					throw new RuntimeException(genotype.toString());
				// Note this algorithm will result in an extra trailing delimiter that will be trimmed later.
				haplotype1LineBuilder.append(a1).append(DELIMITER); // LOOK!!
				haplotype2LineBuilder.append(a2).append(DELIMITER);
			}
			else { // fastPHASE output is expected to have imputed missing genotype data so haplotypes won't have missing values.
				throw new RuntimeException(sample.getName() + " " + snp.getName());
			}
		}
		writer.write(sampleLineBuilder.toString());
		writer.write(haplotype1LineBuilder.toString().trim());
		writer.write(EOL);
		writer.write(haplotype2LineBuilder.toString().trim());
		writer.write(EOL);
	}
}
