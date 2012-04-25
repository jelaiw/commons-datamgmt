package edu.uab.ssg.reports;

import edu.uab.ssg.io.ncbi.*;
import java.util.*;
import java.io.*;
import java.util.zip.GZIPInputStream;

/**
 *	@author Jelai Wang
 */
public class MarkerAnnotationReport {
	public static void main(String[] args) throws IOException {
		String seqMdFileName = args[0];
		String groupLabel = args[1];
		String geneInfoFileName = args[2];

		Map<String, List<SeqGeneMdParser.Record>> chr2md = parseSeqGeneMd(seqMdFileName, groupLabel);
		Map<String, GeneInfoParser.Record> entrez2geneinfo = parseGeneInfo(geneInfoFileName);

		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String DELIMITER = "\t";
		String line = null;
		while ((line = in.readLine()) != null) {
			String[] tokens = line.split(DELIMITER, -1);
			if (tokens.length < 3) // We expect at least chr snp bp fields.
				throw new RuntimeException(line);
			String chr = tokens[0];
			String snp = tokens[1];
			int pos = Integer.parseInt(tokens[2]);

			StringBuilder builder = new StringBuilder();
			builder.append(line);

			// Grab NCBI gene name and boundaries.
			SeqGeneMdParser.Record seqGeneMdRecord = findRecord(chr, pos, chr2md);
			if (seqGeneMdRecord != null) {
				String featureID = seqGeneMdRecord.getFeatureID();
				String[] tmp = featureID.split(":");
				String entrez = tmp[1];

				if (entrez2geneinfo.containsKey(entrez)) {
					GeneInfoParser.Record geneInfoRecord = entrez2geneinfo.get(entrez);
					String symbol = geneInfoRecord.getSymbol();
					int chrStart = seqGeneMdRecord.getChrStart();
					int chrStop = seqGeneMdRecord.getChrStop();
					builder.append(DELIMITER).append(entrez);
					builder.append(DELIMITER).append(symbol);
					builder.append(DELIMITER).append(chrStart);
					builder.append(DELIMITER).append(chrStop);
				}
				else { // See LOC100129610 for an example.
					builder.append(DELIMITER).append(entrez);
					builder.append(DELIMITER).append("");
					builder.append(DELIMITER).append("");
					builder.append(DELIMITER).append("");
				}
			}
			else {
				builder.append(DELIMITER).append("");
				builder.append(DELIMITER).append("");
				builder.append(DELIMITER).append("");
				builder.append(DELIMITER).append("");
			}
			System.out.println(builder.toString());
		}
	}

	private static SeqGeneMdParser.Record findRecord(String chr, int pos, Map<String, List<SeqGeneMdParser.Record>> chr2md) {
		if (chr2md.containsKey(chr)) {
			List<SeqGeneMdParser.Record> list = chr2md.get(chr);
			for (int i = 0; i < list.size(); i++) {
				SeqGeneMdParser.Record record = list.get(i);
				int chrStart = record.getChrStart();
				int chrStop = record.getChrStop();
				if (pos >= chrStart && pos <= chrStop) return record;
			}
		}
		return null;
	}

	private static Map<String, GeneInfoParser.Record> parseGeneInfo(String geneInfoFileName) throws IOException {
		Map<String, GeneInfoParser.Record> map = new LinkedHashMap<String, GeneInfoParser.Record>();
		GeneInfoParser parser = new GeneInfoParser();
		List<GeneInfoParser.Record> list = parser.parse(new GZIPInputStream(new FileInputStream(geneInfoFileName)));
		for (int i = 0; i < list.size(); i++) {
			GeneInfoParser.Record record = list.get(i);
			if (!map.containsKey(record.getGeneID())) {
				map.put(record.getGeneID(), record);
			}
			else { // Checking our assumption that the Entrez Gene ID is unique.
				System.err.println(record.toString());
			}
		}
		return map;
	}

	private static Map<String, List<SeqGeneMdParser.Record>> parseSeqGeneMd(String seqMdFileName, final String groupLabel) throws IOException {
		Map<String, List<SeqGeneMdParser.Record>> map = new LinkedHashMap<String, List<SeqGeneMdParser.Record>>(); 
		SeqGeneMdParser parser = new SeqGeneMdParser();
		List<SeqGeneMdParser.Record> records = parser.parse(new GZIPInputStream(new FileInputStream(seqMdFileName)), new SeqGeneMdParser.RecordFilter() {
			public boolean acceptRecord(SeqGeneMdParser.Record record) {
				if ("GENE".equals(record.getFeatureType()) && groupLabel.equals(record.getGroupLabel())) return true;
				return false;
			}
		});
		for (int i = 0; i < records.size(); i++) {
			SeqGeneMdParser.Record record = records.get(i);
			String chr = record.getChromosome();
			List<SeqGeneMdParser.Record> list = map.get(chr);
			if (list == null) {
				list = new ArrayList<SeqGeneMdParser.Record>();
				map.put(chr, list);
			}
			list.add(record);
		}
		return map;
	}
}
