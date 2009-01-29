/*----------------------------------------------------------------------------------------
 * This file is part of the
 * WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de)
 * project SpyGlass is free software; you can redistribute it and/or modify it under the terms of
 * the BSD License. Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for
 * further details.
 * ---------------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.simpleglobalinformation;

import java.util.HashMap;
import java.util.Map;

import de.uniluebeck.itm.spyglass.xmlconfig.StatisticalInformationEvaluator.STATISTICAL_OPERATIONS;

// --------------------------------------------------------------------------------
/**
 * Instances of this class perform statistical operations on an map of buffered values.
 * 
 * @author Sebastian Ebers
 * 
 */
public class IDBasedStatisticalOperation {

	/** The buffer where the identifiers are mapped associated values */
	private Map<Integer, Float> valueMap;

	/** The object which actually performs the statistical operation */
	private StatisticalOperation statOperation;

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 * 
	 * @param defaultOperation
	 *            the operation used by default when calculating a statistical value
	 */
	public IDBasedStatisticalOperation(final STATISTICAL_OPERATIONS defaultOperation) {
		statOperation = new StatisticalOperation(1, defaultOperation);
		valueMap = new HashMap<Integer, Float>();
	}

	// --------------------------------------------------------------------------------
	/**
	 * Add a value to the buffer
	 * 
	 * @param id
	 *            an identifier of the object which is associated to the value
	 * @param value
	 *            the value to add
	 * @return the result of the statistical operation which is applianced on the buffer.
	 */
	public synchronized float addValue(final int id, final float value) {
		valueMap.put(id, value);
		return getValue();
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns the value a statistical operation calculated using the buffered values
	 * 
	 * @param operation
	 *            the statistical operation to use
	 * @return the value a statistical operation calculated using the buffered values
	 */
	public synchronized float getValue(final STATISTICAL_OPERATIONS operation) {

		final float[] values = new float[valueMap.size()];
		int i = 0;
		for (final float value : valueMap.values()) {
			values[i++] = value;
		}
		final float val = statOperation.getValue(operation, values);
		return val;

	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns the value a statistical operation calculated using the buffered values and the
	 * default statistical operation
	 * 
	 * @return the value a statistical operation calculated using the buffered values and the
	 *         default statistical operation
	 */
	public synchronized float getValue() {
		return getValue(statOperation.getDefaultOperation());
	}

	// --------------------------------------------------------------------------------
	/**
	 * Resets the buffer
	 */
	public synchronized void reset() {
		statOperation.reset();
		valueMap.clear();
	}

}
