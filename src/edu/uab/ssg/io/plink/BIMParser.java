package edu.uab.ssg.io.plink;

import edu.uab.ssg.model.snp.*;
import java.util.*;
import java.io.*;

/**
 * A parser for the PLINK extended MAP input file format described at <a href="http://pngu.mgh.harvard.edu/~purcell/plink/data.shtml#bed">http://pngu.mgh.harvard.edu/~purcell/plink/data.shtml#bed</a>.
 *
 * <p><tt>
 * chromosome (1-22, X, Y or 0 if unplaced)<br/>
 * rs# or snp identifier<br/>
 * Genetic distance (morgans)<br/>
 * Base-pair position (bp units)<br/>
 * Allele 1<br/>
 * Allele 2<br/>
 * </tt></p>
 *
 * The <tt>test.bim</tt> file from the PLINK binary PED documentation at <a href="http://pngu.mgh.harvard.edu/~purcell/plink/binary.shtml">http://pngu.mgh.harvard.edu/~purcell/plink/binary.shtml</a> is shown below as an example.
 *
 * <p><tt>
 * 1 snp1 0 1 G A<br/>
 * 1 snp2 0 2 1 2<br/>
 * 1 snp3 0 3 A C<br/>
 * </tt></p>
 *
 * <p>This implementation expects the tab character as the field delimiter and
 * a Unix- or Windows-style line ending.</p>
 *
 * @author Jelai Wang
 */
public final class BIMParser {
	private static final String DELIMITER = "\t";

	/**
	 * Constructs the parser.
	 */
	public BIMParser() {
	}

	/**
	 * Parses the given input stream for SNP records in PLINK extended MAP file format.
	 */
	public List<SNP> parse(InputStream in, BadRecordFormatListener listener) throws IOException {
		if (in == null)
			throw new NullPointerException("in");
		if (listener == null)
			throw new NullPointerException("listener");
		List<SNP> list = new ArrayList<SNP>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = null;
		while ((line = reader.readLine()) != null) {
			String[] tokens = line.split(DELIMITER, -1);
			if (tokens.length != 6) {
				listener.handleBadRecordFormat(line);
				continue;
			}
			String chr = tokens[0];
			String name = tokens[1];
			// Skip genetic distance field at tokens[2].
			int pos = Integer.parseInt(tokens[3]);
			// Skip allele 1 field at tokens[4].
			// Skip allele 2 field at tokens[5].
			list.add(new DefaultSNP(name, chr, pos));
		}
		reader.close();
		return list;
	}

	/**
	 * A listener for handling problems due to bad record formatting.
	 */
	public static interface BadRecordFormatListener {
		/**
		 * Handles input that could not be parsed due to a formatting problem.
		 */
		void handleBadRecordFormat(String record);
	}
}
