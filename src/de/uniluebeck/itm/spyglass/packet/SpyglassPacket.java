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
	
	/**
	 * Number of bytes in this packet (excluding this field).
	 */
	protected int length;
	
	/**
	 * Version if this packet. should always be 02.
	 */
	protected int version;
	
	/**
	 * Syntax type of this spyglass packet.
	 */
	protected SyntaxTypes syntaxType;
	
	/**
	 * Semantic type of this spyglass packet.
	 */
	protected int semanticType;
	
	/**
	 * Sender ID of this spyglass packet.
	 */
	protected int senderId;
	
	/**
	 * Timestamp of this spyglass packet.
	 */
	protected Time time;
	
	/**
	 * positon of this spyglass packet.
	 */
	private AbsolutePosition position;
	
	/**
	 * The payload
	 */
	private byte[] payload;
	
	/**
	 * Deserializes a Spyglass Packet.
	 * 
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @throws SpyglassPacketException
	 */
	void deserialize() throws SpyglassPacketException {
		deserialize(getPayload());
	}
	
	/**
	 * Deserializes a Spyglass Packet
	 * 
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @param buf
	 *            Byte Array of the Packet
	 * @throws SpyglassPacketException
	 */
	void deserialize(final byte[] buf) throws SpyglassPacketException {
		length = deserializeUint16(buf[0], buf[1]);
		if (length + 2 != buf.length) {
			throw new SpyglassPacketException("Wrong SpyglassPacket-Size");
		}
		version = deserializeUint8(buf[2]);
		syntaxType = SyntaxTypes.toEnum(deserializeUint8(buf[3]));
		semanticType = deserializeUint8(buf[4]);
		senderId = deserializeUint16(buf[5], buf[6]);
		time = new Time();
		time.sec_ = deserializeUint32(buf[7], buf[8], buf[9], buf[10]);
		time.ms_ = deserializeUint16(buf[11], buf[12]);
		final int x = deserializeInt16(buf[13], buf[14]);
		final int y = deserializeInt16(buf[15], buf[16]);
		final int z = deserializeInt16(buf[17], buf[18]);
		this.position = new AbsolutePosition(x, y, z);
		
		// copy the packets payload (starts at buf[19]) as content
		if (length > 17) {
			final byte[] tmpContent = new byte[length - 17];
			System.arraycopy(buf, 19, tmpContent, 0, length - 17);
			setPayload(tmpContent);
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
		return "length:" + length + ", syntax_type:" + syntaxType + ", semantic_type:"
				+ semanticType + ", sender_id:" + senderId + ",time: " + time.toString()
				+ ", Position:" + position;
	}
	
	/**
	 * Property getter
	 * 
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @return the length
	 */
	public int getLength() {
		return length;
	}
	
	/**
	 * Property setter
	 * 
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @param length
	 *            the length to set
	 */
	public void setLength(final int length) {
		this.length = length;
	}
	
	/**
	 * Property getter
	 * 
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @return the syntax_type
	 */
	public SyntaxTypes getSyntaxType() {
		return syntaxType;
	}
	
	/**
	 * Property setter
	 * 
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @param syntaxType
	 *            the syntax_type to set
	 */
	public void setSyntaxType(final SyntaxTypes syntaxType) {
		this.syntaxType = syntaxType;
	}
	
	/**
	 * Property getter
	 * 
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @return the semantic_type
	 */
	public int getSemanticType() {
		return semanticType;
	}
	
	/**
	 * Property setter
	 * 
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @param semanticType
	 *            the semanticType to set
	 */
	public void setSemanticType(final int semanticType) {
		this.semanticType = semanticType;
	}
	
	/**
	 * Property getter
	 * 
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @return the senderId
	 */
	public int getSenderId() {
		return senderId;
	}
	
	/**
	 * Property setter
	 * 
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @param senderId
	 *            the sender_id to set
	 */
	public void setSenderId(final int senderId) {
		this.senderId = senderId;
	}
	
	/**
	 * Property getter
	 * 
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @return the time
	 */
	public Time getTime() {
		return time;
	}
	
	/**
	 * Property setter
	 * 
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @param time
	 *            the time to set
	 */
	public void setTime(final Time time) {
		this.time = time;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the position
	 */
	public AbsolutePosition getPosition() {
		return position;
	}
	
	// --------------------------------------------------------------------------
	// ------
	/**
	 * Return the payload of the packet
	 */
	public byte[] getPayload() {
		return payload;
	}
	
	// --------------------------------------------------------------------------
	// ------
	/**
	 * Set the payload of the packet
	 */
	public void setPayload(final byte[] content) {
		this.payload = content;
	}
	
}
