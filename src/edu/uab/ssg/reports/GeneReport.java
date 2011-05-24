package edu.uab.ssg.reports;

import edu.uab.ssg.io.entrez.*;
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
 *		<li>feature type, usually GENE, look at the <tt>feature_type</tt> field in the <tt>seq_gene.md</tt> file for valid values</li>
 *		<li>assembly name, usually something like reference or GRCh37.p2-Primary Assembly, look at the <tt>group_label</tt> field in the <tt>seq_gene.md</tt> file for valid values</li>
 *	</ol>
 *
 *	<p>The <tt>gene_info</tt> file is parsed, in both the symbol and synonym fields, for the user-supplied gene names. If a match is found, the Entrez Gene ID is used to cross-reference the mapping data in the <tt>seq_gene.md</tt> file. Specifically, the <tt>seq_gene.md</tt> file is parsed for records of feature type <i>GENE</i> and group label <i>GRCh37.p2-Primary Assembly</i> and the chromosome start and end positions for the gene are retrieved as described in the <a href="http://www.ncbi.nlm.nih.gov/bookshelf/br.fcgi?book=helpgene&part=genefaq">Entrez Gene FAQ</a>. Note that the position data in the <tt>seq_gene.md</tt> file is one-based (see FAQ).</p>
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
		System.out.println("Found " + userSuppliedNames.size() + " gene names.");
		System.out.println(userSuppliedNames);

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

	// Returns the first record that has the given gene name in either the symbol or synonyms field.
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
