package edu.uab.ssg.model.snp;

/**
 * An enum implementation for sex.
 *
 * @author Jelai Wang
 */

// See Bloch 104 for hints on enum implementation in Java.
public final class Sex {
	/**
	 * The male sex.
	 */
	public static final Sex MALE = new Sex("MALE");

	/**
	 * The female sex.
	 */
	public static final Sex FEMALE = new Sex("FEMALE");

	private String name;

	private Sex(String name) {
		if (name == null)
			throw new NullPointerException("name");
		this.name = name;	
	}

	/**
	 * Returns a string representation for debugging purposes.
	 */
	public String toString() { return name; }
}
