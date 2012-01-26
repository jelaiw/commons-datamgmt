package edu.uab.ssg.util;

import edu.uab.ssg.io.ncbi.*;
import java.util.*;

/**
 * An index for searching SeqGeneMdParser.Records by chromosome position.
 *
 * @author Jelai Wang
 */
public final class SeqGeneMdPositionIndex {
	private Map<String, List<SeqGeneMdParser.Record>> chr2md = new LinkedHashMap<String, List<SeqGeneMdParser.Record>>();
	
	/**
	 * Constructs the index.
	 */
	public SeqGeneMdPositionIndex(List<SeqGeneMdParser.Record> records) {
		if (records == null)
			throw new NullPointerException("records");
		// Organize records by chromosome.
		for (int i = 0, n = records.size(); i < n; i++) {
			SeqGeneMdParser.Record record = records.get(i);
			String chr = record.getChromosome();
			List<SeqGeneMdParser.Record> list = chr2md.get(chr);
			if (list == null) {
				list = new ArrayList<SeqGeneMdParser.Record>();
				chr2md.put(chr, list);
			}
			list.add(record);
		}
	}

	/**
	 * Returns SeqGeneMdParser.Records that contain the given base-pair position within the chr start and stop boundaries.
	 */
	List<SeqGeneMdParser.Record> getRecords(String chr, int pos) {
		if (chr == null)
			throw new NullPointerException("chr");
		if (pos < 0)
			throw new IllegalArgumentException(String.valueOf(pos));
		if (!chr2md.containsKey(chr))
			throw new IllegalArgumentException(chr);
		List<SeqGeneMdParser.Record> matches = new ArrayList<SeqGeneMdParser.Record>();
		List<SeqGeneMdParser.Record> list = chr2md.get(chr);

		for (int i = 0, n = list.size(); i < n; i++) { // Linear search.
			SeqGeneMdParser.Record record = list.get(i);
			int chrStart = record.getChrStart();
			int chrStop = record.getChrStop();
			if (pos >= chrStart && pos <= chrStop) matches.add(record);
		}
		return matches;
	}
}
