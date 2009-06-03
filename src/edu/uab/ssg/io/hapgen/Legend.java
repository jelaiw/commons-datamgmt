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
public interface Legend {
	/**
	 * Returns the SNPs for which this legend provides allele 0 and 1 recodes.
	 */
	List<SNP> getSNPs();

	/**
	 * Returns the allele coded as allele 0 at this SNP.
	 */
	String getAllele0(SNP snp);

	/**
	 * Returns the allele coded as allele 1 at this SNP.
	 */
	String getAllele1(SNP snp);

	/**
	 * Writes this legend to the given output stream.
	 */
	void write(OutputStream out) throws IOException;
}
