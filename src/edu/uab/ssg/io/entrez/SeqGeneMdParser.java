package edu.uab.ssg.io.entrez;

import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 *	A parser for the Entrez Gene seq_gene.md file located at <a href="ftp://ftp.ncbi.nih.gov/genomes/H_sapiens/mapview/seq_gene.md.gz">ftp://ftp.ncbi.nih.gov/genomes/H_sapiens/mapview/seq_gene.md.gz</a>.
 *	<p>An abbreviated example is shown below:</p>
 *	<p><tt>
 *	#tax_id chromosome      chr_start       chr_stop        chr_orient      contig ctg_start        ctg_stop        ctg_orient      feature_name    feature_id     feature_type     group_label     transcript      evidence_code<br/>
 *	9606    1       716     2038    +       NW_001838563.2  3842    5164    -      LOC100131754     GeneID:100131754        GENE    HuRef-Primary Assembly  -      <br/>
 *	9606    1       716     2038    +       NW_001838563.2  3842    5164    -      LOC100131754     GeneID:100131754        PSEUDO  HuRef-Primary Assembly  -      <br/>
 *	9606    1       716     2038    +       NW_001838563.2  3842    5164    -      XR_112033.1      GeneID:100131754        RNA     HuRef-Primary Assembly  XR_112033.1     <br/>
 *	9606    1       716     1724    +       NW_001838563.2  4156    5164    -      XR_112033.1      GeneID:100131754        UTR     HuRef-Primary Assembly  XR_112033.1     -<br/>
 *	...
 *	</tt></p>
 * 	<p>The field delimiter is a tab character. The dash character, '-', and sometimes the empty string, in fields like transcript and evidence code, indicates that a value is not available.</p>
 *
 *	@author Jelai Wang
 */
public final class SeqGeneMdParser {
	private static final String DELIMITER = "\t";
	private static final String NOT_AVAILABLE = "-";

	/**
	 *	Constructs the parser.
	 */
	public SeqGeneMdParser() {
	}

	/**
	 *	Parses the input stream for gene info records.
	 */
	public List<Record> parse(InputStream in) throws IOException {
		if (in == null)
			throw new NullPointerException("in");
		List<Record> list = new ArrayList<Record>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = null;
		while ((line = reader.readLine()) != null) {
			// Skip comments.
			if (line.startsWith("#")) continue;
			String[] tmp = line.split(DELIMITER, -1);
			if (tmp.length != 15) {
				System.err.println("Ignoring line with " + tmp.length + " tokens: " + line);
				continue;
			}

			String taxID = tmp[0];
			String chromosome = tmp[1];
			int chrStart = Integer.parseInt(tmp[2]);
			int chrStop = Integer.parseInt(tmp[3]);
			String chrOrient = tmp[4];
			String contig = tmp[5];
			int ctgStart = Integer.parseInt(tmp[6]);
			int ctgStop = Integer.parseInt(tmp[7]);
			String ctgOrient = tmp[8];
			String featureName = tmp[9];
			String featureID = tmp[10];
			String featureType = tmp[11];
			String groupLabel = tmp[12];
			String transcript = tmp[13];
			String evidenceCode = tmp[14];

			DefaultRecord record = new DefaultRecord(line, taxID, chromosome, chrStart, chrStop, chrOrient, contig, ctgStart, ctgStop, ctgOrient, featureName, featureID, featureType, groupLabel, transcript, evidenceCode);
			list.add(record);
		}
		reader.close();
		return list;
	}

	/**
	 *	A gene info record.
	 */
	public interface Record {
		/**
		 *	Returns the unique identifier provided by the NCBI Taxonomy for the species or strain/isolate.
		 */
		String getTaxID();

		/**
		 * Returns the chromosome name.
		 */
		String getChromosome();

		/**
		 * Returns the start position of this feature on the chromosome for this assembly.
		 */
		int getChrStart();

		/**
		 * Returns the stop position of this feature on the chromosome for this assembly.
		 */
		int getChrStop();

		/**
		 * Returns the orientation of this feature on the chromosome for this asembly.
		 */
		String getChrOrient();

		/**
		 * Returns the contig name.
		 */
		String getContig();

		/**
		 * Returns the start position of this feature on the contig.
		 */
		int getCtgStart();

		/**
		 * Returns the stop position of this feature on the contig.
		 */
		int getCtgStop();

		/**
		 * Returns the orientation of this feature on the contig.
		 */
		String getCtgOrient();

		/**
		 * Returns the name of this feature.
		 */
		String getFeatureName();

		/**
		 * Returns the ID of this feature.
		 */
		String getFeatureID();

		/**
		 * Returns the type of this feature, usually GENE, PSEUDO, RNA, UTR, or CDS.
		 */
		String getFeatureType();

		/**
		 * Returns the group label, usually the name and version of the assembly.
		 */
		String getGroupLabel();

		/**
		 * Returns the transcript name (for RNA-related features).
		 */
		String getTranscript();

		/**
		 * Returns the evidence code.
		 */
		String getEvidenceCode();
	}

	private class DefaultRecord implements Record {
		private String line;
		private String taxID;
		private String chromosome, chrOrient;
		private int chrStart, chrStop;
		private String contig, ctgOrient;
		private int ctgStart, ctgStop;
		private String featureName, featureID, featureType;
		private String groupLabel, transcript, evidenceCode;

		private DefaultRecord(String line, String taxID, String chromosome, int chrStart, int chrStop, String chrOrient, String contig, int ctgStart, int ctgStop, String ctgOrient, String featureName, String featureID, String featureType, String groupLabel, String transcript, String evidenceCode) {
			this.line = line;
			this.taxID = taxID;
			this.chromosome = chromosome;
			this.chrStart = chrStart;
			this.chrStop = chrStop;
			this.chrOrient = chrOrient;
			this.contig = contig;
			this.ctgStart = ctgStart;
			this.ctgStop = ctgStop;
			this.ctgOrient = ctgOrient;
			this.featureName = featureName;
			this.featureID = featureID;
			this.featureType = featureType;
			this.groupLabel = groupLabel;
			this.transcript = transcript;
			this.evidenceCode = evidenceCode;
		}

		public String getTaxID() { return taxID; }
		public String getChromosome() { return chromosome; }
		public int getChrStart() { return chrStart; }
		public int getChrStop() { return chrStop; }
		public String getChrOrient() { return chrOrient; }
		public String getContig() { return contig; }
		public int getCtgStart() { return ctgStart; }
		public int getCtgStop() { return ctgStop; }
		public String getCtgOrient() { return ctgOrient; }
		public String getFeatureName() { return featureName; }
		public String getFeatureID() { return featureID; }
		public String getFeatureType() { return featureType; }
		public String getGroupLabel() { return groupLabel; }
		public String getTranscript() { return transcript; }
		public String getEvidenceCode() { return evidenceCode; }

		public String toString() { return line; }
	}
}
