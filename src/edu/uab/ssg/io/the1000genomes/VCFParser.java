package edu.uab.ssg.io.the1000genomes;

import java.util.*;
import java.io.*;

/**
 * A parser for the VCF (Variant Call Format) file format described at <a href="http://www.1000genomes.org/wiki/Analysis/Variant%20Call%20Format/vcf-variant-call-format-version-40">http://www.1000genomes.org/wiki/Analysis/Variant%20Call%20Format/vcf-variant-call-format-version-40</a>.
 *
 * @author Jelai Wang
 */
public final class VCFParser {
	private static final String DELIMITER = "\t";
	private static final String MISSING_VALUE = ".";
	private static final String GENOTYPE_FIELD_DELIMITER = ":";
	private static final char PHASED_GENOTYPE_SEPARATOR = '|';
	private static final char UNPHASED_GENOTYPE_SEPARATOR = '/';

	/**
	 * Constructs the parser.
	 */
	public VCFParser() {
	}
	
	/**
	 * Parses the input stream for metadata and variant records.
	 * @param in The input stream, typically a file input stream, of the
	 * file to be parsed.
	 * @param listener As the input stream is parsed, each variant record
	 * is passed to the user-supplied record listener.
	 */
public void parse(InputStream in, RecordListener listener) throws IOException {
		if (in == null)
			throw new NullPointerException("in");
		if (listener == null)
			throw new NullPointerException("listener");
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = null;

		// Skip metadata for now.
		while ((line = reader.readLine()).startsWith("##")) {
			continue;
		}

		// We expect the header next.
		String[] header = line.split(DELIMITER, -1);
		if (!"#CHROM".equals(header[0]))
			throw new IllegalArgumentException(header[0]);
		if (!"POS".equals(header[1]))
			throw new IllegalArgumentException(header[1]);
		if (!"ID".equals(header[2]))
			throw new IllegalArgumentException(header[2]);
		if (!"REF".equals(header[3]))
			throw new IllegalArgumentException(header[3]);
		if (!"ALT".equals(header[4]))
			throw new IllegalArgumentException(header[4]);
		if (!"QUAL".equals(header[5]))
			throw new IllegalArgumentException(header[5]);
		if (!"FILTER".equals(header[6]))
			throw new IllegalArgumentException(header[6]);
		if (!"INFO".equals(header[7]))
			throw new IllegalArgumentException(header[7]);
		if (!"FORMAT".equals(header[8]))
			throw new IllegalArgumentException(header[8]);

		List<String> samples = Collections.unmodifiableList(Arrays.asList(header).subList(9, header.length));

		while ((line = reader.readLine()) != null) {
			VariantRecord record = null;
			try {
				record = new ParsedVariantRecord(line, samples);
			}
			catch (RuntimeException e) {
				listener.handleBadRecordFormat(line);
				continue;
			}
			listener.handleParsedRecord(record);
		}
	}

	/**
	 * A listener for handling parsed variant records and problems due to
	 * bad record formatting.
	 */
	public interface RecordListener {
		/**
		 * Handles successfully parsed variant record.
		 */
		void handleParsedRecord(VariantRecord record);

		/**
		 * Handles input that could not be parsed due to a formatting problem.
		 * @param line The line of text that could not be parsed.
		 */
		void handleBadRecordFormat(String line);
	}

	/**
	 * A variant record.
	 */
	public interface VariantRecord {
		String getChromosome();
		int getPosition();
		String getID();
		String getReferenceAllele();
		String getAlternateAllele();
		List<String> getSamples();
		String getAllele1(String sample);
		String getAllele2(String sample);
		String getAllele(String gt);
	}

	private class ParsedVariantRecord implements VariantRecord {
		private String line;
		private String id, chr;
		private int pos;
		private String ref, alt;
		private List<String> samples;
		private Map<String, String> sample2genotype = new LinkedHashMap<String, String>();

		private ParsedVariantRecord(String line, List<String> samples) {
			if (line == null)
				throw new NullPointerException("line");
			this.line = line;
			this.samples = samples; // LOOK!!

			String[] tokens = line.split(DELIMITER, -1);
			if (tokens.length != (samples.size() + 9))
				throw new IllegalArgumentException(line);
			this.chr = tokens[0];
			this.pos = Integer.parseInt(tokens[1]);
			this.id = MISSING_VALUE.equals(tokens[2]) ? null : tokens[2];
			this.ref = tokens[3];
			this.alt = tokens[4];

			for (int i = 0; i < samples.size(); i++) {
				String sample = samples.get(i);
				sample2genotype.put(sample, tokens[i + 9]);
			}
		}

		public String getChromosome() { return chr; }
		public int getPosition() { return pos; }
		public String getID() { return id; }
		public String getReferenceAllele() { return ref; }
		public String getAlternateAllele() { return alt; }
		public List<String> getSamples() { return samples; } // LOOK!!

		public String getAllele1(String sample) {
			if (sample == null)
				throw new NullPointerException("sample");
			if (!sample2genotype.containsKey(sample))
				throw new IllegalArgumentException(sample);
			String genotypeField = sample2genotype.get(sample);
			String[] tmp = genotypeField.split(GENOTYPE_FIELD_DELIMITER, -1);
			// Check tmp.length against expected format.
			String gt = tmp[0];
			if (gt.length() != 3 || (gt.charAt(1) != UNPHASED_GENOTYPE_SEPARATOR && gt.charAt(1) != PHASED_GENOTYPE_SEPARATOR))
				throw new RuntimeException(genotypeField); // Bad format.
			return String.valueOf(gt.charAt(0));
		}

		public String getAllele2(String sample) {
			if (sample == null)
				throw new NullPointerException("sample");
			if (!sample2genotype.containsKey(sample))
				throw new IllegalArgumentException(sample);
			String genotypeField = sample2genotype.get(sample);
			String[] tmp = genotypeField.split(GENOTYPE_FIELD_DELIMITER, -1);
			// Check tmp.length against expected format.
			String gt = tmp[0];
			if (gt.length() != 3 || (gt.charAt(1) != UNPHASED_GENOTYPE_SEPARATOR && gt.charAt(1) != PHASED_GENOTYPE_SEPARATOR))
				throw new RuntimeException(genotypeField); // Bad format.
			return String.valueOf(gt.charAt(2));
		}

		public String getAllele(String gt) {
			if (gt == null)
				throw new NullPointerException("gt");
			if ("0".equals(gt))
				return getReferenceAllele();
			else if ("1".equals(gt))
				return getAlternateAllele();
			return null;
		}

		public String toString() { return line; }
	}
}
