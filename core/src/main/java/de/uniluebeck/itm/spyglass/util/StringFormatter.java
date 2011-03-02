/*
 * --------------------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass.
 * Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de) project SpyGlass is free
 * software; you can redistribute it and/or modify it under the terms of the BSD License.
 * Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for further
 * details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.util;

import java.nio.ByteBuffer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.uniluebeck.itm.spyglass.packet.SpyglassPacket;

/**
 * The StringFormatter generates a String based on a given expression that mostly will contain at
 * least one placeholder. Each placeholder must be consistent with the scheme. It always starts with
 * a '%' followed by the type of the value and the offset (bytes) of the data in the packet. Allowed
 * data types are
 * <ul>
 * <li>'u' (unsigned 8 Bit integer)</li>
 * <li>'U' (unsigned 32 Bit integer)</li>
 * <li>'v' (unsigned 16 Bit integer)</li>
 * <li>'i' (signed 16 Bit integer)</li>
 * <li>'f' (signed 32 Bit float).</li>
 * </ul>
 * 
 * Examples:
 * <ul>
 * <li>%u0: The first byte of the packets to be parsed contain an uint8 value</li>
 * <li>%f17: The bytes 18, 19, 20 and 21 contain of the packets contain a float value</li>
 * <ul>
 * 
 * If the character % should not denote the beginning of a placeholder, it must be masked as %% in
 * the expression.
 * 
 * @author Oliver Kleine
 * @version 1.0
 * 
 */
public class StringFormatter {

	private static final Pattern notNumeric = Pattern.compile("[^0-9]");

	private final String origFormatExpression;
	private final String formatExpression;
	private String resultString = "";

	/**
	 * The parameter of the constructor must be a String containing an expression. If the expression
	 * is invalid, an IllegalArgumentException will be thrown. An expression is unvalid, if it
	 * <ul>
	 * <li>contains a substring % followed by any character expect u,v,U,i,f or %,</li>
	 * <li>contains a substring %u, %v, %U, %i or %f followed by a non digit character,</li>
	 * <li>ends with % or</li>
	 * <li>ends with %u, ,%v, %U, %i or %f</li>
	 * </ul>
	 * Note, that %% masks a %. Thus an expression may contain e.g. a substring '%%fish', even
	 * though this strictly spoken breaks rule 2 (it contains '%f').
	 * 
	 * @param pFormatExpression
	 *            String, that should contain a valid expression
	 * @throws IllegalArgumentException
	 *             if the expression is invalid
	 */
	public StringFormatter(String pFormatExpression) throws IllegalArgumentException {

		origFormatExpression = pFormatExpression + "";

		// temporarily replace "%%" with "percent_twice" to avoid trouble
		final String tmp = "percent_twice";
		pFormatExpression = pFormatExpression.replaceAll("%%", tmp);

		// test if the given expression is valid and throw an exception
		// otherwise

		if (pFormatExpression.matches(".*%[^uvUif%].*") || pFormatExpression.matches(".*%[uvUif][^0-9].*") || pFormatExpression.matches(".*%")
				|| pFormatExpression.matches(".*%[uvUif]")) {
			final String msg = "StringFormatterExpression '" + pFormatExpression + "' is invalid";
			throw new IllegalArgumentException(msg);
		}

		this.formatExpression = convertLineBreaks(pFormatExpression);
	}

	// --------------------------------------------------------------------------------
	/**
	 * This method parses the given Packet and returns a String corresponding to the expression
	 * given to the constructor. The placeholders of the expression are displaced with the values
	 * from the packet. The positions of these values in the packet's data are declared in the
	 * placeholders.
	 * 
	 * @param packet
	 * @return A String, where the placeholders of the expression are changed with values from the
	 *         packet
	 * @exception IllegalArgumentException
	 *                if the packet cannot be parsed because of a faulty payload or header
	 */
	public String parse(final SpyglassPacket packet) throws IllegalArgumentException {
		String result = "";
		// final byte[] content = packet.getPayload();
		final byte[] content = packet.getPacketData();
		String tmpExp = this.formatExpression;
		// first part of result is given by expression as substring until the
		// first occurence of "%"
		int index = tmpExp.indexOf("%");
		int indexNotNum = 0;
		while (index != -1) {
			// write substring until first occurrence of "%" to the result string
			result += tmpExp.substring(0, index);

			// shorten the remaining expression until first occurrence of "%"
			tmpExp = tmpExp.substring(index);

			// identify the offset of the value in the data packet
			indexNotNum = indexOfNonNumeric(tmpExp.substring(2)) + 2;

			int offset;
			if (indexNotNum == 1) {
				offset = Integer.parseInt(tmpExp.substring(2));
			} else {
				offset = Integer.parseInt(tmpExp.substring(2, indexNotNum));
			}

			// identify the type of data in the packet and add the data to the
			// result string
			byte[] b;
			ByteBuffer bb;
			switch (tmpExp.charAt(1)) {

				// uint8 (1 byte):
				case 'u':
					b = new byte[2];
					try {
						b[0] = 0;
						b[1] = content[offset];
						bb = ByteBuffer.allocate(2);
						bb.put(b);
						result += bb.getShort(0);
					} catch (final ArrayIndexOutOfBoundsException e) {
						result += "<packet to short>";
					} catch (final NullPointerException e) {
						result += "<packet without payload>";
					}
					break;

				// uint16 (2 bytes):
				case 'v':
					b = new byte[4];
					try {
						for (int i = 0; i < 4; i++) {
							if (i < 2) {
								b[i] = 0;
							} else {
								b[i] = content[offset + i - 2];
							}
							bb = ByteBuffer.allocate(4);
							bb.put(b);
							result += bb.getInt(0);
						}
					} catch (final ArrayIndexOutOfBoundsException e) {
						result += "<packet to short>";
					} catch (final NullPointerException e) {
						result += "<packet without payload>";
					}
					break;

				// uint32 (4 bytes):
				case 'U':
					b = new byte[8];
					try {
						for (int i = 0; i < 8; i++) {
							if (i < 4) {
								b[i] = 0;
							} else {
								b[i] = content[offset + i - 4];
							}
						}
						bb = ByteBuffer.allocate(8);
						bb.put(b);
						result += bb.getLong(0);
					} catch (final ArrayIndexOutOfBoundsException e) {
						result += "<packet to short>";
					} catch (final NullPointerException e) {
						result += "<packet without payload>";
					}
					break;

				// int16 (2 bytes in two�s complement):
				case 'i':
					b = new byte[2];
					try {
						b[0] = content[offset];
						b[1] = content[offset + 1];
						bb = ByteBuffer.allocate(2);
						bb.put(b);
						result += bb.getShort(0);
					} catch (final ArrayIndexOutOfBoundsException e) {
						result += "<packet to short>";
					} catch (final NullPointerException e) {
						result += "<packet without payload>";
					}
					break;

				// float (4 bytes as IEEE 754 single precision floating point
				// value ):
				case 'f':
					b = new byte[4];
					try {
						for (int i = 0; i < 4; i++) {
							b[i] = content[offset + i];
						}
						bb = ByteBuffer.allocate(4);
						bb.put(b, 0, 4);
						result += bb.getFloat(0);
					} catch (final ArrayIndexOutOfBoundsException e) {
						result += "<packet to short>";
					} catch (final NullPointerException e) {
						result += "<packet without payload>";
					}
					break;
			}

			// shorten the remainig expression until the end of the offset
			// definition
			tmpExp = tmpExp.substring(indexNotNum);

			// find out the index of the next "%" in the remainig expression
			index = tmpExp.indexOf("%");
		}

		// add the text behind the last "%" of the given expression to the
		// result
		if (indexNotNum != 1) {
			result += tmpExp;
		}

		result = result.replaceAll("percent_twice", "%");
		resultString = result;
		return resultString;
	}

	private static int indexOfNonNumeric(final String lookIn) {
		final Matcher m = notNumeric.matcher(lookIn);
		if (m.find()) {
			return m.start();
		}
		return -1;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns the original expression, from which this formatter was build from.
	 * 
	 * @return the original expression, from which this formatter was build from.
	 */
	public String getOrigExpression() {
		return origFormatExpression;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Converts the line breaks the user intended to create in the created to "real" ones which are
	 * interpreted correctly by the parser
	 * 
	 * @param str
	 *            the string which line breaks are to be converted
	 * @return the provided strings with correctly interpretable line break commands
	 */
	public static String convertLineBreaks(String str) {
		str = str.replace("\\r\\n", "<br>");
		str = str.replace("\\n", "<br>");
		str = str.replace("\\r", "<br>");
		return str.replace("<br>", "\r\n");
	}

	@Override
	public String toString() {
		return resultString;
	}

	// /**
	// * Main method for testing
	// *
	// * @param args
	// * an array of arguments
	// */
	// public static void main(final String[] args) {
	// // final StringFormatter test = new StringFormatter("Temp: %u5\nBattery %U1%");
	// // final StringFormatter test = new StringFormatter("Temp: %u5 Battery %u1");
	// final StringFormatter test = new StringFormatter(
	// "Wert 1 (uint8): %u0, Wert 2 (uint32): %U4, Wert 3 (int16): %i8, Wert 4 (float): %f10 und noch Text");
	// // final StringFormatter test = new StringFormatter("");
	// final SpyglassPacket packet = new SpyglassPacket();
	//
	// final byte[] content = { -128, 0, 0, 0, -1, -1, -1, -1, -128, 0, 65, -109, 51, 51 };
	// // final byte[] content = { -128, 0, 0, 0, -1, -1, -1, -1, -128, 0, 65 };
	// // final byte[] content = { 0, -2, -1, -1, -1, -1, 49, 0, 0, 10 };
	// // packet.setPayload(content);
	// final String result = test.parse(packet);
	// if (result.equalsIgnoreCase("")) {
	// System.out.println("<leer>");
	// } else {
	// System.out.println(result);
	// }
	// }

}