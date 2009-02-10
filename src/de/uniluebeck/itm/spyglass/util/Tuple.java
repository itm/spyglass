// --------------------------------------------------------------------------------
/**
 * 
 */
package de.uniluebeck.itm.spyglass.util;

// --------------------------------------------------------------------------------
/**
 * A simple type-safe helper class for methods returning or receiving tuples of data.
 * 
 * @author Daniel Bimschas
 */
public class Tuple<V, W> {

	/**
	 * The first element of the tuple.
	 */
	public V first;

	/**
	 * The second element of the tuple.
	 */
	public W second;

	// --------------------------------------------------------------------------------
	/**
	 * Constructs a new Tuple instance.
	 * 
	 * @param first
	 * @param second
	 */
	public Tuple(final V first, final W second) {
		super();
		this.first = first;
		this.second = second;
	}

}
