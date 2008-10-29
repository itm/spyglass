package de.uniluebeck.itm.spyglass.packet;

import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;

/**
 * Generic Spyglass packet.
 */
public class SpyglassPacket {
	
	/**
	 * 
	 */
	public static final int EXPECTED_PACKET_SIZE = 18;
	
	/** The packets raw data */
	private byte[] rawData;
	
	/**
	 * Deserializes a Spyglass Packet.
	 * 
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @throws SpyglassPacketException
	 */
	void deserialize() throws SpyglassPacketException {
		deserialize(rawData);
	}
	
	/**
	 * Deserializes a Spyglass Packet
	 * 
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @param rawData
	 *            Byte Array of the Packet
	 * @throws SpyglassPacketException
	 */
	void deserialize(final byte[] buf) throws SpyglassPacketException {
		rawData = buf;
		
		if (deserializeUint16(rawData[0], rawData[1]) + 2 != rawData.length) {
			throw new SpyglassPacketException("Wrong SpyglassPacket-Size");
		}
		
	}
	
	/**
	 * Deserializes a serialized uint8.
	 * 
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @param b
	 *            byte containing the uint8
	 * @return value as int
	 */
	protected int deserializeUint8(final byte b) {
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
	protected int deserializeUint16(final byte b0, final byte b1) {
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
	protected int deserializeInt16(final byte b0, final byte b1) {
		final int i0 = b0;
		// Masked because a byte is signed in Java.
		final int i1 = (b1 & 0xFF);
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
	protected long deserializeUint32(final byte b0, final byte b1, final byte b2, final byte b3) {
		final long i0 = (b0 & 0xFF);
		final long i1 = (b1 & 0xFF);
		final long i2 = (b2 & 0xFF);
		final long i3 = (b3 & 0xFF);
		// return (((((b3 & 0xFF) & 0xFFFFFFFF) | (((b2 & 0xFF) << 8) &
		// 0xFFFFFFFF))) | (((b1 & 0xFF) << 16) & 0xFFFFFFFF)) | (((b0 & 0xFF)
		// << 24) & 0xFFFFFFFF);
		final long ret = (i3 | (i2 << 8) | (i1 << 16) | (i0 << 24));
		return ret;
	}
	
	/**
	 * Deserializes a serialized Int64.
	 * 
	 * @author Nils Glombitza, ITM Uni Luebeck
	 */
	protected long deserializeInt64(final byte b0, final byte b1, final byte b2, final byte b3,
			final byte b4, final byte b5, final byte b6, final byte b7) {
		final long i0 = b0;
		final long i1 = (b1 & 0xFF);
		final long i2 = (b2 & 0xFF);
		final long i3 = (b3 & 0xFF);
		final long i4 = (b4 & 0xFF);
		final long i5 = (b5 & 0xFF);
		final long i6 = (b6 & 0xFF);
		final long i7 = (b7 & 0xFF);
		// Masked because a byte is signed in Java.
		final long ret = (i7 | (i6 << 8) | (i5 << 16) | (i4 << 24) | (i3 << 32) | (i2 << 40)
				| (i1 << 48) | (i0 << 56));
		return ret;
	}
	
	/**
	 * Deserializes a serialized Float.
	 * 
	 * @author Nils Glombitza, ITM Uni Luebeck
	 */
	protected float deserializeFloat(final byte b0, final byte b1, final byte b2, final byte b3) {
		final int i0 = b0;
		final int i1 = (b1 & 0xFF);
		final int i2 = (b2 & 0xFF);
		final int i3 = (b3 & 0xFF);
		final int ret = (i3 | (i2 << 8) | (i1 << 16) | (i0 << 24));
		return Float.intBitsToFloat(ret);
		
	}
	
	/**
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @see de.uniluebeck.itm.spyglass.packet.Packet#toString()
	 */
	@Override
	public String toString() {
		return "length:" + getLength() + ", syntax_type:" + getSyntaxType() + ", semantic_type:"
				+ getSemanticType() + ", sender_id:" + getSenderId() + ",time: "
				+ getTime().toString() + ", Position:" + getPosition();
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param rawData
	 *            the rawData to set
	 * @exception SpyglassPacketException
	 *                will be thrown if the data was already set previously
	 */
	protected void setRawData(final byte[] rawData) throws SpyglassPacketException {
		if (this.rawData == null) {
			this.rawData = rawData;
		} else {
			throw new SpyglassPacketException("Resetting the packets contents is not allowed");
		}
	}
	
	/**
	 * Property getter
	 * 
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @return the length
	 */
	public int getLength() {
		return deserializeUint16(rawData[0], rawData[1]);
	}
	
	/**
	 * Property getter
	 * 
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @return the syntax_type
	 */
	public SyntaxTypes getSyntaxType() {
		return SyntaxTypes.toEnum(deserializeUint8(rawData[3]));
	}
	
	/**
	 * Property getter
	 * 
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @return the semantic_type
	 */
	public int getSemanticType() {
		return deserializeUint8(rawData[4]);
	}
	
	/**
	 * Property getter
	 * 
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @return the senderId
	 */
	public int getSenderId() {
		return deserializeUint16(rawData[5], rawData[6]);
	}
	
	/**
	 * Property getter
	 * 
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @return the time
	 */
	public Time getTime() {
		final Time time = new Time();
		time.sec_ = deserializeUint32(rawData[7], rawData[8], rawData[9], rawData[10]);
		time.ms_ = deserializeUint16(rawData[11], rawData[12]);
		return time;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Property getter
	 * 
	 * @return the version
	 */
	public int getVersion() {
		return deserializeUint8(rawData[2]);
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the position
	 */
	public AbsolutePosition getPosition() {
		final int x = deserializeInt16(rawData[13], rawData[14]);
		final int y = deserializeInt16(rawData[15], rawData[16]);
		final int z = deserializeInt16(rawData[17], rawData[18]);
		return new AbsolutePosition(x, y, z);
	}
	
	// --------------------------------------------------------------------------
	// ------
	/**
	 * Return the payload of the packet
	 */
	public byte[] getPayload() {
		final int length = getLength();
		if (length > 17) {
			final byte[] tmpContent = new byte[length - 17];
			System.arraycopy(rawData, 19, tmpContent, 0, length - 17);
			return tmpContent;
		}
		return null;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the header
	 */
	public byte[] getHeader() {
		if (getLength() > 17) {
			final byte[] headerContent = new byte[18];
			System.arraycopy(rawData, 0, headerContent, 0, 18);
			return headerContent;
		}
		return null;
	}
	
	/**
	 * Returns the serialized the packet
	 * 
	 * @return the serialized packet
	 */
	public byte[] serialize() {
		// final byte[] buffer = new byte[header.length + payload.length + 1];
		// System.arraycopy(header, 0, buffer, 0, header.length);
		// System.arraycopy(payload, 0, buffer, header.length + 1, payload.length);
		// return buffer;
		return rawData;
	}
	
}
