package edu.uab.ssg.io.haploview;

import edu.uab.ssg.model.snp.*;
import java.util.*;
import java.io.*;

/**
 * A writer for the Haploview marker information input file format described in the <a href="http://www.broadinstitute.org/science/programs/medical-and-population-genetics/haploview/input-file-formats-0">Input File Formats section</a> of the Files chapter of the <a href="http://www.broadinstitute.org/haploview/user-manual">online user manual</a>.
 *
 * <p><tt>
 * marker01 190299<br/>
 * marker02 190950<br/>
 * marker03 191287<br/>
 * </tt></p>
 *
 * Also see the <a href="http://www.broadinstitute.org/ftp/pub/mpg/haploview/sample.info">sample.info</a> file in the Supplemental Examples section of the <a href="http://www.broadinstitute.org/haploview/haploview-downloads">Downloads page</a>.
 *
 * <p>This implementation uses the tab character as the field delimiter and 
 * Unix-style line ending.</p>
 *
 * @author Jelai Wang
 */
public final class MarkerInfoWriter {
	private static final char DELIMITER = '\t';
	private static final char EOL = '\n';

	/**
	 * Constructs the writer.
	 */
	public MarkerInfoWriter() {
	}

	/**
	 * Writes the SNPs to the output stream in the Haploview marker information file format.
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
			builder.append(snp.getName());
			builder.append(DELIMITER).append(snp.getPosition());
			builder.append(EOL);
			writer.write(builder.toString());
		}
		writer.flush();
		writer.close();
	}
}
