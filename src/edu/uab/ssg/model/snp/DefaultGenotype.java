package edu.uab.ssg.model.snp;

/* package private */ final class DefaultGenotype implements Sample.Genotype {
	private SNP snp;
	private String a1, a2;
	private Strand strand;

	/* package private */ DefaultGenotype(SNP snp, String a1, String a2, Strand strand) {
		if (snp == null)
			throw new NullPointerException("snp");
		this.snp = snp;
		this.a1 = a1;
		this.a2 = a2;
		this.strand = strand;
	}

	public SNP getSNP() { return snp; }
	public String getAllele1() { return a1; }
	public String getAllele2() { return a2; }
	public Strand getStrand() { return strand; }

	public boolean equals(Object o) {
		if (o instanceof DefaultGenotype) {
			DefaultGenotype tmp = (DefaultGenotype) o;
			if (a1 != null && a2 != null && a1.equals(a2)) { // Homozygous.
				return snp.equals(tmp.snp) && a1.equals(tmp.a1) && a2.equals(tmp.a2) && strand.equals(tmp.strand);
			}
			else if (a1 != null && a2 != null && !a1.equals(a2)) { // Heterozygous.
				boolean b = (a1.equals(tmp.a1) && a2.equals(tmp.a2)) || (a1.equals(tmp.a2) && a2.equals(tmp.a1));
				return snp.equals(tmp.snp) && b && strand.equals(tmp.strand);
			}
			else if (a1 == null && a2 == null) { // Missing.
				return snp.equals(tmp.snp) && tmp.a1 == null && tmp.a2 == null && strand.equals(tmp.strand);
			}
		}
		return false;
	}

	public int hashCode() {
		int tmp = 17;
		if (a1 != null && a2 != null && a1.equals(a2)) { // Homozygous.
			tmp = 37 * tmp + snp.hashCode();
			tmp = 37 * tmp + a1.hashCode();
			tmp = 37 * tmp + a2.hashCode();
			tmp = 37 * tmp + strand.hashCode();
		}
		else if (a1 != null && a2 != null && !a1.equals(a2)) { // Heterozygous.
			if (a1.compareTo(a2) < 0) {
				tmp = 37 * tmp + snp.hashCode();
				tmp = 37 * tmp + a1.hashCode();
				tmp = 37 * tmp + a2.hashCode();
				tmp = 37 * tmp + strand.hashCode();
			}
			else {
				tmp = 37 * tmp + snp.hashCode();
				tmp = 37 * tmp + a2.hashCode();
				tmp = 37 * tmp + a1.hashCode();
				tmp = 37 * tmp + strand.hashCode();
			}
		}
		else if (a1 == null && a2 == null) { // Missing.
			tmp = 37 * tmp + snp.hashCode();
			tmp = 37 * tmp + strand.hashCode();
		}
		return tmp;
	}

	public String toString() { 
		String DELIMITER = " ";
		return snp.getName() + DELIMITER + a1 + DELIMITER + a2 + DELIMITER + strand; 
	}
}
