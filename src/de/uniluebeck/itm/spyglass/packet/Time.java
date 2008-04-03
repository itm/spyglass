package de.uniluebeck.itm.spyglass.packet;

/**
 * Represents the Time of a Spyglass Packet
 * 
 * @author Nils Glombitza, ITM Uni Luebeck
 *
 */
public class Time
{
	/**
	 * Seconds
	 */
	long sec_;
	
	/**
	 * Milliseconds
	 */
	int ms_;

	/** 
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return "" + sec_ + "." + ms_;
	}
}