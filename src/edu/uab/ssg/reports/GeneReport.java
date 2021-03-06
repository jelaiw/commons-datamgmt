package edu.uab.ssg.reports;

import edu.uab.ssg.io.ncbi.*;
import java.util.*;
import java.io.*;
import java.util.zip.GZIPInputStream;

/**
 *	This gene report parses NCBI-formatted <tt>gene_info</tt> and <tt>seq_gene.md</tt> files and outputs a tab-delimited text table containing annotation records for each user-supplied gene of interest.
 *
 *	<p>The required command-line parameters are:</p>
 *	<ol>
 *		<li>a text file of user-supplied gene names, one name per line</li>
 *		<li>a <tt>gene_info</tt> file, gzipped</li>
 *		<li>a <tt>seq_gene.md</tt> file, gzipped</li>
 *		<li>feature type, usually <i>GENE</i>, look at the <tt>feature_type</tt> field in the <tt>seq_gene.md</tt> file for valid values</li>
 *		<li>assembly name, usually something like <i>reference</i> or <i>GRCh37.p2-Primary Assembly</i>, look at the <tt>group_label</tt> field in the <tt>seq_gene.md</tt> file for valid values</li>
 *	</ol>
 *
 *	<p>The <tt>gene_info</tt> file is parsed, in both the symbol and synonym fields, for the user-supplied gene names. If a match is found, the Entrez Gene ID is used to cross-reference the mapping data in the <tt>seq_gene.md</tt> file. In other words, the <tt>seq_gene.md</tt> file is parsed for records of feature type, e.g. <i>GENE</i>, and group label, e.g. <i>GRCh37.p2-Primary Assembly</i>, specified as command-line arguments. The chromosome start and end positions for the gene are retrieved as described in the <a href="http://www.ncbi.nlm.nih.gov/bookshelf/br.fcgi?book=helpgene&part=genefaq">Entrez Gene FAQ</a>. Note that the position data in the <tt>seq_gene.md</tt> file is one-based (see FAQ).</p>
 *
 *	<p>If the output has no mapping data, that means the feature type and assembly name specified as command-line arguments don't appear together in records from <tt>seq_gene.md</tt>. This may be due to a typo, so double-check the input. Also, take care to properly quote values (especially for assembly name) that contain spaces.</p>
 *
 *	@author Jelai Wang
 */
public final class GeneReport {
	private static final String DELIMITER = "\t";
	private static final String EOL = "\n";
	private static final String NOT_AVAILABLE = "";

	private GeneReport() {
	}

	public static void main(String[] args) throws IOException {
		File geneNamesFile = new File(args[0]);
		File geneInfoFile = new File(args[1]);
		File seqGeneMdFile = new File(args[2]);
		final String featureType = args[3];
		final String groupLabel = args[4];

		Set<String> userSuppliedNames = parseUserSuppliedNames(new FileInputStream(geneNamesFile));
		System.out.println("Read " + userSuppliedNames.size() + " user-supplied gene names.");

		GeneInfoParser geneInfoParser = new GeneInfoParser();
		List<GeneInfoParser.Record> geneInfoRecords = geneInfoParser.parse(new GZIPInputStream(new FileInputStream(geneInfoFile)));

		SeqGeneMdParser seqGeneMdParser = new SeqGeneMdParser();
		List<SeqGeneMdParser.Record> seqGeneMdRecords = seqGeneMdParser.parse(new GZIPInputStream(new FileInputStream(seqGeneMdFile)), new SeqGeneMdParser.RecordFilter() {
			public boolean acceptRecord(SeqGeneMdParser.Record record) {
				// See NCBI Entrez Gene FAQ for further detail.
				if (featureType.equals(record.getFeatureType()) && groupLabel.equals(record.getGroupLabel())) return true;
				return false;
			}
		});

		Map<String, List<GeneInfoParser.Record>> geneInfoMap  = new LinkedHashMap<String, List<GeneInfoParser.Record>>();
		for (Iterator<String> it = userSuppliedNames.iterator(); it.hasNext(); ) {
			String geneName = it.next();
			List<GeneInfoParser.Record> _geneInfoRecords = findGeneInfo(geneName, geneInfoRecords);
			if (_geneInfoRecords.size() == 0) {
				System.err.println("Couldn't find gene info for " + geneName + ".");
			}
			else {
				geneInfoMap.put(geneName, _geneInfoRecords);
			}
		}

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
		for (Iterator<String> it1 = geneInfoMap.keySet().iterator(); it1.hasNext(); ) {
			String geneName = it1.next();
			List<GeneInfoParser.Record> _geneInfoRecords = geneInfoMap.get(geneName);
			for (Iterator<GeneInfoParser.Record> it2 = _geneInfoRecords.iterator(); it2.hasNext(); ) {
				GeneInfoParser.Record geneInfoRecord = it2.next();
				builder.append(geneName);
				// Append gene info.
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

	// Return a list of records that contain the given gene name in either the symbol or synonym fields.
	private static List<GeneInfoParser.Record> findGeneInfo(String geneName, List<GeneInfoParser.Record> geneInfoRecords) {
		List<GeneInfoParser.Record> list = new ArrayList<GeneInfoParser.Record>();
		for (Iterator<GeneInfoParser.Record> it = geneInfoRecords.iterator(); it.hasNext(); ) {
			GeneInfoParser.Record record = it.next();
			if (geneName.equals(record.getSymbol())) {
				list.add(record);
			}
			else if (record.getSynonyms().contains(geneName)) {
				list.add(record);
			}
		}
		return list;
	}

	private static SeqGeneMdParser.Record findSeqGeneMd(String entrez, List<SeqGeneMdParser.Record> seqGeneMdRecords) {
		for (Iterator<SeqGeneMdParser.Record> it = seqGeneMdRecords.iterator(); it.hasNext(); ) {
			SeqGeneMdParser.Record record = it.next();
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
