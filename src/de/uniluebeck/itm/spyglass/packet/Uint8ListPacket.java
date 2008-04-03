package de.uniluebeck.itm.spyglass.packet;

/**
 * Represents a Packet with the syntaxtype Uint8ListPacket
 * 
 * @author Nils Glombitza, ITM Uni Luebeck
 *
 */
public class Uint8ListPacket extends IntListPacket
{
	/**
	 * Syntaxtype of this Packet
	 */
	public static final int SYNTAXTYPE=1;


	/** 
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @see SpyglassPacket#deserialize(byte[])
	 */
	@Override
	public void deserialize(byte[] buf) throws SpyglassPacketException
	{
		
		super.deserialize(buf);
		if(syntax_type!=Uint8ListPacket.SYNTAXTYPE) throw new SpyglassPacketException("Wrong Syntaxtype");
		values=new int[buf.length-SpyglassPacket.EXPECTED_PACKET_SIZE];
		for(int i=0;(i+SpyglassPacket.EXPECTED_PACKET_SIZE)<buf.length;i++)
		{
			values[i]=deserializeUint8(buf[i+SpyglassPacket.EXPECTED_PACKET_SIZE]);
		}
	}
	

}
