package edu.uab.ssg.io.decode;

import java.util.*;
import java.io.*;

/**
 * A parser for the <i>snps_fullscore_20080328.tab</i> file compiled by Gisli 
 * Masson at deCODE and circulated to the UAB popgen team on March 28, 2008.
 *
 * @author Jelai Wang
 */
public final class FullScoreFileParser {
	/**
	 * Constructs the parser.
	 */
	public FullScoreFileParser() {
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
		// Process header.
		String[] header = reader.readLine().split("\t"); 
		if (header.length != 90)
			throw new IllegalArgumentException(String.valueOf(header.length));
		if (!"SNP_Name".equals(header[0]))
			throw new IllegalArgumentException(header[0]);
		if (!"Chr".equals(header[3]))
			throw new IllegalArgumentException(header[3]);
		if (!"Coordinate".equals(header[4]))
			throw new IllegalArgumentException(header[4]);
		if (!"INCLUDE".equals(header[49]))
			throw new IllegalArgumentException(header[49]);
		if (!"MHC".equals(header[50]))
			throw new IllegalArgumentException(header[50]);
		if (!"AIM".equals(header[51]))
			throw new IllegalArgumentException(header[51]);
		if (!"CNV".equals(header[52]))
			throw new IllegalArgumentException(header[52]);
		if (!"HARLEY".equals(header[53]))
			throw new IllegalArgumentException(header[53]);
		if (!"HUGO symbol".equals(header[56]))
			throw new IllegalArgumentException(header[56]);
		if (!"func".equals(header[64]))
			throw new IllegalArgumentException(header[64]);
		// Process other rows.
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
		String getSNPName();
		String getChr();
		int getCoordinate();
		boolean isInclude();
		boolean isMHC();
		boolean isAIM();
		boolean isCNV();
		boolean isHarley();
		String getHugoSymbol();
		String getFunc();
	}

	private class ParsedSNPRecord implements SNPRecord {
		private String line;
		private String name, chr;
		private int coordinate;
		private boolean include, mhc, aim, cnv, harley;
		private String hugo, func;

		private ParsedSNPRecord(String line) {
			if (line == null)
				throw new NullPointerException("line");
			this.line = line;

			String[] tmp = line.split("\t");
			if (tmp.length != 90) // LOOK!! We expect 90 fields per record.
				throw new IllegalArgumentException(line);
			this.name = tmp[0];
			this.chr = tmp[3];
			this.coordinate = Integer.parseInt(tmp[4]);
			if (coordinate < 1)
				throw new IllegalArgumentException(String.valueOf(coordinate));
			this.include = "1".equals(tmp[49]);
			this.mhc = "1".equals(tmp[50]);
			this.aim = "1".equals(tmp[51]);
			this.cnv = "1".equals(tmp[52]);
			this.harley = "1".equals(tmp[53]);
			this.hugo = tmp[56];
			this.func = tmp[64];
		}

		public String getSNPName() { return name; }
		public String getChr() { return chr; }
		public int getCoordinate() { return coordinate; }
		public boolean isInclude() { return include; }
		public boolean isMHC() { return mhc; }
		public boolean isAIM() { return aim; }
		public boolean isCNV() { return cnv; }
		public boolean isHarley() { return harley; }
		public String getHugoSymbol() { return hugo; }
		public String getFunc() { return func; }
		public String toString() { return line; }
	}
}
