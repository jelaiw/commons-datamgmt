package edu.uab.ssg.io.mach;

import java.io.*;
import java.util.*;

/**
 * @author Jelai Wang
 */
public final class GENOParser {
	public interface RecordListener {
		void handleParsedRecord(IndividualRecord record);
		void handleBadRecordFormat(String line);
	}

	public interface IndividualRecord {
		public String getFamilyID();
		public String getIndividualID();
		public List<String> getGenotypes();
	}

	public void parse(InputStream in, RecordListener listener) throws IOException {
		if (in == null)
			throw new NullPointerException("in");
		if (listener == null)
			throw new NullPointerException("listener");
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = null;
		while ((line = reader.readLine()) != null) {
			IndividualRecord record = null;
			try {
				record = new ParsedIndividualRecord(line);
			}
			catch (RuntimeException e) {
				listener.handleBadRecordFormat(line);
				continue;
			}
			listener.handleParsedRecord(record);
		}
		reader.close();
	}

	private class ParsedIndividualRecord implements IndividualRecord {
		private String line;
		private String familyID, individualID;
		private List<String> genotypes = new ArrayList<String>();

		private ParsedIndividualRecord(String line) {
			if (line == null)
				throw new NullPointerException("line");
			this.line = line;

			StringTokenizer tokenizer = new StringTokenizer(line);
			if (tokenizer.countTokens() < 3) // Expect at least one genotype.
				throw new IllegalArgumentException(line);
			String[] tmp = tokenizer.nextToken().split("->");
			if (tmp.length != 2)
				throw new IllegalArgumentException(line);
			this.familyID = tmp[0];
			this.individualID = tmp[1];
			String GENO = tokenizer.nextToken(); // Hard-coded constant "GENO".
			if (!"GENO".equals(GENO))
				throw new IllegalArgumentException(GENO + "," + line);
			while (tokenizer.hasMoreTokens()) {
				String genotype = tokenizer.nextToken();
				if (genotype.length() == 3 && genotype.charAt(1) == '/')
					genotypes.add(genotype);
				else
					throw new IllegalArgumentException(genotype + "," + line);
			}
		}

		public String getFamilyID() { return familyID; }
		public String getIndividualID() { return individualID; }
		public List<String> getGenotypes() { return new ArrayList<String>(genotypes); }

		public String toString() { return line; }
	}
}
