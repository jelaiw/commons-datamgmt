package edu.uab.ssg.io.decode;

import java.util.*;
import java.io.*;

/**
 * A parser for the "JHY_SNP.txt" file format.
 *
 * This file format has the following fields: <i>Name, Chr, Position, ILMN Strand, Customer Strand, and SNP</i>.
 * Here is an abbreviated example:
 *
 * <p><tt>
 * Name    Chr     Position        ILMN Strand     Customer Strand SNP<br/>
 * AICDA-007875    12      8650011 BOT     TOP     [T/C]<br/>
 * AICDA-007902    12      8649984 BOT     TOP     [T/C]<br/>
 * AICDA-007922    12      8649964 BOT     BOT     [T/C]<br/>
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

		String[] header = reader.readLine().split("\t", -1);
		if (!"Name".equals(header[0]))
			throw new IllegalArgumentException(header[0]);
		if (!"Chr".equals(header[1]))
			throw new IllegalArgumentException(header[1]);
		if (!"Position".equals(header[2]))
			throw new IllegalArgumentException(header[2]);
		if (!"ILMN Strand".equals(header[3]))
			throw new IllegalArgumentException(header[3]);
		if (!"Customer Strand".equals(header[4]))
			throw new IllegalArgumentException(header[4]);
		if (!"SNP".equals(header[5]))
			throw new IllegalArgumentException(header[5]);

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
		String getChr();

		/**
		 * Returns the position of this SNP on the chromosome.
		 */
		int getPosition();

		/**
		 * Returns the Illumina TOP/BOT strand.
		 */
		String getILMNStrand();

		/**
		 * Returns the strand of the allele reported by the customer in 
		 * Illumina TOP/BOT strand notation.
		 */
		String getCustomerStrand();

		/**
		 * Returns a text representation of the nucleotides found at this 
		 * SNP position, for example, "[A/G]".
		 */
		String getSNP();
	}

	private class ParsedSNPRecord implements SNPRecord {
		private String line;
		private String name, chromosome;
		private int position;
		private String ilmnStrand, customerStrand;
		private String SNP;

		private ParsedSNPRecord(String line) {
			if (line == null)
				throw new NullPointerException("line");
			this.line = line;

			StringTokenizer tokenizer = new StringTokenizer(line);
			if (tokenizer.countTokens() != 6)
				throw new IllegalArgumentException(line);
			this.name = tokenizer.nextToken();
			this.chromosome = tokenizer.nextToken();
			this.position = Integer.parseInt(tokenizer.nextToken());
			if (position < 1)
				throw new IllegalArgumentException(String.valueOf(position));
			this.ilmnStrand = tokenizer.nextToken();
			this.customerStrand = tokenizer.nextToken();
			this.SNP = tokenizer.nextToken();
		}

		public String getName() { return name; }
		public String getChr() { return chromosome; }
		public int getPosition() { return position; }
		public String getILMNStrand() { return ilmnStrand; }
		public String getCustomerStrand() { return customerStrand; }
		public String getSNP() { return SNP; }
		public String toString() { return line; }
	}
}
