package edu.uab.ssg.io.hapmap;

import java.util.*;
import java.io.*;

/**
 * A parser for the allele frequency file format for the HapMap phase II data located at <a href="http://hapmap.ncbi.nlm.nih.gov/downloads/frequencies/latest_phaseII_ncbi_b36/">http://hapmap.ncbi.nlm.nih.gov/downloads/frequencies/latest_phaseII_ncbi_b36/</a>.
 * The file format is described in the README at <a href="http://hapmap.ncbi.nlm.nih.gov/downloads/frequencies/latest_phaseII_ncbi_b36/00README.txt">http://hapmap.ncbi.nlm.nih.gov/downloads/frequencies/latest_phaseII_ncbi_b36/00README.txt</a>. An excerpt is below:
 *
 * <p><tt>
 * Col1: refSNP rs# identifer in dbSNP<br/>
 * Col2: chromosome SNP maps to on current reference sequence<br/>
 * Col3: chromosome position on reference sequence<br/>
 * Col4: strand of reference sequence<br/>
 * Col5: reference sequence build version (currently NCBI build34)<br/>
 * Col6: genotyping center that submitted underlying genotypes<br/>
 * Col7: LSID for HapMap protocol used for genotyping<br/>
 * Col8: LSID for HapMap assay used for genotyping<br/>
 * Col9: LSID for panel of individuals genotyped<br/>
 * Col10: QC-code, currently 'QC+' for all entries (for future use)<br/>
 * Col11: reference allele<br/>
 * Col12: frequency of ref-allele<br/>
 * Col13: number of ref-alleles observed<br/>
 * Col14: other allele<br/>
 * Col15: frequency of other allele<br/>
 * Col16: number of other alleles observed<br/>
 * Col17: total number of chromosomes observed<br/>
 * </tt></p>
 * An example, with selected records from ,llele_freqs_chr19_CEU_r24_nr.b36_fwd.txt is below:
 * <p><tt>
 * rs# chrom pos strand build center protLSID assayLSID panelLSID QC_code refallele refallele_freq refallele_count otherallele otherallele_freq otherallele_count totalcount<br/>
 * rs2261761 chr19 40310 + ncbi_b36 perlegen urn:lsid:perlegen.hapmap.org:Protocol:Genotyping_1.0.0:2 urn:lsid:perlegen.hapmap.org:Assay:25761.7226392:1 urn:lsid:dcc.hapmap.org:Panel:CEPH-30-trios:1 QC+ G 1.000 118 A 0 0 118<br/>
 * rs4046307 chr19 195999 + ncbi_b36 imsut-riken urn:lsid:imsut-riken.hapmap.org:Protocol:genotyping:1 urn:lsid:imsut-riken.hapmap.org:Assay:4068315:1 urn:lsid:dcc.hapmap.org:Panel:CEPH-30-trios:1 QC+ C 1.000 120 G 0 0 120<br/>
 * rs7247199 chr19 204938 + ncbi_b36 imsut-riken urn:lsid:imsut-riken.hapmap.org:Protocol:genotyping:1 urn:lsid:imsut-riken.hapmap.org:Assay:7247199:2 urn:lsid:dcc.hapmap.org:Panel:CEPH-30-trios:1 QC+ A 0.400 48 G 0.600 72 120<br/>
 * rs8102643 chr19 207859 + ncbi_b36 perlegen urn:lsid:perlegen.hapmap.org:Protocol:Genotyping_1.0.0:2 urn:lsid:perlegen.hapmap.org:Assay:25766.1594198:1 urn:lsid:dcc.hapmap.org:Panel:CEPH-30-trios:1 QC+ T 0.393 44 C 0.607 68 112<br/>
 * ...
 *
 * </tt><p>
 *
 * The field delimiter is the space character.
 *
 * @author Jelai Wang
 */
public final class AlleleFreqFileParser {
	private static final String DELIMITER = " ";
	private static final int EXPECTED_NUM_OF_FIELDS = 17;

	/**
	 * Constructs the parser.
	 */
	public AlleleFreqFileParser() {
	}
	
	/**
	 * Parses the input stream for allele frequency records.
	 * @param in The input stream, typically a file input stream, of the
	 * file to be parsed.
	 * @param listener As the input stream is parsed, each allele frequency 
	 * record is passed to the user-supplied record listener.
	 */
	public void parse(InputStream in, RecordListener listener) throws IOException {
		if (in == null)
			throw new NullPointerException("in");
		if (listener == null)
			throw new NullPointerException("listener");
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		// Check for expected header.
		String headerLine = reader.readLine();
		String[] header = headerLine.split(DELIMITER, 0);
		if (header.length != EXPECTED_NUM_OF_FIELDS)
			throw new IllegalArgumentException(String.valueOf(header.length));
		// Spot check for presence of a few important fields.
		if (!"rs#".equals(header[0]))
			throw new IllegalArgumentException(header[0]);
		if (!"chrom".equals(header[1]))
			throw new IllegalArgumentException(header[2]);
		if (!"pos".equals(header[2]))
			throw new IllegalArgumentException(header[2]);
		if (!"strand".equals(header[3]))
			throw new IllegalArgumentException(header[3]);
		if (!"build".equals(header[4]))
			throw new IllegalArgumentException(header[4]);
		if (!"refallele".equals(header[10]))
			throw new IllegalArgumentException(header[10]);
		if (!"refallele_freq".equals(header[11]))
			throw new IllegalArgumentException(header[11]);
		if (!"refallele_count".equals(header[12]))
			throw new IllegalArgumentException(header[12]);
		if (!"otherallele".equals(header[13]))
			throw new IllegalArgumentException(header[13]);
		if (!"otherallele_freq".equals(header[14]))
			throw new IllegalArgumentException(header[14]);
		if (!"otherallele_count".equals(header[15]))
			throw new IllegalArgumentException(header[15]);
		if (!"totalcount".equals(header[16]))
			throw new IllegalArgumentException(header[16]);
		// Parse the rest of the rows.
		String line = null;
		while ((line = reader.readLine()) != null) {
			try {
				listener.handleParsedRecord(new ParsedAlleleFreqRecord(line));
			}
			catch (IllegalArgumentException e) {
				listener.handleBadRecordFormat(line);
				continue;
			}
		}
	}

	/**
	 * A listener for handling parsed allele frequency records and problems
	 * due to bad record formatting.
	 */
	public interface RecordListener {
		/**
		 * Handles successfully parsed allele frequency record.
		 */
		void handleParsedRecord(AlleleFreqRecord record);

		/**
		 * Handles input that could not be parsed due to a formatting problem.
		 * @param line The line of text that could not be parsed.
		 */
		void handleBadRecordFormat(String line);
	}

	/**
	 * An allele frequency record.
	 */
	public interface AlleleFreqRecord {
		/**
		 * Returns the refSNP rs# identifier in dbSNP.
		 */
		String getRsnum();

		/**
		 * Returns the chromosome the SNP maps to on current reference sequence.
		 */
		String getChrom();

		/**
		 * Returns the chromosome position on reference sequence.
		 */
		int getPos();

		/**
		 * Returns the strand of reference sequence.
		 */
		String getStrand();

		/**
		 * Returns the reference sequence build version.
		 */
		String getBuild();

		/**
		 * Returns the genotyping center that submitted underlying genotypes.
		 */
		String getCenter();

		/**
		 * Returns the LSID for HapMap protocol used for genotyping.
		 */
		String getProtLSID();

		/**
		 * Returns the LSID for HapMap assay used for genotyping.
		 */
		String getAssayLSID();

		/**
		 * Returns the LSID for panel of individuals genotyped.
		 */
		String getPanelLSID();

		/**
		 * Returns the QC-code, currently 'QC+' for all entries (for future use).
		 */
		String getQC_code();

		/**
		 * Returns the reference allele.
		 */
		String getRefAllele();

		/**
		 * Returns the frequency of reference allele.
		 */
		double getRefAlleleFreq();

		/**
		 * Returns the number of reference alleles observed.
		 */
		int getRefAlleleCount();

		/**
		 * Returns the other allele.
		 */
		String getOtherAllele();

		/**
		 * Returns the frequency of other allele.
		 */
		double getOtherAlleleFreq();

		/**
		 * Returns the number of other alleles observed.
		 */
		int getOtherAlleleCount();

		/**
		 * Returns the total number of chromosomes observed.
		 */
		int getTotalCount();
	}

	private class ParsedAlleleFreqRecord implements AlleleFreqRecord {
		private String line;
		private String rsnum;
		private int pos;
		private String chrom;
		private String strand;
		private String build, center, protLSID, assayLSID, panelLSID, qcCode;
		private String refAllele, otherAllele;
		private double refAlleleFreq, otherAlleleFreq;
		private int refAlleleCount, otherAlleleCount, totalCount;

		private ParsedAlleleFreqRecord(String line) {
			if (line == null)
				throw new NullPointerException("line");
			this.line = line;
			String[] tokens = line.split(DELIMITER, -1);
			if (tokens.length != EXPECTED_NUM_OF_FIELDS)
				throw new IllegalArgumentException(tokens.length + " " + line);

			this.rsnum = tokens[0];
			this.chrom = tokens[1];
			this.pos = Integer.parseInt(tokens[2]);
			this.strand = tokens[3];

			this.build = tokens[4];
			this.center = tokens[5];
			this.protLSID = tokens[6];
			this.assayLSID = tokens[7];
			this.panelLSID = tokens[8];
			this.qcCode = tokens[9];

			this.refAllele = tokens[10];
			this.refAlleleFreq = Double.parseDouble(tokens[11]);
			this.refAlleleCount = Integer.parseInt(tokens[12]);
			this.otherAllele = tokens[13];
			this.otherAlleleFreq = Double.parseDouble(tokens[14]);
			this.otherAlleleCount = Integer.parseInt(tokens[15]);
			this.totalCount = Integer.parseInt(tokens[16]);
		}

		public String getRsnum() { return rsnum; }
		public int getPos() { return pos; }
		public String getChrom() { return chrom; }
		public String getStrand() { return strand; }

		public String getBuild() { return build; }
		public String getCenter() { return center; }
		public String getProtLSID() { return protLSID; }
		public String getAssayLSID() { return assayLSID; }
		public String getPanelLSID() { return panelLSID; }
		public String getQC_code() { return qcCode; }

		public String getRefAllele() { return refAllele; }
		public double getRefAlleleFreq() { return refAlleleFreq; }
		public int getRefAlleleCount() { return refAlleleCount; }

		public String getOtherAllele() { return otherAllele; }
		public double getOtherAlleleFreq() { return otherAlleleFreq; }
		public int getOtherAlleleCount() { return otherAlleleCount; }

		public int getTotalCount() { return totalCount; }

		public String toString() { return line; }
	}
}
