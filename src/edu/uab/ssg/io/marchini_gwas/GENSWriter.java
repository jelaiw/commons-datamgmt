package edu.uab.ssg.io.marchini_gwas;

import edu.uab.ssg.model.snp.*;
import java.io.*;
import java.util.*;

/**
 * A writer for the genotype file format described at <a href="http://www.stats.ox.ac.uk/~marchini/software/gwas/file_format.html">http://www.stats.ox.ac.uk/~marchini/software/gwas/file_format.html</a>.
 *
 * @author Jelai Wang
 */
public final class GENSWriter {
	private static final char DELIMITER = ' ';
	private static final char EOL = '\n';

	private List<Sample> samples;
	private BufferedWriter writer;

	/**
	 * Constructs the writer.
	 */
	public GENSWriter(List<Sample> samples, OutputStream out) {
		if (samples == null)
			throw new NullPointerException("samples");
		if (out == null)
			throw new NullPointerException("out");
		this.samples = new ArrayList<Sample>(samples);
		this.writer = new BufferedWriter(new OutputStreamWriter(out));
	}

	/**
	 * Closes the writer.
	 */
	public void close() throws IOException {
		writer.flush();
		writer.close();
	}

	/**
	 * Writes a formatted record for the given SNP to the output stream.
	 */
	public void write(SNP snp) throws IOException {
		if (snp == null)
			throw new NullPointerException("snp");

		// Figure out allele A and B.
		AlleleCounter counter = new AlleleCounter();
		for (Iterator<Sample> it = samples.iterator(); it.hasNext(); ) {
			Sample sample = it.next();
			if (sample.existsGenotype(snp)) {
				Sample.Genotype genotype = sample.getGenotype(snp);
				counter.addAllele(genotype.getAllele1());
				counter.addAllele(genotype.getAllele2());
			}
		}
		Set<String> alleles = counter.getAlleles();
		String alleleA = null, alleleB = null;
		if (alleles.size() == 2 && counter.existsMinorAllele()) { // Assign minor allele to allele B.
			alleleB = counter.getMinorAllele();
			Iterator<String> it = alleles.iterator();
			alleleA  = it.next();
			if (alleleA.equals(alleleB)) {
				alleleA = it.next();
			}
		}
		else if (alleles.size() == 2 && !counter.existsMinorAllele()) { // Assign allele A and B according to the order of alleles return from the iterator.
			Iterator<String> it = alleles.iterator();
			alleleA = it.next();
			alleleB = it.next();
		}
		else if (alleles.size() == 1) { 
			alleleA = alleles.iterator().next();
			alleleB = null;
		}
		else if (alleles.size() > 2) { 
			throw new IllegalArgumentException("CAN'T HANDLE > 2 ALLELES: " + counter.toString());
		}
		else if (alleles.size() == 0) { 
			alleleA = null;
			alleleB = null;
		}
		else {
			throw new RuntimeException("REPORT THIS TO AUTHORS: " + counter.toString());
		}

		StringBuilder builder = new StringBuilder();
		builder.append(snp.getChromosome());
		builder.append(DELIMITER).append(snp.getName());
		builder.append(DELIMITER).append(snp.getPosition());
		builder.append(DELIMITER).append(alleleA);
		builder.append(DELIMITER).append(alleleB);

		// Is the missing genotype coding documented in the specification??
		String MISSING_GENOTYPE = "0" + DELIMITER + "0" + DELIMITER + "0";
		String AA = "1" + DELIMITER + "0" + DELIMITER + "0";
		String AB = "0" + DELIMITER + "1" + DELIMITER + "0";
		String BB = "0" + DELIMITER + "0" + DELIMITER + "1";
		for (Iterator<Sample> it = samples.iterator(); it.hasNext(); ) {
			Sample sample = it.next();
			if (sample.existsGenotype(snp)) {
				Sample.Genotype genotype = sample.getGenotype(snp);
				String allele1 = genotype.getAllele1();
				String allele2 = genotype.getAllele2();

				if (allele1.equals(alleleA) && allele2.equals(alleleA)) { // AA.
					builder.append(DELIMITER).append(AA);
				}
				else if ((allele1.equals(alleleA) && allele2.equals(alleleB)) || (allele1.equals(alleleB) && allele2.equals(alleleA))) { // AB.
					builder.append(DELIMITER).append(AB);
				}
				else if (allele1.equals(alleleB) && allele2.equals(alleleB)) { // BB.
					builder.append(DELIMITER).append(BB);
				}
				else {
					throw new RuntimeException(genotype + " " + alleleA + " " + alleleB);
				}
			}
			else { // Genotype not called.
				builder.append(DELIMITER).append(MISSING_GENOTYPE);
			}
		}
		builder.append(EOL);
		writer.write(builder.toString());
	}
}
