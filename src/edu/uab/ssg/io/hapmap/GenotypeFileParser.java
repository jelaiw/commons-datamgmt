package edu.uab.ssg.io.hapmap;

import java.util.*;
import java.io.*;

/**
 * A parser for the genotype file format for the HapMap phases I+II+III release #28 (NCBI build 36, dbSNP b126) data located at <a href="http://hapmap.ncbi.nlm.nih.gov/downloads/genotypes/2010-08_phaseII+III/">http://hapmap.ncbi.nlm.nih.gov/downloads/genotypes/2010-08_phaseII+III/</a>.
 * The file format is described in the README at <a href="http://hapmap.ncbi.nlm.nih.gov/downloads/genotypes/00README.txt">http://hapmap.ncbi.nlm.nih.gov/downloads/genotypes/00README.txt</a>. An excerpt is below:
 *
 * <p><tt>
 * Col1: refSNP rs# identifier at the time of release (NB might merge with another rs# in the future)<br/>
 * Col2: SNP alleles according to dbSNP<br/>
 * Col3: chromosome that SNP maps to <br/>
 * Col4: chromosome position of SNP, in basepairs on reference sequence<br/>
 * Col5: strand of reference sequence that SNP maps to<br/>
 * Col6: version of reference sequence assembly<br/>
 * Col7: HapMap genotype center that produced the genotypes<br/>
 * Col8: LSID for HapMap protocol used for genotyping<br/>
 * Col9: LSID for HapMap assay used for genotyping<br/>
 * Col10: LSID for panel of individuals genotyped<br/>
 * Col11: QC-code, currently 'QC+' for all entries (for future use)<br/>
 * Col12 and on: observed genotypes of samples, one per column, sample identifiers in column headers (Coriell catalog numbers, example: NA10847). Duplicate samples have .dup suffix.<br/>
 * </tt></p>
 * An example, with selected records from genotypes_chr16_CHB_r28_nr.b36_fwd.txt, is below:
 * <p><tt>
 * rs# alleles chrom pos strand assembly# center protLSID assayLSID panelLSID QCcode NA18524 NA18525 NA18526 NA18527 NA18528 NA18529 NA18531 NA18532 NA18533 NA18534 NA18536 NA18537 NA18538 NA18539 NA18540 NA18541 NA18542 NA18543 NA18544 NA18545 NA18546 NA18547 NA18548 NA18550 NA18552 NA18553 NA18555 NA18557 NA18558 NA18559 NA18560 NA18561 NA18562 NA18563 NA18564 NA18566 NA18567 NA18568 NA18569 NA18570 NA18571 NA18572 NA18573 NA18576 NA18577 NA18579 NA18580 NA18582 NA18583 NA18591 NA18592 NA18593 NA18594 NA18595 NA18596 NA18597 NA18599 NA18602 NA18603 NA18605 NA18608 NA18609 NA18610 NA18611 NA18612 NA18613 NA18614 NA18615 NA18616 NA18617 NA18618 NA18619 NA18620 NA18621 NA18622 NA18623 NA18624 NA18626 NA18627 NA18628 NA18629 NA18630 NA18631 NA18632 NA18633 NA18634 NA18635 NA18636 NA18637 NA18638 NA18639 NA18640 NA18641 NA18642 NA18643 NA18644 NA18645 NA18647 NA18648 NA18649 NA18739 NA18740 NA18741 NA18742 NA18743 NA18745 NA18747 NA18748 NA18749 NA18750 NA18751 NA18752 NA18755 NA18757 NA18758 NA18759 NA18760 NA18761 NA18762 NA18763 NA18765 NA18769 NA18771 NA18772 NA18773 NA18774 NA18777 NA18778 NA18779 NA18780 NA18783 NA18784 NA18785 NA18787 NA18790 NA18792 NA18794 NA18795 NA18798<br/>
 * rs3743872 A/G chr16 24045 + ncbi_b36 sanger urn:LSID:illumina.hapmap.org:Protocol:Human_1M_BeadChip:3 urn:LSID:sanger.hapmap.org:Assay:H1Mrs3743872:3 urn:lsid:dcc.hapmap.org:Panel:Han_Chinese:4 QC+ GG GG GG GG GG GG GG AG GG GG GG GG GG GG NN GG GG GG GG GG GG NN GG GG GG GG GG GG GG GG GG GG GG GG GG GG GG GG GG GG GG GG GG GG AG GG GG GG GG GG GG GG GG GG GG GG GG GG GG GG GG GG GG GG GG GG GG GG GG GG GG GG GG GG GG GG GG GG GG GG GG GG GG GG GG AG GG GG GG GG GG GG GG GG GG GG GG GG GG GG GG GG GG GG GG GG GG GG GG GG GG GG GG GG GG AG GG GG GG GG AG GG AG GG GG GG GG GG GG GG GG GG GG GG GG GG AG GG AG<br/>
 * rs2562132 C/T chr16 24170 + ncbi_b36 imsut-riken urn:lsid:imsut-riken.hapmap.org:Protocol:genotyping:1 urn:lsid:imsut-riken.hapmap.org:Assay:2562132:21 urn:LSID:dcc.hapmap.org:Panel:Han_Chinese:1 QC+ CT NN TT NN NN TT NN CT NN NN NN CT NN NN CC NN CT NN NN CC NN CT NN CC CC NN TT NN CT NN NN CC TT TT CC CT NN NN NN TT TT TT TT CT TT CT NN CC NN NN CT CC CT NN NN NN NN NN CC CT CC TT NN TT CC NN NN NN NN NN NN NN CT TT TT CT CT NN NN NN NN NN NN CT CT NN CT TT CT NN NN NN NN NN NN NN NN NN NN NN NN NN NN NN NN NN NN NN NN NN NN NN NN NN NN NN NN NN NN NN NN NN NN NN NN NN NN NN NN NN NN NN NN NN NN NN NN NN NN<br/>
 * ...<br/>
 * rs9673344 C/T chr16 88801983 + ncbi_b36 perlegen urn:lsid:perlegen.hapmap.org:Protocol:Genotyping_1.0.0:2 urn:lsid:perlegen.hapmap.org:Assay:25760.6896899:1 urn:LSID:dcc.hapmap.org:Panel:Han_Chinese:2 QC+ CC NN CC NN NN CC NN CC NN NN NN CC NN NN CC NN CC NN NN CC NN CC NN CC CC NN CC NN CC NN NN CC CC CC CC CC NN NN NN CC CC CC CC CC CC CC NN CC NN NN CC CC CC NN NN NN NN NN CC CC CC CC NN CC CC NN NN NN NN NN NN NN CC CC CC CC CC NN NN NN NN NN NN CC CC NN CC CC CC NN NN NN NN NN NN NN NN NN NN NN NN NN NN NN NN NN NN NN NN NN NN NN NN NN NN NN NN NN NN NN NN NN NN NN NN NN NN NN NN NN NN NN NN NN NN NN NN NN NN<br/>
 * </tt><p>
 *
 * The field delimiter is the space character and missing values should be encoded with the 'N' character. Some additional information is available in the README at <a href="http://hapmap.ncbi.nlm.nih.gov/downloads/genotypes/2010-08_phaseII+III/00README.txt">http://hapmap.ncbi.nlm.nih.gov/downloads/genotypes/2010-08_phaseII+III/00README.txt</a>.
 *
 * @author Jelai Wang
 */
public final class GenotypeFileParser {
	private static final String DELIMITER = " ";
	private static final String MISSING_VALUE = "N";

	/**
	 * Constructs the parser.
	 */
	public GenotypeFileParser() {
	}
	
	/**
	 * Parses the input stream for genotype records.
	 * @param in The input stream, typically a file input stream, of the
	 * file to be parsed.
	 * @param listener As the input stream is parsed, each genotype record
	 * is passed to the user-supplied record listener.
	 */
	public void parse(InputStream in, RecordListener listener) throws IOException {
		if (in == null)
			throw new NullPointerException("in");
		if (listener == null)
			throw new NullPointerException("listener");
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String headerLine = reader.readLine();
		String[] header = headerLine.split(" ", 0);
		if (!"rs#".equals(header[0]))
			throw new IllegalArgumentException(header[0]);
		if (!"alleles".equals(header[1]))
			throw new IllegalArgumentException(header[1]);
		if (!"chrom".equals(header[2]))
			throw new IllegalArgumentException(header[2]);
		if (!"pos".equals(header[3]))
			throw new IllegalArgumentException(header[3]);
		if (!"strand".equals(header[4]))
			throw new IllegalArgumentException(header[4]);
		if (!"assembly#".equals(header[5]))
			throw new IllegalArgumentException(header[5]);

		List<String> sampleNames = Collections.unmodifiableList(Arrays.asList(header).subList(11, header.length));
		int expectedNumberOfTokens = 11 + sampleNames.size();
		if (header.length != expectedNumberOfTokens) {
			listener.handleBadRecordFormat(headerLine);
			throw new RuntimeException(header.length + " " + expectedNumberOfTokens);
		}

		int offset = 11; // Index offset to the beginning of the genotype calls.
		String line = null;
		while ((line = reader.readLine()) != null) {
			String[] tokens = line.split(DELIMITER, -1);
			if (tokens.length != expectedNumberOfTokens) {
				listener.handleBadRecordFormat(line);
				continue;
			}

			String snp = tokens[0];
			String alleles = tokens[1];
			String chr = tokens[2];
			int position = Integer.parseInt(tokens[3]);
			String strand = tokens[4];
			String assemblyVersion = tokens[5];

			for (int i = 0, n = sampleNames.size(); i < n; i++) {
				String sampleID = sampleNames.get(i);
				String genotype = tokens[i + offset];
				if (genotype.length() != 2) {
					listener.handleBadRecordFormat(line);
					continue;
				}
				String allele1 = String.valueOf(genotype.charAt(0));
				String allele2 = String.valueOf(genotype.charAt(1));
				if (MISSING_VALUE.equals(allele1)) allele1 = null;
				if (MISSING_VALUE.equals(allele2)) allele2 = null;

				listener.handleParsedRecord(new ParsedGenotypeRecord(line, snp, position, chr, alleles, strand, assemblyVersion, sampleID, allele1, allele2));
			}
		}
	}

	/**
	 * A listener for handling parsed genotype records and problems due to
	 * bad record formatting.
	 */
	public interface RecordListener {
		/**
		 * Handles successfully parsed genotype record.
		 */
		void handleParsedRecord(GenotypeRecord record);

		/**
		 * Handles input that could not be parsed due to a formatting problem.
		 * @param line The line of text that could not be parsed.
		 */
		void handleBadRecordFormat(String line);
	}

	/**
	 * A genotype record.
	 */
	public interface GenotypeRecord {
		/**
		 * Returns the SNP name.
		 */
		String getSNPName();

		/**
		 * Returns the position of the SNP on the chromosome.
		 */
		int getPosition();

		/**
		 * Returns the chromosome.
		 */
		String getChromosome();

		/**
		 * Returns the observed alleles for the strand.
		 */
		String getAlleles();

		/**
		 * Returns the strand.
		 */
		String getStrand();

		/**
		 * Returns the genome assembly version.
		 */
		String getAssemblyVersion();

		/**
		 * Returns the sample identifier.
		 */
		String getSampleID();

		/**
		 * Returns the first allele observed on the strand for this sample or null if the data are missing.
		 */
		String getAllele1();

		/**
		 * Returns the second allele observed on the strand for this sample or null if the data are missing.
		 */
		String getAllele2();
	}

	private class ParsedGenotypeRecord implements GenotypeRecord {
		private String line;
		private String snp;
		private int position;
		private String chr;
		private String alleles, strand, assemblyVersion;
		private String sampleID;
		private String allele1, allele2;

		private ParsedGenotypeRecord(String line, String snp, int position, String chr, String alleles, String strand, String assemblyVersion, String sampleID, String allele1, String allele2) {
			if (line == null)
				throw new NullPointerException("line");
			if (snp == null)
				throw new NullPointerException("snp");
			if (position < 0)
				throw new IllegalArgumentException(String.valueOf(position));
			if (chr == null)
				throw new NullPointerException("chr");
			if (alleles == null)
				throw new NullPointerException("alleles");
			if (strand == null)
				throw new NullPointerException("strand");
			if (assemblyVersion == null)
				throw new NullPointerException("assemblyVersion");
			if (sampleID == null)
				throw new NullPointerException("sampleID");

			this.line = line;
			this.snp = snp;
			this.position = position;
			this.chr = chr;
			this.alleles = alleles;
			this.strand = strand;
			this.assemblyVersion = assemblyVersion;
			this.sampleID = sampleID;
			this.allele1 = allele1;
			this.allele2 = allele2;
		}

		public String getSNPName() { return snp; }
		public int getPosition() { return position; }
		public String getChromosome() { return chr; }
		public String getAlleles() { return alleles; }
		public String getStrand() { return strand; }
		public String getAssemblyVersion() { return assemblyVersion; }
		public String getSampleID() { return sampleID; }
		public String getAllele1() { return allele1; }
		public String getAllele2() { return allele2; }

		public String toString() { return line; }
	}
}
