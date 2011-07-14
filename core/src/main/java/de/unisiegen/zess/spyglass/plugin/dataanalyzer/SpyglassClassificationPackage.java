
package de.unisiegen.zess.spyglass.plugin.dataanalyzer;

import de.uniluebeck.itm.spyglass.packet.SpyglassPacket;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacketException;


public class SpyglassClassificationPackage extends SpyglassPacket
{
	public static final int PACKET_TYPE = 105;
	public static final int PACKET_SIZE = 20;

	public SpyglassClassificationPackage(final byte[] buf) throws SpyglassPacketException  {
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
		
		type = deserializeUint8(buf[19]);
	}
	
	private int type = -1;

	public int GetType() {return type;}
	public String GetTypeAsString() {
		switch (type) {
			case 0: return "Person";
			case 1: return "Person mit Waffe";
			case 2: return "Auto";
			default: return "Unbekannt";
		}
	}
};

