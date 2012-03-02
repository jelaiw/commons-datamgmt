package edu.uab.ssg.reports;

import edu.uab.ssg.io.plink.*;
import edu.uab.ssg.model.snp.*;
import java.util.*;
import java.io.*;

public class FlipCalls {
	public static void main(String[] args) throws IOException {
		String strandMatchReportFileName = args[0];
		String mapFileName = args[1];
		String pedFileName = args[2];
		String outputFileName = args[3];

		System.out.print("Parsing strand match report file " + strandMatchReportFileName + " ... ");
		final Map<String, Boolean> marker2flip = parseStrandMatchReport(strandMatchReportFileName);
		System.out.println("read " + marker2flip.size() + " flip calls.");

		MAPParser mapParser = new MAPParser();
		final List<SNP> markers = mapParser.parse(new FileInputStream(mapFileName), new MAPParser.BadRecordFormatListener() {
			public void handleBadRecordFormat(String line) {
				System.err.println(line);
			}
		});
		System.out.println("Read " + markers.size() + " markers from " + mapFileName + ".");

		System.out.print("Writing output to " + outputFileName + " ... ");
		final PEDWriter pedWriter = new PEDWriter(markers, new FileOutputStream(outputFileName));
		PEDParser pedParser = new PEDParser();
		pedParser.parse(new FileInputStream(pedFileName), new PEDParser.RecordListener() {
			public void handleParsedRecord(PEDParser.SampleRecord record) {
				int numOfCalls = record.getNumberOfAvailableGenotypeCalls();
				if (numOfCalls != markers.size()) { // Sanity check.
					throw new RuntimeException(numOfCalls + " " + markers.size());
				}
				SampleBuilder builder = new SampleBuilder(record.getFID());
				for (int i = 0; i < numOfCalls; i++) {
					SNP snp = markers.get(i);
					String a1 = record.getAllele1(i);
					String a2 = record.getAllele2(i);
					Strand strand = IlluminaStrand.TOP; // LOOK!!

					// Translate genotype call to opposite strand if flip.
					boolean flip = marker2flip.get(snp.getName());
					if (flip && a1 != null) a1 = translateToOppositeStrand(a1);
					if (flip && a2 != null) a2 = translateToOppositeStrand(a2);

					builder.setGenotype(snp, a1, a2, strand);
				}
				try {
					pedWriter.write(builder.getInstance());
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}

			public void handleBadRecordFormat(String line) {
				System.err.println(line);
			}
		});
		pedWriter.close();
		System.out.println("done!");
	}

	private static String translateToOppositeStrand(String allele) {
		if ("A".equals(allele)) {
			return "T";
		}
		else if ("C".equals(allele)) {
			return "G";
		}
		else if ("G".equals(allele)) {
			return "C";
		}
		else if ("T".equals(allele)) {
			return "A";
		}
		else {
			throw new RuntimeException(allele);
		}
	}

	private static Map<String, Boolean> parseStrandMatchReport(String fileName) throws IOException {
		Map<String, Boolean> map = new LinkedHashMap<String, Boolean>();
		BufferedReader in = new BufferedReader(new FileReader(fileName));
		String DELIMITER = "\t";
		// Discard first three header lines.
		in.readLine();
		in.readLine();
		in.readLine();
		// Parse rest of header.
		String[] header = in.readLine().split(DELIMITER, -1);
		if (header.length != 11)
			throw new RuntimeException(String.valueOf(header.length));
		if (!"ref marker".equals(header[0]))
			throw new RuntimeException(header[0]);
		if (!"flip".equals(header[10]))
			throw new RuntimeException(header[10]);
		// Parse rest of the rows.
		String line = null;
		while ((line = in.readLine()) !=  null) {
			String[] tokens = line.split(DELIMITER, -1);
			if (tokens.length != 11)
				throw new RuntimeException(String.valueOf(tokens.length));
			String refMarker = tokens[0];
			Boolean flip = Boolean.valueOf(tokens[10]);
			map.put(refMarker, flip);
		}
		in.close();
		return map;
	}
}
