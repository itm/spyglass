// --------------------------------------------------------------------------------
/**
 * 
 */
package de.uniluebeck.itm.spyglass.packet;

import java.util.LinkedList;
import java.util.List;

// --------------------------------------------------------------------------------
/**
 * @author Administrator
 * 
 */
public class LinePainterPacket extends IntListPacket {

	/**
	 * Syntaxtype of this Packet
	 */
	public static final SyntaxTypes SYNTAXTYPE = SyntaxTypes.ISENSE_SPYGLASS_PACKET_UINT16;

	/**
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @see SpyglassPacket#deserialize(byte[])
	 */
	@Override
	void deserialize(final byte[] buf) throws SpyglassPacketException {
		super.deserialize(buf);
		if (getSyntaxType() != Uint16ListPacket.SYNTAXTYPE) {
			throw new SpyglassPacketException("Wrong Syntaxtype");
		}
		values = new Integer[(buf.length - SpyglassPacket.EXPECTED_PACKET_SIZE) / 2];
		for (int i = 0; (i * 2 + SpyglassPacket.EXPECTED_PACKET_SIZE) < buf.length; i++) {
			final int pos = i * 2 + SpyglassPacket.EXPECTED_PACKET_SIZE;
			values[i] = deserializeUint16(buf[pos], buf[pos + 1]);
		}
	}

	/**
	 * Interprets the payload as a list of node IDs and returns them as a list.
	 * 
	 * Note: The packet itself has no intrinsic knowledge if it is in fact a neighborhood packet. It
	 * is up to the caller to guess the type of the payload (e.g. by looking at the semantic type).
	 * 
	 * Please do not edit the returned array as changes to this list reflect to the packet content.
	 * 
	 * @returns a list of node IDs.
	 */
	public List<Integer> getPayloadList() {
		final List<Integer> list = new LinkedList<Integer>();
		for (int i = 1; i < values.length; i++) {
			list.add(values[i]);
		}
		return list;
	}

	// --------------------------------------------------------------------------------
	/**
	 * @return
	 */
	public Integer getReceiver() {
		return values[0];
	}

}
