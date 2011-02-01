package edu.uab.ssg.io.plink;

import edu.uab.ssg.model.snp.*;
import java.util.*;
import java.io.*;

/**
 * A parser for the PLINK MAP input file format described in the <a href="http://pngu.mgh.harvard.edu/~purcell/plink/data.shtml">basic usage/data formats section</a> of the online documentation.
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
 * <p>This implementation expects the tab character as the field delimiter and
 * a Unix- or Windows-style line ending.</p>
 *
 * @author Jelai Wang
 */
public final class MAPParser {
	private static final String DELIMITER = "\t";

	/**
	 * Constructs the parser.
	 */
	public MAPParser() {
	}

	/**
	 * Parses the given input stream for SNP records in PLINK MAP file format.
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
			if (tokens.length != 4) {
				listener.handleBadRecordFormat(line);
				continue;
			}
			String chr = tokens[0];
			String name = tokens[1];
			// Skip genetic distance field at tokens[2].
			int pos = Integer.parseInt(tokens[3]);
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
