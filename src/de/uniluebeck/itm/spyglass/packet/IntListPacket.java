package de.uniluebeck.itm.spyglass.packet;

/**
 * Represents a generic IntergerListPacket
 * 
 * @author Nils Glombitza, ITM Uni Luebeck
 * @author Dariush Forouher
 * 
 */
public class IntListPacket extends SpyglassPacket {
	
	/** Listelements */
	protected Integer[] values = new Integer[0];
	
	/**
	 * Returns the Listelements
	 * 
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @return the values
	 */
	public Integer[] getValues() {
		return values;
	}
	
	/**
	 * Sets the Listelements
	 * 
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @param values
	 *            the values to set
	 */
	void setValues(final Integer[] values) {
		this.values = values;
	}
	
	/**
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @see SpyglassPacket#toString()
	 */
	@Override
	public String toString() {
		String ret = super.toString() + ", values: ";
		for (int i = 0; i < values.length; i++) {
			if (i > 0) {
				ret = ret + ", ";
			}
			ret = ret + values[i];
		}
		return ret;
	}
}
