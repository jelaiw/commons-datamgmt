package edu.uab.ssg.io.limdi;

import java.io.*;
import java.util.*;

/**
 * A parser for the tab-delimited text file, called <i>GWAS_AA_feb19_09_B109.tab</i>, containing the dose data for the Limdi warfarin analysis.
 *
 * <p><tt>
 * IDnum<br/>
 * Gender<br/>
 * AvgDose<br/>
 * Stabledose<br/>
 * </tt></p>
 *
 * This file was created by opening the original MS Excel spreadsheet
 * and saving as tab-delimited text in MS Excel 2000.
 *
 * @author Jelai Wang
 */
public final class DoseTabParser {
	/**
	 * Constructs the parser.
	 */
	public DoseTabParser() {
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
	 * An sample record.
	 */
	public interface SampleRecord {
		String getIDNum();
		String getGender();
		String getAvgDose();
		String getStabledose();
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
		String header = reader.readLine();
		String[] tmp = header.split("\t");
		// Spot check a few expected column names in the header.
		if (!"IDNum".equals(dequote(tmp[0])))
			throw new IllegalArgumentException(header);
		if (!"Gender".equals(dequote(tmp[1])))
			throw new IllegalArgumentException(header);
		if (!"AvgDose".equals(dequote(tmp[2])))
			throw new IllegalArgumentException(header);
		if (!"Stabledose".equals(dequote(tmp[3])))
			throw new IllegalArgumentException(header);
		// Process the rest of the rows.
		String line = null;
		while ((line = reader.readLine()) != null) {
			SampleRecord record = null;
			try {
				record = new ParsedSampleRecord(line);
			}
			catch (RuntimeException e) {
				listener.handleBadRecordFormat(line);
				continue;
			}
			listener.handleParsedRecord(record);
		}
		reader.close();
	}

	private String dequote(String tmp) {
		if (tmp.startsWith("\"") && tmp.endsWith("\""))
			return tmp.substring(1, tmp.length() - 1);
		return tmp;	
	}

	private class ParsedSampleRecord implements SampleRecord {
		private String line;
		private String idnum, gender, avgdose, stabledose;

		private ParsedSampleRecord(String line) {
			if (line == null)
				throw new NullPointerException("line");
			this.line = line;

			String[] tmp = line.split("\t", -1); // See String API.

			if (tmp.length != 4) // Expect 4 columns.
				throw new IllegalArgumentException(tmp.length + "," + line);
			this.idnum = dequote(tmp[0]);
			if ("".equals(idnum))
				throw new IllegalArgumentException(tmp.length + "," + line);
			this.gender = dequote(tmp[1]);
			if ("".equals(gender))
				throw new IllegalArgumentException(tmp.length + "," + line);
			this.avgdose = dequote(tmp[2]);
			if ("".equals(avgdose)) avgdose = null;
			this.stabledose = dequote(tmp[3]);
			if ("".equals(stabledose)) stabledose = null;
		}

		public String getIDNum() { return idnum; }
		public String getGender() { return gender; }
		public String getAvgDose() { return avgdose; }
		public String getStabledose() { return stabledose; }

		public String toString() { return line; }
	}
}
