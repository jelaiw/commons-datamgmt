package edu.uab.ssg.io.limdi;

import java.util.*;
import java.io.*;

/**
 * A parser for the genotype file format used by the Genome Sciences Genotyping and Resequencing Center (GS GRC) to report genotyping results from an Illumina Infinium 1M Duo V3 SNP array. 
 *
 * Here is a short example:
 * <p><tt>
 * [Header]<br/>
 * BSGT Version	3.3.4<br/>
 * Processing Date	2/4/2009 11:30 AM<br/>
 * Content		Human1M-Duov3_B.bpm<br/>
 * Num SNPs	1199187<br/>
 * Total SNPs	1199187<br/>
 * Num Samples	297<br/>
 * Total Samples	320<br/>
 * [Data]<br/>
 * SNP Name	Sample ID	Allele1 - Forward	Allele2 - Forward<br/>
 * 200003	A0001	G	G<br/>
 * 200006	A0001	T	T<br/>
 * 200047	A0001	A	A<br/>
 * ...
 * </tt></p>
 *
 * <p>In this file format, missing alleles are coded with the '-' character (i.e. the hyphen, minus sign) and the data table record delimiter is the tab character.</p>
 *
 * @author Jelai Wang
 */
public final class GenotypeFileParser {
	private static final String MISSING_ALLELE = "-";

	/**
	 * Constructs the parser.
	 */
	public GenotypeFileParser() {
	}

	/**
	 * Parses the input stream for genotype records.
	 * @param in The input stream, typically a file input stream, of the
	 * file to be parsed.
	 * @param listener As the input stream is parsed, each genotype record
	 * is passed to the user-supplied record listener.
	 */
	public void parse(InputStream in, RecordListener listener) throws IOException {
		if (in == null)
			throw new NullPointerException("in");
		if (listener == null)
			throw new NullPointerException("listener");
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = null;
		boolean inDataSection = false;
		while ((line = reader.readLine()) != null) {
			if (!inDataSection && "[Data]".equals(line)) { // Detect the beginning of the data section.
				inDataSection = true;
				reader.readLine(); // Discard the first row of the data section.
			}
			else if (inDataSection) {
				GenotypeRecord record = null;
				try {
					record = new ParsedGenotypeRecord(line);
				}
				catch (RuntimeException e) {
					listener.handleBadRecordFormat(line);
					continue;
				}
				listener.handleParsedRecord(record);
			}
			// Skip all other lines, including the header section.
		}
		reader.close();
	}

	/**
	 * A listener for handling parsed genotype records and problems due to
	 * bad record formatting.
	 */
	public interface RecordListener {
		/**
		 * Handles successfully parsed genotype record.
		 */
		void handleParsedRecord(GenotypeRecord record);

		/**
		 * Handles input that could not be parsed due to a formatting problem.
		 * @param line The line of text that could not be parsed.
		 */
		void handleBadRecordFormat(String line);
	}

	/**
	 * A genotype record.
	 */
	public interface GenotypeRecord {
		/**
		 * Returns the SNP name.
		 */
		String getSNPName();

		/**
		 * Returns the sample identifier.
		 */
		String getSampleID();

		/**
		 * Returns the first allele from the forward strand or null if the data are missing.
		 */
		String getAllele1Forward();

		/**
		 * Returns the second allele from the forward strand or null if the data are missing.
		 */
		String getAllele2Forward();
	}

	private class ParsedGenotypeRecord implements GenotypeRecord {
		private static final String DELIMITER = "\t";
		private String line;
		private String snpName, sampleID;
		private String a1Forward, a2Forward;

		private ParsedGenotypeRecord(String line) {
			if (line == null)
				throw new NullPointerException("line");
			this.line = line;

			StringTokenizer tokenizer = new StringTokenizer(line, DELIMITER);
			if (tokenizer.countTokens() != 4)
				throw new IllegalArgumentException(line);
			this.snpName = tokenizer.nextToken();
			this.sampleID = tokenizer.nextToken();
			this.a1Forward = tokenizer.nextToken();
			if (MISSING_ALLELE.equals(a1Forward)) a1Forward = null;
			this.a2Forward = tokenizer.nextToken();
			if (MISSING_ALLELE.equals(a2Forward)) a2Forward = null;
		}

		public String getSNPName() { return snpName; }
		public String getSampleID() { return sampleID; }
		public String getAllele1Forward() { return a1Forward; }
		public String getAllele2Forward() { return a2Forward; }
		public String toString() { return line; }
	}
}
