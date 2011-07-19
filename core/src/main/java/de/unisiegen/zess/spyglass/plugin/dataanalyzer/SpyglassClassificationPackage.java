
package de.unisiegen.zess.spyglass.plugin.dataanalyzer;

import de.uniluebeck.itm.spyglass.packet.SpyglassPacket;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacketException;


public class SpyglassClassificationPackage extends SpyglassPacket
{
	public static byte PACKET_TYPE(int type) {
		if (type >= 0 && type <= 3) {
			return (byte) (type + 20);
		}
		return (byte) (3 + 20);
	}

	public static final int PACKET_SIZE = 17;

	public SpyglassClassificationPackage(final byte[] buf) throws SpyglassPacketException  {
		deserialize(buf);
	}


	void deserialize(final byte[] buf) throws SpyglassPacketException {
		if (deserializeUint16(buf[0], buf[1]) + 2 != buf.length) {
			throw new SpyglassPacketException("Wrong SpyglassPacket-Size");
		}
		
		setRawData(buf);

		if (getLength() != PACKET_SIZE || getSemanticType() < 0 || getSemanticType() > 20+3) {
			throw new SpyglassPacketException("Packet is not a SpyglassClassificationPackage!");
		}

		type = getSemanticType() - 20;
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

