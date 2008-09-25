package edu.uab.ssg.model.snp;

/**
 * @author Jelai Wang
 */

public final class IlluminaStrand implements Strand {
	// See Bloch 104 for hints on enum implementation in Java.
	public static final IlluminaStrand TOP = new IlluminaStrand("TOP");
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
