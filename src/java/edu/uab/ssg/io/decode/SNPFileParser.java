package edu.uab.ssg.io.decode;

import java.util.*;
import java.io.*;

/**
 * @author Jelai Wang
 */
public final class SNPFileParser {
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

	public interface RecordListener {
		void handleParsedRecord(SNPRecord record);
		void handleBadRecordFormat(String line);
	}

	public interface SNPRecord {
		String getName();
		String getChromosome();
		int getPosition();
		double getGenTrainScore();
		String getSNP();
		String getILMNStrand();
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
