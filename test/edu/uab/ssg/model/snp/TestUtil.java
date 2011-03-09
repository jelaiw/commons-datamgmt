package edu.uab.ssg.model.snp;

import junit.framework.TestCase;
import junit.framework.Assert;
import java.util.*;

/**
 * @author Jelai Wang
 */

public final class TestUtil extends TestCase {
	public void testComparator() {
		List<SNP> list = new ArrayList<SNP>();
		SNP snp1 = new DefaultSNP("snp1", "chr1", 3000);
		SNP snp2 = new DefaultSNP("snp2", "chr1", 2000);
		SNP snp3 = new DefaultSNP("snp3", "chr1", 1000);
		SNP snp4 = new DefaultSNP("snp4", "chrX", 1000);
		SNP snp5 = new DefaultSNP("snp5", "chr1", 5000);
		SNP snp6 = new DefaultSNP("snp6", "chrX", 2000);
		SNP snp7 = new DefaultSNP("snp7", "chr10", 2000);
		SNP snp8 = new DefaultSNP("snp8", "chr10", 4000);
		SNP snp9 = new DefaultSNP("snp9", "chr10", 3000);
		list.add(snp1); list.add(snp2); list.add(snp3); list.add(snp4); list.add(snp5); list.add(snp6); list.add(snp7); list.add(snp8); list.add(snp9);

		Collections.sort(list, Util.ORDER_BY_CHROMOSOME_POSITION);

		Assert.assertEquals(snp3, list.get(0));
		Assert.assertEquals(snp2, list.get(1));
		Assert.assertEquals(snp1, list.get(2));
		Assert.assertEquals(snp5, list.get(3));
		Assert.assertEquals(snp7, list.get(4));
		Assert.assertEquals(snp9, list.get(5));
		Assert.assertEquals(snp8, list.get(6));
		Assert.assertEquals(snp4, list.get(7));
		Assert.assertEquals(snp6, list.get(8));
	}
}
