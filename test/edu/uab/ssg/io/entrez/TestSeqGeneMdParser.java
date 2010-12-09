package edu.uab.ssg.io.entrez;

import junit.framework.TestCase;
import junit.framework.Assert;
import java.io.*;
import java.util.List;
import java.util.zip.GZIPInputStream;

/**
 * @author Jelai Wang
 */

public final class TestSeqGeneMdParser extends TestCase {
	public void testExampleFile() throws IOException {
		// This input file contains hand-selected records corresponding to the 9606 tax ID (Homo sapiens) from a seq_gene.md.gz file downloaded from the Entrez Gene FTP site on Nov 30 2010, see README_CURRENT_BUILD for details.
		InputStream in = getClass().getClassLoader().getResourceAsStream("edu/uab/ssg/io/entrez/test-seq_gene.md");
		SeqGeneMdParser parser = new SeqGeneMdParser();
		List<SeqGeneMdParser.Record> records = parser.parse(in);
		Assert.assertEquals(27, records.size());
		// Spot check values from first record.
		SeqGeneMdParser.Record firstRecord = records.get(0);
		Assert.assertEquals("9606", firstRecord.getTaxID());
		Assert.assertEquals("1", firstRecord.getChromosome());
		Assert.assertEquals(716, firstRecord.getChrStart());
		Assert.assertEquals(2038, firstRecord.getChrStop());
		Assert.assertEquals("+", firstRecord.getChrOrient());
		Assert.assertEquals("NW_001838563.2", firstRecord.getContig());
		Assert.assertEquals(3842, firstRecord.getCtgStart());
		Assert.assertEquals(5164, firstRecord.getCtgStop());
		Assert.assertEquals("-", firstRecord.getCtgOrient());
		Assert.assertEquals("LOC100131754", firstRecord.getFeatureName());
		Assert.assertEquals("GeneID:100131754", firstRecord.getFeatureID());
		Assert.assertEquals("GENE", firstRecord.getFeatureType());
		Assert.assertEquals("HuRef-Primary Assembly", firstRecord.getGroupLabel());
		Assert.assertEquals("-", firstRecord.getTranscript());
		Assert.assertEquals("", firstRecord.getEvidenceCode());
		
		// Spot check values from last record.
		SeqGeneMdParser.Record lastRecord = records.get(records.size() - 1);
		Assert.assertEquals("9606", lastRecord.getTaxID());
		Assert.assertEquals("Y", lastRecord.getChromosome());
		Assert.assertEquals(59361222, lastRecord.getChrStart());
		Assert.assertEquals(59361778, lastRecord.getChrStop());
		Assert.assertEquals("NT_167206.1", lastRecord.getContig());
		Assert.assertEquals(327173, lastRecord.getCtgStart());
		Assert.assertEquals(327729, lastRecord.getCtgStop());
		Assert.assertEquals("-", lastRecord.getCtgOrient());
		Assert.assertEquals("LOC100506481", lastRecord.getFeatureName());
		Assert.assertEquals("GeneID:100506481", lastRecord.getFeatureID());
		Assert.assertEquals("PSEUDO", lastRecord.getFeatureType());
		Assert.assertEquals("GRCh37.p2-Primary Assembly", lastRecord.getGroupLabel());
		Assert.assertEquals("-", lastRecord.getTranscript());
		Assert.assertEquals("", lastRecord.getEvidenceCode());
	}
}
