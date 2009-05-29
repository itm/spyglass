/*----------------------------------------------------------------------------------------
 * This file is part of the
 * WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de)
 * project SpyGlass is free software; you can redistribute it and/or modify it under the terms of
 * the BSD License. Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for
 * further details.
 * ---------------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.simpleglobalinformation;

import java.util.Arrays;

import de.uniluebeck.itm.spyglass.plugin.simpleglobalinformation.StatisticalInformationEvaluator.STATISTICAL_OPERATIONS;

// --------------------------------------------------------------------------------
/**
 * Instances of this class perform statistical operations on an array of buffered {@link Float}
 * values.<br>
 * <br>
 * The operations {@link STATISTICAL_OPERATIONS#AVG} and {@link STATISTICAL_OPERATIONS#MEDIAN} make
 * use of the last <tt>n</tt> added values provided by {@link StatisticalOperation#addValue(float)}
 * to compute the result.<br> {@link STATISTICAL_OPERATIONS#SUM}, {@link STATISTICAL_OPERATIONS#MAX} and
 * {@link STATISTICAL_OPERATIONS#MIN} work independently of the buffer's size.
 * 
 * @author Sebastian Ebers
 * 
 */
public class StatisticalOperation {

	private float[] buffer;
	private float sumBuffer;
	private Float minBuffer;
	private Float maxBuffer;
	private int pointer;
	private int maxValidFieldValue;
	private int bufferSize;
	private STATISTICAL_OPERATIONS defaultOperation;

	// --------------------------------------------------------------------------------
	/**
	 * Constructor setting the buffer's size and the statistical default operation.<br>
	 * The operations {@link STATISTICAL_OPERATIONS#AVG} and {@link STATISTICAL_OPERATIONS#MEDIAN}
	 * make use of the last <tt>n</tt> added values provided by
	 * {@link StatisticalOperation#addValue(float)} to compute the result.<br>
	 * {@link STATISTICAL_OPERATIONS#SUM}, {@link STATISTICAL_OPERATIONS#MAX} and
	 * {@link STATISTICAL_OPERATIONS#MIN} work independently of the buffer's size.
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
		sumBuffer = 0;
		minBuffer = null;
		maxBuffer = null;
		this.defaultOperation = defaultOperation;
		this.bufferSize = bufferSize;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns the operation used by default when calculating a statistical value
	 * 
	 * @return the operation used by default when calculating a statistical value
	 */
	public STATISTICAL_OPERATIONS getDefaultOperation() {
		return defaultOperation;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Sets the operation used by default when calculating a statistical value
	 * 
	 * @param defaultOperation
	 *            the operation used by default when calculating a statistical value
	 */
	public void setDefaultOperation(final STATISTICAL_OPERATIONS defaultOperation) {
		this.defaultOperation = defaultOperation;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Add a value to the buffer
	 * 
	 * @param value
	 *            the value to add
	 * @return the result of the statistical operation which uses the buffer.
	 */
	public synchronized float addValue(final float value) {

		sumBuffer += value;

		if ((minBuffer == null) || (value < minBuffer)) {
			minBuffer = value;
		}
		if ((maxBuffer == null) || (value > maxBuffer)) {
			maxBuffer = value;
		}
		buffer[pointer++] = value;

		pointer = pointer % bufferSize;
		if (maxValidFieldValue < buffer.length) {
			++maxValidFieldValue;
		}
		return getValue(defaultOperation);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns the value a statistical operation calculated using the buffered values and the
	 * default statistical operation
	 * 
	 * @return the value a statistical operation calculated using the buffered values and the
	 *         default statistical operation
	 */
	public synchronized final float getValue() {
		return getValue(defaultOperation);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns the value a statistical operation calculated using the buffered values
	 * 
	 * @param operation
	 *            the statistical operation to use
	 * @return the value a statistical operation calculated using the buffered values
	 */
	public synchronized final Float getValue(final STATISTICAL_OPERATIONS operation) {
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
	 * Returns the value a statistical operation calculated using a provided array of values and the
	 * default statistical operation
	 * 
	 * @param values
	 *            an array of values
	 * @return the value a statistical operation calculated using a provided array of values and the
	 *         default statistical operation
	 */
	public synchronized final float getValue(final float[] values) {
		return getValue(defaultOperation, values);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns the value a statistical operation calculated using a provided array of values
	 * 
	 * @param operation
	 *            the statistical operation to use
	 * @param values
	 *            an array of values
	 * @return the value a statistical operation calculated using a provided array of values
	 */
	public synchronized final float getValue(final STATISTICAL_OPERATIONS operation, final float[] values) {

		// backup the object's values
		final float[] tmpBuffer = new float[buffer.length];
		System.arraycopy(buffer, 0, tmpBuffer, 0, buffer.length);
		final int tmpValidFieldValue = maxValidFieldValue;

		// switch the object's values with the provided ones
		maxValidFieldValue = values.length;
		buffer = values;
		final float result = getValue(operation);

		// restore the object's original values
		maxValidFieldValue = tmpValidFieldValue;
		buffer = tmpBuffer;

		// and return the result
		return result;

	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns the sum of all provided values
	 * 
	 * @return the sum of all provided values
	 */
	private float getSum() {
		return sumBuffer;
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
	 * Returns the minimum value or <code>null</code> if no values where provided, yet
	 * 
	 * @return the minimum value or <code>null</code> if no values where provided, yet
	 */
	private Float getMinValue() {
		return minBuffer;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns the maximum value or <code>null</code> if no values where provided, yet
	 * 
	 * @return the maximum value or <code>null</code> if no values where provided, yet
	 */
	private Float getMaxValue() {
		return maxBuffer;
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
		sumBuffer = 0;
		minBuffer = null;
		maxBuffer = null;
		for (int i = 0; i < buffer.length; i++) {
			buffer[i] = 0;
		}
	}

	// --------------------------------------------------------------------------------
	@Override
	public synchronized StatisticalOperation clone() {
		final StatisticalOperation other = new StatisticalOperation(this.bufferSize, this.defaultOperation);
		other.maxValidFieldValue = this.maxValidFieldValue;
		other.pointer = this.pointer;
		other.defaultOperation = this.defaultOperation;
		other.bufferSize = this.bufferSize;
		System.arraycopy(this.buffer, 0, other.buffer, 0, maxValidFieldValue);
		other.sumBuffer = this.sumBuffer;
		other.minBuffer = this.minBuffer;
		other.maxBuffer = this.maxBuffer;
		return other;
	}

}
