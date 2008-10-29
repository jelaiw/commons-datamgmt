package edu.uab.ssg.io.decode;

import edu.uab.ssg.model.snp.*;
import java.util.*;
import java.io.*;

/**
 * A parser for the SNP and genotype file formats from deCODE that returns 
 * a Population, a high-level abstraction from the edu.uab.ssg.model.snp
 * package.
 * This implementation ignores file format errors, but prints them to
 * standard error.
 *
 * @author Jelai Wang
 */
public final class PopulationParser {
	/**
	 * Constructs the parser.
	 */
	public PopulationParser() {
	}

	/**
	 * Parses the input streams for the SNP and genotype files and returns a
	 * Population.
	 * @param populationName The name of the study population.
	 * @param in1 The input stream for the SNP file.
	 * @param in2 The input stream for the genotype file.
	 */
	public Population parse(String populationName, InputStream in1, InputStream in2) throws IOException {
		if (populationName == null)
			throw new NullPointerException("populationName");
		if (in1 == null)
			throw new NullPointerException("in1");
		if (in2 == null)
			throw new NullPointerException("in2");

		final Map<String, SNP> map = new LinkedHashMap<String, SNP>();
		SNPFileParser parser1 = new SNPFileParser();
		parser1.parse(in1, new SNPFileParser.RecordListener() {
			public void handleParsedRecord(SNPFileParser.SNPRecord record) {
				SNP snp = SNPFactory.createSNP(record.getName(), record.getChromosome(), record.getPosition());
				map.put(snp.getName(), snp);
			}

			public void handleBadRecordFormat(String line) {
				System.err.println("BAD FORMAT: " + line);
			}
		});

		final PopulationBuilder builder = new PopulationBuilder(populationName);
		GenotypeFileParser parser2 = new GenotypeFileParser();
		parser2.parse(in2, new GenotypeFileParser.RecordListener() {
			public void handleParsedRecord(GenotypeFileParser.GenotypeRecord record) {
				SNP snp = map.get(record.getSNPName());
				if (snp != null) {
					String a1 = record.getAllele1Top();
					String a2 = record.getAllele2Top();
					// Missing alleles are represented with the '-' character
					// in the deCODE genotype file format.
					if ("-".equals(a1)) a1 = null;
					if ("-".equals(a2)) a2 = null;
					// Genotypes are read from the TOP column.
					Strand strand = IlluminaStrand.TOP;
					if (a1 == null && a2 == null) strand = null; // LOOK!!
					builder.setGenotype(record.getSampleID(), snp, a1, a2, strand);
				}
			}

			public void handleBadRecordFormat(String line) {
				System.err.println("BAD FORMAT: " + line);
			}
		});

		return builder.getInstance();
	}
}
