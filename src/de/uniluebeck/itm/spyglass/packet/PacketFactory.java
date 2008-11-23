package de.uniluebeck.itm.spyglass.packet;

/**
 * Factory to create SpyglassPacket objects from raw packets.
 * 
 * @author Dariush Forouher
 * 
 */
public class PacketFactory {
	
	/**
	 * Create a new SpyglassPacket from the given raw packet.
	 * 
	 * @param data
	 *            a raw packet
	 * @throws SpyglassPacketException
	 *             if this is no valid Spyglass packet.
	 */
	public static SpyglassPacket createInstance(final byte[] data) throws SpyglassPacketException {
		try {
			final SyntaxTypes syntaxType = SyntaxTypes.toEnum(data[3]);
			
			SpyglassPacket packet = null;
			switch (syntaxType) {
				case ISENSE_SPYGLASS_PACKET_INT64:
					packet = new Int64ListPacket();
					break;
				case ISENSE_SPYGLASS_PACKET_FLOAT:
					packet = new FloatListPacket();
					break;
				case ISENSE_SPYGLASS_PACKET_INT16:
					packet = new Int16ListPacket();
					break;
				case ISENSE_SPYGLASS_PACKET_STD:
					packet = new SpyglassPacket();
					break;
				case ISENSE_SPYGLASS_PACKET_UINT16:
					packet = new Uint16ListPacket();
					break;
				case ISENSE_SPYGLASS_PACKET_UINT32:
					packet = new Uint32ListPacket();
					break;
				case ISENSE_SPYGLASS_PACKET_UINT8:
					packet = new Uint8ListPacket();
					break;
				case ISENSE_SPYGLASS_PACKET_VARIABLE:
					packet = new VariableListPacket();
					break;
			}
			
			packet.deserialize(data);
			
			return packet;
			
		} catch (final IllegalArgumentException e) {
			throw new SpyglassPacketException(e);
		}
		
	}
	
	public SpyglassPacket castToTrajectoryPacket2D(final Uint16ListPacket packet) {
		return null;
	}
}
