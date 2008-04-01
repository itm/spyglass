package de.uniluebeck.itm.spyglass.packet;
import java.math.BigInteger;

/**
 * Generic Spyglass Packet.
 * It has the following structure:
 * 
 * <table class="MsoNormalTable"
 style="margin-left: -1pt; border-collapse: collapse;" border="0"
 cellpadding="0" cellspacing="0">
  <tbody>
    <tr style="">
      <td
 style="border-style: solid none solid solid; border-color: black -moz-use-text-color black black; border-width: 1pt medium 1pt 1pt; padding: 0cm 5.4pt; width: 50.4pt;"
 valign="top" width="67">
      <p class="MsoNormal" style="">Byte</p>
      </td>
      <td
 style="border-style: solid none solid solid; border-color: black -moz-use-text-color black black; border-width: 1pt medium 1pt 1pt; padding: 0cm 5.4pt; width: 63pt;"
 valign="top" width="84">
      <p class="MsoNormal" style="">Typ</p>
      </td>
      <td
 style="border-style: solid none solid solid; border-color: black -moz-use-text-color black black; border-width: 1pt medium 1pt 1pt; padding: 0cm 5.4pt; width: 232.05pt;"
 valign="top" width="309">
      <p class="MsoNormal" style="">Bedeutung</p>
      </td>
      <td
 style="border: 1pt solid black; padding: 0cm 5.4pt; width: 117.15pt;"
 valign="top" width="156">
      <p class="MsoNormal" style="">Wert</p>
      </td>
    </tr>
  </tbody>
</table>
<p class="MsoNormal">&nbsp;</p>
<table class="MsoNormalTable"
 style="margin-left: -1pt; border-collapse: collapse;" border="0"
 cellpadding="0" cellspacing="0">
  <tbody>
    <tr style="page-break-inside: avoid; height: 25.15pt;">
      <td
 style="border-style: solid none solid solid; border-color: black -moz-use-text-color black black; border-width: 1pt medium 1pt 1pt; padding: 0cm 5.4pt; width: 50.4pt; height: 25.15pt;"
 valign="top" width="67">
      <p class="MsoNormal" style="">00</p>
      </td>
      <td rowspan="2"
 style="border-style: solid none solid solid; border-color: black -moz-use-text-color black black; border-width: 1pt medium 1pt 1pt; padding: 0cm 5.4pt; width: 63pt; height: 25.15pt;"
 valign="top" width="84">
      <p class="MsoNormal" style="">uint16</p>
      </td>
      <td rowspan="2"
 style="border-style: solid none solid solid; border-color: black -moz-use-text-color black black; border-width: 1pt medium 1pt 1pt; padding: 0cm 5.4pt; width: 232.4pt; height: 25.15pt;"
 valign="top" width="310">
      <p class="MsoNormal" style="">Anzahl
nachfolgender Bytes</p>
      </td>
      <td rowspan="2"
 style="border: 1pt solid black; padding: 0cm 5.4pt; width: 117.4pt; height: 25.15pt;"
 valign="top" width="157">
      <p class="MsoNormal" style="">&nbsp;</p>
      </td>
    </tr>
    <tr style="page-break-inside: avoid; height: 25.15pt;">
      <td
 style="border-style: none none solid solid; border-color: -moz-use-text-color -moz-use-text-color black black; border-width: medium medium 1pt 1pt; padding: 0cm 5.4pt; width: 50.4pt; height: 25.15pt;"
 valign="top" width="67">
      <p class="MsoNormal" style="">01</p>
      </td>
    </tr>
    <tr style="">
      <td
 style="border-style: none none solid solid; border-color: -moz-use-text-color -moz-use-text-color black black; border-width: medium medium 1pt 1pt; padding: 0cm 5.4pt; width: 50.4pt;"
 valign="top" width="67">
      <p class="MsoNormal" style="">02</p>
      </td>
      <td
 style="border-style: none none solid solid; border-color: -moz-use-text-color -moz-use-text-color black black; border-width: medium medium 1pt 1pt; padding: 0cm 5.4pt; width: 63pt;"
 valign="top" width="84">
      <p class="MsoNormal" style="">uint8</p>
      </td>
      <td
 style="border-style: none none solid solid; border-color: -moz-use-text-color -moz-use-text-color black black; border-width: medium medium 1pt 1pt; padding: 0cm 5.4pt; width: 232.4pt;"
 valign="top" width="310">
      <p class="MsoNormal" style="">Version</p>
      </td>
      <td
 style="border-style: none solid solid; border-color: -moz-use-text-color black black; border-width: medium 1pt 1pt; padding: 0cm 5.4pt; width: 117.4pt;"
 valign="top" width="157">
      <p class="MsoNormal" style="">02</p>
      </td>
    </tr>
    <tr style="">
      <td
 style="border-style: none none solid solid; border-color: -moz-use-text-color -moz-use-text-color black black; border-width: medium medium 1pt 1pt; padding: 0cm 5.4pt; width: 50.4pt;"
 valign="top" width="67">
      <p class="MsoNormal" style="">03</p>
      </td>
      <td
 style="border-style: none none solid solid; border-color: -moz-use-text-color -moz-use-text-color black black; border-width: medium medium 1pt 1pt; padding: 0cm 5.4pt; width: 63pt;"
 valign="top" width="84">
      <p class="MsoNormal" style="">uint8</p>
      </td>
      <td
 style="border-style: none none solid solid; border-color: -moz-use-text-color -moz-use-text-color black black; border-width: medium medium 1pt 1pt; padding: 0cm 5.4pt; width: 232.4pt;"
 valign="top" width="310">
      <p class="MsoNormal" style="">Syntaxtyp</p>
      </td>
      <td
 style="border-style: none solid solid; border-color: -moz-use-text-color black black; border-width: medium 1pt 1pt; padding: 0cm 5.4pt; width: 117.4pt;"
 valign="top" width="157">
      <p class="MsoNormal" style="">0 = std</p>
      <p class="MsoNormal">1 = uint8-List</p>
      <p class="MsoNormal">2 = uInt16List</p>
      <p class="MsoNormal">3 = Int16List</p>
      <p class="MsoNormal">4 = uInt32List</p>
      <p class="MsoNormal">5 = uIInt64List</p>
      <p class="MsoNormal">6 = FloatList </p>
      <p class="MsoNormal">7 = variable</p>
      </td>
    </tr>
    <tr style="">
      <td
 style="border-style: none none solid solid; border-color: -moz-use-text-color -moz-use-text-color black black; border-width: medium medium 1pt 1pt; padding: 0cm 5.4pt; width: 50.4pt;"
 valign="top" width="67">
      <p class="MsoNormal" style="">04</p>
      </td>
      <td
 style="border-style: none none solid solid; border-color: -moz-use-text-color -moz-use-text-color black black; border-width: medium medium 1pt 1pt; padding: 0cm 5.4pt; width: 63pt;"
 valign="top" width="84">
      <p class="MsoNormal" style="">uint8</p>
      </td>
      <td
 style="border-style: none none solid solid; border-color: -moz-use-text-color -moz-use-text-color black black; border-width: medium medium 1pt 1pt; padding: 0cm 5.4pt; width: 232.4pt;"
 valign="top" width="310">
      <p class="MsoNormal" style="">Semantiktyp</p>
      </td>
      <td
 style="border-style: none solid solid; border-color: -moz-use-text-color black black; border-width: medium 1pt 1pt; padding: 0cm 5.4pt; width: 117.4pt;"
 valign="top" width="157">
      <p class="MsoNormal" style="">&nbsp;</p>
      </td>
    </tr>
    <tr style="page-break-inside: avoid; height: 25.15pt;">
      <td
 style="border-style: none none solid solid; border-color: -moz-use-text-color -moz-use-text-color black black; border-width: medium medium 1pt 1pt; padding: 0cm 5.4pt; width: 50.4pt; height: 25.15pt;"
 valign="top" width="67">
      <p class="MsoNormal" style="">05</p>
      </td>
      <td rowspan="2"
 style="border-style: none none solid solid; border-color: -moz-use-text-color -moz-use-text-color black black; border-width: medium medium 1pt 1pt; padding: 0cm 5.4pt; width: 63pt; height: 25.15pt;"
 valign="top" width="84">
      <p class="MsoNormal" style="">uint16</p>
      </td>
      <td rowspan="2"
 style="border-style: none none solid solid; border-color: -moz-use-text-color -moz-use-text-color black black; border-width: medium medium 1pt 1pt; padding: 0cm 5.4pt; width: 232.4pt; height: 25.15pt;"
 valign="top" width="310">
      <p class="MsoNormal" style="">Sender ID</p>
      </td>
      <td rowspan="2"
 style="border-style: none solid solid; border-color: -moz-use-text-color black black; border-width: medium 1pt 1pt; padding: 0cm 5.4pt; width: 117.4pt; height: 25.15pt;"
 valign="top" width="157">
      <p class="MsoNormal" style="">&nbsp;</p>
      </td>
    </tr>
    <tr style="page-break-inside: avoid; height: 25.15pt;">
      <td
 style="border-style: none none solid solid; border-color: -moz-use-text-color -moz-use-text-color black black; border-width: medium medium 1pt 1pt; padding: 0cm 5.4pt; width: 50.4pt; height: 25.15pt;"
 valign="top" width="67">
      <p class="MsoNormal" style="">06</p>
      </td>
    </tr>
    <tr style="page-break-inside: avoid; height: 25.15pt;">
      <td
 style="border-style: none none solid solid; border-color: -moz-use-text-color -moz-use-text-color black black; border-width: medium medium 1pt 1pt; padding: 0cm 5.4pt; width: 50.4pt; height: 25.15pt;"
 valign="top" width="67">
      <p class="MsoNormal" style="">07</p>
      </td>
      <td rowspan="4"
 style="border-style: none none solid solid; border-color: -moz-use-text-color -moz-use-text-color black black; border-width: medium medium 1pt 1pt; padding: 0cm 5.4pt; width: 63pt; height: 25.15pt;"
 valign="top" width="84">
      <p class="MsoNormal" style="">uint32</p>
      </td>
      <td rowspan="4"
 style="border-style: none none solid solid; border-color: -moz-use-text-color -moz-use-text-color black black; border-width: medium medium 1pt 1pt; padding: 0cm 5.4pt; width: 232.4pt; height: 25.15pt;"
 valign="top" width="310">
      <p class="MsoNormal" style="">Sekundenanteil
der Zeit</p>
      </td>
      <td rowspan="4"
 style="border-style: none solid solid; border-color: -moz-use-text-color black black; border-width: medium 1pt 1pt; padding: 0cm 5.4pt; width: 117.4pt; height: 25.15pt;"
 valign="top" width="157">
      <p class="MsoNormal" style="">&nbsp;</p>
      </td>
    </tr>
    <tr style="page-break-inside: avoid; height: 25.15pt;">
      <td
 style="border-style: none none solid solid; border-color: -moz-use-text-color -moz-use-text-color black black; border-width: medium medium 1pt 1pt; padding: 0cm 5.4pt; width: 50.4pt; height: 25.15pt;"
 valign="top" width="67">
      <p class="MsoNormal" style="">08</p>
      </td>
    </tr>
    <tr style="page-break-inside: avoid; height: 25.15pt;">
      <td
 style="border-style: none none solid solid; border-color: -moz-use-text-color -moz-use-text-color black black; border-width: medium medium 1pt 1pt; padding: 0cm 5.4pt; width: 50.4pt; height: 25.15pt;"
 valign="top" width="67">
      <p class="MsoNormal" style="">09</p>
      </td>
    </tr>
    <tr style="page-break-inside: avoid; height: 25.15pt;">
      <td
 style="border-style: none none solid solid; border-color: -moz-use-text-color -moz-use-text-color black black; border-width: medium medium 1pt 1pt; padding: 0cm 5.4pt; width: 50.4pt; height: 25.15pt;"
 valign="top" width="67">
      <p class="MsoNormal" style="">10</p>
      </td>
    </tr>
    <tr style="page-break-inside: avoid; height: 25.15pt;">
      <td
 style="border-style: none none solid solid; border-color: -moz-use-text-color -moz-use-text-color black black; border-width: medium medium 1pt 1pt; padding: 0cm 5.4pt; width: 50.4pt; height: 25.15pt;"
 valign="top" width="67">
      <p class="MsoNormal" style="">11</p>
      </td>
      <td rowspan="2"
 style="border-style: none none solid solid; border-color: -moz-use-text-color -moz-use-text-color black black; border-width: medium medium 1pt 1pt; padding: 0cm 5.4pt; width: 63pt; height: 25.15pt;"
 valign="top" width="84">
      <p class="MsoNormal" style="">uint16</p>
      </td>
      <td rowspan="2"
 style="border-style: none none solid solid; border-color: -moz-use-text-color -moz-use-text-color black black; border-width: medium medium 1pt 1pt; padding: 0cm 5.4pt; width: 232.4pt; height: 25.15pt;"
 valign="top" width="310">
      <p class="MsoNormal" style="">Millisekundenanteil
der Zeit</p>
      </td>
      <td rowspan="2"
 style="border-style: none solid solid; border-color: -moz-use-text-color black black; border-width: medium 1pt 1pt; padding: 0cm 5.4pt; width: 117.4pt; height: 25.15pt;"
 valign="top" width="157">
      <p class="MsoNormal" style="">&nbsp;</p>
      </td>
    </tr>
    <tr style="page-break-inside: avoid; height: 25.15pt;">
      <td
 style="border-style: none none solid solid; border-color: -moz-use-text-color -moz-use-text-color black black; border-width: medium medium 1pt 1pt; padding: 0cm 5.4pt; width: 50.4pt; height: 25.15pt;"
 valign="top" width="67">
      <p class="MsoNormal" style="">12</p>
      </td>
    </tr>
    <tr style="page-break-inside: avoid; height: 25.15pt;">
      <td
 style="border-style: none none solid solid; border-color: -moz-use-text-color -moz-use-text-color black black; border-width: medium medium 1pt 1pt; padding: 0cm 5.4pt; width: 50.4pt; height: 25.15pt;"
 valign="top" width="67">
      <p class="MsoNormal" style="">13</p>
      </td>
      <td rowspan="2"
 style="border-style: none none solid solid; border-color: -moz-use-text-color -moz-use-text-color black black; border-width: medium medium 1pt 1pt; padding: 0cm 5.4pt; width: 63pt; height: 25.15pt;"
 valign="top" width="84">
      <p class="MsoNormal">int16</p>
      </td>
      <td rowspan="2"
 style="border-style: none none solid solid; border-color: -moz-use-text-color -moz-use-text-color black black; border-width: medium medium 1pt 1pt; padding: 0cm 5.4pt; width: 232.4pt; height: 25.15pt;"
 valign="top" width="310">
      <p class="MsoNormal">x - Koordinate</p>
      </td>
      <td rowspan="2"
 style="border-style: none solid solid; border-color: -moz-use-text-color black black; border-width: medium 1pt 1pt; padding: 0cm 5.4pt; width: 117.4pt; height: 25.15pt;"
 valign="top" width="157">
      <p class="MsoNormal">&nbsp;</p>
      </td>
    </tr>
    <tr style="page-break-inside: avoid; height: 25.15pt;">
      <td
 style="border-style: none none solid solid; border-color: -moz-use-text-color -moz-use-text-color black black; border-width: medium medium 1pt 1pt; padding: 0cm 5.4pt; width: 50.4pt; height: 25.15pt;"
 valign="top" width="67">
      <p class="MsoNormal" style="">14</p>
      </td>
    </tr>
    <tr style="page-break-inside: avoid; height: 25.15pt;">
      <td
 style="border-style: none none solid solid; border-color: -moz-use-text-color -moz-use-text-color black black; border-width: medium medium 1pt 1pt; padding: 0cm 5.4pt; width: 50.4pt; height: 25.15pt;"
 valign="top" width="67">
      <p class="MsoNormal" style="">15</p>
      </td>
      <td rowspan="2"
 style="border-style: none none solid solid; border-color: -moz-use-text-color -moz-use-text-color black black; border-width: medium medium 1pt 1pt; padding: 0cm 5.4pt; width: 63pt; height: 25.15pt;"
 valign="top" width="84">
      <p class="MsoNormal">int16</p>
      </td>
      <td rowspan="2"
 style="border-style: none none solid solid; border-color: -moz-use-text-color -moz-use-text-color black black; border-width: medium medium 1pt 1pt; padding: 0cm 5.4pt; width: 232.4pt; height: 25.15pt;"
 valign="top" width="310">
      <p class="MsoNormal">y - Koordinate</p>
      </td>
      <td rowspan="2"
 style="border-style: none solid solid; border-color: -moz-use-text-color black black; border-width: medium 1pt 1pt; padding: 0cm 5.4pt; width: 117.4pt; height: 25.15pt;"
 valign="top" width="157">
      <p class="MsoNormal">&nbsp;</p>
      </td>
    </tr>
    <tr style="page-break-inside: avoid; height: 25.15pt;">
      <td
 style="border-style: none none solid solid; border-color: -moz-use-text-color -moz-use-text-color black black; border-width: medium medium 1pt 1pt; padding: 0cm 5.4pt; width: 50.4pt; height: 25.15pt;"
 valign="top" width="67">
      <p class="MsoNormal" style="">16</p>
      </td>
    </tr>
    <tr style="page-break-inside: avoid; height: 25.15pt;">
      <td
 style="border-style: none none solid solid; border-color: -moz-use-text-color -moz-use-text-color black black; border-width: medium medium 1pt 1pt; padding: 0cm 5.4pt; width: 50.4pt; height: 25.15pt;"
 valign="top" width="67">
      <p class="MsoNormal" style="">17</p>
      </td>
      <td rowspan="2"
 style="border-style: none none solid solid; border-color: -moz-use-text-color -moz-use-text-color black black; border-width: medium medium 1pt 1pt; padding: 0cm 5.4pt; width: 63pt; height: 25.15pt;"
 valign="top" width="84">
      <p class="MsoNormal">int16</p>
      </td>
      <td rowspan="2"
 style="border-style: none none solid solid; border-color: -moz-use-text-color -moz-use-text-color black black; border-width: medium medium 1pt 1pt; padding: 0cm 5.4pt; width: 232.4pt; height: 25.15pt;"
 valign="top" width="310">
      <p class="MsoNormal">z - Koordinate</p>
      </td>
      <td rowspan="2"
 style="border-style: none solid solid; border-color: -moz-use-text-color black black; border-width: medium 1pt 1pt; padding: 0cm 5.4pt; width: 117.4pt; height: 25.15pt;"
 valign="top" width="157">
      <p class="MsoNormal">&nbsp;</p>
      </td>
    </tr>
    <tr style="page-break-inside: avoid; height: 25.15pt;">
      <td
 style="border-style: none none solid solid; border-color: -moz-use-text-color -moz-use-text-color black black; border-width: medium medium 1pt 1pt; padding: 0cm 5.4pt; width: 50.4pt; height: 25.15pt;"
 valign="top" width="67">
      <p class="MsoNormal" style="">18</p>
      </td>
    </tr>
  </tbody>
</table>

 * 
 * @author Nils Glombitza, ITM Uni Luebeck
 *
 */
public class SpyglassPacket extends Packet
{

	public static final int ISENSE_SPYGLASS_PACKET_STD = 0;
	public static final int ISENSE_SPYGLASS_PACKET_UINT8 = 1;
	public static final int ISENSE_SPYGLASS_PACKET_UINT16 = 2;
	public static final int ISENSE_SPYGLASS_PACKET_INT16 = 3;
	public static final int ISENSE_SPYGLASS_PACKET_UINT32 = 4;
	public static final int ISENSE_SPYGLASS_PACKET_INT64 = 5;
	public static final int ISENSE_SPYGLASS_PACKET_FLOAT = 6;
	public static final int ISENSE_SPYGLASS_PACKET_VARIABLE = 7;

	public static final int EXPECTED_PACKET_SIZE = 18;

	protected int length;
	// uint8 version_;
	protected int syntax_type;
	protected int semantic_type;
	protected int sender_id;
	protected Time time;
	protected int x;
	protected int y;
	protected int z;
	
	public void deserialize() throws SpyglassPacketException
	{
		deserialize(getContent());
	}

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
		return (((((b3 & 0xFF) & 0xFFFFFFFF) | (((b2 & 0xFF) << 8) & 0xFFFFFFFF))) | (((b1 & 0xFF) << 16) & 0xFFFFFFFF)) | (((b0 & 0xFF) << 24) & 0xFFFFFFFF);
	}

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

	protected float deserializeFloat(byte b0, byte b1, byte b2, byte b3)
	{
		int i0 = (int) b0;
		int i1 = (int) (b1 & 0xFF);
		int i2 = (int) (b2 & 0xFF);
		int i3 = (int) (b3 & 0xFF);
		int ret = (i3 | (i2 << 8) | (i1 << 16) | (i0 << 24));
		return Float.intBitsToFloat(ret);

	}

	public String toString()
	{
		return "length:" + length + ", syntax_type:" + syntax_type + ", semantic_type:" + semantic_type + ", sender_id:" + sender_id + ",time: " + time.toString() + ", x:" + x + ", y:" + y + ", z:" + z;
	}

	/**
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @return the length
	 */
	public int getLength()
	{
		return length;
	}

	/**
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @param length
	 *            the length to set
	 */
	public void setLength(int length)
	{
		this.length = length;
	}

	/**
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @return the syntax_type
	 */
	public int getSyntax_type()
	{
		return syntax_type;
	}

	/**
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @param syntax_type
	 *            the syntax_type to set
	 */
	public void setSyntax_type(int syntax_type)
	{
		this.syntax_type = syntax_type;
	}

	/**
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @return the semantic_type
	 */
	public int getSemantic_type()
	{
		return semantic_type;
	}

	/**
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @param semantic_type
	 *            the semantic_type to set
	 */
	public void setSemantic_type(int semantic_type)
	{
		this.semantic_type = semantic_type;
	}

	/**
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @return the sender_id
	 */
	public int getSender_id()
	{
		return sender_id;
	}

	/**
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @param sender_id
	 *            the sender_id to set
	 */
	public void setSender_id(int sender_id)
	{
		this.sender_id = sender_id;
	}

	/**
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @return the time
	 */
	public Time getTime()
	{
		return time;
	}

	/**
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @param time
	 *            the time to set
	 */
	public void setTime(Time time)
	{
		this.time = time;
	}

	/**
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @return the x
	 */
	public int getX()
	{
		return x;
	}

	/**
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @param x
	 *            the x to set
	 */
	public void setX(int x)
	{
		this.x = x;
	}

	/**
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @return the y
	 */
	public int getY()
	{
		return y;
	}

	/**
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @param y
	 *            the y to set
	 */
	public void setY(int y)
	{
		this.y = y;
	}

	/**
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @return the z
	 */
	public int getZ()
	{
		return z;
	}

	/**
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @param z
	 *            the z to set
	 */
	public void setZ(int z)
	{
		this.z = z;
	}
}



