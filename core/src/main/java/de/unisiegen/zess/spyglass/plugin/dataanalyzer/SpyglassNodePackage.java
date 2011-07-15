
package de.unisiegen.zess.spyglass.plugin.dataanalyzer;

import de.uniluebeck.itm.spyglass.packet.SpyglassPacket;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacketException;


public class SpyglassNodePackage extends SpyglassPacket
{
	// 10 SPIR - geo
	// 11 SPIR + geo
	// 12 MPIR - geo
	// 13 MPIR + geo
	// 14 LPIR - geo

	public static byte PACKET_TYPE(int PIR_TYPE, boolean geophone) {
		if (PIR_TYPE == 1) {
			return 14;
		}
		if (geophone) {
			PIR_TYPE += 1;
		}
		return (byte) (PIR_TYPE + 10);
	}
	public static final int PACKET_SIZE = 19;

	public SpyglassNodePackage(final byte[] buf) throws SpyglassPacketException  {
		deserialize(buf);
	}


	void deserialize(final byte[] buf) throws SpyglassPacketException {
		if (deserializeUint16(buf[0], buf[1]) + 2 != buf.length) {
			throw new SpyglassPacketException("Wrong SpyglassPacket-Size: Got " + deserializeUint16(buf[0], buf[1]) + " have " + buf.length);
		}
		
		setRawData(buf);

		if (getLength() != PACKET_SIZE || getSemanticType() < 10 || getSemanticType() > 14) {
			throw new SpyglassPacketException("Packet is not a SpyglassNodePackage! Type: " + getSemanticType() + " size: " + getLength());
	
		}
		
		orientation = deserializeUint16(buf[19], buf[20]);
		if (getSemanticType() == 14) {type = 2;}
		else {	if (getSemanticType() <= 11) {type = 0;}
			else {type = 1;}
		}

		hasMagnetsensor = getSemanticType() < 14;
		hasGeophone = getSemanticType() == 11 || getSemanticType() == 13;
	}
	
	private int type = -1;
	private int orientation = 0;
	private boolean hasGeophone = false;
	private boolean hasMagnetsensor = false;

	public int GetType() {return type;}
	public int GetOrientation() {return orientation;}
	public boolean HasGeophone() {return hasGeophone;}
	public boolean HasMagnetsensor() {return hasMagnetsensor;}
};

