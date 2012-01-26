package edu.uab.ssg.util;

import junit.framework.TestCase;
import junit.framework.Assert;
import java.io.*;
import java.util.List;
import edu.uab.ssg.io.ncbi.SeqGeneMdParser;

/**
 * @author Jelai Wang
 */

public final class TestSeqGeneMdPositionIndex extends TestCase {
	// The input file is the seq_gene.md from the NCBI FTP site for human assembly 37.2, see README_CURRENT_BUILD for further detail.
	public void testInterestingCases() throws IOException {
		InputStream in = getClass().getClassLoader().getResourceAsStream("edu/uab/ssg/util/test-seq_gene.md");
		SeqGeneMdParser parser = new SeqGeneMdParser();
		List<SeqGeneMdParser.Record> records = parser.parse(in, new SeqGeneMdParser.RecordFilter() {
			public boolean acceptRecord(SeqGeneMdParser.Record record) {
				if ("GENE".equals(record.getFeatureType()) && "GRCh37.p2-Primary Assembly".equals(record.getGroupLabel())) return true;
				return false;
			}
		});

		SeqGeneMdPositionIndex index = new SeqGeneMdPositionIndex(records);
		// Test single match.
		List<SeqGeneMdParser.Record> matches = index.getRecords("19", 55327893);
		Assert.assertEquals(1, matches.size());
		SeqGeneMdParser.Record record = matches.get(0);
		Assert.assertEquals("19", record.getChromosome());
		Assert.assertEquals(55327893, record.getChrStart());
		Assert.assertEquals(55342233, record.getChrStop());
		Assert.assertEquals("GENE", record.getFeatureType());
		Assert.assertEquals("GRCh37.p2-Primary Assembly", record.getGroupLabel());
		Assert.assertEquals("GeneID:3811", record.getFeatureID());

		// Test overlapping genes TCP1 and ACAT2.
		// See paper at DOI:10.1186/1471-2164-9-169 for further detail.
		matches = index.getRecords("6", 160200000); // Re-use local reference.
		Assert.assertEquals(2, matches.size());
		record = matches.get(0); // ACAT2
		Assert.assertEquals("6", record.getChromosome());
		Assert.assertEquals(160182989, record.getChrStart());
		Assert.assertEquals(160200087, record.getChrStop());
		Assert.assertEquals("GENE", record.getFeatureType());
		Assert.assertEquals("GRCh37.p2-Primary Assembly", record.getGroupLabel());
		Assert.assertEquals("GeneID:39", record.getFeatureID());
		record = matches.get(1); // TCP1
		Assert.assertEquals("6", record.getChromosome());
		Assert.assertEquals(160199530, record.getChrStart());
		Assert.assertEquals(160210735, record.getChrStop());
		Assert.assertEquals("GENE", record.getFeatureType());
		Assert.assertEquals("GRCh37.p2-Primary Assembly", record.getGroupLabel());
		Assert.assertEquals("GeneID:6950", record.getFeatureID());
	}
}
