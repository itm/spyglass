package de.uniluebeck.itm.spyglass.plugin.misc;

public interface StatisticalInformation {

	/**
	 * 
	 * @param values
	 */
	public int compute(int[] values);

	/**
	 * 
	 * @param values
	 */
	public float compute(float[] values);

}