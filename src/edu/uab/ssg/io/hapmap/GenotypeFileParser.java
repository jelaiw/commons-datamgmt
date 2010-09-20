package edu.uab.ssg.io.hapmap;

import java.util.*;
import java.io.*;

/**
 * @author Jelai Wang
 */
public final class GenotypeFileParser {
	private static final String DELIMITER = " ";
	private static final String MISSING_VALUE = "N";

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
		String headerLine = reader.readLine();
		String[] header = headerLine.split(" ", 0);
		if (!"rs#".equals(header[0]))
			throw new IllegalArgumentException(header[0]);
		if (!"alleles".equals(header[1]))
			throw new IllegalArgumentException(header[1]);
		if (!"chrom".equals(header[2]))
			throw new IllegalArgumentException(header[2]);
		if (!"pos".equals(header[3]))
			throw new IllegalArgumentException(header[3]);
		if (!"strand".equals(header[4]))
			throw new IllegalArgumentException(header[4]);
		if (!"assembly#".equals(header[5]))
			throw new IllegalArgumentException(header[5]);

		List<String> sampleNames = Collections.unmodifiableList(Arrays.asList(header).subList(11, header.length));
		int expectedNumberOfTokens = 11 + sampleNames.size();
		if (header.length != expectedNumberOfTokens) {
			listener.handleBadRecordFormat(headerLine);
			throw new RuntimeException(header.length + " " + expectedNumberOfTokens);
		}

		int offset = 11; // Index offset to the beginning of the genotype calls.
		String line = null;
		while ((line = reader.readLine()) != null) {
			String[] tokens = line.split(DELIMITER, -1);
			if (tokens.length != expectedNumberOfTokens) {
				listener.handleBadRecordFormat(line);
				continue;
			}

			String snp = tokens[0];
			String alleles = tokens[1];
			String chr = tokens[2];
			int position = Integer.parseInt(tokens[3]);
			String strand = tokens[4];
			String assemblyVersion = tokens[5];

			for (int i = 0, n = sampleNames.size(); i < n; i++) {
				String sampleID = sampleNames.get(i);
				String genotype = tokens[i + offset];
				if (genotype.length() != 2) {
					listener.handleBadRecordFormat(line);
					continue;
				}
				String allele1 = String.valueOf(genotype.charAt(0));
				String allele2 = String.valueOf(genotype.charAt(1));
				if (MISSING_VALUE.equals(allele1)) allele1 = null;
				if (MISSING_VALUE.equals(allele2)) allele2 = null;

				listener.handleParsedRecord(new ParsedGenotypeRecord(line, snp, position, chr, alleles, strand, assemblyVersion, sampleID, allele1, allele2));
			}
		}
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

	public interface GenotypeRecord {
		/**
		 * Returns the SNP name.
		 */
		String getSNP();

		/**
		 * Returns the position of the SNP on the chromosome.
		 */
		int getPosition();

		String getChromosome();

		String getAlleles();

		String getStrand();

		String getAssemblyVersion();

		String getSampleID();

		/**
		 * Returns allele1 at the SNP for the sample.
		 */
		String getAllele1();

		/**
		 * Returns allele2 at the SNP for the sample.
		 */
		String getAllele2();
	}

	private class ParsedGenotypeRecord implements GenotypeRecord {
		private String line;
		private String snp;
		private int position;
		private String chr;
		private String alleles, strand, assemblyVersion;
		private String sampleID;
		private String allele1, allele2;

		private ParsedGenotypeRecord(String line, String snp, int position, String chr, String alleles, String strand, String assemblyVersion, String sampleID, String allele1, String allele2) {
			if (line == null)
				throw new NullPointerException("line");
			if (snp == null)
				throw new NullPointerException("snp");
			if (position < 0)
				throw new IllegalArgumentException(String.valueOf(position));
			if (chr == null)
				throw new NullPointerException("chr");
			if (alleles == null)
				throw new NullPointerException("alleles");
			if (strand == null)
				throw new NullPointerException("strand");
			if (assemblyVersion == null)
				throw new NullPointerException("assemblyVersion");
			if (sampleID == null)
				throw new NullPointerException("sampleID");

			this.line = line;
			this.snp = snp;
			this.position = position;
			this.chr = chr;
			this.alleles = alleles;
			this.strand = strand;
			this.assemblyVersion = assemblyVersion;
			this.sampleID = sampleID;
			this.allele1 = allele1;
			this.allele2 = allele2;
		}

		public String getSNP() { return snp; }
		public int getPosition() { return position; }
		public String getChromosome() { return chr; }
		public String getAlleles() { return alleles; }
		public String getStrand() { return strand; }
		public String getAssemblyVersion() { return assemblyVersion; }
		public String getSampleID() { return sampleID; }
		public String getAllele1() { return allele1; }
		public String getAllele2() { return allele2; }

		public String toString() { return line; }
	}
}
