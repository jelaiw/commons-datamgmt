package edu.uab.ssg.io.decode;

import edu.uab.ssg.model.snp.*;
import java.util.*;
import java.io.*;

/**
 * @author Jelai Wang
 */
public final class PopulationParser {
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
			}
		});

		final PopulationBuilder builder = new PopulationBuilder(populationName);
		GenotypeFileParser parser2 = new GenotypeFileParser();
		parser2.parse(in2, new GenotypeFileParser.RecordListener() {
			public void handleParsedRecord(GenotypeFileParser.GenotypeRecord record) {
				SNP snp = map.get(record.getSNPName());
				if (snp != null)
					builder.setGenotype(record.getSampleID(), snp, record.getAllele1Top(), record.getAllele2Top(), IlluminaStrand.TOP);
			}

			public void handleBadRecordFormat(String line) {
			}
		});

		return builder.getInstance();
	}
}
