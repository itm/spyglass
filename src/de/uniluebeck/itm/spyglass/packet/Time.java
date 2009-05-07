package de.uniluebeck.itm.spyglass.packet;

/**
 * Represents the Time of a Spyglass Packet
 * 
 * @author Nils Glombitza, ITM Uni Luebeck
 * 
 */
public class Time {

	/**
	 * Constrcutor
	 */
	public Time() {
		// nothing to do
	};

	/**
	 * Constructor
	 * 
	 * @param sec
	 *            Seconds
	 * @param ms
	 *            Milliseconds
	 * 
	 */
	public Time(final long sec, final int ms) {
		sec_ = sec;
		ms_ = ms;
	}

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
	@Override
	public String toString() {
		return "" + sec_ + "." + ms_;
	}

	/**
	 * Returns the time in milliseconds
	 * 
	 * @author Sebastian Ebers
	 * @return the time in milliseconds
	 */
	public Long getMillis() {
		return sec_ * 1000 + ms_;
	}
}