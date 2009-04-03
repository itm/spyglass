// --------------------------------------------------------------------------------
/**
 *
 */
package de.uniluebeck.itm.spyglass.plugin.mappainter;

import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;

// --------------------------------------------------------------------------------
/**
 * Element for the DataStore structure.
 *
 * @author Dariush Forouher
 *
 */
public abstract class MetricPoint implements Comparable<MetricPoint> {

	/**
	 * The position of this pata point
	 */
	public AbsolutePosition position = null;

	/**
	 * the value
	 */
	public double value = 0;

}
