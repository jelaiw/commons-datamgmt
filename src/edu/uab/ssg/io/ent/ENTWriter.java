package edu.uab.ssg.io.ent;

import edu.uab.ssg.model.snp.*;
import java.io.*;
import java.util.*;

/**
 * A writer for the ENT <a href="http://dna.engr.uconn.edu/~software/ent/">http://dna.engr.uconn.edu/~software/ent/</a> input file format.
 *
 * <p>This implementation uses the space character as the field delimiter, the
 * '0' character to represent missing values, the '?' character to represent
 * missing genotype values, and the '\n' (new line) character as the line 
 * separator. Note that the current API does not have a mechanism for the 
 * client programmer to provide sex or family data for the samples, so the 
 * sex and parent fields are coded as missing.</p>
 *
 * Here is an excerpt from the online <a href="http://dna.engr.uconn.edu/~software/ent/README.TXT">README.TXT</a> (a <a href="doc-files/README.TXT">local copy</a> is also available) describing the file format: 
 *
 * <p><tt>
 * ENT accepts sequences of the form 0/1/2/? where 0/1 denote the<br/>
 * genotypes that are homozygous for the major/minor allele,<br/> 
 * 2 denotes a heterozygous genotype, and ? denotes an unknown genotype.<br/>
 * <br/>
 * The ENT input file format is as follows:<br/>
 * <br/>
 * * First line: &lt;number of individuals&gt; &lt;number of snps&gt;<br/>
 * <br/>
 * * Additional lines:<br/>
 * <br/>
 * &lt;individual id&gt; &lt;sex&gt; &lt;parent 1 id&gt; &lt;parent 2 id&gt; &lt;genotype sequence&gt;<br/>
 * <br/>
 * All individual id's must be non-zero, a parent id of 0 represents no<br/>
 * known parent.<br/>
 * </tt></p>
 *
 * <p>An example input file is available at <a href="http://dna.engr.uconn.edu/~software/ent/sample_input">http://dna.engr.uconn.edu/~software/ent/sample_input</a> or see <a href="doc-files/sample_input">local copy</a>. An excerpt from this example is below:</p>
 *
 * <p><tt>
 * 30 103<br/>
 * 1 M 0 0 22222222022?22000?0000000222222222220202010022002222202220022222222000022?2002000022020?22?220220222220<br/>
 * 2 M 0 0 00000000022?22000?0000000???22222?2202?01???0000200000000000000000000000??002?2?0?20022?00?112122211100<br/>
 * 3 M 0 0 0000000000000020020000000222000002000000010022002222202220022?22222000022220220000?20?0???0??0?0?0????0<br/>
 * ...<br/>
 * 29 M 0 0 0000000000000000202?0000000000000100001000000000000000?0?000?0000002022001?0?000?0?0??0?000000000000?10<br/>
 * 30 M 0 0 0000000020000022222?222?02??00000000?20??1222??01??2?0?2200222??2?200002?0??110?0?220201111111101011100<br/>
 * </tt></p>
 *
 * @author Jelai Wang
 */
public final class ENTWriter {
	private static final String DELIMITER = " ";
	private static final String EOL = "\n";
	private static final String MISSING_VALUE = "0";
	private static final String MISSING_GENOTYPE = "?";

	/**
	 * Constructs the writer.
	 */
	public ENTWriter() {
	}

	/**
	 * Writes the genotypes for the given samples in a study population 
	 * to the output stream in the ENT input file format.
	 */
	public void write(List<Sample> samples, List<SNP> snps, OutputStream out) throws IOException {
		if (samples == null)
			throw new NullPointerException("samples");
		if (snps == null)
			throw new NullPointerException("snps");
		if (out == null)
			throw new NullPointerException("out");
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
		// Number of individuals.
		writer.write(String.valueOf(samples.size()));
		writer.write(DELIMITER);
		// Number of SNPs.
		writer.write(String.valueOf(snps.size()));
		writer.write(EOL);

		Map<SNP, AlleleCounter> snp2counter = mapSNP2Counter(snps, samples);
		String HOMOZYGOUS_MAJOR = "0";
		String HOMOZYGOUS_MINOR = "1";
		String HETEROZYGOUS = "2";
		for (Iterator<Sample> it1 = samples.iterator(); it1.hasNext(); ) {
			Sample sample = it1.next();
			StringBuilder builder = new StringBuilder();
			builder.append(sample.getName());
			builder.append(DELIMITER).append(MISSING_VALUE); // Sex.
			builder.append(DELIMITER).append(MISSING_VALUE); // parent 1 id.
			builder.append(DELIMITER).append(MISSING_VALUE); // parent 2 id.
			builder.append(DELIMITER);

			for (Iterator<SNP> it2 = snps.iterator(); it2.hasNext(); ) {
				SNP snp = it2.next();
				AlleleCounter counter = snp2counter.get(snp);
				Set<String> alleles = counter.getAlleles();
				if (sample.existsGenotype(snp)) {
					Sample.Genotype genotype = sample.getGenotype(snp);
					String a1 = genotype.getAllele1();
					String a2 = genotype.getAllele2();
					if (a1 == null && a2 == null) {
						builder.append(MISSING_GENOTYPE);
					}
					else if (counter.existsMinorAllele()) {
						String minorAllele = counter.getMinorAllele();
						if (!a1.equals(a2)) {
							builder.append(HETEROZYGOUS);
						}
						else if (a1.equals(a2) && a1.equals(minorAllele)) {
							builder.append(HOMOZYGOUS_MINOR);
						}
						else if (a1.equals(a2) && !a1.equals(minorAllele)) {
							builder.append(HOMOZYGOUS_MAJOR);
						}
						else { // This should be unreachable!!
							throw new RuntimeException(genotype.toString());
						}
					}
					else if (!counter.existsMinorAllele() && (alleles.size() == 1)) { // Monomorphic.
						builder.append(HOMOZYGOUS_MAJOR);
					}
					else if (!counter.existsMinorAllele() && (alleles.size() == 2)) { // Polymorphic, but equal numbers of each allele in the population.
						Iterator<String> it = alleles.iterator();
						String allele = it.next();
						if (!a1.equals(a2)) {
							builder.append(HETEROZYGOUS);
						}
						else if (a1.equals(a2) && a1.equals(allele)) {
							builder.append(HOMOZYGOUS_MINOR);
						}
						else if (a1.equals(a2) && !a1.equals(allele)) {
							builder.append(HOMOZYGOUS_MAJOR);
						}
						else { // This should be unreachable!!
							throw new RuntimeException(genotype.toString());
						}
					}
					else { // This can be reached with allele total > 2, but we haven't thought about handling tri-alleles yet.
						throw new RuntimeException(genotype.toString());
					}
				}
				else { // Untyped.
					builder.append(MISSING_GENOTYPE);
				}
			}
			writer.write(builder.toString());
			writer.write(EOL);
		}
		writer.flush();
		writer.close();
	}

	private Map<SNP, AlleleCounter> mapSNP2Counter(List<SNP> snps, List<Sample> samples) {
		Map<SNP, AlleleCounter> map = new LinkedHashMap<SNP, AlleleCounter>();
		for (Iterator<SNP> it1 = snps.iterator(); it1.hasNext(); ) {
			SNP snp = it1.next();
			AlleleCounter counter = map.get(snp);
			if (counter == null) {
				counter = new AlleleCounter();
				map.put(snp, counter);
			}
			for (Iterator<Sample> it2 = samples.iterator(); it2.hasNext(); ) {
				Sample sample = it2.next();
				if (sample.existsGenotype(snp)) {
					Sample.Genotype genotype = sample.getGenotype(snp);
					String a1 = genotype.getAllele1();
					String a2 = genotype.getAllele2();
					counter.addAllele(a1);
					counter.addAllele(a2);
				}
				else { // Tally missing values.
					counter.addAllele(null);
					counter.addAllele(null);
				}
			}
		}
		return map;
	}
}
