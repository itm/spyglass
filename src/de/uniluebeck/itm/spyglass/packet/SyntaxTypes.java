// --------------------------------------------------------------------------------
/**
 * 
 */
package de.uniluebeck.itm.spyglass.packet;

/**
 * 
 * Enumeration representing all legal syntax types of an spyglass packet
 * 
 * The syntax type describes the layout of the payload.
 * 
 * @author Dariush Forouher
 * 
 */
public enum SyntaxTypes {
	
	/**
	 * 
	 */
	ISENSE_SPYGLASS_PACKET_STD(0),

	/**
	 * 
	 */
	ISENSE_SPYGLASS_PACKET_UINT8(1),

	/**
	 * 
	 */
	ISENSE_SPYGLASS_PACKET_UINT16(2),

	/**
	 * 
	 */
	ISENSE_SPYGLASS_PACKET_INT16(3),

	/**
	 * 
	 */
	ISENSE_SPYGLASS_PACKET_UINT32(4),

	/**
	 *
	 **/
	ISENSE_SPYGLASS_PACKET_INT64(5),

	/**
	 * 
	 */
	ISENSE_SPYGLASS_PACKET_FLOAT(6),

	/**
	 * 
	 */
	ISENSE_SPYGLASS_PACKET_VARIABLE(7);
	
	private int value;
	
	private SyntaxTypes(final int value) {
		this.value = value;
	}
	
	public int toID() {
		return value;
	}
	
	/**
	 * Return the corresponding syntax type
	 */
	public static SyntaxTypes toEnum(final int value) {
		switch (value) {
			case 0:
				return SyntaxTypes.ISENSE_SPYGLASS_PACKET_STD;
			case 1:
				return SyntaxTypes.ISENSE_SPYGLASS_PACKET_UINT8;
			case 2:
				return SyntaxTypes.ISENSE_SPYGLASS_PACKET_UINT16;
			case 3:
				return SyntaxTypes.ISENSE_SPYGLASS_PACKET_INT16;
			case 4:
				return SyntaxTypes.ISENSE_SPYGLASS_PACKET_UINT32;
			case 5:
				return SyntaxTypes.ISENSE_SPYGLASS_PACKET_INT64;
			case 6:
				return SyntaxTypes.ISENSE_SPYGLASS_PACKET_FLOAT;
			case 7:
				return SyntaxTypes.ISENSE_SPYGLASS_PACKET_VARIABLE;
			default:
				throw new IllegalArgumentException("Unknown syntax type.");
		}
		
	}
	
}