package edu.uab.ssg.io.decode;

import java.util.*;
import java.io.*;

/**
 * A parser for the genotype file format used by deCODE to report genotyping
 * results from a custom Illumina SNP array. Here is a short example:
 *
 * <p><tt>
 * [Header]<br/>
 * BSGT Version    3.2.32<br/>
 * Processing Date 8/27/2008 4:12 PM<br/>
 * Content         iSelect_JHY_273439_A.bpm<br/>
 * Num SNPs        7295<br/>
 * Total SNPs      7800<br/>
 * Num Samples     2306<br/>
 * Total Samples   2306<br/>
 * [Data]<br/>
 * SNP Name        Sample ID       Allele1 - Top   Allele2 - Top   Allele1 - Forward       Allele2 - Forward       GC Score<br/>
 * AICDA-007875    AVA1001 G       G       C       C       0.4495<br/>
 * AICDA-007902    AVA1001 G       G       C       C       0.7680<br/>
 * AICDA-007922    AVA1001 G       G       G       G       0.2848<br/>
 * ...
 * </tt></p>
 *
 * <p>The file format is described in more detail in <a href="doc-files/GenotypeFileParser-1.doc">this MS Word document</a>.
 * Read more about the "TOP/BOT" strand in <a href="doc-files/GenotypeFileParser-1.pdf">this technical note</a> from Illumina.</p>
 *
 * @author Jelai Wang
 */
public final class GenotypeFileParser {
	/**
	 * Constructs the parser.
	 */
	public GenotypeFileParser() {
	}

	/**
	 * Parses the input stream for genotype records.
	 * @param in The input stream, typically a file input stream, of the
	 * genotype file to be parsed.
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
		 * Returns the first allele from the TOP strand.
		 */
		String getAllele1Top();

		/**
		 * Returns the second allele from the TOP strand.
		 */
		String getAllele2Top();

		/**
		 * Returns the first allele from the forward strand.
		 */
		String getAllele1Forward();

		/**
		 * Returns the second allele from the forward strand.
		 */
		String getAllele2Forward();

		/**
		 * Returns the GenCall (GC) score for this SNP and sample.
		 * This score is the product of the GenTrain score and a
		 * data-to-model fit score from Illumina BeadStudio.
		 */
		double getGCScore();
	}

	private class ParsedGenotypeRecord implements GenotypeRecord {
		private String line;
		private String snpName, sampleID;
		private String a1Top, a2Top;
		private String a1Forward, a2Forward;
		private double gcScore;

		private ParsedGenotypeRecord(String line) {
			if (line == null)
				throw new NullPointerException("line");
			this.line = line;

			StringTokenizer tokenizer = new StringTokenizer(line);
			if (tokenizer.countTokens() != 7)
				throw new IllegalArgumentException(line);
			this.snpName = tokenizer.nextToken();
			this.sampleID = tokenizer.nextToken();
			this.a1Top = tokenizer.nextToken();
			this.a2Top = tokenizer.nextToken();
			this.a1Forward = tokenizer.nextToken();
			this.a2Forward = tokenizer.nextToken();
			this.gcScore = Double.parseDouble(tokenizer.nextToken());
		}

		public String getSNPName() { return snpName; }
		public String getSampleID() { return sampleID; }
		public String getAllele1Top() { return a1Top; }
		public String getAllele2Top() { return a2Top; }
		public String getAllele1Forward() { return a1Forward; }
		public String getAllele2Forward() { return a2Forward; }
		public double getGCScore() { return gcScore; }

		public String toString() { return line; }
	}
}
