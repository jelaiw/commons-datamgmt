package edu.uab.ssg.io.marchini_gwas;

import edu.uab.ssg.model.snp.*;
import java.util.*;
import java.io.*;

/**
 * A writer for the HAPS file format of the HapGen software at <a href="http://www.stats.ox.ac.uk/~marchini/software/gwas/hapgen.html">http://www.stats.ox.ac.uk/~marchini/software/gwas/hapgen.html</a>. This is also the HapMap phase2 haplotype file format, see the files at <a href="http://www.hapmap.org/downloads/phasing/2006-07_phaseII/all/">http://www.hapmap.org/downloads/phasing/2006-07_phaseII/all/</a>.
 *
 * A simple example of this file format, with six haplotypes at three SNPs, is below:
 * <p><tt>
 * 0 0 0<br/>
 * 0 1 1<br/>
 * 0 0 0<br/>
 * - - -<br/>
 * 0 0 -<br/>
 * 0 1 -<br/>
 * </tt></p>
 *
 * Alleles are recoded to 0 or 1 according to the user-supplied legend. The field delimiter is the space character. Missing data are coded as the hyphen character (dash).
 *
 * @author Jelai Wang
 */
public final class HAPSWriter {
	private static final char DELIMITER = ' ';
	private static final char MISSING = '-';
	private static final String EOL = "\n";

	/**
	 * Constructs the writer.
	 */
	public HAPSWriter() {
	}

	/**
	 * Writes the samples to the output stream in the HAPS file format, recoding alleles to 0 or 1 according to the legend.
	 * @param samples The samples to write to the output stream.
	 * @param legend The legend contains the mapping of alleles at a biallelic SNP to 0 or 1.
	 * @param out This output stream is closed after the write operation completes.
	 */
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
	/* package private */ String recode(Legend legend, SNP snp, String allele) {
		if (allele == null)
			return String.valueOf(MISSING);
		else if (allele.equals(legend.getAllele0(snp)))
			return "0";
		else if (allele.equals(legend.getAllele1(snp)))
			return "1";
		else
			throw new RuntimeException(snp + "\t" + allele);
	}
}
