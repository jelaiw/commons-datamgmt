package edu.uab.ssg.io.hapmap;

import java.util.*;
import java.io.*;

/**
 * @author Jelai Wang
 */
public final class HaplotypeFileParser {
	public void parse(InputStream in, RecordListener listener) throws IOException {
		if (in == null)
			throw new NullPointerException("in");
		if (listener == null)
			throw new NullPointerException("listener");
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));

		String[] header = reader.readLine().split(" ", 0);
		if (!"rsID".equals(header[0]))
			throw new IllegalArgumentException(header[0]);
		if (!"position_b36".equals(header[1]))
			throw new IllegalArgumentException(header[1]);

		List<String> sampleNames = Collections.unmodifiableList(Arrays.asList(header).subList(2, header.length));
		if (sampleNames.size() % 2 != 0) // We expect pairs of sample names.
			throw new IllegalArgumentException(sampleNames.toString());

		String line = null;
		while ((line = reader.readLine()) != null) {
			SNPRecord record = null;
			try {
				record = new ParsedSNPRecord(line, sampleNames);
			}
			catch (RuntimeException e) {
				listener.handleBadRecordFormat(line);
				continue;
			}
			listener.handleParsedRecord(record);
		}
	}

	public interface RecordListener {
		void handleParsedRecord(SNPRecord record);
		void handleBadRecordFormat(String line);
	}

	public interface SNPRecord {
		String getName();
		int getPosition();
		List<String> getSampleNames();
		boolean existsAlleleA(String sampleName);
		boolean existsAlleleB(String sampleName);
		String getAlleleA(String sampleName);
		String getAlleleB(String sampleName);
	}

	private class ParsedSNPRecord implements SNPRecord {
		private String line;
		private String name;
		private int position;
		private Map<String, String> sampleNameToAllele;

		private ParsedSNPRecord(String line, List<String> sampleNames) {
			if (line == null)
				throw new NullPointerException("line");
			this.line = line;
			this.sampleNameToAllele = new LinkedHashMap<String, String>();

			StringTokenizer tokenizer = new StringTokenizer(line);
			if (tokenizer.countTokens() != sampleNames.size() + 2)
				throw new IllegalArgumentException(line);
			this.name = tokenizer.nextToken();
			this.position = Integer.parseInt(tokenizer.nextToken());

			int index = 0;
			while (tokenizer.hasMoreTokens()) {
				String allele = tokenizer.nextToken();
				String sampleName = sampleNames.get(index++);
				sampleNameToAllele.put(sampleName, allele);
			}
		}

		public String getName() { return name; }
		public int getPosition() { return position; }

		public List<String> getSampleNames() { 
			Set<String> set = new LinkedHashSet<String>();
			for (Iterator<String> it = sampleNameToAllele.keySet().iterator(); it.hasNext(); ) {
				String sampleName = it.next();
				set.add(sampleName.substring(0, sampleName.lastIndexOf('_')));
			}
			return new ArrayList<String>(set);
		}

		public boolean existsAlleleA(String sampleName) { return sampleNameToAllele.containsKey(sampleName + "_A"); }
		public boolean existsAlleleB(String sampleName) { return sampleNameToAllele.containsKey(sampleName + "_B"); }

		public String getAlleleA(String sampleName) {
			if (sampleName == null)
				throw new NullPointerException("sampleName");
			if (!sampleNameToAllele.containsKey(sampleName + "_A"))
				throw new IllegalArgumentException(sampleName);
			return sampleNameToAllele.get(sampleName + "_A");
		}

		public String getAlleleB(String sampleName) {
			if (sampleName == null)
				throw new NullPointerException("sampleName");
			if (!sampleNameToAllele.containsKey(sampleName + "_B"))
				throw new IllegalArgumentException(sampleName);
			return sampleNameToAllele.get(sampleName + "_B");
		}

		public String toString() { return line; }
	}
}
