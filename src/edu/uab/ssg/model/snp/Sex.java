package edu.uab.ssg.model.snp;

/**
 * @author Jelai Wang
 */

public final class Sex {
	// See Bloch 104 for hints on enum implementation in Java.
	public static final Sex MALE = new Sex("MALE");
	public static final Sex FEMALE = new Sex("FEMALE");

	private String name;

	private Sex(String name) {
		if (name == null)
			throw new NullPointerException("name");
		this.name = name;	
	}

	public String getName() { return name; }
	public String toString() { return name; }
}
