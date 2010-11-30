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
 * 	<p>The field delimiter is a tab character. The dash character, '-', indicates that a value is not available, and will be returned as null in this API.</p>
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
			/*
			// Replace values coded as "-" with null.
			for (int i = 0; i < tmp.length; i++) {
				if (NOT_AVAILABLE.equals(tmp[i])) tmp[i] = null;
			}
			*/

			String taxID = tmp[0];
			String chromosome = tmp[1];
			String chrStart = tmp[2];
			String chrStop = tmp[3];
			String chrOrient = tmp[4];
			String contig = tmp[5];
			String ctgStart = tmp[6];
			String ctgStop = tmp[7];
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

		String getChromosome();
		String getChrStart();
		String getChrStop();
		String getChrOrient();
		String getContig();
		String getCtgStart();
		String getCtgStop();
		String getCtgOrient();
		String getFeatureName();
		String getFeatureID();
		String getFeatureType();
		String getGroupLabel();
		String getTranscript();
		String getEvidenceCode();
	}

	private class DefaultRecord implements Record {
		private String line;
		private String taxID;
		private String chromosome, chrStart, chrStop, chrOrient;
		private String contig, ctgStart, ctgStop, ctgOrient;
		private String featureName, featureID, featureType;
		private String groupLabel, transcript, evidenceCode;

		private DefaultRecord(String line, String taxID, String chromosome, String chrStart, String chrStop, String chrOrient, String contig, String ctgStart, String ctgStop, String ctgOrient, String featureName, String featureID, String featureType, String groupLabel, String transcript, String evidenceCode) {
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
		public String getChrStart() { return chrStart; }
		public String getChrStop() { return chrStop; }
		public String getChrOrient() { return chrOrient; }
		public String getContig() { return contig; }
		public String getCtgStart() { return ctgStart; }
		public String getCtgStop() { return ctgStop; }
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
