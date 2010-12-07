package edu.uab.ssg.io.illumina;

import java.util.*;
import java.io.*;

/**
 * A parser that can flexibly handle the various formats that Illumina genotyping reports can have. 
 *
 * Here is an example:
 * <p><tt>
 * [Header]<br/>
 * GSGT Version	1.7.4<br/>
 * Processing Date	10/29/2010 11:42<br/>
 * Content		Immuno_BeadChip_11419691_B.bpm<br/>
 * Num SNPs	196492<br/>
 * Total SNPs	196524<br/>
 * Num Samples	768<br/>
 * Total Samples	768<br/>
 * [Data]<br/>
 * SNP Name	Sample ID	Allele1 - Top	Allele2 - Top	Allele1 - AB	Allele2 - AB	Chr	Position<br/>
 * 1-159076491-G-DELETION	AVA1001a	I	I	B	B	1	159076491<br/>
 * 1-159093319-A-DELETION	AVA1001a	I	I	A	A	1	159093319<br/>
 * 1-159105568-A-INSERTION	AVA1001a	I	I	A	A	1	159105568<br/>
 * ...
 * </tt></p>
 *
 * <p>In this implementation, the data table record delimiter is the tab character.</p>
 *
 * @author Jelai Wang
 */
public final class GenotypeFileParser {
	/**
	 * A string constant for the character (a dash, '-') that represents a missing allele call in the Illumina genotyping report format.
	 */
	public static final String MISSING_ALLELE = "-";

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
		String DELIMITER = "\t";
		List<String> header = null;
		String line = null;
		boolean inDataSection = false;
		while ((line = reader.readLine()) != null) {
			if (!inDataSection && "[Data]".equals(line)) { // Detect the beginning of the data section.
				inDataSection = true;
				header = Arrays.asList(reader.readLine().split(DELIMITER, -1));
			}
			else if (inDataSection) {
				String[] tokens = line.split(DELIMITER, -1);
				if (tokens.length != header.size()) {
					listener.handleBadRecordFormat(line);
					continue;
				}
				GenotypeRecord record = new ParsedGenotypeRecord(line, header, tokens);
				listener.handleParsedRecord(record);
			}
			// Skip all other lines, usually just the header section.
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
		 * Returns the value for this record at the given column index.
		 */
		String getValue(int columnIndex);

		/**
		 * Returns the value for this record at the given column index.
		 * If the value to be returned equals the given <tt>matchValue</tt>, then <tt>replacementValue</tt> is instead returned.
		 */
		String getValue(int columnIndex, String matchValue, String replacementValue);

		/**
		 * Returns the value for this record corresponding to the given column name.
		 */
		String getValue(String columnName);

		/**
		 * Returns the value for this record corresponding to the given column name.
		 * If the value to be returned equals the given <tt>matchValue</tt>, then <tt>replacementValue</tt> is instead returned.
		 */
		String getValue(String columnName, String matchValue, String replacementValue);

		/**
		 * Returns the list of column names available for this record.
		 */
		List<String> getColumnNames();
	}

	private class ParsedGenotypeRecord implements GenotypeRecord {
		private String line;
		private List<String> header;
		private String[] tokens;

		private ParsedGenotypeRecord(String line, List<String> header, String[] tokens) {
			if (line == null)
				throw new NullPointerException("line");
			if (header == null)
				throw new NullPointerException("header");
			if (tokens == null)
				throw new NullPointerException("tokens");
			this.line = line;
			this.header = header;
			this.tokens = tokens;
		}

		public String getValue(int columnIndex) {
			return tokens[columnIndex];
		}

		public String getValue(int columnIndex, String matchValue, String replacementValue) {
			if (matchValue == null)
				throw new NullPointerException("matchValue");
			String value = getValue(columnIndex);
			if (matchValue.equals(value)) {
				value = replacementValue;
			}
			return value;
		}

		public String getValue(String columnName) {
			if (columnName == null)
				throw new NullPointerException("columnName");
			if (!header.contains(columnName))
				throw new IllegalArgumentException(columnName);
			return getValue(header.indexOf(columnName));
		}

		public String getValue(String columnName, String matchValue, String replacementValue) {
			if (columnName == null)
				throw new NullPointerException("columnName");
			if (!header.contains(columnName))
				throw new IllegalArgumentException(columnName);
			return getValue(header.indexOf(columnName), matchValue, replacementValue);
		}

		public List<String> getColumnNames() { return new ArrayList<String>(header); }
		public String toString() { return line; }
	}
}
