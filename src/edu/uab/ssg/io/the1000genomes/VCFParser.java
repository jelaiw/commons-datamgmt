package edu.uab.ssg.io.the1000genomes;

import java.util.*;
import java.io.*;

/**
 * A parser for the VCF (Variant Call Format) file format described at <a href="http://www.1000genomes.org/wiki/Analysis/Variant%20Call%20Format/vcf-variant-call-format-version-40">http://www.1000genomes.org/wiki/Analysis/Variant%20Call%20Format/vcf-variant-call-format-version-40</a>.
 * An example is shown below:
 *
 * <p><tt>
 * ##fileformat=VCFv4.0<br/>
 * ##fileDate=20090805<br/>
 * ##source=myImputationProgramV3.1<br/>
 * ##reference=1000GenomesPilot-NCBI36<br/>
 * ##phasing=partial<br/>
 * ##INFO=&lt;ID=NS,Number=1,Type=Integer,Description="Number of Samples With Data"&gt;<br/>
 * ##INFO=&lt;ID=DP,Number=1,Type=Integer,Description="Total Depth"&gt;<br/>
 * ##INFO=&lt;ID=AF,Number=.,Type=Float,Description="Allele Frequency"&gt;<br/>
 * ##INFO=&lt;ID=AA,Number=1,Type=String,Description="Ancestral Allele"&gt;<br/>
 * ##INFO=&lt;ID=DB,Number=0,Type=Flag,Description="dbSNP membership, build 129"&gt;<br/>
 * ##INFO=&lt;ID=H2,Number=0,Type=Flag,Description="HapMap2 membership"&gt;<br/>
 * ##FILTER=&lt;ID=q10,Description="Quality below 10"&gt;<br/>
 * ##FILTER=&lt;ID=s50,Description="Less than 50% of samples have data"&gt;<br/>
 * ##FORMAT=&lt;ID=GT,Number=1,Type=String,Description="Genotype"&gt;<br/>
 * ##FORMAT=&lt;ID=GQ,Number=1,Type=Integer,Description="Genotype Quality"&gt;<br/>
 * ##FORMAT=&lt;ID=DP,Number=1,Type=Integer,Description="Read Depth"&gt;<br/>
 * ##FORMAT=&lt;ID=HQ,Number=2,Type=Integer,Description="Haplotype Quality"&gt;<br/>
 * #CHROM	POS	ID	REF	ALT	QUAL	FILTER	INFO	FORMAT	NA00001	NA00002	NA00003<br/>
 * 20	14370	rs6054257	G	A	29	PASS	NS=3;DP=14;AF=0.5;DB;H2	GT:GQ:DP:HQ	0|0:48:1:51,51	1|0:48:8:51,51	1/1:43:5:.,.<br/>
 * 20	17330	.	T	A	3	q10	NS=3;DP=11;AF=0.017	GT:GQ:DP:HQ	0|0:49:3:58,50	0|1:3:5:65,3	0/0:41:3<br/>
 * 20	1110696	rs6040355	A	G,T	67	PASS	NS=2;DP=10;AF=0.333,0.667;AA=T;DB	GT:GQ:DP:HQ	1|2:21:6:23,27	2|1:2:0:18,2	2/2:35:4<br/>
 * 20	1230237	.	T	.	47	PASS	NS=3;DP=13;AA=T	GT:GQ:DP:HQ	0|0:54:7:56,60	0|0:48:4:51,51	0/0:61:2<br/>
 * 20	1234567	microsat1	GTCT	G,GTACT	50	PASS	NS=3;DP=9;AA=G	GT:GQ:DP	0/1:35:4	0/2:17:2	1/1:40:3<br/>
 * </tt></p>
 *
 * <p>The field delimiter is the tab character and missing values are coded with a period, '.',  character.</p>
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
		/**
		 * Returns the chromosome.
		 */
		String getChromosome();

		/**
		 * Returns the position of this variant on the chromosome.
		 */
		int getPosition();

		/**
		 * Returns the id for this variant.
		 */
		String getID();

		/**
		 * Returns the reference allele.
		 */
		String getReferenceAllele();

		/**
		 * Returns the alternate, non-reference allele.
		 */
		String getAlternateAllele();

		/**
		 * Returns the list of samples.
		 */
		List<String> getSamples();

		/**
		 * Returns allele 1 for the given sample for this variant.
		 */
		String getAllele1(String sample);

		/**
		 * Returns allele 2 for the given sample for this variant.
		 */
		String getAllele2(String sample);

		/**
		 * Returns the reference or alternate allele, corresponding to the given GT code.
		 */
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
