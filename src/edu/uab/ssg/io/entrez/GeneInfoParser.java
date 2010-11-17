package edu.uab.ssg.io.entrez;

import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 *	A parser for the Entrez Gene gene_info file located at <a href="ftp://ftp.ncbi.nih.gov/gene/DATA/gene_info.gz">ftp://ftp.ncbi.nih.gov/gene/DATA/gene_info.gz</a>.
 *	<p>The gene_info file format is described at <a href="ftp://ftp.ncbi.nih.gov/gene/DATA/README">ftp://ftp.ncbi.nih.gov/gene/DATA/README</a>. A locally cached version is <a href="doc-files/README">available here</a>. This parser should also work for the species-specific extractions of gene_info available at, for example, <a href="ftp://ftp.ncbi.nih.gov/gene/DATA/GENE_INFO/Mammalia/">ftp://ftp.ncbi.nih.gov/gene/DATA/GENE_INFO/Mammalia/</a>.</p>
 * 	
 * 	<p>The field delimiter is a tab character. The dash character, '-', indicates that a value is not available, and will be returned as null in this API. For certain fields, like synonyms and dbXrefs, multiple values are concatenated with a pipe character, '|', as the delimiter. These values will be parsed and returned as a list.</p>
 *
 *	@author Jelai Wang
 */
public final class GeneInfoParser {
	private static final String DELIMITER = "\t";
	private static final String NOT_AVAILABLE = "-";
	private static final String PIPE_DELIMITER = "\\|";

	/**
	 *	Constructs the parser.
	 */
	public GeneInfoParser() {
	}

	/**
	 *	Parses the input stream for gene info records.
	 */
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
			// Replace values coded as "-" with null.
			for (int i = 0; i < tmp.length; i++) {
				if (NOT_AVAILABLE.equals(tmp[i])) tmp[i] = null;
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

	/**
	 *	A gene info record.
	 */
	public interface Record {
		/**
		 *	Returns the unique identifier provided by the NCBI Taxonomy for the species or strain/isolate.
		 */
		String getTaxID();

		/**
		 *	Returns the unique identifier for this gene.
		 */
		String getGeneID();

		/**
		 *	Returns the default symbol for this gene.
		 */
		String getSymbol();

		/**
		 *	Returns the LocusTag value.
		 */
		String getLocusTag();

		/**
		 *	Returns a pipe-delimited set of unofficial symbols or aliases for this gene.
		 */
		List<String> getSynonyms();

		/**
		 *	Returns a pipe-delimited set of identifiers, of the form <code>database:value</code>, in other databases for this gene.
		 */
		List<String> getdbXrefs();

		/**
		 *	Returns the chromosome on which this gene is located.
		 *	For mitochondrial genomes, the value 'MT' is used.
		 */
		String getChromosome();

		/**
		 *	Returns the map location for this gene.
		 */
		String getMapLocation();

		/**
		 *	Returns a description for this gene.
		 */
		String getDescription();

		/**
		 *	Returns the type assigned to this gene according to the list of options provided in <a href="http://www.ncbi.nlm.nih.gov/IEB/ToolBox/CPP_DOC/lxr/source/src/objects/entrezgene/entrezgene.asn">http://www.ncbi.nlm.nih.gov/IEB/ToolBox/CPP_DOC/lxr/source/src/objects/entrezgene/entrezgene.asn</a>.
		 */
		String getTypeOfGene();

		/**
		 *	Returns the symbol from nomenclature authority, if available.
		 */
		String getSymbolFromNomenclatureAuthority();

		/**
		 *	Returns the full name from nomenclature authority, if available.
		 */
		String getFullNameFromNomenclatureAuthority();

		/**
		 *	Returns the status (O for official, I for interim) of the name from the nomenclature authority, if available.
		 */
		String getNomenclatureStatus();

		/**
		 *	Returns a pipe-delimited set of alternate descriptions that have been assigned to this gene.
		 */
		String getOtherDesignations();

		/**
		 *	Returns the last date, in YYYYMMDD format, this record was updated.
		 */
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
			if (text != null) { // See NOT_AVAILABLE named constant.
				String[] tmp = text.split(PIPE_DELIMITER);
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
