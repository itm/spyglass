package de.uniluebeck.itm.spyglass.packet;

/**
 * Represents a Packet with the syntaxtype Int16ListPacket
 * 
 * @author Nils Glombitza, ITM Uni Luebeck
 *
 */
public class Int16ListPacket extends IntListPacket
{
	
	public static final int SYNTAXTYPE=3;

	/** 
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @see SpyglassPacket#deserialize(byte[])
	 */
	@Override
	public void deserialize(byte[] buf) throws SpyglassPacketException
	{
		super.deserialize(buf);
		if(syntax_type!=Int16ListPacket.SYNTAXTYPE) throw new SpyglassPacketException("Wrong Syntaxtype");
		values=new int[(buf.length-SpyglassPacket.EXPECTED_PACKET_SIZE)/2];
		for(int i=0;(i*2+SpyglassPacket.EXPECTED_PACKET_SIZE)<buf.length;i++)
		{
			int pos=i*2+SpyglassPacket.EXPECTED_PACKET_SIZE;
			values[i]=deserializeInt16(buf[pos], buf[pos+1]);
		}
	}
	
}
