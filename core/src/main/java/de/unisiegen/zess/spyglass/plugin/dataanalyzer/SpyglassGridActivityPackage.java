
package de.unisiegen.zess.spyglass.plugin.dataanalyzer;

import de.uniluebeck.itm.spyglass.packet.SpyglassPacket;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacketException;


public class SpyglassGridActivityPackage extends SpyglassPacket
{
	public static final int PACKET_TYPE = 104;
	public static final int PACKET_SIZE = 17;

	public SpyglassGridActivityPackage(final byte[] buf) throws SpyglassPacketException  {
		deserialize(buf);
	}


	void deserialize(final byte[] buf) throws SpyglassPacketException {
		if (deserializeUint16(buf[0], buf[1]) + 2 != buf.length) {
			throw new SpyglassPacketException("Wrong SpyglassPacket-Size");
		}
		
		setRawData(buf);

		if (getLength() != PACKET_SIZE || getSemanticType() != PACKET_TYPE) {
			throw new SpyglassPacketException("Packet is not a SpyglassGridActivityPackage!");
		}
		
		row = deserializeUint16(buf[19], buf[20]);
		col = deserializeUint16(buf[21], buf[22]);
	}
	
	private int row = 0;
	private int col = 0;

	public int GetRow() {return row;}
	public int GetCol() {return col;}
};

