package edu.uab.ssg.io.hapgen;

import edu.uab.ssg.model.snp.*;
import java.io.*;
import java.util.*;

/**
 * An abstraction for the "legend" concept and file format required by the HapGen software at <a href="http://www.stats.ox.ac.uk/~marchini/software/gwas/hapgen.html">http://www.stats.ox.ac.uk/~marchini/software/gwas/hapgen.html</a>.
 * An example of the file format, taken from the <i>ex.leg</i> file in HapGen version 1.3.0, is below:
 *
 * <p><tt>
 * rs      position        0       1<br/>
 * rs11089130      14431347        C       G<br/>
 * rs738829        14432618        A       G<br/>
 * rs915674        14433624        A       G<br/>
 * rs915675        14433659        A       C<br/>
 * rs915677        14433758        A       G<br/>
 * rs9604721       14434713        C       T<br/>
 * rs4389403       14435207        A       G<br/>
 * rs5746356       14439734        C       T<br/>
 * rs9617528       14441016        C       T<br/>
 * rs2154787       14449374        C       T<br/>
 * </tt></p>
 *
 * The field delimiter is the space character. Missing data is coded as the hyphen (dash) character. See the <a href="http://www.stats.ox.ac.uk/~marchini/software/gwas/hapgen.html#Options">HapGen Options</a> section on the -l switch for more detail, including a link to example legend files from HapMap at <a href="http://www.hapmap.org/downloads/phasing/2006-07_phaseII/all/">http://www.hapmap.org/downloads/phasing/2006-07_phaseII/all/</a>. Also see <a href="http://www.hapmap.org/downloads/phasing/2006-07_phaseII/00README.txt">this README</a>.
 *
 * @author Jelai Wang
 */
public final class Legend {
	private Map<SNP, AlleleCounter> snp2counter = new LinkedHashMap<SNP, AlleleCounter>();

	/**
	 * Constructs the legend.
	 * @param snps The given samples will be queried at the SNPs provided in
	 * this list to establish how the alleles (usually in nucleotide bases)
	 * are recoded as allele 0 or 1.
	 * @param samples The set of samples of interest in the study population.
	 */
	public Legend(List<SNP> snps, Set<Sample> samples) {
		if (snps == null)
			throw new NullPointerException("snps");
		if (samples == null)
			throw new NullPointerException("samples");
		// Figure out the minor allele for the SNPs of interest
		// amongst the given samples; this information will be
		// used to establish allele0 and allele1.
		for (Iterator<SNP> it1 = snps.iterator(); it1.hasNext(); ) {
			SNP snp = it1.next();
			AlleleCounter counter = new AlleleCounter();

			for (Iterator<Sample> it2 = samples.iterator(); it2.hasNext(); ) {
				Sample sample = it2.next();
				if (sample.existsGenotype(snp)) {
					Sample.Genotype genotype = sample.getGenotype(snp);
					counter.addAllele(genotype.getAllele1());
					counter.addAllele(genotype.getAllele2());
				}
			}
			// The 0 and 1 allele encodings are only defined for biallelic SNPs.
			Set<String> alleles = counter.getAlleles();
			if (alleles.size() > 0 && alleles.size() <= 2) {
				snp2counter.put(snp, counter);
			}
			else if (alleles.size() > 2) { // We don't handle this, but log it.
				System.err.println(alleles);
			}
		}
	}

	/**
	 * Returns the SNPs for which this legend provides allele 0 and 1 recodes.
	 */
	public List<SNP> getSNPs() { return new ArrayList<SNP>(snp2counter.keySet()); }

	/**
	 * Returns the allele coded as allele 0 at this SNP.
	 */
	public String getAllele0(SNP snp) {
		if (snp == null)
			throw new NullPointerException("snp");
		AlleleCounter counter = snp2counter.get(snp);
		if (counter != null) {
			if (counter.existsMinorAllele()) { // The major allele is coded as 0.
				Set<String> alleles = counter.getAlleles();
				alleles.remove(counter.getMinorAllele());
				if (alleles.size() > 1)
					throw new RuntimeException(counter.getAlleles().toString());
				return alleles.iterator().next();
			}
			else { // Samples are monomorphic at this SNP, we code the observed allele as 0.
				Set<String> alleles = counter.getAlleles();
				return alleles.iterator().next();
			}
		}
		return null;
	}

	/**
	 * Returns the allele coded as allele 1 at this SNP.
	 */
	public String getAllele1(SNP snp) {
		if (snp == null)
			throw new NullPointerException("snp");
		AlleleCounter counter = snp2counter.get(snp);
		if (counter != null) {
			if (counter.existsMinorAllele()) { // The minor allele is coded as 1.
				return counter.getMinorAllele();
			}
			else { // Samples are monomorphic at this SNP, there is no available allele to code as 1 so we return null.
				return null;
			}
		}
		return null;
	}

	/**
	 * Writes this legend to the given output stream.
	 */
	public void write(OutputStream out) throws IOException {
		if (out == null)
			throw new NullPointerException("out");
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
		writer.write(toString());
		writer.flush();
		writer.close();
	}

	/**
	 * Returns a tabular string representation of this legend.
	 */
	public String toString() {
		char DELIMITER = ' ';
	   	String EOL = "\n";
	   	String MISSING = "-";

		StringBuilder builder = new StringBuilder();
		// Create header.
		builder.append("rs");
		builder.append(DELIMITER).append("position");
		builder.append(DELIMITER).append("a0");
		builder.append(DELIMITER).append("a1");
		builder.append(EOL);
		// Create the rest.
		List<SNP> snps = getSNPs();
		for (Iterator<SNP> it = snps.iterator(); it.hasNext(); ) {
			SNP snp = it.next();
			String a0 = getAllele0(snp);
			if (a0 == null) a0 = MISSING;
			String a1 = getAllele1(snp);
			if (a1 == null) a1 = MISSING;

			builder.append(snp.getName());
			builder.append(DELIMITER).append(snp.getPosition());
			builder.append(DELIMITER).append(a0);
			builder.append(DELIMITER).append(a1);
			builder.append(EOL);
		}
		return builder.toString();
	}
}
