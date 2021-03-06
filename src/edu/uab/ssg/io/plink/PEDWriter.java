package edu.uab.ssg.io.plink;

import edu.uab.ssg.model.snp.*;
import java.io.*;
import java.util.*;

/**
 * A writer for the PLINK PED input file format described in <a href="http://pngu.mgh.harvard.edu/~purcell/plink/data.shtml">the basic usage/data formats section</a> of the online documentation.
 *
 * The first six columns are:
 * <p><tt>
 * Family ID<br/>
 * Individual ID<br/>
 * Paternal ID<br/>
 * Maternal ID<br/>
 * Sex (1=male; 2=female; other=unknown)<br/>
 * Phenotype<br/>
 * </tt></p>
 *
 * Additional columns are for specifying genotypes. The test.ped file from
 * the PLINK binary distribution is included below as an example.
 *
 * <p><tt>
 * 1 1 0 0 1  1  A A  G T<br/>
 * 2 1 0 0 1  1  A C  T G<br/>
 * 3 1 0 0 1  1  C C  G G<br/>
 * 4 1 0 0 1  2  A C  T T<br/>
 * 5 1 0 0 1  2  C C  G T<br/>
 * 6 1 0 0 1  2  C C  T T<br/>
 * </tt></p>
 *
 * Also see the hapmap1.ped file in the PLINK tutorial <a href="http://pngu.mgh.harvard.edu/~purcell/plink/hapmap1.zip">example data archive</a>.
 * 
 * <p>This implementation uses the tab character as the field delimiter, the
 * zero character to represent missing values, and the Unix-style line ending.
 * It also encodes the sex and phenotype fields as missing values to encourage
 * the use of PLINK phenotype and covariate input files.</p>
 *
 * @author Jelai Wang
 */
public final class PEDWriter {
	private static final char DELIMITER = '\t';
	private static final char MISSING_VALUE = '0';
	private static final char EOL = '\n';

	private List<SNP> snps;
	private BufferedWriter writer;

	/**
	 * Constructs the writer.
	 */
	public PEDWriter(List<SNP> snps, OutputStream out) {
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
		writer.flush();
		writer.close();
	}

	/**
	 * Writes the genotypes for the given sample to the output stream 
	 * in PLINK PED format.
	 */
	public void write(Sample sample) throws IOException {
		if (sample == null)
			throw new NullPointerException("sample");

		String sampleName = sample.getName();
		StringBuilder builder = new StringBuilder();
		// See the hapmap1.ped file in the example presented in the PLINK tutorial at http://pngu.mgh.harvard.edu/~purcell/plink/hapmap1.zip.
		builder.append(sampleName); // Family ID
		builder.append(DELIMITER).append(1); // Individual ID
		builder.append(DELIMITER).append(MISSING_VALUE); // Paternal ID
		builder.append(DELIMITER).append(MISSING_VALUE); // Maternal ID
		builder.append(DELIMITER).append(MISSING_VALUE); // Sex
		builder.append(DELIMITER).append(MISSING_VALUE); // Phenotype

		for (Iterator<SNP> it = snps.iterator(); it.hasNext(); ) {
			SNP snp = it.next();
			if (sample.existsGenotype(snp)) {
				Sample.Genotype genotype = sample.getGenotype(snp);
				String a1 = genotype.getAllele1();
				String a2 = genotype.getAllele2();
				if (a1 != null)
					builder.append(DELIMITER).append(a1);
				else
					builder.append(DELIMITER).append(MISSING_VALUE);
				if (a2 != null)
					builder.append(DELIMITER).append(a2);
				else
					builder.append(DELIMITER).append(MISSING_VALUE);
			}
			else { // The genotype was not assessed for this sample.
				builder.append(DELIMITER).append(MISSING_VALUE);
				builder.append(DELIMITER).append(MISSING_VALUE);
			}
		}
		builder.append(EOL);
		writer.write(builder.toString());
	}
}
