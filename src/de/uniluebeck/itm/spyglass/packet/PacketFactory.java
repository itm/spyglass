package de.uniluebeck.itm.spyglass.packet;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;
import de.uniluebeck.itm.spyglass.xmlconfig.MetricsXMLConfig;

/**
 * Factory to create SpyglassPacket objects from raw packets.
 * 
 * @author Dariush Forouher
 * 
 */
public class PacketFactory {
	
	private Spyglass spyglass;
	
	public PacketFactory(final Spyglass spyglass) {
		this.spyglass = spyglass;
	}
	
	/**
	 * Create a new SpyglassPacket from the given raw packet.
	 * 
	 * @param data
	 *            a raw packet
	 * @throws SpyglassPacketException
	 *             if this is no valid Spyglass packet.
	 */
	public SpyglassPacket createInstance(final byte[] data) throws SpyglassPacketException {
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

			fixPosition(packet);
			
			return packet;
			
		} catch (final IllegalArgumentException e) {
			throw new SpyglassPacketException(e);
		}
		
	}

	/**
	 * Apply the scale and offset values from general settings
	 */
	private void fixPosition(final SpyglassPacket packet) {
		final MetricsXMLConfig conf = spyglass.getConfigStore().getSpyglassConfig().getGeneralSettings().getMetrics();
		final AbsolutePosition pos = packet.getPosition();
		pos.x += conf.getAbs2metricOffsetX();
		pos.y += conf.getAbs2metricOffsetY();
		pos.x *= conf.getAbs2metricFactorX();
		pos.y *= conf.getAbs2metricFactorY();
		
		if (pos.x >= Math.pow(2, 15)) {
			pos.x = (int) Math.pow(2, 15)-1;
		}
		if (pos.y >= Math.pow(2, 15)) {
			pos.y = (int) Math.pow(2, 15)-1;
		}
		if (pos.x <= -Math.pow(2, 15)) {
			pos.x = (int) -Math.pow(2, 15)+1;
		}
		if (pos.y <= -Math.pow(2, 15)) {
			pos.y = (int) -Math.pow(2, 15)+1;
		}
		
		packet.setPosition(pos);
	}
}
