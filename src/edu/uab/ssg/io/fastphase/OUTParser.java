package edu.uab.ssg.io.fastphase;

import java.io.*;
import java.util.regex.*;

/**
 * A parser for the fastPHASE inferred haplotypes output file format.
 * Here is an example:
 *
 * <p><tt>
 * BEGIN COMMAND_LINE<br/>
 * ./fastPHASE -ochrom16 -uchrom16.subpop -K20 -s10 -bchrom16.knownhap chrom16.in <br/>
 * END COMMAND_LINE<br/>
 *<br/>
 * BEGIN COMMAND_EXPLAIN<br/>
 *  K no. clusters (chosen or supplied): 20<br/>
 *  S seed for random numbers (chosen or supplied): 1186943850<br/>
 * END COMMAND_EXPLAIN<br/>
 *<br/>
 * BEGIN DESCRIBE_TASKS<br/>
 * minimize switch error<br/>
 * END DESCRIBE_TASKS<br/>
 *<br/>
 * BEGIN GENOTYPES<br/>
 * HGDP00001  # subpop. label: 1  (internally 1)<br/>
 * </tt></p>
 *
 * <p>These data are taken from the HGDP project at <a href="http://hgdp.uchicago.edu/Phased_data/">http://hgdp.uchicago.edu/Phased_data/</a>.</p>
 *
 * This implementation parses the sample records between the BEGIN GENOTYPES and END GENTOTYPES sections of the file format and ignores all other lines. Each sample record consists of three lines: <i>a sample line</i> (with sample ID and subpop label), <i>a line containing alleles from the first haplotype of the sample</i>, and <i>a line containing alleles from the second haplotype of the sample</i>. In this file format, the separator for the fields of the haplotype lines is a single space character.
 *
 * @author Jelai Wang
 */
public final class OUTParser {
	/**
	 * Constructs the parser.
	 */
	public OUTParser() {
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
		 * @param sampleLine The line containing the sample identifier and subpop label.
		 * @param haplotype1Line The line containing alleles from the first haplotype of the sample.
		 * @param haplotype2Line The line containing alleles from the second haplotype of the sample.
		 */
		void handleBadRecordFormat(String sampleLine, String haplotype1Line, String haplotype2Line);
	}

	/**
	 * A sample record.
	 */
	public interface SampleRecord {
		/**
		 *	Returns the sample identifier.
		 */
		String getSampleID();

		/**
		 * Returns the subpopulation label.
		 */
		String getSubpopLabel();

		/**
		 * Returns the number of SNPs in each haplotype.
		 */
		int getNumberOfSNPs();

		/**
		 * Returns the allele at the given SNP index on the first haplotype.
		 */
		String getAllele1At(int index);

		/**
		 * Returns the allele at the given SNP index on the second haplotype.
		 */
		String getAllele2At(int index);
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
		// Find the BEGIN GENOTYPES section.
		while (!"BEGIN GENOTYPES".equals(reader.readLine())) {
		}
		// Process the rest of the rows, three lines at a time.
		String sampleLine = null;
		while (!"END GENOTYPES".equals(sampleLine = reader.readLine())) {
			String haplotype1Line = reader.readLine();
			String haplotype2Line = reader.readLine();

			SampleRecord record = null;
			try {
				record = new ParsedSampleRecord(sampleLine, haplotype1Line, haplotype2Line);
			}
			catch (RuntimeException e) {
				listener.handleBadRecordFormat(sampleLine, haplotype1Line, haplotype2Line);
				continue;
			}
			listener.handleParsedRecord(record);
		}
		reader.close();
	}

	private class ParsedSampleRecord implements SampleRecord {
		private String sampleLine, haplotype1Line, haplotype2Line;
		private String sampleID, subpopLabel;
		private String[] a1, a2;

		private ParsedSampleRecord(String sampleLine, String haplotype1Line, String haplotype2Line) {
			if (sampleLine == null)
				throw new NullPointerException("sampleLine");
			if (haplotype1Line == null)
				throw new NullPointerException("haplotype1Line");
			if (haplotype2Line == null)
				throw new NullPointerException("haplotype2Line");
			this.sampleLine = sampleLine;
			this.haplotype1Line = haplotype1Line;
			this.haplotype2Line = haplotype2Line;
			// Pick apart the sample line for the identifier and subpop label.
			Pattern pattern = Pattern.compile("(\\w+)\\s+# subpop. label: (\\w+)\\s+");
			Matcher matcher = pattern.matcher(sampleLine);
			if (matcher.find()) {
				this.sampleID = matcher.group(1);
				this.subpopLabel = matcher.group(2);
			}
			else {
				throw new IllegalArgumentException(sampleLine);
			}
			// Parse the haplotype lines.
			this.a1 = haplotype1Line.split("\\s+");
			this.a2 = haplotype2Line.split("\\s+");
			if (a1.length != a2.length) // LOOK!!
				throw new IllegalArgumentException(a1.length + " " + a2.length);
		}

		public String getSampleID() { return sampleID; }
		public String getSubpopLabel() { return subpopLabel; }
		public int getNumberOfSNPs() { return a1.length; }
		public String getAllele1At(int index) { return a1[index]; }
		public String getAllele2At(int index) { return a2[index]; }

		public String toString() {
			String EOL = "\n";
			StringBuilder builder = new StringBuilder();
			builder.append(sampleLine);
			builder.append(EOL).append(haplotype1Line);
			builder.append(EOL).append(haplotype2Line);
			return builder.toString();
		}
	}
}
