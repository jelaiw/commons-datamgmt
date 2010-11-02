package edu.uab.ssg.io.entrez;

import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public final class GeneInfoParser {
	private static final String DELIMITER = "\t";
	private static final String NOT_AVAILABLE = "-";

	public List<Record> parse(InputStream in) throws IOException {
		if (in == null)
			throw new NullPointerException("in");
		List<Record> list = new ArrayList<Record>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = null;
		while ((line = reader.readLine()) != null) {
			// Skip comments.
			if (line.startsWith("#")) continue;
			String[] tmp = line.split(DELIMITER, -1);
			if (tmp.length != 15) {
				System.err.println("Ignoring line with " + tmp.length + " tokens: " + line);
				continue;
			}

			String taxID = tmp[0];
			String geneID = tmp[1];
			String symbol = tmp[2];
			String locusTag = tmp[3];
			String synonyms = tmp[4];
			String dbXrefs = tmp[5];
			String chromosome = tmp[6];
			String mapLocation = tmp[7];
			String description = tmp[8];
			String typeOfGene = tmp[9];
			String symbolFromNomenclatureAuthority = tmp[10];
			String fullNameFromNomenclatureAuthority = tmp[11];
			String nomenclatureStatus = tmp[12];
			String otherDesignations = tmp[13];
			String modificationDate = tmp[14];

			DefaultRecord record = new DefaultRecord(line, taxID, geneID, symbol, locusTag, synonyms, dbXrefs, chromosome, mapLocation, description, typeOfGene, symbolFromNomenclatureAuthority, fullNameFromNomenclatureAuthority, nomenclatureStatus, otherDesignations, modificationDate);
			list.add(record);
		}
		reader.close();
		return list;
	}

	public interface Record {
		String getTaxID();
		String getGeneID();
		String getSymbol();
		String getLocusTag();
		List<String> getSynonyms();
		List<String> getdbXrefs();
		String getChromosome();
		String getMapLocation();
		String getDescription();
		String getTypeOfGene();
		String getSymbolFromNomenclatureAuthority();
		String getFullNameFromNomenclatureAuthority();
		String getNomenclatureStatus();
		String getOtherDesignations();
		String getModificationDate();
	}

	private class DefaultRecord implements Record {
		private String line;
		private String taxID, geneID, symbol, locusTag;
		private List<String> synonyms, dbXrefs;
		private String chromosome, mapLocation;
		private String description, typeOfGene;
		private String symbolFromNomenclatureAuthority, fullNameFromNomenclatureAuthority, nomenclatureStatus;
		private String otherDesignations, modificationDate;

		private DefaultRecord(String line, String taxID, String geneID, String symbol, String locusTag, String synonyms, String dbXrefs, String chromosome, String mapLocation, String description, String typeOfGene, String symbolFromNomenclatureAuthority, String fullNameFromNomenclatureAuthority, String nomenclatureStatus, String otherDesignations, String modificationDate) {
			this.taxID = taxID;
			this.geneID = geneID;
			this.symbol = symbol;
			this.locusTag = locusTag;
			this.synonyms = parsePipeDelimitedText(synonyms);
			this.dbXrefs = parsePipeDelimitedText(dbXrefs);
			this.chromosome = chromosome;
			this.mapLocation = mapLocation;
			this.description = description;
			this.typeOfGene = typeOfGene;
			this.symbolFromNomenclatureAuthority = symbolFromNomenclatureAuthority;
			this.fullNameFromNomenclatureAuthority = fullNameFromNomenclatureAuthority;
			this.nomenclatureStatus = nomenclatureStatus;
			this.otherDesignations = otherDesignations;
			this.modificationDate = modificationDate;
		}

		private List<String> parsePipeDelimitedText(String text) {
			List<String> list = new ArrayList<String>();
			if (!NOT_AVAILABLE.equals(text)) {
				String[] tmp = text.split("\\|");
				for (int i = 0; i < tmp.length; i++) {
					list.add(tmp[i]);
				}
			}
			return list;
		}

		public String getTaxID() { return taxID; }
		public String getGeneID() { return geneID; }
		public String getSymbol() { return symbol; }
		public String getLocusTag() { return locusTag; }
		public List<String> getSynonyms() { return Collections.unmodifiableList(synonyms); }
		public List<String> getdbXrefs() { return Collections.unmodifiableList(dbXrefs); }
		public String getChromosome() { return chromosome; }
		public String getMapLocation() { return mapLocation; }
		public String getDescription() { return description; }
		public String getTypeOfGene() { return typeOfGene; }
		public String getSymbolFromNomenclatureAuthority() { return symbolFromNomenclatureAuthority; }
		public String getFullNameFromNomenclatureAuthority() { return fullNameFromNomenclatureAuthority; }
		public String getNomenclatureStatus() { return nomenclatureStatus; }
		public String getOtherDesignations() { return otherDesignations; }
		public String getModificationDate() { return modificationDate; }

		public String toString() { return line; }
	}
}
