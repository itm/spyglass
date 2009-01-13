/*
 * -------------------------------------------------------------------------------- This file is
 * part of the WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet
 * (www.swarmnet.de) project SpyGlass is free software; you can redistribute it and/or modify it
 * under the terms of the BSD License. Refer to spyglass-licence.txt file in the root of the
 * SpyGlass source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.simpleglobalinformation;

import java.util.Arrays;

import de.uniluebeck.itm.spyglass.xmlconfig.StatisticalInformationEvaluator.STATISTICAL_OPERATIONS;

// --------------------------------------------------------------------------------
/**
 * Instances of this class perform statistical operations on an array of buffered values.
 * 
 * @author Sebastian Ebers
 * 
 */
public class StatisticalOperation {

	private float[] buffer;
	private int pointer;
	private int maxValidFieldValue;
	private int bufferSize;
	private STATISTICAL_OPERATIONS defaultOperation;

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 * 
	 * @param bufferSize
	 *            the size of the buffer
	 * @param defaultOperation
	 *            the statistical operation to be used by default
	 */
	public StatisticalOperation(final int bufferSize, final STATISTICAL_OPERATIONS defaultOperation) {
		buffer = new float[bufferSize];
		maxValidFieldValue = 0;
		pointer = 0;
		this.defaultOperation = defaultOperation;
		this.bufferSize = bufferSize;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Add a value to the buffer
	 * 
	 * @param value
	 *            the value to add
	 * @return the result of the statistical operation which is applianced on the buffer.
	 */
	public synchronized float addValue(final float value) {
		buffer[pointer++] = value;
		pointer = pointer % bufferSize;
		if (maxValidFieldValue < buffer.length) {
			++maxValidFieldValue;
		}
		return getValue(defaultOperation);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns the value a statistical operation calculated using the buffered buffer
	 * 
	 * @param operation
	 *            the statistical operation to use
	 * @return the value a statistical operation calculated using the buffered buffer
	 */
	public synchronized final float getValue(final STATISTICAL_OPERATIONS operation) {
		switch (operation) {
			case AVG:
				return getAverageValue();

			case MAX:
				return getMaxValue();

			case MIN:
				return getMinValue();

			case MEDIAN:
				return getMedianValue();

			case SUM:
				return getSum();
		}
		return 0.0f;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns the sum of the buffer's values
	 * 
	 * @return the sum of the buffer's values
	 */
	private float getSum() {
		int sum = 0;
		for (int i = 0; i < maxValidFieldValue; i++) {
			sum += buffer[i];
		}
		return sum;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns the median of the buffer's values
	 * 
	 * @return the median of the buffer's values
	 */
	private float getMedianValue() {
		final float[] tmp = new float[maxValidFieldValue];
		System.arraycopy(buffer, 0, tmp, 0, maxValidFieldValue);

		Arrays.sort(tmp);
		if (maxValidFieldValue > 0) {
			return tmp[(maxValidFieldValue - 1) / 2];
		}

		return buffer[0];
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns the minimum of the buffer's values
	 * 
	 * @return the minimum of the buffer's values
	 */
	private float getMinValue() {
		float min = Integer.MAX_VALUE;
		for (int i = 0; i < maxValidFieldValue; i++) {
			if (buffer[i] < min) {
				min = buffer[i];
			}
		}
		return min;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns the maximum of the buffer's values
	 * 
	 * @return the maximum of the buffer's values
	 */
	private float getMaxValue() {
		float max = Integer.MIN_VALUE;
		for (int i = 0; i < maxValidFieldValue; i++) {
			if (buffer[i] > max) {
				max = buffer[i];
			}
		}
		return max;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns the average of the buffer's values
	 * 
	 * @return the average of the buffer's values
	 */
	private float getAverageValue() {
		float sum = 0;
		for (int i = 0; i < maxValidFieldValue; i++) {
			sum += buffer[i];
		}
		if (maxValidFieldValue > 0) {
			return sum / maxValidFieldValue;
		}
		return sum;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Resets the buffer
	 */
	public synchronized void reset() {
		pointer = 0;
		maxValidFieldValue = 0;
		buffer[0] = 0;
	}

}
