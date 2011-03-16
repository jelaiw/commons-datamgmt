package edu.uab.ssg.io.ucsc;

import java.util.*;
import java.io.*;

/**
 * A parser for the UCSC SNP annotation format.
 *
 * <p>See example at <a href="http://hgdownload.cse.ucsc.edu/goldenPath/hg19/database/snp131.txt.gz">http://hgdownload.cse.ucsc.edu/goldenPath/hg19/database/snp131.txt.gz</a>. An excerpt is below:</p>
 *
 * <p><tt>
 * 585	chr1	10433	10433	rs56289060	0	+	-	-	-/C	genomic	insertion	unknown	0	0	near-gene-5	between	1<br/>
 * 585	chr1	10491	10492	rs55998931	0	+	C	C	C/T	genomic	single	unknown	0	0	near-gene-5	exact	1<br/>
 * 585	chr1	10518	10519	rs62636508	0	+	G	G	C/G	genomic	single	unknown	0	0	near-gene-5	exact	1<br/>
 * ...<br/>
 * 1037	chrY	59362526	59362527	rs12353930	0	+	C	C	C/G	genomic	single	unknown	0	0	unknown	exact	3<br/>
 * 1037	chrY	59362673	59362674	rs56053134	0	+	G	G	A/G	genomic	single	unknown	0	0	unknown	exact	3<br/>
 * </tt></p>
 *
 * <p>The SQL data definition is available at <a href="http://hgdownload.cse.ucsc.edu/goldenPath/hg19/database/snp131.sql">http://hgdownload.cse.ucsc.edu/goldenPath/hg19/database/snp131.sql</a>.</p>
 *
 * The field delimiter is the tab character.
 *
 * @author Jelai Wang
 */
public final class SNPAnnotationParser {
	private static final String DELIMITER = "\t";

	/**
	 * Constructs the parser.
	 */
	public SNPAnnotationParser() {
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
	 * A listener for handling parsed SNP annotation records and problems due to
	 * bad record formatting.
	 */
	public interface RecordListener {
		/**
		 * Handles successfully parsed SNP annotation record.
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
	 * See SQL data definition for further detail, especially on the enums.
	 */
	public interface SNPRecord {
		/**
		 * Returns the chromosome.
		 */
		String getChrom();

		/**
		 * Returns the start position on the chromosome.
		 */
		int getChromStart();

		/**
		 * Returns the end position on the chromosome.
		 */
		int getChromEnd();

		/**
		 * Returns the rsid.
		 */
		String getName();

		/**
		 * Returns the strand for the observed alleles.
		 */
		String getStrand();

		/**
		 * Returns the observed alleles.
		 */
		String getObserved();

		/**
		 * Returns the molecule type (unknown, genomic, or cDNA).
		 */
		String getMolType();

		/**
		 * Returns the class (single, in-del, ..., insertion, deletion).
		 */
		String getSNPClass();

		/**
		 * Returns the validation strategy (by-cluster, ..., by-1000genomes).
		 */
		String getValid();

		/**
		 * Returns the functional annotation (coding-synon, intron, ..., splice-3, splice-5).
		 */
		String getFunc();

		/**
		 * Returns the "loc" (maybe location or locus) type (range, exact, between, ..., rangeSubstitution, rangeDeletion).
		 */
		String getLocType();
	}

	private class ParsedSNPRecord implements SNPRecord {
		private String line;
		private String chrom;
		private int chromStart, chromEnd;
		private String name;
		private String strand, observed;
		private String molType, snpClass, valid, func, locType;

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
			this.molType = tokens[10];
			this.snpClass = tokens[11];
			this.valid = tokens[12];
			// Skip avHet.
			// Skip avHetSE.
			this.func = tokens[15];
			this.locType = tokens[16];
			// Skip weight.
		}

		public String getChrom() { return chrom; }
		public int getChromStart() { return chromStart; }
		public int getChromEnd() { return chromEnd; }
		public String getName() { return name; }
		public String getStrand() { return strand; }
		public String getObserved() { return observed; }
		public String getMolType() { return molType; }
		public String getSNPClass() { return snpClass; }
		public String getValid() { return valid; }
		public String getFunc() { return func; }
		public String getLocType() { return locType; }

		public String toString() { return line; }
	}
}
