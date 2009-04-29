package edu.uab.ssg.io.decode;

import java.util.*;
import java.io.*;

/**
 * @author Jelai Wang
 */
public final class GTypeFileParser {
	public void parse(InputStream in, RecordListener listener) throws IOException {
		if (in == null)
			throw new NullPointerException("in");
		if (listener == null)
			throw new NullPointerException("listener");
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));

		String[] header = reader.readLine().split("\t", -1);
		if (!"Name".equals(header[0]))
			throw new IllegalArgumentException(header[0]);
		if (!"Chr".equals(header[1]))
			throw new IllegalArgumentException(header[1]);
		if (!"Position".equals(header[2]))
			throw new IllegalArgumentException(header[2]);

		List<String> sampleNames = Collections.unmodifiableList(Arrays.asList(header).subList(3, header.length));

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
		String getChr();
		int getPosition();
		List<String> getSampleNames();
		boolean existsGenotype(String sampleName);
		String getGenotype(String sampleName);
	}

	private class ParsedSNPRecord implements SNPRecord {
		private String line;
		private String name, chr;
		private int position;
		private Map<String, String> sampleNameToGenotype;

		private ParsedSNPRecord(String line, List<String> sampleNames) {
			if (line == null)
				throw new NullPointerException("line");
			this.line = line;
			this.sampleNameToGenotype = new LinkedHashMap<String, String>();

			StringTokenizer tokenizer = new StringTokenizer(line, "\t");
			if (tokenizer.countTokens() != sampleNames.size() + 3)
				throw new IllegalArgumentException(line);
			this.name = tokenizer.nextToken();
			this.chr = tokenizer.nextToken();
			this.position = Integer.parseInt(tokenizer.nextToken());

			int index = 0;
			while (tokenizer.hasMoreTokens()) {
				String genotype = tokenizer.nextToken();
				String sampleName = sampleNames.get(index++);
				sampleNameToGenotype.put(sampleName, genotype);
			}
		}

		public String getName() { return name; }
		public String getChr() { return chr; }
		public int getPosition() { return position; }
		public List<String> getSampleNames() { return Collections.unmodifiableList(new ArrayList<String>(sampleNameToGenotype.keySet())); }
		public boolean existsGenotype(String sampleName) { return sampleNameToGenotype.containsKey(sampleName); }

		public String getGenotype(String sampleName) {
			if (sampleName == null)
				throw new NullPointerException("sampleName");
			if (!sampleNameToGenotype.containsKey(sampleName))
				throw new IllegalArgumentException(sampleName);
			return sampleNameToGenotype.get(sampleName);
		}

		public String toString() { return line; }
	}
}
