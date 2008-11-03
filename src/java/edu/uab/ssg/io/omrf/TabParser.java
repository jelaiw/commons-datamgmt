package edu.uab.ssg.io.omrf;

import java.io.*;
import java.util.*;

/**
 * A parser for the tab-delimited text file containing the de-identified and
 * coded clinical data from the Oklahoma Medical Research Foundation (OMRF)
 * for the UAB popgen project.
 *
 * <p><tt>
 * Number<br/>
 * OMRF Barcode<br/>
 * WRAMC Barcode<br/>
 * Group<br/>
 * Sex<br/>
 * Race<br/>
 * Age<br/>
 * Date Received<br/>
 * Date of Last Vacc<br/>
 * Yrs post Vacc<br/>
 * No. vacc<br/>
 * PA End Titer<br/>
 * LF End Titer<br/>
 * EF End Titer<br/>
 * % Viability (1:100) Average<br/>
 * Neutralization Group<br/>
 * </tt></p>
 *
 * This file was created by opening the original MS Excel spreadsheet
 * and exporting a CSV (with tab delimiter) in Calc from Open Office 2.4.
 *
 * @author Jelai Wang
 */
public final class TabParser {
	/**
	 * Constructs the parser.
	 */
	public TabParser() {
	}

	/**
	 * A listener for handling parsed individual records and problems due to
	 * bad record formatting.
	 */
	public interface RecordListener {
		/**
		 * Handles successfully parsed individual record.
		 */
		void handleParsedRecord(IndividualRecord record);

		/**
		 * Handles input that could not be parsed due to a formatting problem.
		 * @param line The line of text that could not be parsed.
		 */
		void handleBadRecordFormat(String line);
	}

	/**
	 * An individual record.
	 */
	public interface IndividualRecord {
		String getOMRFBarCode();
		String getWRAMCBarCode();
		String getGroup();
		String getSex();
		String getRace();
		String getAge();
		String getDateReceived();
		String getDateLastVacc();
		String getYearsPostVacc();
		String getNumberOfVacc();
		String getPAEndTiter();
		String getLFEndTiter();
		String getEFEndTiter();
		String getViability();
		String getNeutralizationGroup();
	}

	/**
	 * Parses the input stream for individual records.
	 * @param in The input stream, typically a file input stream, of the
	 * file to be parsed.
	 * @param listener As the input stream is parsed, each individual record
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
		if (!"OMRF Barcode".equals(dequote(tmp[1])))
			throw new IllegalArgumentException(header);
		if (!"WRAMC Barcode".equals(dequote(tmp[2])))
			throw new IllegalArgumentException(header);
		if (!"Race".equals(dequote(tmp[5])))
			throw new IllegalArgumentException(header);
		if (!"% Viability (1:100) Average".equals(dequote(tmp[14])))
			throw new IllegalArgumentException(header);
		// Process the rest of the rows.
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

	private String dequote(String tmp) {
		if (tmp.startsWith("\"") && tmp.endsWith("\""))
			return tmp.substring(1, tmp.length() - 1);
		return tmp;	
	}

	private class ParsedIndividualRecord implements IndividualRecord {
		private String line;
		private String omrfBarCode, wramcBarCode;
		private String group, sex, race, age;
		private String dateReceived, dateLastVacc, yearsPostVacc, numOfVacc;
		private String paEndTiter, lfEndTiter, efEndTiter;
		private String viability, neutralizationGroup;

		private ParsedIndividualRecord(String line) {
			if (line == null)
				throw new NullPointerException("line");
			this.line = line;

			String[] tmp = line.split("\t");
			if (tmp.length < 16) // Expect at least 16 columns.
				throw new IllegalArgumentException(tmp.length + "," + line);
			this.omrfBarCode = dequote(tmp[1]);
			this.wramcBarCode = dequote(tmp[2]);
			this.group = dequote(tmp[3]);
			this.sex = dequote(tmp[4]);
			this.race = dequote(tmp[5]);
			this.age = dequote(tmp[6]);
			this.dateReceived = dequote(tmp[7]);
			this.dateLastVacc = dequote(tmp[8]);
			this.yearsPostVacc = dequote(tmp[9]);
			this.numOfVacc = dequote(tmp[10]);
			this.paEndTiter = dequote(tmp[11]);
			this.lfEndTiter = dequote(tmp[12]);
			this.efEndTiter = dequote(tmp[13]);
			this.viability = dequote(tmp[14]);
			this.neutralizationGroup = dequote(tmp[15]);
		}

		public String getOMRFBarCode() { return omrfBarCode; }
		public String getWRAMCBarCode() { return wramcBarCode; }
		public String getGroup() { return group; }
		public String getSex() { return sex; }
		public String getRace() { return race; }
		public String getAge() { return age; }
		public String getDateReceived() { return dateReceived; }
		public String getDateLastVacc() { return dateLastVacc; }
		public String getYearsPostVacc() { return yearsPostVacc; }
		public String getNumberOfVacc() { return numOfVacc; }
		public String getPAEndTiter() { return paEndTiter; }
		public String getLFEndTiter() { return lfEndTiter; }
		public String getEFEndTiter() { return efEndTiter; }
		public String getViability() { return viability; }
		public String getNeutralizationGroup() { return neutralizationGroup; }

		public String toString() { return line; }
	}
}
