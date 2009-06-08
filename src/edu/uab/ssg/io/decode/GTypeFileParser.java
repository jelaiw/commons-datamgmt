package edu.uab.ssg.io.decode;

import java.util.*;
import java.io.*;

/**
 * A parser for the <i>JHY_GType.txt</i> file format from deCODE containing A/B allele calls.
 * An example is below:
 * <p><tt>
 * Name	Chr	Position	AVA1001.GType	AVA1002.GType	AVA1007.GType	AVA1011.GType	AVA1017_1.GType	AVA1017.GType	AVA1018.GType<br/>
 * AICDA-007875	12	8650011	BB	BB	BB	BB	BB	BB	BB<br/>
 * AICDA-011435	12	8646452	AA	AA	AA	AA	AA	AA	AA<br/>
 * BLR1-011551	11	118269327	BB	BB	BB	BB	AB	AB	BB<br/>
 * BLR1-014511	11	118272286	BB	BB	BB	BB	NC	BB	NC<br/>
 * CCL24-004508	7	75278461	NC	NC	NC	NC	NC	NC	NC<br/>
 * CCR3-000155	3	46257282	BB	BB	BB	BB	AB	AB	BB<br/>
 * CCR4-000898	3	32966966	AB	AB	BB	AA	BB	BB	BB<br/>
 * ...
 * </tt></p>
 *
 * The field delimiter is the tab character.
 *
 * @author Jelai Wang
 */
public final class GTypeFileParser {
	/**
	 * Constructs the parser.
	 */
	public GTypeFileParser() {
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

		String[] header = reader.readLine().split("\t", -1);
		if (!"Name".equals(header[0]))
			throw new IllegalArgumentException(header[0]);
		if (!"Chr".equals(header[1]))
			throw new IllegalArgumentException(header[1]);
		if (!"Position".equals(header[2]))
			throw new IllegalArgumentException(header[2]);

		List<String> sampleNames = Collections.unmodifiableList(Arrays.asList(header).subList(3, header.length));

		String line = null;
		while ((line = reader.readLine()) != null) {
			SNPRecord record = null;
			try {
				record = new ParsedSNPRecord(line, sampleNames);
			}
			catch (RuntimeException e) {
				listener.handleBadRecordFormat(line);
				continue;
			}
			listener.handleParsedRecord(record);
		}
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
		/**
		 * Returns the SNP name.
		 */
		String getName();

		/**
		 * Returns the chromosome name.
		 */
		String getChr();

		/**
		 * Returns the position of this SNP on the chromosome.
		 */
		int getPosition();

		/**
		 * Returns the samples for which this record contains genotypes.
		 */
		List<String> getSampleNames();

		/**
		 * Returns true if this record contains a genotype (at this SNP) for the given sample.
		 */
		boolean existsGenotype(String sampleName);

		/**
		 * Returns the genotype at this SNP for the given sample.
		 */
		String getGenotype(String sampleName);
	}

	private class ParsedSNPRecord implements SNPRecord {
		private String line;
		private String name, chr;
		private int position;
		private Map<String, String> sampleNameToGenotype;

		private ParsedSNPRecord(String line, List<String> sampleNames) {
			if (line == null)
				throw new NullPointerException("line");
			this.line = line;
			this.sampleNameToGenotype = new LinkedHashMap<String, String>();

			StringTokenizer tokenizer = new StringTokenizer(line, "\t");
			if (tokenizer.countTokens() != sampleNames.size() + 3)
				throw new IllegalArgumentException(line);
			this.name = tokenizer.nextToken();
			this.chr = tokenizer.nextToken();
			this.position = Integer.parseInt(tokenizer.nextToken());

			int index = 0;
			while (tokenizer.hasMoreTokens()) {
				String genotype = tokenizer.nextToken();
				String sampleName = sampleNames.get(index++);
				sampleNameToGenotype.put(sampleName, genotype);
			}
		}

		public String getName() { return name; }
		public String getChr() { return chr; }
		public int getPosition() { return position; }
		public List<String> getSampleNames() { return Collections.unmodifiableList(new ArrayList<String>(sampleNameToGenotype.keySet())); }
		public boolean existsGenotype(String sampleName) { return sampleNameToGenotype.containsKey(sampleName); }

		public String getGenotype(String sampleName) {
			if (sampleName == null)
				throw new NullPointerException("sampleName");
			if (!sampleNameToGenotype.containsKey(sampleName))
				throw new IllegalArgumentException(sampleName);
			return sampleNameToGenotype.get(sampleName);
		}

		public String toString() { return line; }
	}
}
