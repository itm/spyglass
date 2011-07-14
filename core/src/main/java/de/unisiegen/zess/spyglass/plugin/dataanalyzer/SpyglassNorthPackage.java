
package de.unisiegen.zess.spyglass.plugin.dataanalyzer;

import de.uniluebeck.itm.spyglass.packet.SpyglassPacket;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacketException;


public class SpyglassNorthPackage extends SpyglassPacket
{
	public static final int PACKET_TYPE = 103;
	public static final int PACKET_SIZE = 21;

	public SpyglassNorthPackage(final byte[] buf) throws SpyglassPacketException  {
		deserialize(buf);
	}


	void deserialize(final byte[] buf) throws SpyglassPacketException {
		if (deserializeUint16(buf[0], buf[1]) + 2 != buf.length) {
			throw new SpyglassPacketException("Wrong SpyglassPacket-Size");
		}
		
		setRawData(buf);

		if (getLength() != PACKET_SIZE || getSemanticType() != PACKET_TYPE) {
			throw new SpyglassPacketException("Packet is not a SpyglassNorthPackage!");
		}
		
		north = deserializeUint16(buf[19], buf[20]);
	}
	
	private int north = 0;

	public int GetNorth() {return north;}
};

