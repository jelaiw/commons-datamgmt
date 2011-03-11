package edu.uab.ssg.io.marchini_gwas;

import junit.framework.TestCase;
import junit.framework.Assert;
import java.io.*;
import java.util.*;

/**
 * @author Jelai Wang
 */

public final class TestGENSParser extends TestCase {
	public void testExampleFile() throws IOException {
		// Arbitrary (head and tail) subset of some imputed data (from IMPUTE2).
		InputStream in = getClass().getClassLoader().getResourceAsStream("edu/uab/ssg/io/marchini_gwas/test.gens");
		GENSParser parser = new GENSParser();
		TestHelper helper = new TestHelper();
		parser.parse(in, helper);
		Assert.assertEquals(20, helper.getNumberOfParsedRecords());
		Assert.assertEquals(0, helper.getNumberOfBadRecords());
	}

	private static final class TestHelper implements GENSParser.RecordListener {
		private int numOfParsedRecords = 0;
		private int numOfBadRecords = 0;

		public void handleParsedRecord(GENSParser.GenotypeRecord record) {
			if (numOfParsedRecords == 0) { // First record.
				Assert.assertEquals("---", record.getChromosome());
				Assert.assertEquals("10-94443589", record.getName());
				Assert.assertEquals(94443589, record.getPosition());
				Assert.assertEquals("C", record.getAlleleA());
				Assert.assertEquals("T", record.getAlleleB());
				List<GENSParser.GenotypeProbabilities> probs = record.getGenotypeProbabilities();
				Assert.assertEquals(297, probs.size());
				GENSParser.GenotypeProbabilities prob0 = probs.get(0);
				Assert.assertEquals(1., prob0.getProbAA());
				Assert.assertEquals(0., prob0.getProbAB());
				Assert.assertEquals(0., prob0.getProbBB());
				GENSParser.Genotype g0 = prob0.getGenotype(0.9);
				Assert.assertEquals("C", g0.getAllele1());
				Assert.assertEquals("C", g0.getAllele2());
				GENSParser.GenotypeProbabilities prob5 = probs.get(5);
				Assert.assertEquals(0.561, prob5.getProbAA());
				Assert.assertEquals(0.439, prob5.getProbAB());
				Assert.assertEquals(0., prob5.getProbBB());
				GENSParser.Genotype g5 = prob5.getGenotype(0.4);
				Assert.assertEquals("C", g5.getAllele1());
				Assert.assertEquals("C", g5.getAllele2());
			}
			else if (numOfParsedRecords == 15) { // Record with a BB.
				Assert.assertEquals("---", record.getChromosome());
				Assert.assertEquals("rs2636792", record.getName());
				Assert.assertEquals(98828669, record.getPosition());
				Assert.assertEquals("T", record.getAlleleA());
				Assert.assertEquals("G", record.getAlleleB());
				List<GENSParser.GenotypeProbabilities> probs = record.getGenotypeProbabilities();
				Assert.assertEquals(297, probs.size());
				GENSParser.GenotypeProbabilities prob0 = probs.get(0);
				Assert.assertEquals(0., prob0.getProbAA());
				Assert.assertEquals(0.251, prob0.getProbAB());
				Assert.assertEquals(0.749, prob0.getProbBB());
				GENSParser.Genotype g0 = prob0.getGenotype(0.7);
				Assert.assertEquals("G", g0.getAllele1());
				Assert.assertEquals("G", g0.getAllele2());
				// Does not meet threshold.
				Assert.assertNull(prob0.getGenotype(0.8));
			}
			else if (numOfParsedRecords == 16) { // Record with an rsid.
				Assert.assertEquals("---", record.getChromosome());
				Assert.assertEquals("rs11813871", record.getName());
				Assert.assertEquals(98828716, record.getPosition());
				Assert.assertEquals("C", record.getAlleleA());
				Assert.assertEquals("A", record.getAlleleB());
				List<GENSParser.GenotypeProbabilities> probs = record.getGenotypeProbabilities();
				Assert.assertEquals(297, probs.size());
				GENSParser.GenotypeProbabilities prob0 = probs.get(0);
				Assert.assertEquals(0.999, prob0.getProbAA());
				Assert.assertEquals(0.001, prob0.getProbAB());
				Assert.assertEquals(0., prob0.getProbBB());
				GENSParser.GenotypeProbabilities prob296 = probs.get(296);
				Assert.assertEquals(0.964, prob296.getProbAA());
				Assert.assertEquals(0.036, prob296.getProbAB());
				Assert.assertEquals(0., prob296.getProbBB());
				GENSParser.GenotypeProbabilities prob2 = probs.get(2);
				Assert.assertEquals(0.011, prob2.getProbAA());
				Assert.assertEquals(0.988, prob2.getProbAB());
				Assert.assertEquals(0., prob2.getProbBB());
				GENSParser.Genotype g2 = prob2.getGenotype(0.9);
				Assert.assertEquals("C", g2.getAllele1());
				Assert.assertEquals("A", g2.getAllele2());
			}
			// Increment total number of handled records.
			numOfParsedRecords++;
		}

		public void handleBadRecordFormat(String line) {
			numOfBadRecords++;
		}

		private int getNumberOfParsedRecords() { return numOfParsedRecords; }
		private int getNumberOfBadRecords() { return numOfBadRecords; }
	}
}
