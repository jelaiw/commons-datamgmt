package edu.uab.ssg.reports;

import edu.uab.ssg.io.entrez.*;
import java.util.*;
import java.io.*;
import java.util.zip.GZIPInputStream;

public final class GeneReport {
	private static final String DELIMITER = "\t";
	private static final String EOL = "\n";
	private static final String NOT_AVAILABLE = "";

	public static void main(String[] args) throws IOException {
		Set<String> userSuppliedNames = parseUserSuppliedNames(new FileInputStream(args[0]));
		System.out.println("Found " + userSuppliedNames.size() + " gene names.");
		System.out.println(userSuppliedNames);

		GeneInfoParser geneInfoParser = new GeneInfoParser();
		List<GeneInfoParser.Record> geneInfoRecords = geneInfoParser.parse(new GZIPInputStream(new FileInputStream(args[1])));

		SeqGeneMdParser seqGeneMdParser = new SeqGeneMdParser();
		List<SeqGeneMdParser.Record> seqGeneMdRecords = seqGeneMdParser.parse(new GZIPInputStream(new FileInputStream(args[2])));

		StringBuilder builder = new StringBuilder();
		// Header.
		builder.append("User-supplied Gene Name");
		builder.append(DELIMITER).append("Entrez Gene ID");
		builder.append(DELIMITER).append("Gene Symbol");
		builder.append(DELIMITER).append("Synonyms");
		builder.append(DELIMITER).append("Chromosome");
		builder.append(DELIMITER).append("Map Location");
		builder.append(DELIMITER).append("Chromosome");
		builder.append(DELIMITER).append("Chr Start");
		builder.append(DELIMITER).append("Chr Stop");
		builder.append(DELIMITER).append("Chr Orient");
		builder.append(DELIMITER).append("Assembly");
		builder.append(EOL);
		// Rest of the rows.
		for (Iterator<String> it = userSuppliedNames.iterator(); it.hasNext(); ) {
			String geneName = it.next();
			builder.append(geneName);
			// Append gene info.
			GeneInfoParser.Record geneInfoRecord = findGeneInfo(geneName, geneInfoRecords);
			if (geneInfoRecord != null) {
				builder.append(DELIMITER).append(geneInfoRecord.getGeneID());
				builder.append(DELIMITER).append(geneInfoRecord.getSymbol());
				builder.append(DELIMITER).append(geneInfoRecord.getSynonyms());
				builder.append(DELIMITER).append(geneInfoRecord.getChromosome());
				builder.append(DELIMITER).append(geneInfoRecord.getMapLocation());
			}
			else {
				builder.append(DELIMITER).append(NOT_AVAILABLE);
				builder.append(DELIMITER).append(NOT_AVAILABLE);
				builder.append(DELIMITER).append(NOT_AVAILABLE);
				builder.append(DELIMITER).append(NOT_AVAILABLE);
				builder.append(DELIMITER).append(NOT_AVAILABLE);
			}
			// Append map data.
			if (geneInfoRecord != null) {
				SeqGeneMdParser.Record seqGeneMdRecord = findSeqGeneMd(geneInfoRecord.getGeneID(), seqGeneMdRecords);
				if (seqGeneMdRecord != null) {
					builder.append(DELIMITER).append(seqGeneMdRecord.getChromosome());
					builder.append(DELIMITER).append(seqGeneMdRecord.getChrStart());
					builder.append(DELIMITER).append(seqGeneMdRecord.getChrStop());
					builder.append(DELIMITER).append(seqGeneMdRecord.getChrOrient());
					builder.append(DELIMITER).append(seqGeneMdRecord.getGroupLabel());
				}
				else {
					builder.append(DELIMITER).append(NOT_AVAILABLE);
					builder.append(DELIMITER).append(NOT_AVAILABLE);
					builder.append(DELIMITER).append(NOT_AVAILABLE);
					builder.append(DELIMITER).append(NOT_AVAILABLE);
					builder.append(DELIMITER).append(NOT_AVAILABLE);
				}
			}
			else {
				builder.append(DELIMITER).append(NOT_AVAILABLE);
				builder.append(DELIMITER).append(NOT_AVAILABLE);
				builder.append(DELIMITER).append(NOT_AVAILABLE);
				builder.append(DELIMITER).append(NOT_AVAILABLE);
				builder.append(DELIMITER).append(NOT_AVAILABLE);
			}
			builder.append(EOL);
		}

		System.out.print(builder.toString());
	}

	private static Set<String> parseUserSuppliedNames(InputStream in) throws IOException {
		Set<String> set = new LinkedHashSet<String>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = null;
		while ((line = reader.readLine()) != null) {
			set.add(line);
		}
		in.close();
		return set;
	}

	private static GeneInfoParser.Record findGeneInfo(String geneName, List<GeneInfoParser.Record> geneInfoRecords) {
		for (Iterator<GeneInfoParser.Record> it = geneInfoRecords.iterator(); it.hasNext(); ) {
			GeneInfoParser.Record record = it.next();
			if (geneName.equals(record.getSymbol())) {
				return record;
			}
			else if (record.getSynonyms().contains(geneName)) {
				return record;
			}
		}
		return null;
	}

	private static SeqGeneMdParser.Record findSeqGeneMd(String entrez, List<SeqGeneMdParser.Record> seqGeneMdRecords) {
		for (Iterator<SeqGeneMdParser.Record> it = seqGeneMdRecords.iterator(); it.hasNext(); ) {
			SeqGeneMdParser.Record record = it.next();
			// See NCBI Entrez Gene FAQ for further detail.
			if (!"GENE".equals(record.getFeatureType()) || !"GRCh37.p2-Primary Assembly".equals(record.getGroupLabel())) continue;
			// Find record with matching Entrez Gene ID.
			String featureID = record.getFeatureID();
			String[] tmp = featureID.split(":");
			if (entrez.equals(tmp[1])) {
				return record;
			}
		}
		return null;
	}
}
