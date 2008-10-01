package edu.uab.ssg.io.decode;

import junit.framework.TestCase;
import junit.framework.Assert;
import java.util.*;
import java.io.*;
import edu.uab.ssg.model.snp.*;

/**
 * @author Jelai Wang
 */

public final class TestPopulationParser extends TestCase {
	public void testExample() throws IOException {
		InputStream in1 = getClass().getClassLoader().getResourceAsStream("edu/uab/ssg/io/decode/snps.txt");
		InputStream in2 = getClass().getClassLoader().getResourceAsStream("edu/uab/ssg/io/decode/genotypes.txt");
		PopulationParser parser = new PopulationParser();
		Population population = parser.parse("foo", in1, in2);
		Assert.assertEquals("foo", population.getName());
		
		Sample ava1001 = population.getSample("AVA1001");
		Set<Sample> samples = population.getSamples();
		Assert.assertEquals(1, samples.size());
		Set<SNP> snps = population.getSNPs();
		Assert.assertEquals(10, snps.size());

		// Create a map to more conveniently refer to a SNP by name.
		Map<String, SNP> map = new HashMap<String, SNP>();
		for (Iterator<SNP> it = snps.iterator(); it.hasNext(); ) {
			SNP snp = it.next();
			map.put(snp.getName(), snp);
		}

		// Spot check important SNP and genotype fields.
		SNP aicda007875 = map.get("AICDA-007875");
		Assert.assertEquals("12", aicda007875.getChromosome().getName());
		Assert.assertEquals(8650011, aicda007875.getPosition());
		Assert.assertEquals("G", ava1001.getGenotype(aicda007875).getAllele1());
		Assert.assertEquals("G", ava1001.getGenotype(aicda007875).getAllele2());
		Assert.assertEquals(IlluminaStrand.TOP, ava1001.getGenotype(aicda007875).getStrand());

		SNP blr1014511 = map.get("BLR1-014511");
		Assert.assertEquals("11", blr1014511.getChromosome().getName());
		Assert.assertEquals(118272286, blr1014511.getPosition());
		Assert.assertEquals("A", ava1001.getGenotype(blr1014511).getAllele1());
		Assert.assertEquals("G", ava1001.getGenotype(blr1014511).getAllele2());
		Assert.assertEquals(IlluminaStrand.TOP, ava1001.getGenotype(blr1014511).getStrand());
	}
}
