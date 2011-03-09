package edu.uab.ssg.model.snp;

import java.util.Comparator;

/**
 * 	A collection of utility objects and functions.
 *
 *	@author Jelai Wang
 */
public final class Util {
	/**
	 *	A Comparator that orders <tt>SNP</tt> objects by chromosome (lexicographically by name) and position.
	 */
	public static final Comparator<SNP> ORDER_BY_CHROMOSOME_POSITION = new Comparator<SNP>() {
		public int compare(SNP snp1, SNP snp2) {
			String chr1 = snp1.getChromosome();
			String chr2 = snp2.getChromosome();
			if (chr1.equals(chr2)) {
				int pos1 = snp1.getPosition();
				int pos2 = snp2.getPosition();
				if (pos1 < pos2) {
					return -1;
				}
				else if (pos1 > pos2) {
					return 1;
				}
				else {
					return 0;
				}
			}
			else {
				return chr1.compareTo(chr2); // See String.compareTo().
			}
		}

		public boolean equals(Object o) {
			if (o == this) {
				return true;
			}
			return false;
		}
	};

	private Util() {
	}
}
