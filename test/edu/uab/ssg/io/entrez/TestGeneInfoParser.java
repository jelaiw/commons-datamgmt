package edu.uab.ssg.io.entrez;

import junit.framework.TestCase;
import junit.framework.Assert;
import java.io.*;
import java.util.List;
import java.util.zip.GZIPInputStream;

/**
 * @author Jelai Wang
 */

public final class TestGeneInfoParser extends TestCase {
	public void testExampleFile() throws IOException {
		// This input file contains records corresponding to the 9606 tax ID (Homo sapiens) from a gene_info.gz file downloaded from the Entrez Gene FTP site on Nov 1 2010, see README_CURRENT_BUILD for details.
		InputStream in = getClass().getClassLoader().getResourceAsStream("edu/uab/ssg/io/entrez/9606_subset.gene_info.gz");
		GeneInfoParser parser = new GeneInfoParser();
		List<GeneInfoParser.Record> records = parser.parse(new GZIPInputStream(in));
		Assert.assertEquals(45774, records.size());
		// Spot check values from first record.
		GeneInfoParser.Record firstRecord = records.get(0);
		Assert.assertEquals("9606", firstRecord.getTaxID());
		Assert.assertEquals("1", firstRecord.getGeneID());
		Assert.assertEquals("A1BG", firstRecord.getSymbol());
		Assert.assertEquals("-", firstRecord.getLocusTag());
		Assert.assertEquals(5, firstRecord.getSynonyms().size());
		Assert.assertEquals("A1B", firstRecord.getSynonyms().get(0));
		Assert.assertEquals("HYST2477", firstRecord.getSynonyms().get(4));
		Assert.assertEquals(4, firstRecord.getdbXrefs().size());
		Assert.assertEquals("HGNC:5", firstRecord.getdbXrefs().get(0));
		Assert.assertEquals("HPRD:00726", firstRecord.getdbXrefs().get(3));
		Assert.assertEquals("19", firstRecord.getChromosome());
		Assert.assertEquals("19q13.4", firstRecord.getMapLocation());
		Assert.assertEquals("alpha-1-B glycoprotein", firstRecord.getDescription());
		Assert.assertEquals("protein-coding", firstRecord.getTypeOfGene());
		Assert.assertEquals("A1BG", firstRecord.getSymbolFromNomenclatureAuthority());
		Assert.assertEquals("alpha-1-B glycoprotein", firstRecord.getFullNameFromNomenclatureAuthority());
		Assert.assertEquals("O", firstRecord.getNomenclatureStatus());
		Assert.assertEquals("alpha-1B-glycoprotein", firstRecord.getOtherDesignations());
		Assert.assertEquals("20101029", firstRecord.getModificationDate());
		
		// Spot check values from last record.
		GeneInfoParser.Record lastRecord = records.get(records.size() - 1);
		Assert.assertEquals("9606", lastRecord.getTaxID());
		Assert.assertEquals("100526648", lastRecord.getGeneID());
		Assert.assertEquals("MIR1273E", lastRecord.getSymbol());
		Assert.assertEquals("-", lastRecord.getLocusTag());
		Assert.assertEquals(1, lastRecord.getSynonyms().size());
		Assert.assertEquals("hsa-mir-1273e", lastRecord.getSynonyms().get(0));
		Assert.assertEquals(1, lastRecord.getdbXrefs().size());
		Assert.assertEquals("miRBase:MI0016059", lastRecord.getdbXrefs().get(0));
		Assert.assertEquals("-", lastRecord.getChromosome());
		Assert.assertEquals("-", lastRecord.getMapLocation());
		Assert.assertEquals("microRNA mir-1273e", lastRecord.getDescription());
		Assert.assertEquals("miscRNA", lastRecord.getTypeOfGene());
		Assert.assertEquals("-", lastRecord.getSymbolFromNomenclatureAuthority());
		Assert.assertEquals("-", lastRecord.getFullNameFromNomenclatureAuthority());
		Assert.assertEquals("-", lastRecord.getNomenclatureStatus());
		Assert.assertEquals("-", lastRecord.getOtherDesignations());
		Assert.assertEquals("20101030", lastRecord.getModificationDate());
	}
}
