
package de.unisiegen.zess.spyglass.plugin.dataanalyzer;

import de.uniluebeck.itm.spyglass.packet.SpyglassPacket;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacketException;


public class SpyglassNodePackage extends SpyglassPacket
{
	public SpyglassNodePackage(final byte[] buf) throws SpyglassPacketException  {
		deserialize(buf);
	}


	void deserialize(final byte[] buf) throws SpyglassPacketException {
		if (deserializeUint16(buf[0], buf[1]) + 2 != buf.length) {
			throw new SpyglassPacketException("Wrong SpyglassPacket-Size");
		}
		
		setRawData(buf);

		if (getLength() != 24 || getSemanticType() != 102) {
			throw new SpyglassPacketException("Packet is not a SpyglassNodePackage!");
		}
		
		type = deserializeUint8(buf[19]);
		orientation = deserializeUint16(buf[20], buf[21]);
		hasMagnetsensor = deserializeUint8(buf[22]) != 0;
		hasGeophone = deserializeUint8(buf[23]) != 0;
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

