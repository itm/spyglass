package de.uniluebeck.itm.spyglass.packet;

/**
 * Generic Spyglass packet.
 */
public class SpyglassPacket extends Packet
{

	// Spyglass Packet Types
	public static final int ISENSE_SPYGLASS_PACKET_STD = 0;
	public static final int ISENSE_SPYGLASS_PACKET_UINT8 = 1;
	public static final int ISENSE_SPYGLASS_PACKET_UINT16 = 2;
	public static final int ISENSE_SPYGLASS_PACKET_INT16 = 3;
	public static final int ISENSE_SPYGLASS_PACKET_UINT32 = 4;
	public static final int ISENSE_SPYGLASS_PACKET_INT64 = 5;
	public static final int ISENSE_SPYGLASS_PACKET_FLOAT = 6;
	public static final int ISENSE_SPYGLASS_PACKET_VARIABLE = 7;

	public static final int EXPECTED_PACKET_SIZE = 18;

	/**
	 * Number of bytes in this packet (excluding this field).
	 */
	protected int length;

	/**
	 * Syntax type of this spyglass packet.
	 */
	protected int syntax_type;

	/**
	 * Semantic type of this spyglass packet.
	 */
	protected int semantic_type;

	/**
	 * Sender ID of this spyglass packet.
	 */
	protected int sender_id;

	/**
	 * Timestamp of this spyglass packet.
	 */
	protected Time time;

	/**
	 * X-Coordinate of this spyglass packet.
	 */
	protected int x;

	/**
	 * Y-Coordinate of this spyglass packet.
	 */
	protected int y;

	/**
	 * Z-Coordinate of this spyglass packet.
	 */
	protected int z;

	/**
	 * Deserializes a Spyglass Packet.
	 * 
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @throws SpyglassPacketException
	 */
	public void deserialize() throws SpyglassPacketException
	{
		deserialize(getContent());
	}

	/**
	 * Deserializes a Spyglass Packet
	 * 
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @param buf
	 *            Byte Array of the Packet
	 * @throws SpyglassPacketException
	 */
	public void deserialize(byte[] buf) throws SpyglassPacketException
	{
		length = deserializeUint16(buf[0], buf[1]);
		if (length + 1 != buf.length)
			throw new SpyglassPacketException("Wrong SpyglassPacket-Size");
		syntax_type = deserializeUint8(buf[2]);
		semantic_type = deserializeUint8(buf[3]);
		sender_id = deserializeUint16(buf[4], buf[5]);
		time = new Time();
		time.sec_ = deserializeUint32(buf[6], buf[7], buf[8], buf[9]);
		time.ms_ = deserializeUint16(buf[10], buf[11]);
		x = deserializeInt16(buf[12], buf[13]);
		y = deserializeInt16(buf[14], buf[15]);
		z = deserializeInt16(buf[16], buf[17]);

	}

	/**
	 * Deserializes a serialized uint8.
	 * 
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @param b
	 *            byte containing the uint8
	 * @return value as int
	 */
	protected int deserializeUint8(byte b)
	{
		// Masked because a byte is signed in Java.
		return b & 0xff;
	}

	/**
	 * Deserializes a serialized uint16.
	 * 
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @param b0
	 *            containing the first byte of the uint16.
	 * @param b1
	 *            containing the second byte of the uint16.
	 * @return value as int
	 */
	protected int deserializeUint16(byte b0, byte b1)
	{
		return (((b1 & 0xFF) & 0xFFFF) | (((b0 & 0xFF) << 8) & 0xFFFF));
	}

	/**
	 * Deserializes a serialized int16.
	 * 
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @param b0
	 *            containing the first byte of the int16.
	 * @param b1
	 *            containing the second byte of the int16.
	 * @return value as int
	 */
	protected int deserializeInt16(byte b0, byte b1)
	{
		int i0 = (int) b0;
		// Masked because a byte is signed in Java.
		int i1 = (int) (b1 & 0xFF);
		return (i1 | (i0 << 8));
	}

	/**
	 * Deserializes a serialized uint32.
	 * 
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @param b0
	 *            containing the first byte of the uint32.
	 * @param b1
	 *            containing the 2nd. byte of the uint32.
	 * @param b2
	 *            containing the 3rd. byte of the uint32.
	 * @param b3
	 *            containing the 4th. byte of the uint32.
	 * @return value as long
	 */
	protected long deserializeUint32(byte b0, byte b1, byte b2, byte b3)
	{
		long i0 = (long) (b0 & 0xFF);
		long i1 = (long) (b1 & 0xFF);
		long i2 = (long) (b2 & 0xFF);
		long i3 = (long) (b3 & 0xFF);
		// return (((((b3 & 0xFF) & 0xFFFFFFFF) | (((b2 & 0xFF) << 8) &
		// 0xFFFFFFFF))) | (((b1 & 0xFF) << 16) & 0xFFFFFFFF)) | (((b0 & 0xFF)
		// << 24) & 0xFFFFFFFF);
		long ret = (long) (i3 | (i2 << 8) | (i1 << 16) | (i0 << 24));
		return ret;
	}

	/**
	 * Deserializes a serialized Int64.
	 * 
	 * @author Nils Glombitza, ITM Uni Luebeck
	 */
	protected long deserializeInt64(byte b0, byte b1, byte b2, byte b3, byte b4, byte b5, byte b6, byte b7)
	{
		long i0 = (long) b0;
		long i1 = (long) (b1 & 0xFF);
		long i2 = (long) (b2 & 0xFF);
		long i3 = (long) (b3 & 0xFF);
		long i4 = (long) (b4 & 0xFF);
		long i5 = (long) (b5 & 0xFF);
		long i6 = (long) (b6 & 0xFF);
		long i7 = (long) (b7 & 0xFF);
		// Masked because a byte is signed in Java.
		long ret = (long) (i7 | (i6 << 8) | (i5 << 16) | (i4 << 24) | (i3 << 32) | (i2 << 40) | (i1 << 48) | (i0 << 56));
		return ret;
	}

	/**
	 * Deserializes a serialized Float.
	 * 
	 * @author Nils Glombitza, ITM Uni Luebeck
	 */
	protected float deserializeFloat(byte b0, byte b1, byte b2, byte b3)
	{
		int i0 = (int) b0;
		int i1 = (int) (b1 & 0xFF);
		int i2 = (int) (b2 & 0xFF);
		int i3 = (int) (b3 & 0xFF);
		int ret = (i3 | (i2 << 8) | (i1 << 16) | (i0 << 24));
		return Float.intBitsToFloat(ret);

	}

	/**
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @see de.uniluebeck.itm.spyglass.packet.Packet#toString()
	 */
	public String toString()
	{
		return "length:" + length + ", syntax_type:" + syntax_type + ", semantic_type:" + semantic_type + ", sender_id:" + sender_id + ",time: " + time.toString() + ", x:" + x + ", y:" + y + ", z:" + z;
	}

	/**
	 * Property getter
	 * 
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @return the length
	 */
	public int getLength()
	{
		return length;
	}

	/**
	 * Property setter
	 * 
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @param length
	 *            the length to set
	 */
	public void setLength(int length)
	{
		this.length = length;
	}

	/**
	 * Property getter
	 * 
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @return the syntax_type
	 */
	public int getSyntax_type()
	{
		return syntax_type;
	}

	/**
	 * Property setter
	 * 
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @param syntax_type
	 *            the syntax_type to set
	 */
	public void setSyntax_type(int syntax_type)
	{
		this.syntax_type = syntax_type;
	}

	/**
	 * Property getter
	 * 
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @return the semantic_type
	 */
	public int getSemantic_type()
	{
		return semantic_type;
	}

	/**
	 * Property setter
	 * 
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @param semantic_type
	 *            the semantic_type to set
	 */
	public void setSemantic_type(int semantic_type)
	{
		this.semantic_type = semantic_type;
	}

	/**
	 * Property getter
	 * 
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @return the sender_id
	 */
	public int getSender_id()
	{
		return sender_id;
	}

	/**
	 * Property setter
	 * 
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @param sender_id
	 *            the sender_id to set
	 */
	public void setSender_id(int sender_id)
	{
		this.sender_id = sender_id;
	}

	/**
	 * Property getter
	 * 
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @return the time
	 */
	public Time getTime()
	{
		return time;
	}

	/**
	 * Property setter
	 * 
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @param time
	 *            the time to set
	 */
	public void setTime(Time time)
	{
		this.time = time;
	}

	/**
	 * Property getter
	 * 
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @return the x
	 */
	public int getX()
	{
		return x;
	}

	/**
	 * Property setter
	 * 
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @param x
	 *            the x to set
	 */
	public void setX(int x)
	{
		this.x = x;
	}

	/**
	 * Property getter
	 * 
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @return the y
	 */
	public int getY()
	{
		return y;
	}

	/**
	 * Property setter
	 * 
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @param y
	 *            the y to set
	 */
	public void setY(int y)
	{
		this.y = y;
	}

	/**
	 * Property getter
	 * 
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @return the z
	 */
	public int getZ()
	{
		return z;
	}

	/**
	 * Property setter
	 * 
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @param z
	 *            the z to set
	 */
	public void setZ(int z)
	{
		this.z = z;
	}
}
