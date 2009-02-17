package edu.uab.ssg.io.illumina;

import java.util.*;
import java.io.*;

/**
 * A parser for the Illumina "SNP_Map.txt" file format.
 *
 * This file format has the following fields: <i>Index, Name, Chromosome,
 * Position, GenTrain Score, SNP, ILMN Strand, Customer Strand, and NormID</i>.
 * Here is an abbreviated example:
 *
 * <p><tt>
 * Index   Name    Chromosome      Position        GenTrain Score  SNP     ILMN Strand     Customer Strand NormID<br/>
 * 1       AICDA-007875    12      8650011 0.5980  [T/C]   BOT     TOP     0<br/>
 * 2       AICDA-007902    12      8649984 0.7706  [T/C]   BOT     TOP     0<br/>
 * 3       AICDA-007922    12      8649964 0.5139  [T/C]   BOT     BOT     0<br/>
 * ...
 * </tt></p>
 *
 * @author Jelai Wang
 */
public final class SNPFileParser {
	/**
	 * Constructs the parser.
	 */
	public SNPFileParser() {
	}

	/**
	 * Parses the input stream for SNP records.
	 * @param in The input stream, typically a file input stream, of the
	 * SNP file to be parsed.
	 * @param listener As the input stream is parsed, each SNP record
	 * is passed to the user-supplied record listener.
	 */
	public void parse(InputStream in, RecordListener listener) throws IOException {
		if (in == null)
			throw new NullPointerException("in");
		if (listener == null)
			throw new NullPointerException("listener");
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String header = reader.readLine(); // Discard first row of column names.
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
		reader.close();
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
	 * A SNP record.
	 */
	public interface SNPRecord {
		/**
		 * Returns the SNP name.
		 */
		String getName();

		/**
		 * Returns the chromosome name.
		 */
		String getChromosome();

		/**
		 * Returns the position of this SNP on the chromosome.
		 */
		int getPosition();

		double getGenTrainScore();

		/**
		 * Returns a text representation of the nucleotides found at this 
		 * SNP position, for example, "[A/G]".
		 */
		String getSNP();

		/**
		 * Returns the Illumina TOP/BOT strand.
		 */
		String getILMNStrand();

		/**
		 * Returns the strand of the allele reported by the customer in 
		 * Illumina TOP/BOT strand notation.
		 */
		String getCustomerStrand();

		String getNormID();
	}

	private class ParsedSNPRecord implements SNPRecord {
		private String line;
		private String name, chromosome;
		private int position;
		private double genTrainScore;
		private String SNP;
		private String ilmnStrand, customerStrand;
		private String normID;

		private ParsedSNPRecord(String line) {
			if (line == null)
				throw new NullPointerException("line");
			this.line = line;

			StringTokenizer tokenizer = new StringTokenizer(line);
			if (tokenizer.countTokens() != 9)
				throw new IllegalArgumentException(line);
			tokenizer.nextToken(); // Discard index field.
			this.name = tokenizer.nextToken();
			this.chromosome = tokenizer.nextToken();
			this.position = Integer.parseInt(tokenizer.nextToken());
			if (position < 1)
				throw new IllegalArgumentException(String.valueOf(position));
			this.genTrainScore = Double.parseDouble(tokenizer.nextToken());
			this.SNP = tokenizer.nextToken();
			this.ilmnStrand = tokenizer.nextToken();
			this.customerStrand = tokenizer.nextToken();
			this.normID = tokenizer.nextToken();
		}

		public String getName() { return name; }
		public String getChromosome() { return chromosome; }
		public int getPosition() { return position; }
		public double getGenTrainScore() { return genTrainScore; }
		public String getSNP() { return SNP; }
		public String getILMNStrand() { return ilmnStrand; }
		public String getCustomerStrand() { return customerStrand; }
		public String getNormID() { return normID; }
		public String toString() { return line; }
	}
}
