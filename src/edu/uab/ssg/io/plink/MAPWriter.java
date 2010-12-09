package edu.uab.ssg.io.plink;

import edu.uab.ssg.model.snp.*;
import java.util.*;
import java.io.*;

/**
 * A writer for the PLINK MAP input file format described in <a href="http://pngu.mgh.harvard.edu/~purcell/plink/data.shtml">the basic usage/data formats section</a> of the online documentation.
 *
 * <p><tt>
 * chromosome (1-22, X, Y or 0 if unplaced)<br/>
 * rs# or snp identifier<br/>
 * Genetic distance (morgans)<br/>
 * Base-pair position (bp units)<br/>
 * </tt></p>
 *
 * The test.map file from the PLINK binary distribution is included below
 * as an example.
 *
 * <p><tt>
 * 1 snp1 0 1<br/>
 * 1 snp2 0 2<br/>
 * </tt></p>
 *
 * Also see the hapmap1.map file in the PLINK tutorial <a href="http://pngu.mgh.harvard.edu/~purcell/plink/hapmap1.zip">example data archive</a>.
 *
 * <p>This implementation uses the tab character as the field delimiter and 
 * Unix-style line ending.</p>
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
			builder.append(snp.getChromosome());
			builder.append(DELIMITER).append(snp.getName());
			builder.append(DELIMITER).append("0"); // LOOK!!
			builder.append(DELIMITER).append(snp.getPosition());
			builder.append(EOL);
			writer.write(builder.toString());
		}
		writer.flush();
		writer.close();
	}
}
