package de.uniluebeck.itm.spyglass.packet;
import java.math.BigInteger;

/**
 * Represents a Packet with the syntaxtype Int64ListPacket
 * 
 * @author Nils Glombitza, ITM Uni Luebeck
 *
 */
public class Int64ListPacket extends LongListPacket
{
	
	public static final int SYNTAXTYPE=5;
	
	
	

	/** 
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @see SpyglassPacket#deserialize(byte[])
	 */
	@Override
	public void deserialize(byte[] buf) throws SpyglassPacketException
	{
		super.deserialize(buf);
		if(syntax_type!=Int64ListPacket.SYNTAXTYPE) throw new SpyglassPacketException("Wrong Syntaxtype");
		values=new long[(buf.length-SpyglassPacket.EXPECTED_PACKET_SIZE)/8];
		for(int i=0;(i*8+SpyglassPacket.EXPECTED_PACKET_SIZE)<buf.length;i++)
		{
			int pos=i*8+SpyglassPacket.EXPECTED_PACKET_SIZE;
			values[i]=deserializeInt64(buf[pos], buf[pos+1],buf[pos+2],buf[pos+3],buf[pos+4],buf[pos+5],buf[pos+6],buf[pos+7]);
		}
	}
	
	

	


}
