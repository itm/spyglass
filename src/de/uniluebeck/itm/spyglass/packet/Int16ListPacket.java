package de.uniluebeck.itm.spyglass.packet;

/**
 * Represents a Packet with the syntaxtype Int16ListPacket
 * 
 * @author Nils Glombitza, ITM Uni Luebeck
 * 
 */
public class Int16ListPacket extends IntListPacket {
	/**
	 * Syntaxtype of this Packet
	 */
	public static final SyntaxTypes SYNTAXTYPE = SyntaxTypes.ISENSE_SPYGLASS_PACKET_INT16;
	
	/**
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @see SpyglassPacket#deserialize(byte[])
	 */
	@Override
	public void deserialize(final byte[] buf) throws SpyglassPacketException {
		super.deserialize(buf);
		if (getSyntaxType() != Int16ListPacket.SYNTAXTYPE) {
			throw new SpyglassPacketException("Wrong Syntaxtype");
		}
		values = new int[(buf.length - SpyglassPacket.EXPECTED_PACKET_SIZE) / 2];
		for (int i = 0; (i * 2 + SpyglassPacket.EXPECTED_PACKET_SIZE) < buf.length; i++) {
			final int pos = i * 2 + SpyglassPacket.EXPECTED_PACKET_SIZE;
			values[i] = deserializeInt16(buf[pos], buf[pos + 1]);
		}
	}
	
}
