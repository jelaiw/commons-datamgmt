package edu.uab.ssg.io.mach;

import java.io.*;
import java.util.*;

/**
 * A parser for the GENO output file format from <a href="http://www.sph.umich.edu/csg/abecasis/MaCH/">MACH</a>.
 *
 * In other words, this is a parser for the output file with extension ".geno" containing imputed values for missing genotypes. For example:
 *
 * <p><tt>
 * FAM1-&gt;IND1 GENO  C/C A/A T/T T/T C/C T/T G/G<br/>
 * FAM2-&gt;IND1 GENO  C/C A/A T/T T/T C/C T/T G/G<br/>
 * FAM3-&gt;IND1 GENO  C/A A/A T/T T/T C/C T/T G/G<br/>
 * FAM4-&gt;IND1 GENO  C/C A/A T/T T/T C/C T/T G/G<br/>
 * FAM5-&gt;IND1 GENO  C/A A/A T/T T/T C/C T/T G/G<br/>
 * FAM6-&gt;IND1 GENO  C/C A/A T/T T/T C/C T/T G/G<br/>
 * FAM7-&gt;IND1 GENO  C/C A/A T/T T/T C/C T/T G/G<br/>
 * FAM8-&gt;IND1 GENO  C/A A/G T/T T/C C/G T/C G/A<br/>
 * FAM9-&gt;IND1 GENO  C/A A/A T/T T/T C/C T/T G/G<br/>
 * FAM10-&gt;IND1 GENO  C/C A/A T/T T/T C/C T/T G/G<br/>
 * ...
 * </tt></p>
 *
 * @author Jelai Wang
 */
public final class GENOParser {
	/**
	 * Constructs the parser.
	 */
	public GENOParser() {
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
		/**
		 * Returns the family ID.
		 */
		public String getFamilyID();

		/**
		 * Returns the individual ID.
		 */
		public String getIndividualID();

		/**
		 * Returns the genotypes as a list of strings.
		 * @return A list of strings. Each string represents a genotype as
		 * formatted by the MACH program, typically using the '/' character
		 * as the separator (e.g. A/T, G/G).
		 */
		public List<String> getGenotypes();
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
