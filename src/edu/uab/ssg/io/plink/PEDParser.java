package edu.uab.ssg.io.plink;

import edu.uab.ssg.model.snp.Sex;
import java.util.*;
import java.io.*;

/**
 * @author Jelai Wang
 */
public final class PEDParser {
	public static final String MISSING_VALUE = "0";

	/**
	 * Constructs the parser.
	 */
	public PEDParser() {
	}

	/**
	 * Parses the input stream for sample records.
	 * @param in The input stream, typically a file input stream, of the
	 * file to be parsed.
	 * @param listener As the input stream is parsed, each sample record
	 * is passed to the user-supplied record listener.
	 */
	public void parse(InputStream in, RecordListener listener) throws IOException {
		if (in == null)
			throw new NullPointerException("in");
		if (listener == null)
			throw new NullPointerException("listener");
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = null;
		int count = -1; // Number of expected genotype calls per line.
		while ((line = reader.readLine()) != null) {
			try {
				SampleRecord record = new ParsedSampleRecord(line);
				listener.handleParsedRecord(record);
				if (count == -1) { // Initialize count to number of calls in the first line.
					count = record.getNumberOfAvailableGenotypeCalls();
				}
				else { // Kind of kludgy, but probably ok for now.
					if (count != record.getNumberOfAvailableGenotypeCalls()) {
						System.err.println(count);
						System.err.println(record.getNumberOfAvailableGenotypeCalls());
						System.err.println(line);
					}
				}
			}
			catch (RuntimeException e) {
				listener.handleBadRecordFormat(line);
			}
		}
		reader.close();
	}

	/**
	 * A listener for handling parsed sample records and problems due to
	 * bad record formatting.
	 */
	public interface RecordListener {
		/**
		 * Handles successfully parsed sample record.
		 */
		void handleParsedRecord(SampleRecord record);

		/**
		 * Handles input that could not be parsed due to a formatting problem.
		 * @param line The line of text that could not be parsed.
		 */
		void handleBadRecordFormat(String line);
	}

	/**
	 * A sample record.
	 */
	public interface SampleRecord {
		public String getFID();
		public String getIID();
		public String getPaternalID();
		public String getMaternalID();
		public Sex getSex();
		public String getPhenotype();
		public int getNumberOfAvailableGenotypeCalls();
		public String getAllele1(int index);
		public String getAllele2(int index);
	}

	/* package private */ static class ParsedSampleRecord implements SampleRecord {
		private String line;
		private String[] tokens;
		private String fid, iid, fatherid, motherid;
		private Sex sex;
		private String phenotype;
		private List<String> allele1, allele2;

		/* package private */ ParsedSampleRecord(String line) {
			if (line == null)
				throw new NullPointerException("line");
			this.line = line;

			String DELIMITER = "\\s+";
			this.tokens = line.split(DELIMITER, -1);

			if (tokens.length < 6) { // Quick-and-dirty sanity check.
				throw new IllegalArgumentException(line);
			}
			this.fid = tokens[0];
			this.iid = tokens[1];
			this.fatherid = MISSING_VALUE.equals(tokens[2]) ? null : tokens[2];
			this.motherid = MISSING_VALUE.equals(tokens[3]) ? null : tokens[3];
			// See http://pngu.mgh.harvard.edu/~purcell/plink/data.shtml#ped.
			if ("1".equals(tokens[4])) {
				sex = Sex.MALE;
			}
			else if ("2".equals(tokens[4])) {
				sex = Sex.FEMALE;
			}
			this.phenotype = tokens[5];
			// Check that genotype calls are complete pairs of allele calls.
			if (tokens.length % 2 != 0) {
				throw new IllegalArgumentException(String.valueOf(tokens.length));
			}
			// Parse the genotype calls into separate allele1 and allele2 calls.
			this.allele1 = new ArrayList<String>();
			this.allele2 = new ArrayList<String>();
			for (int i = 6; i < tokens.length; i = i + 2) {
				allele1.add(tokens[i]);
				allele2.add(tokens[i+1]);
			}
			if (allele1.size() != allele2.size()) { // Probably redundant.
				throw new RuntimeException(String.valueOf(tokens.length));
			}
		}

		public String getFID() { return fid; }
		public String getIID() { return iid; }
		public String getPaternalID() { return fatherid; }
		public String getMaternalID() { return motherid; }
		public Sex getSex() { return sex; }
		public String getPhenotype() { return phenotype; }
		public int getNumberOfAvailableGenotypeCalls() { return allele1.size(); }

		public String getAllele1(int index) {
			if (index < 0 || index >= allele1.size())
				throw new IllegalArgumentException(String.valueOf(index));
			String tmp = allele1.get(index);
			return MISSING_VALUE.equals(tmp) ? null : tmp;
		}

		public String getAllele2(int index) {
			if (index < 0 || index >= allele2.size())
				throw new IllegalArgumentException(String.valueOf(index));
			String tmp = allele2.get(index);
			return MISSING_VALUE.equals(tmp) ? null : tmp;
		}

		public String toString() { return line; }
	}
}
