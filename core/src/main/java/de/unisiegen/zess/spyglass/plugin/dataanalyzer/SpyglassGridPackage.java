
package de.unisiegen.zess.spyglass.plugin.dataanalyzer;

import de.uniluebeck.itm.spyglass.packet.SpyglassPacket;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacketException;


public class SpyglassGridPackage extends SpyglassPacket
{
	public static final int PACKET_TYPE = 101;
	public static final int PACKET_SIZE = 27;

	public SpyglassGridPackage(final byte[] buf) throws SpyglassPacketException  {
		deserialize(buf);
	}


	void deserialize(final byte[] buf) throws SpyglassPacketException {
		if (deserializeUint16(buf[0], buf[1]) + 2 != buf.length) {
			throw new SpyglassPacketException("Wrong SpyglassPacket-Size");
		}
		
		setRawData(buf);

		if (getLength() != PACKET_SIZE || getSemanticType() != PACKET_TYPE) {
			throw new SpyglassPacketException("Packet is not a SpyglassGridPackage!");
		}
		
		height = deserializeUint16(buf[19], buf[20]);
		width = deserializeUint16(buf[21], buf[22]);
		rows = deserializeUint16(buf[23], buf[24]);
		cols = deserializeUint16(buf[25], buf[26]);
	}
	
	private int height = 0;
	private int width = 0;
	private int rows = 0;
	private int cols = 0;

	public int GetHeight() {return height;}
	public int GetWidth() {return width;}
	public int GetRows() {return rows;}
	public int GetCols() {return cols;}
};

