package edu.uab.ssg.io.hapgen;

import edu.uab.ssg.model.snp.*;
import java.util.*;
import java.io.*;

/**
 * A file-based Legend implementation.
 *
 * @author Jelai Wang
 */
public final class LegendFile implements Legend {
	private static final String DELIMITER = " ";
	private static final String MISSING_VALUE = "-";
	private Map<SNP, String[]> map = new LinkedHashMap<SNP, String[]>();

	/**
	 * Constructs the legend from a user-supplied input stream.
	 * @param in The input stream, usually a FileInputStream, from which to construct this legend.
	 */
	public LegendFile(InputStream in) throws IOException {
		if (in == null)
			throw new NullPointerException("in");
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		// Header.
		String[] header = reader.readLine().split(DELIMITER);
		if (!"rs".equals(header[0]))
			throw new IllegalArgumentException(header[0]);
		if (!"position".equals(header[1]))
			throw new IllegalArgumentException(header[1]);
		if (header.length != 4)
			throw new IllegalArgumentException(Arrays.asList(header).toString());
		// Rest of lines.
		String line = null;
		while ((line = reader.readLine()) != null) {
			String[] tmp = line.split(DELIMITER);
			if (tmp.length != 4)
				throw new IllegalArgumentException(Arrays.asList(tmp).toString());
			String snpName = tmp[0];
			String chr = "NA"; // LOOK!! This file format isn't complete.
			int position = Integer.parseInt(tmp[1]);
			String a0 = tmp[2];
			if (MISSING_VALUE.equals(a0)) a0 = null;
			String a1 = tmp[3];
			if (MISSING_VALUE.equals(a1)) a1 = null;

			SNP snp = new DefaultSNP(snpName, chr, position);
			map.put(snp, new String[] { a0, a1 });
		}
		reader.close();
	}

	public String getAllele0(SNP snp) {
		if (snp == null)
			throw new NullPointerException("snp");
		if (map.containsKey(snp)) {
			String[] tmp = map.get(snp);
			return tmp[0];
		}
		return null;
	}

	public String getAllele1(SNP snp) {
		if (snp == null)
			throw new NullPointerException("snp");
		if (map.containsKey(snp)) {
			String[] tmp = map.get(snp);
			return tmp[1];
		}
		return null;
	}

	public List<SNP> getSNPs() { return new ArrayList<SNP>(map.keySet()); }

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
