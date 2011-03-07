package edu.uab.ssg.io.ucsc;

import java.util.*;
import java.io.*;

/**
 * A parser for the UCSC SNP annotation table format, see an example at <a href="http://hgdownload.cse.ucsc.edu/goldenPath/hg19/database/snp131.txt.gz">http://hgdownload.cse.ucsc.edu/goldenPath/hg19/database/snp131.txt.gz</a>, the SQL data definition for this example table is available at <a href="http://hgdownload.cse.ucsc.edu/goldenPath/hg19/database/snp131.sql">http://hgdownload.cse.ucsc.edu/goldenPath/hg19/database/snp131.sql</a>.
 *
 * @author Jelai Wang
 */
public final class SNPAnnotationTableParser {
	private static final String DELIMITER = "\t";

	/**
	 * Constructs the parser.
	 */
	public SNPAnnotationTableParser() {
	}
	
	/**
	 * Parses the input stream for SNP annotation records.
	 * @param in The input stream, typically a file input stream, of the
	 * file to be parsed.
	 * @param listener As the input stream is parsed, each SNP annotation record
	 * is passed to the user-supplied record listener.
	 */
public void parse(InputStream in, RecordListener listener) throws IOException {
		if (in == null)
			throw new NullPointerException("in");
		if (listener == null)
			throw new NullPointerException("listener");
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));

		String line = null;
		while ((line = reader.readLine()) != null) {
			SNPRecord record = null;
			try {
				record = new ParsedSNPRecord(line);
			}
			catch (RuntimeException e) {
				listener.handleBadRecordFormat(line);
				continue;
			}
			listener.handleParsedRecord(record);
		}
	}

	/**
	 * A listener for handling parsed SNP records and problems due to
	 * bad record formatting.
	 */
	public interface RecordListener {
		/**
		 * Handles successfully parsed SNP record.
		 */
		void handleParsedRecord(SNPRecord record);

		/**
		 * Handles input that could not be parsed due to a formatting problem.
		 * @param line The line of text that could not be parsed.
		 */
		void handleBadRecordFormat(String line);
	}

	/**
	 * A SNP annotation record.
	 */
	public interface SNPRecord {
		String getChrom();
		int getChromStart();
		int getChromEnd();
		String getName();
		String getStrand();
		String getObserved();
	}

	private class ParsedSNPRecord implements SNPRecord {
		private String line;
		private String chrom;
		private int chromStart, chromEnd;
		private String name;
		private String strand, observed;

		private ParsedSNPRecord(String line) {
			if (line == null)
				throw new NullPointerException("line");
			this.line = line;

			String[] tokens = line.split(DELIMITER, -1);
			if (tokens.length != 18)
				throw new IllegalArgumentException(String.valueOf(tokens.length) + ", LINE=" + line);

			// Skip bin.
			this.chrom = tokens[1];
			this.chromStart = Integer.parseInt(tokens[2]);
			this.chromEnd = Integer.parseInt(tokens[3]);
			this.name = tokens[4];
			// Skip score.
			this.strand = tokens[6];
			// Skip refNCBI.
			// Skip refUCSC.
			this.observed = tokens[9];
		}

		public String getChrom() { return chrom; }
		public int getChromStart() { return chromStart; }
		public int getChromEnd() { return chromEnd; }
		public String getName() { return name; }
		public String getStrand() { return strand; }
		public String getObserved() { return observed; }

		public String toString() { return line; }
	}
}
