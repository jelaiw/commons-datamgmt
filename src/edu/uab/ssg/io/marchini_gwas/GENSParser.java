package edu.uab.ssg.io.marchini_gwas;

import java.util.*;
import java.io.*;

/**
 * A parser for the genotype file format described at <a href="http://www.stats.ox.ac.uk/~marchini/software/gwas/file_format.html">http://www.stats.ox.ac.uk/~marchini/software/gwas/file_format.html</a>.
 *
 * @author Jelai Wang
 */
public final class GENSParser {
	private static final String DELIMITER = " ";

	/**
	 * Constructs the parser.
	 */
	public GENSParser() {
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

		String line = null;
		while ((line = reader.readLine()) != null) {
			listener.handleParsedRecord(new ParsedGenotypeRecord(line));
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
	 * A record of sample genotypes at a particular SNP.
	 */
	public interface GenotypeRecord {
		/**
		 * Returns the chromosome.
		 */
		String getChromosome();

		/**
		 * Returns the SNP name.
		 */
		String getName();

		/**
		 * Returns the position of the SNP on the chromosome.
		 */
		int getPosition();

		String getAlleleA();
		String getAlleleB();
		List<GenotypeProbabilities> getGenotypeProbabilities();
	}

	public interface GenotypeProbabilities {
		double getProbAA();
		double getProbAB();
		double getProbBB();
		Genotype getGenotype(double threshold);
	}

	public interface Genotype {
		String getAllele1();
		String getAllele2();
	}

	private class ParsedGenotypeRecord implements GenotypeRecord {
		private String line;
		private String chr;
		private String snp;
		private int position;
		private String alleleA, alleleB;
		private List<GenotypeProbabilities> probs;

		private ParsedGenotypeRecord(String line) {
			if (line == null)
				throw new NullPointerException("line");
			this.line = line;

			String[] tokens = line.split(DELIMITER, -1);
			this.chr = tokens[0];
			this.snp = tokens[1];
			this.position = Integer.parseInt(tokens[2]);
			if (position < 0)
				throw new IllegalArgumentException(String.valueOf(position));
			this.alleleA = tokens[3];
			this.alleleB = tokens[4];
			// Check that the genotype probabilities come in sets of three.
			int numOfSamples = tokens.length - 5 / 3;
			if ((tokens.length - 5) % 3 != 0)
				throw new IllegalArgumentException("THE NUMBER OF GENOTYPE PROBABILITIES IS NOT EVENLY DIVISIBLE BY 3: " + line);
			probs = new ArrayList<GenotypeProbabilities>(numOfSamples);
			for (int i = 5; i < tokens.length; i+=3) {
				probs.add(new ParsedGenotypeProbabilities(tokens[i], tokens[i+1], tokens[i+2]));
			}
		}

		public String getChromosome() { return chr; }
		public String getName() { return snp; }
		public int getPosition() { return position; }
		public String getAlleleA() { return alleleA; }
		public String getAlleleB() { return alleleB; }
		public List<GenotypeProbabilities> getGenotypeProbabilities() { return new ArrayList<GenotypeProbabilities>(probs); }

		public String toString() { return line; }

		private class ParsedGenotypeProbabilities implements GenotypeProbabilities {
			private double probAA, probAB, probBB;

			private ParsedGenotypeProbabilities(String probAA, String probAB, String probBB) {
				if (probAA == null) 
					throw new NullPointerException("probAA");
				if (probAB == null) 
					throw new NullPointerException("probAB");
				if (probBB == null) 
					throw new NullPointerException("probBB");
				this.probAA = Double.parseDouble(probAA);
				if (this.probAA < 0. || this.probAA > 1.)
					throw new IllegalArgumentException(probAA);
				this.probAB = Double.parseDouble(probAB);
				if (this.probAB < 0. || this.probAB > 1.)
					throw new IllegalArgumentException(probAB);
				this.probBB = Double.parseDouble(probBB);
				if (this.probBB < 0. || this.probBB > 1.)
					throw new IllegalArgumentException(probBB);
			}

			public double getProbAA() { return probAA; }
			public double getProbAB() { return probAB; }
			public double getProbBB() { return probBB; }

			public Genotype getGenotype(double threshold) {
				if (threshold < 0. || threshold > 1.) {
					throw new IllegalArgumentException(String.valueOf(threshold));
				}
				if (probAA > probAB && probAA > probBB && probAA > threshold) { // AA.
					return new DefaultGenotype(alleleA, alleleA);
				}
				else if (probAB > probAA && probAB > probBB && probAB > threshold) { // AB.
					return new DefaultGenotype(alleleA, alleleB);
				}
				else if (probBB > probAA && probBB > probAB && probBB > threshold) { // BB.
					return new DefaultGenotype(alleleB, alleleB);
				}
				return null;
			}
		}

		private class DefaultGenotype implements Genotype {
			private String allele1, allele2;

			private DefaultGenotype(String allele1, String allele2) {
				this.allele1 = allele1;
				this.allele2 = allele2;
			}

			public String getAllele1() { return allele1; }
			public String getAllele2() { return allele2; }
		}
	}
}
