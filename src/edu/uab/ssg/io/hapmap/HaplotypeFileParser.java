package edu.uab.ssg.io.hapmap;

import java.util.*;
import java.io.*;

/**
 * A parser for the "snps x haplotype" file format for the HapMap phase3 data located at <a href="http://ftp.hapmap.org/phasing/2009-02_phaseIII/HapMap3_r2/">http://ftp.hapmap.org/phasing/2009-02_phaseIII/HapMap3_r2/</a>.
 * An example, with selected records from ASW/UNRELATED/hapmap3_r2_b36_fwd.consensus.qc.poly.chr1_asw.unr.phased, is below:
 *
 * <p><tt>
 * rsID position_b36 NA19904_A NA19904_B NA20340_A NA20340_B NA20297_A NA20297_B NA<br/>
 * 20281_A NA20281_B NA20348_A NA20348_B NA20300_A NA20300_B NA19625_A NA19625_B NA<br/>
 * 20279_A NA20279_B NA19712_A NA19712_B NA20322_A NA20322_B NA19711_A NA19711_B NA<br/>
 * 19708_A NA19708_B NA20341_A NA20341_B <br/>
 * rs10458597 554484 C C C C C C C C C C C C C C C C C C C C C C C C C C <br/>
 * rs11240767 718814 C C C C C C C T C C C C C C C C C C T T C C C C C C <br/>
 * rs3131972 742584 A A G G A A A A A G A A A G A G A G A A A G G G A G <br/>
 * rs3131969 744045 A A G G A A A A A G A A A G G G A G A A G G G G A G <br/>
 * rs3131967 744197 T C C C T T T T T C T T T C C C C C T T C C C C T C <br/>
 * ...
 * </tt></p>
 *
 * The field delimiter is the space character. The file format is described in more detail at <a href="http://ftp.hapmap.org/phasing/2009-02_phaseIII/HapMap3_r2/readme.txt">http://ftp.hapmap.org/phasing/2009-02_phaseIII/HapMap3_r2/readme.txt</a>. Also see <a href="http://ftp.hapmap.org/phasing/2009-02_phaseIII/HapMap3_r2/hapmap3_r2_phasing_summary.doc">http://ftp.hapmap.org/phasing/2009-02_phaseIII/HapMap3_r2/hapmap3_r2_phasing_summary.doc</a>.
 *
 * @author Jelai Wang
 */
public final class HaplotypeFileParser {
	/**
	 * Constructs the parser.
	 */
	public HaplotypeFileParser() {
	}
	
	/**
	 * Parses the input stream for "SNP x haplotype" records.
	 * @param in The input stream, typically a file input stream, of the
	 * file to be parsed.
	 * @param listener As the input stream is parsed, each SNP record
	 * is passed to the user-supplied record listener.
	 */
public void parse(InputStream in, RecordListener listener) throws IOException {
		if (in == null)
			throw new NullPointerException("in");
		if (listener == null)
			throw new NullPointerException("listener");
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));

		String[] header = reader.readLine().split(" ", 0);
		if (!"rsID".equals(header[0]))
			throw new IllegalArgumentException(header[0]);
		if (!"position_b36".equals(header[1]))
			throw new IllegalArgumentException(header[1]);

		List<String> sampleNames = Collections.unmodifiableList(Arrays.asList(header).subList(2, header.length));
		if (sampleNames.size() % 2 != 0) // We expect pairs of sample names.
			throw new IllegalArgumentException(sampleNames.toString());

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
	 * A "SNP x haplotype" record.
	 * The definition of A and B is available at <a href="http://ftp.hapmap.org/phasing/2009-02_phaseIII/HapMap3_r2/readme.txt">http://ftp.hapmap.org/phasing/2009-02_phaseIII/HapMap3_r2/readme.txt</a>.
	 */
	public interface SNPRecord {
		/**
		 * Returns the SNP name.
		 */
		String getName();

		/**
		 * Returns the position of this SNP on the chromosome.
		 * The chromosome and position for a given SNP with an rsid is probably best retrieved directly from an authoritative source, like, dbSNP, but in a pinch, this info also appears to be encoded in the file name.  
		 * For example, for <i>hapmap3_r2_b36_fwd.consensus.qc.poly.chr1_asw.unr.phased</i>, the chromosome is "chr1" and the position is from "b36".
		 */
		int getPosition();

		/**
		 * Returns a list of sample names for which there are haplotype data.
		 * In other words, a list containing the IIDs of the individuals that
		 * have been phased.
		 */
		List<String> getSampleNames();

		/**
		 * Returns true if the A allele for the given sample exists.
		 */
		boolean existsAlleleA(String sampleName);

		/**
		 * Returns true if the B allele for the given sample exists.
		 */
		boolean existsAlleleB(String sampleName);

		/**
		 * Returns the A allele for the given sample.
		 */
		String getAlleleA(String sampleName);

		/**
		 * Returns the B allele for the given sample.
		 */
		String getAlleleB(String sampleName);
	}

	private class ParsedSNPRecord implements SNPRecord {
		private String line;
		private String name;
		private int position;
		private Map<String, String> sampleNameToAllele;

		private ParsedSNPRecord(String line, List<String> sampleNames) {
			if (line == null)
				throw new NullPointerException("line");
			this.line = line;
			this.sampleNameToAllele = new LinkedHashMap<String, String>();

			StringTokenizer tokenizer = new StringTokenizer(line);
			if (tokenizer.countTokens() != sampleNames.size() + 2)
				throw new IllegalArgumentException(line);
			this.name = tokenizer.nextToken();
			this.position = Integer.parseInt(tokenizer.nextToken());

			int index = 0;
			while (tokenizer.hasMoreTokens()) {
				String allele = tokenizer.nextToken();
				String sampleName = sampleNames.get(index++);
				sampleNameToAllele.put(sampleName, allele);
			}
		}

		public String getName() { return name; }
		public int getPosition() { return position; }

		public List<String> getSampleNames() { 
			Set<String> set = new LinkedHashSet<String>();
			for (Iterator<String> it = sampleNameToAllele.keySet().iterator(); it.hasNext(); ) {
				String sampleName = it.next();
				set.add(sampleName.substring(0, sampleName.lastIndexOf('_')));
			}
			return new ArrayList<String>(set);
		}

		public boolean existsAlleleA(String sampleName) { return sampleNameToAllele.containsKey(sampleName + "_A"); }
		public boolean existsAlleleB(String sampleName) { return sampleNameToAllele.containsKey(sampleName + "_B"); }

		public String getAlleleA(String sampleName) {
			if (sampleName == null)
				throw new NullPointerException("sampleName");
			if (!sampleNameToAllele.containsKey(sampleName + "_A"))
				throw new IllegalArgumentException(sampleName);
			return sampleNameToAllele.get(sampleName + "_A");
		}

		public String getAlleleB(String sampleName) {
			if (sampleName == null)
				throw new NullPointerException("sampleName");
			if (!sampleNameToAllele.containsKey(sampleName + "_B"))
				throw new IllegalArgumentException(sampleName);
			return sampleNameToAllele.get(sampleName + "_B");
		}

		public String toString() { return line; }
	}
}
