package edu.uab.ssg.io.limdi;

import java.io.*;
import java.util.*;

/**
 * A parser for the tab-delimited text file, called <i>Data_jelai_25March_09.tab</i>, containing the covariate data for the Limdi warfarin analysis.
 *
 * <p><tt>
 * IDnum<br/>
 * Age<br/>
 * Gender<br/>
 * Race<br/>
 * Height<br/>
 * Weight<br/>
 * BMI<br/>
 * CYP2C9<br/>
 * C2C9<br/>
 * VKOR<br/>
 * rfCHF<br/>
 * rfDM<br/>
 * Statin<br/>
 * AvgMaint<br/>
 * Avg1Maint<br/>
 * AvgOfK<br/>
 * alcohol<br/>
 * smoke<br/>
 * amiod<br/>
 * CKD<br/>
 * CKD1<br/>
 * logdose<br/>
 * sqdose<br/>
 * </tt></p>
 *
 * This file was created by opening the original MS Excel spreadsheet
 * and saving as tab-delimited text in MS Excel 2007.
 *
 * @author Jelai Wang
 */
public final class CovariateTabParser {
	/**
	 * Constructs the parser.
	 */
	public CovariateTabParser() {
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
		String getAge();
		String getGender();
		String getRace();
		String getHeight();
		String getWeight();
		String getBMI();
		String getCYP2C9();
		String getC2C9();
		String getVKOR();
		String getrfCHF();
		String getrfDM();
		String getStatin();
		String getAvgMaint();
		String getAvg1Maint();
		String getAvgOfK();
		String getalcohol();
		String getsmoke();
		String getamiod();
		String getCKD();
		String getCKD1();
		String getlogdose();
		String getsqdose();
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
		if (!"IDNum".equals(tmp[0]))
			throw new IllegalArgumentException(header);
		if (!"Age".equals(tmp[1]))
			throw new IllegalArgumentException(header);
		if (!"Gender".equals(tmp[2]))
			throw new IllegalArgumentException(header);
		if (!"Race".equals(tmp[3]))
			throw new IllegalArgumentException(header);
		if (!"Height".equals(tmp[4]))
			throw new IllegalArgumentException(header);
		if (!"Weight".equals(tmp[5]))
			throw new IllegalArgumentException(header);
		if (!"BMI".equals(tmp[6]))
			throw new IllegalArgumentException(header);
		if (!"alcohol".equals(tmp[16]))
			throw new IllegalArgumentException(header);
		if (!"smoke".equals(tmp[17]))
			throw new IllegalArgumentException(header);
		if (!"sqdose".equals(tmp[22]))
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

	private String translateEmptyStringToNull(String tmp) {
		return "".equals(tmp) ? null : tmp;
	}

	private class ParsedSampleRecord implements SampleRecord {
		private String line;
		private String idnum, age, gender, race, height, weight, bmi;
		private String cyp2c9, c2c9, vkor, rfchf, rfdm, statin, avgMaint, avg1Maint, avgOfK, alcohol, smoke, amiod, ckd, ckd1, logdose, sqdose;

		private ParsedSampleRecord(String line) {
			if (line == null)
				throw new NullPointerException("line");
			this.line = line;

			String[] tmp = line.split("\t", -1); // See String API.

			if (tmp.length != 23) // Expect 23 columns.
				throw new IllegalArgumentException(tmp.length + "," + line);
			this.idnum = translateEmptyStringToNull(tmp[0]); this.age = translateEmptyStringToNull(tmp[1]); this.gender = translateEmptyStringToNull(tmp[2]); this.race = translateEmptyStringToNull(tmp[3]); this.height = translateEmptyStringToNull(tmp[4]); this.weight = translateEmptyStringToNull(tmp[5]); this.bmi = translateEmptyStringToNull(tmp[6]);
			this.cyp2c9 = translateEmptyStringToNull(tmp[7]); this.c2c9 = translateEmptyStringToNull(tmp[8]); this.vkor = translateEmptyStringToNull(tmp[9]); this.rfchf = translateEmptyStringToNull(tmp[10]); this.rfdm = translateEmptyStringToNull(tmp[11]); this.statin = translateEmptyStringToNull(tmp[12]); this.avgMaint = translateEmptyStringToNull(tmp[13]); this.avg1Maint = translateEmptyStringToNull(tmp[14]); this.avgOfK = translateEmptyStringToNull(tmp[15]); this.alcohol = translateEmptyStringToNull(tmp[16]); this.smoke = translateEmptyStringToNull(tmp[17]); this.amiod = translateEmptyStringToNull(tmp[18]); this.ckd = translateEmptyStringToNull(tmp[19]); this.ckd1 = translateEmptyStringToNull(tmp[20]); this.logdose = translateEmptyStringToNull(tmp[21]); this.sqdose = translateEmptyStringToNull(tmp[22]);
		}

		public String getIDNum() { return idnum; }
		public String getAge() { return age; }
		public String getGender() { return gender; }
		public String getRace() { return race; }
		public String getHeight() { return height; }
		public String getWeight() { return weight; }
		public String getBMI() { return bmi; }
		public String getCYP2C9() { return cyp2c9; }
		public String getC2C9() { return c2c9; }
		public String getVKOR() { return vkor; }
		public String getrfCHF() { return rfchf; }
		public String getrfDM() { return rfdm; }
		public String getStatin() { return statin; }
		public String getAvgMaint() { return avgMaint; }
		public String getAvg1Maint() { return avg1Maint; }
		public String getAvgOfK() { return avgOfK; }
		public String getalcohol() { return alcohol; }
		public String getsmoke() { return smoke; }
		public String getamiod() { return amiod; }
		public String getCKD() { return ckd; }
		public String getCKD1() { return ckd1; }
		public String getlogdose() { return logdose; }
		public String getsqdose() { return sqdose; }

		public String toString() { return line; }
	}
}
