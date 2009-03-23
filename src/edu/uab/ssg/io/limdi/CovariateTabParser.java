package edu.uab.ssg.io.limdi;

import java.io.*;
import java.util.*;

/**
 * A parser for the tab-delimited text file, called <i>Dose_18march09_jelai.tab</i>, containing the covariate data for the Limdi warfarin analysis.
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
 * VKOR<br/>
 * rfCHF<br/>
 * Statin<br/>
 * Amiod<br/>
 * AvgMaint<br/>
 * Avg1Maint<br/>
 * AvgOfK<br/>
 * Dose_Target<br/>
 * Dose_Stable<br/>
 * alcohol<br/>
 * smoke<br/>
 * cyp2c9v<br/>
 * vkorv<br/>
 * CKD<br/>
 * CKD1<br/>
 * aged<br/>
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
		String getVKOR();
		String getrfCHF();
		String getStatin();
		String getAmiod();
		String getAvgMaint();
		String getAvg1Maint();
		String getAvgOfK();
		String getDose_Target();
		String getDose_Stable();
		String getalcohol();
		String getsmoke();
		String getcyp2c9v();
		String getvkorv();
		String getCKD();
		String getCKD1();
		String getaged();
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
		if (!"alcohol".equals(tmp[17]))
			throw new IllegalArgumentException(header);
		if (!"smoke".equals(tmp[18]))
			throw new IllegalArgumentException(header);
		if (!"aged".equals(tmp[23]))
			throw new IllegalArgumentException(header);
		if (!"sqdose".equals(tmp[25]))
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
		private String cyp2c9, vkor, rfchf, statin, amiod, avgMaint, avg1Maint, avgOfK, doseTarget, doseStable, alcohol, smoke, cyp2c9v, vkorv, ckd, ckd1, aged, logdose, sqdose;

		private ParsedSampleRecord(String line) {
			if (line == null)
				throw new NullPointerException("line");
			this.line = line;

			String[] tmp = line.split("\t", -1); // See String API.

			if (tmp.length != 26) // Expect 26 columns.
				throw new IllegalArgumentException(tmp.length + "," + line);
			this.idnum = translateEmptyStringToNull(tmp[0]); this.age = translateEmptyStringToNull(tmp[1]); this.gender = translateEmptyStringToNull(tmp[2]); this.race = translateEmptyStringToNull(tmp[3]); this.height = translateEmptyStringToNull(tmp[4]); this.weight = translateEmptyStringToNull(tmp[5]); this.bmi = translateEmptyStringToNull(tmp[6]);
			this.cyp2c9 = translateEmptyStringToNull(tmp[7]); this.vkor = translateEmptyStringToNull(tmp[8]); this.rfchf = translateEmptyStringToNull(tmp[9]); this.statin = translateEmptyStringToNull(tmp[10]); this.amiod = translateEmptyStringToNull(tmp[11]); this.avgMaint = translateEmptyStringToNull(tmp[12]); this.avg1Maint = translateEmptyStringToNull(tmp[13]); this.avgOfK = translateEmptyStringToNull(tmp[14]); this.doseTarget = translateEmptyStringToNull(tmp[15]); this.doseStable = translateEmptyStringToNull(tmp[16]); this.alcohol = translateEmptyStringToNull(tmp[17]); this.smoke = translateEmptyStringToNull(tmp[18]); this.cyp2c9v = translateEmptyStringToNull(tmp[19]); this.vkorv = translateEmptyStringToNull(tmp[20]); this.ckd = translateEmptyStringToNull(tmp[21]); this.ckd1 = translateEmptyStringToNull(tmp[22]); this.aged = translateEmptyStringToNull(tmp[23]); this.logdose = translateEmptyStringToNull(tmp[24]); this.sqdose = translateEmptyStringToNull(tmp[25]);
		}

		public String getIDNum() { return idnum; }
		public String getAge() { return age; }
		public String getGender() { return gender; }
		public String getRace() { return race; }
		public String getHeight() { return height; }
		public String getWeight() { return weight; }
		public String getBMI() { return bmi; }
		public String getCYP2C9() { return cyp2c9; }
		public String getVKOR() { return vkor; }
		public String getrfCHF() { return rfchf; }
		public String getStatin() { return statin; }
		public String getAmiod() { return amiod; }
		public String getAvgMaint() { return avgMaint; }
		public String getAvg1Maint() { return avg1Maint; }
		public String getAvgOfK() { return avgOfK; }
		public String getDose_Target() { return doseTarget; }
		public String getDose_Stable() { return doseStable; }
		public String getalcohol() { return alcohol; }
		public String getsmoke() { return smoke; }
		public String getcyp2c9v() { return cyp2c9v; }
		public String getvkorv() { return vkorv; }
		public String getCKD() { return ckd; }
		public String getCKD1() { return ckd1; }
		public String getaged() { return aged; }
		public String getlogdose() { return logdose; }
		public String getsqdose() { return sqdose; }

		public String toString() { return line; }
	}
}
