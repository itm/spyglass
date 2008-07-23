package de.uniluebeck.itm.spyglass.util;

import java.nio.ByteBuffer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.uniluebeck.itm.spyglass.packet.Packet;

public class StringFormatter {
	
	private final String formatExpression;
	private static final Pattern notNumeric = Pattern.compile("[^0-9]");
	
	public StringFormatter(String pFormatExpression) throws IllegalArgumentException {
		
		// temporarily replace "%%" with "percent_twice" to avoid trouble
		final String tmp = "percent_twice";
		pFormatExpression = pFormatExpression.replaceAll("%%", tmp);
		
		// test if the given expression is valid and throw an exception
		// otherwise
		if (pFormatExpression.matches(".*%[^uUif%].*") || pFormatExpression.matches(".*%[uUif][^0-9].*") || pFormatExpression.matches(".*%")
				|| pFormatExpression.matches("*.%[uUif]")) {
			final String msg = "StringFormatterExpression is invalid";
			throw new IllegalArgumentException(msg);
		}
		
		this.formatExpression = pFormatExpression;
	}
	
	/**
	 * 
	 * @param packet
	 */
	public String parse(final Packet packet) throws IllegalArgumentException {
		final byte[] content = packet.getContent();
		String tmpExp = this.formatExpression;
		String result = "";
		
		// first part of result is given by expression as substring until the
		// first occurence of "%"
		int index = tmpExp.indexOf("%");
		while (index != -1) {
			// write substring until first occurence of "%" to the result string
			result += tmpExp.substring(0, index);
			
			// shorten the remaining expression until first occurence of "%"
			tmpExp = tmpExp.substring(index);
			
			// identify the offset of the value in the data packet
			final int indexNotNum = indexOfNonNumeric(tmpExp.substring(2)) + 2;
			int offset;
			if (indexNotNum == -1) {
				offset = Integer.parseInt(tmpExp.substring(1));
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
					b[0] = 0;
					b[1] = content[offset];
					bb = ByteBuffer.allocate(2);
					bb.put(b);
					result += bb.getShort(0);
					break;
				// uint32 (4 bytes):
				case 'U':
					b = new byte[8];
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
					break;
				// int16 (2 bytes in two�s complement):
				case 'i':
					b = new byte[2];
					b[0] = content[offset];
					b[1] = content[offset + 1];
					bb = ByteBuffer.allocate(2);
					bb.put(b);
					result += bb.getShort(0);
					break;
				// float (4 bytes as IEEE 754 single precision floating point
				// value ):
				case 'f':
					b = new byte[4];
					for (int i = 0; i < 4; i++) {
						b[i] = content[offset + i];
					}
					bb = ByteBuffer.allocate(4);
					bb.put(b, 0, 4);
					result += bb.getFloat(0);
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
		result += tmpExp;
		
		result = result.replaceAll("percent_twice", "%");
		return result;
	}
	
	private static int indexOfNonNumeric(final String lookIn) {
		final Matcher m = notNumeric.matcher(lookIn);
		if (m.find()) {
			return m.start();
		} else {
			return -1;
		}
	}
	
	public static void main(final String[] args) {
		final StringFormatter test = new StringFormatter("Temp: %u5\nBattery %U1%%%%");
		final Packet packet = new Packet();
		final byte[] content = { 0, -1, -1, -1, -1, -1, 49, 0, 0, 10 };
		packet.setContent(content);
		System.out.println(test.parse(packet));
	}
	
}