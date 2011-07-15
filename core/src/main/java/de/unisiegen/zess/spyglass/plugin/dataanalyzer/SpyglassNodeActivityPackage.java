
package de.unisiegen.zess.spyglass.plugin.dataanalyzer;

import de.uniluebeck.itm.spyglass.packet.SpyglassPacket;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacketException;


public class SpyglassNodeActivityPackage extends SpyglassPacket
{
	// number of types. a type = num_types is considered unknown
	static final int num_types = 5;
	public static byte PACKET_TYPE(int type) {
		if (type >= 0 && type < num_types) {
			return (byte) (type + 30);
		}
		return (byte) (num_types + 30);
	}

	public static final int PACKET_SIZE = 17;

	public SpyglassNodeActivityPackage(final byte[] buf) throws SpyglassPacketException  {
		deserialize(buf);
	}


	void deserialize(final byte[] buf) throws SpyglassPacketException {
		if (deserializeUint16(buf[0], buf[1]) + 2 != buf.length) {
			throw new SpyglassPacketException("Wrong SpyglassPacket-Size");
		}
		
		setRawData(buf);

		if (getLength() != PACKET_SIZE || getSemanticType() < 0 || getSemanticType() > 30+num_types) {
			throw new SpyglassPacketException("Packet is not a SpyglassNodeActivityPackage!");
		}

		type = getSemanticType() - 30;
	}
	
	private int type = -1;

	public int GetType() {return type;}
};

