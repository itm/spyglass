package de.uniluebeck.itm.spyglass.packet;

/**
 * Represents a Packet with the syntaxtype Uint32ListPacket
 * 
 * @author Nils Glombitza, ITM Uni Luebeck
 * 
 */
public class Uint32ListPacket extends LongListPacket
{
	/**
	 * Syntaxtype of this Packet
	 */
	public static final int SYNTAXTYPE = 4;

	/**
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @see SpyglassPacket#deserialize(byte[])
	 */
	@Override
	public void deserialize(byte[] buf) throws SpyglassPacketException
	{
		super.deserialize(buf);
		if (syntax_type != Uint32ListPacket.SYNTAXTYPE)
			throw new SpyglassPacketException("Wrong Syntaxtype");
		values = new long[(buf.length - SpyglassPacket.EXPECTED_PACKET_SIZE) / 4];
		for (int i = 0; (i * 4 + SpyglassPacket.EXPECTED_PACKET_SIZE) < buf.length; i++)
		{
			int pos = i * 4 + SpyglassPacket.EXPECTED_PACKET_SIZE;
			values[i] = deserializeUint32(buf[pos], buf[pos + 1], buf[pos + 2], buf[pos + 3]);
		}
	}

}
