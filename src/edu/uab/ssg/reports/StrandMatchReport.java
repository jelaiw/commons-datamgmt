package edu.uab.ssg.reports;

import edu.uab.ssg.io.plink.*;
import edu.uab.ssg.model.snp.*;
import java.io.*;
import java.util.*;
import java.text.NumberFormat;

public final class StrandMatchReport {
	public static void main(String[] args) throws IOException {
		String refMapFileName = args[0];
		String refPedFileName = args[1];
		String tgtMapFileName = args[2];
		String tgtPedFileName = args[3];

		System.out.print("Parsing " + refMapFileName + " ... ");
		List<SNP> refMarkers = parseMapFile(refMapFileName);
		System.out.println("read " + refMarkers.size() + " markers from marker map file.");

		System.out.print("Parsing " + tgtMapFileName + " ... ");
		List<SNP> tgtMarkers = parseMapFile(tgtMapFileName);
		System.out.println("read " + tgtMarkers.size() + " markers from marker map file.");

		Map<String, AlleleCounter> ref2counter = countAlleles(refMarkers, refPedFileName);
		Map<String, AlleleCounter> tgt2counter = countAlleles(tgtMarkers, tgtPedFileName);

		// Intersection set of markers by name.
		Map<String, SNP> refMarkerMap = new LinkedHashMap<String, SNP>();
		Map<String, SNP> tgtMarkerMap = new LinkedHashMap<String, SNP>();
		for (Iterator<SNP> it = refMarkers.iterator(); it.hasNext(); ) {
			SNP snp = it.next();
			if (refMarkerMap.containsKey(snp.getName())) {
				System.err.println(snp.getName() + " already exists!");
			}
			refMarkerMap.put(snp.getName(), snp);
		}
		for (Iterator<SNP> it = tgtMarkers.iterator(); it.hasNext(); ) {
			SNP snp = it.next();
			if (tgtMarkerMap.containsKey(snp.getName())) {
				System.err.println(snp.getName() + " already exists!");
			}
			tgtMarkerMap.put(snp.getName(), snp);
		}
		Set<String> intersectionSet = new LinkedHashSet<String>(refMarkerMap.keySet());
		intersectionSet.retainAll(tgtMarkerMap.keySet());
		System.out.println("Number of markers in intersection set (by name) = " + intersectionSet.size());
		
		StringBuilder builder = new StringBuilder();
		String DELIMITER = "\t", EOL = "\n";
		// Build header.
		builder.append("ref marker");
		builder.append(DELIMITER).append("chr");
		builder.append(DELIMITER).append("pos");
		builder.append(DELIMITER).append("alleles");
		builder.append(DELIMITER).append("freqs");
		builder.append(DELIMITER).append("tgt marker");
		builder.append(DELIMITER).append("chr");
		builder.append(DELIMITER).append("pos");
		builder.append(DELIMITER).append("alleles");
		builder.append(DELIMITER).append("freqs");
		builder.append(DELIMITER).append("flip");
		builder.append(EOL);
		// Build the rest of the rows.	
		for (Iterator<String> it = intersectionSet.iterator(); it.hasNext(); ) {
			String markerName = it.next();
			SNP refMarker = refMarkerMap.get(markerName);
			SNP tgtMarker = tgtMarkerMap.get(markerName);
			AlleleCounter refCounter = ref2counter.get(refMarker.getName());
			AlleleCounter tgtCounter = tgt2counter.get(tgtMarker.getName());
			Set<String> refAlleles = refCounter.getAlleles();
			Set<String> tgtAlleles = tgtCounter.getAlleles();

			boolean flip = false;
			if (refCounter.isBiallelic() && tgtCounter.isBiallelic()) {
				if (!refCounter.isAmbiguous() && !tgtCounter.isAmbiguous()) {
					if (!refAlleles.equals(tgtAlleles)) flip = true;
				}
			}

			builder.append(refMarker.getName());
			builder.append(DELIMITER).append(refMarker.getChromosome());
			builder.append(DELIMITER).append(refMarker.getPosition());
			builder.append(DELIMITER).append(refAlleles);
			builder.append(DELIMITER).append(freqsOneLiner(refCounter));
			builder.append(DELIMITER).append(tgtMarker.getName());
			builder.append(DELIMITER).append(tgtMarker.getChromosome());
			builder.append(DELIMITER).append(tgtMarker.getPosition());
			builder.append(DELIMITER).append(tgtAlleles);
			builder.append(DELIMITER).append(freqsOneLiner(tgtCounter));
			builder.append(DELIMITER).append(flip);
			builder.append(EOL);
		}
		System.out.print(builder.toString());
	}

	static final NumberFormat formatter = NumberFormat.getInstance();
	static {
		formatter.setMaximumFractionDigits(2);
	}

	private static String freqsOneLiner(AlleleCounter counter) {
		StringBuilder builder = new StringBuilder();
		String DELIMITER = ",";
		Set<String> alleles = counter.getAlleles();
		if (alleles.size() >= 2) {
			Iterator<String> it = alleles.iterator();
			String allele = it.next();
			double freq = counter.getRelativeFrequency(allele);
			builder.append(allele).append("=").append(formatter.format(freq));
			while (it.hasNext()) {
				allele = it.next();
				freq = counter.getRelativeFrequency(allele);
				builder.append(DELIMITER).append(allele).append("=").append(formatter.format(freq));
			}
		}
		else if (alleles.size() == 1) {
			Iterator<String> it = alleles.iterator();
			String allele = it.next();
			double freq = counter.getRelativeFrequency(allele);
			builder.append(allele).append("=").append(formatter.format(freq));
		}
		return builder.toString();
	}

	private static List<SNP> parseMapFile(String mapFileName) throws IOException {
		MAPParser mapParser = new MAPParser();
		List<SNP> markers = mapParser.parse(new FileInputStream(mapFileName), new MAPParser.BadRecordFormatListener() {
			public void handleBadRecordFormat(String line) {
				System.err.println(line);
			}
		});
		return markers;
	}

	private static Map<String, AlleleCounter> countAlleles(final List<SNP> markers, String pedFileName) throws IOException {
		final Map<String, AlleleCounter> map = new LinkedHashMap<String, AlleleCounter>();
		for (Iterator<SNP> it = markers.iterator(); it.hasNext(); ) {
			SNP snp = it.next();
			String snpName = snp.getName();
			if (map.containsKey(snpName)) {
				throw new RuntimeException(snp + " already exists.");
			}
			map.put(snpName, new AlleleCounter());
		}

		// Parse the reference PED file for genotype calls and count alleles.
		PEDParser pedParser = new PEDParser();
		pedParser.parse(new FileInputStream(pedFileName), new PEDParser.RecordListener() {
			public void handleParsedRecord(PEDParser.SampleRecord record) {
				int numOfCalls = record.getNumberOfAvailableGenotypeCalls();
				if (numOfCalls != markers.size()) { // Sanity check.
					throw new RuntimeException(numOfCalls + " " + markers.size());
				}
				for (int i = 0; i < numOfCalls; i++) {
					SNP snp = markers.get(i);
					AlleleCounter counter = map.get(snp.getName());
					counter.addAllele(record.getAllele1(i));
					counter.addAllele(record.getAllele2(i));
				}
			}

			public void handleBadRecordFormat(String line) {
				System.err.println(line);
			}
		});
		return map;
	}
}
