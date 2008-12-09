package edu.uab.ssg.model.snp;

/**
 * A strand designation, developed by Illumina, "based on the actual or
 * contextual sequence of each individual SNP".
 * For details, please refer to <a href="doc-files/IlluminaStrand-1.pdf">this technical note</a>.
 *
 * @author Jelai Wang
 */

public final class IlluminaStrand implements Strand {
	// See Bloch 104 for hints on enum implementation in Java.
	
	/**
	 * The TOP strand.
	 */
	public static final IlluminaStrand TOP = new IlluminaStrand("TOP");

	/**
	 * The BOT strand.
	 */
	public static final IlluminaStrand BOT = new IlluminaStrand("BOT");

	private String name;

	private IlluminaStrand(String name) {
		if (name == null)
			throw new NullPointerException("name");
		this.name = name;	
	}

	public String getName() { return name; }
	public String toString() { return name; }
}
