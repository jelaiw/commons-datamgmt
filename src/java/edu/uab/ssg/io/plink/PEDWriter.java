package edu.uab.ssg.io.plink;

import edu.uab.ssg.model.snp.*;
import java.io.*;
import java.util.*;

/**
 * A writer for the PLINK PED input file format described in <a href="http://pngu.mgh.harvard.edu/~purcell/plink/data.shtml">the basic usage/data formats section</a> of the PLINK online documentation.
 *
 * The first six columns are:
 * <p><tt>
 * Family ID<br/>
 * Individual ID<br/>
 * Paternal ID<br/>
 * Maternal ID<br/>
 * Sex (1=male; 2=female; other=unknown)<br/>
 * Phenotype<br/>
 * </tt></p>
 *
 * <p>Additional columns are for specifying genotypes.</p>
 *
 * Here is an excerpt (only includes the first ten markers) created from the hapmap1.ped file in the PLINK tutorial <a href="http://pngu.mgh.harvard.edu/~purcell/plink/hapmap1.zip">example data archive</a> that may help illustrate the file format:
 *
 * <p><tt>
 * HCB181 1 0 0 1 1 2 2 2 2 2 2 1 2 2 2 2 2 2 2 0 0 2 2 2 2<br/>
 * HCB182 1 0 0 1 1 2 2 1 2 2 2 1 2 1 2 2 2 2 2 0 0 2 2 2 2<br/>
 * HCB183 1 0 0 1 2 2 2 1 2 2 2 1 2 1 1 2 2 2 2 0 0 2 2 2 2<br/>
 * HCB184 1 0 0 1 1 2 2 1 2 2 2 1 1 2 2 2 2 2 2 0 0 2 2 2 2<br/>
 * HCB185 1 0 0 1 1 2 2 1 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2<br/>
 * HCB186 1 0 0 1 1 2 2 2 2 2 2 1 1 2 2 2 2 2 2 2 2 2 2 2 2<br/>
 * HCB187 1 0 0 1 1 2 2 2 2 2 2 1 2 1 2 2 2 2 2 2 2 2 2 2 2<br/>
 * HCB188 1 0 0 1 1 2 2 1 2 2 2 1 1 2 2 2 2 2 2 0 0 2 2 2 2<br/>
 * HCB189 1 0 0 1 1 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2<br/>
 * HCB190 1 0 0 1 1 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2<br/>
 * ...
 * </tt></p>
 * 
 * <p>This implementation uses the tab character as the field delimiter, the
 * zero character to represent missing values, and Unix-style line ending.</p>
 *
 * @author Jelai Wang
 */
public final class PEDWriter {
	private static final char DELIMITER = '\t';
	private static final char MISSING_VALUE = '0';
	private static final char EOL = '\n';

	/**
	 * Constructs the writer.
	 */
	public PEDWriter() {
	}

	/**
	 * Writes the study population to the output stream in PLINK PED format.
	 * @param population A study population of unrelated samples.
	 * @param out An output stream. This stream is closed.
	 */
	public void write(Population population, OutputStream out) throws IOException {
		if (population == null)
			throw new NullPointerException("population");
		if (out == null)
			throw new NullPointerException("out");
		OutputStreamWriter writer = new OutputStreamWriter(out);
		Set<SNP> snps = population.getSNPs();
		Set<Sample> samples = population.getSamples();
		for (Iterator<Sample> it1 = samples.iterator(); it1.hasNext(); ) {
			Sample sample = it1.next();
			
			StringBuilder builder = new StringBuilder();
			// See the hapmap1.ped file in the example presented in the PLINK tutorial at http://pngu.mgh.harvard.edu/~purcell/plink/hapmap1.zip.
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
				else { // The genotype was not assessed for this sample.
					builder.append(DELIMITER).append(MISSING_VALUE);
					builder.append(DELIMITER).append(MISSING_VALUE);
				}
			}
			builder.append(EOL);
			writer.write(builder.toString());
		}
		writer.flush();
		writer.close();
	}
}
