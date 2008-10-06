package edu.uab.ssg.io.plink;

import edu.uab.ssg.model.snp.*;
import java.io.*;
import java.util.*;

/**
 * @author Jelai Wang
 */
public final class PEDWriter {
	private static final char DELIMITER = '\t';
	private static final char MISSING_VALUE = '0';
	private static final char EOL = '\n';

	public void write(Population population, OutputStream out) throws IOException {
		if (population == null)
			throw new NullPointerException("population");
		if (out == null)
			throw new NullPointerException("out");
		Set<SNP> snps = population.getSNPs();
		Set<Sample> samples = population.getSamples();
		StringBuilder builder = new StringBuilder();
		for (Iterator<Sample> it1 = samples.iterator(); it1.hasNext(); ) {
			Sample sample = it1.next();
			
			builder.append(sample.getName()); // Family ID
			builder.append(DELIMITER).append(1); // Individual ID
			builder.append(DELIMITER).append(MISSING_VALUE); // Paternal ID
			builder.append(DELIMITER).append(MISSING_VALUE); // Maternal ID
			builder.append(DELIMITER).append(MISSING_VALUE); // Sex
			builder.append(DELIMITER).append(MISSING_VALUE); // Phenotype

			for (Iterator<SNP> it2 = snps.iterator(); it2.hasNext(); ) {
				SNP snp = it2.next();
				if (sample.existsGenotype(snp)) {
					Sample.Genotype genotype = sample.getGenotype(snp);
					String a1 = genotype.getAllele1();
					String a2 = genotype.getAllele2();
					if (a1 != null)
						builder.append(DELIMITER).append(a1);
					else
						builder.append(DELIMITER).append(MISSING_VALUE);
					if (a2 != null)
						builder.append(DELIMITER).append(a2);
					else
						builder.append(DELIMITER).append(MISSING_VALUE);
				}
				else {
					builder.append(DELIMITER).append(MISSING_VALUE);
					builder.append(DELIMITER).append(MISSING_VALUE);
				}
			}

			builder.append(EOL);
		}

		OutputStreamWriter writer = new OutputStreamWriter(out);
		writer.write(builder.toString());
		writer.flush();
		writer.close();
	}
}
