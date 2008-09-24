package edu.uab.ssg.io.decode;

import java.util.*;
import java.io.*;

/**
 * @author Jelai Wang
 */

public final class GenotypeFileParser {
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
				listener.handle(new DefaultGenotypeRecord(line));
			}
			// Skip all other lines, including the header section.
		}
		reader.close();
	}

	public interface RecordListener {
		void handle(GenotypeRecord record);
	}

	public interface GenotypeRecord {
		String getSNPName();
		String getSampleID();
		String getAllele1Top();
		String getAllele2Top();
		String getAllele1Forward();
		String getAllele2Forward();
		double getGCScore();
	}

	private class DefaultGenotypeRecord implements GenotypeRecord {
		private String line;
		private String snpName, sampleID;
		private String a1Top, a2Top;
		private String a1Forward, a2Forward;
		private double gcScore;

		private DefaultGenotypeRecord(String line) {
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
