package edu.uab.ssg.io.plink;

import edu.uab.ssg.model.snp.*;
import java.util.*;
import java.io.*;

/**
 * A writer for the PLINK MAP input file format described at <a href="http://pngu.mgh.harvard.edu/~purcell/plink/data.shtml">http://pngu.mgh.harvard.edu/~purcell/plink/data.shtml</a>.
 * This implementation uses the tab character as the field delimiter and 
 * Unix-style line ending.
 *
 * @author Jelai Wang
 */
public final class MAPWriter {
	private static final char DELIMITER = '\t';
	private static final char EOL = '\n';

	/**
	 * Constructs the writer.
	 */
	public MAPWriter() {
	}

	/**
	 * Writes the SNPs to the output stream in PLINK MAP file format.
	 * @param out The output stream. This stream is closed.
	 */
	public void write(List<SNP> snps, OutputStream out) throws IOException {
		if (snps == null)
			throw new NullPointerException("snps");
		if (out == null)
			throw new NullPointerException("out");
		
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
		for (Iterator<SNP> it = snps.iterator(); it.hasNext(); ) {
			SNP snp = it.next();
			StringBuilder builder = new StringBuilder();
			builder.append(snp.getChromosome().getName());
			builder.append(DELIMITER).append(snp.getName());
			builder.append(DELIMITER).append(snp.getPosition());
			builder.append(EOL);
			writer.write(builder.toString());
		}
		writer.flush();
		writer.close();
	}
}
