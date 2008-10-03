package de.uniluebeck.itm.spyglass.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.junit.Before;
import org.junit.Test;

import de.uniluebeck.itm.spyglass.packet.SpyglassPacket;

/**
 * Some Testcases for the StringFormatter
 * 
 * @author dariush
 * 
 */
public class StringFormatterTests {
	
	private SpyglassPacket packet1 = null;
	private SpyglassPacket packet2 = null;
	private SpyglassPacket packet3 = null;
	private SpyglassPacket packet4 = null;
	
	@Before
	public void setUp() throws Exception {
		packet1 = new SpyglassPacket();
		packet1.setPayload(new byte[] { 0x00, 0x01, 0x02 });
		
		packet2 = new SpyglassPacket();
		packet2.setPayload(new byte[] { 0x00, 0x00, 0x00, 0x04 });
		
		packet3 = new SpyglassPacket();
		packet3.setPayload(new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFE });
		
		final ByteBuffer buf = ByteBuffer.allocate(7 * 4);
		buf.order(ByteOrder.BIG_ENDIAN);
		
		buf.putFloat((float) 14.7); // 0
		buf.putFloat((float) 9.380918E-8); // 4
		buf.putFloat(Float.NaN); // 8
		buf.putFloat(Float.NEGATIVE_INFINITY); // 12
		buf.putFloat(Float.POSITIVE_INFINITY); // 16
		buf.putFloat(Float.MIN_VALUE); // 20
		buf.putFloat(Float.MAX_VALUE); // 24
		
		packet4 = new SpyglassPacket();
		packet4.setPayload(buf.array());
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Tests if the given Fmt-String and packet produce the expected string.
	 * 
	 * @param fmt
	 *            a fmt string
	 * @param p
	 *            a spyglass packet
	 * @param res
	 *            the expected string
	 */
	private void testString(final String fmt, final SpyglassPacket p, final String res) {
		final StringFormatter f = new StringFormatter(fmt);
		final String r = f.parse(p);
		System.out.println("Ergebnis: " + r);
		assertEquals(res, r);
		assertEquals(fmt, f.getOrigExpression());
		
	}
	
	// Some simple tests without any substitution
	
	@Test
	public void testParseSimple() {
		testString("Hello World", packet1, "Hello World");
	}
	
	@Test
	public void testParseGaensefuesschen() {
		testString("Gänsefüßchen: \"", packet1, "Gänsefüßchen: \"");
	}
	
	@Test
	public void testParseNewLine() {
		testString("New Line: \n", packet1, "New Line: \n");
	}
	
	@Test
	public void testParse_percent_twice() {
		testString("My name is Freddy \"percent_twice\" Krueger.", packet1,
				"My name is Freddy \"percent_twice\" Krueger.");
	}
	
	// failure tests - these should fail
	
	@Test
	public void testParseBogusFmt() {
		try {
			new StringFormatter("a: %i");
			fail("Excepted exception.");
		} catch (final IllegalArgumentException e) {
			// ok.
		}
	}
	
	@Test
	public void testParseBogusFmt2() {
		try {
			new StringFormatter("%%%");
			fail("Excepted exception.");
		} catch (final IllegalArgumentException e) {
			// ok.
		}
	}
	
	@Test
	public void testParseBogusFmt3() {
		try {
			new StringFormatter("%-");
			fail("Excepted exception.");
		} catch (final IllegalArgumentException e) {
			// ok.
		}
	}
	
	@Test
	public void testParseBogusFmt4() {
		try {
			new StringFormatter("%bla");
			fail("Excepted exception.");
		} catch (final IllegalArgumentException e) {
			// ok.
		}
	}
	
	@Test
	public void testParseBogusFmt5() {
		try {
			new StringFormatter("%U-1");
			fail("Excepted exception.");
		} catch (final IllegalArgumentException e) {
			// ok.
		}
	}
	
	@Test
	public void testParseBogusFmt6() {
		try {
			new StringFormatter("%UU");
			fail("Excepted exception.");
		} catch (final IllegalArgumentException e) {
			// ok.
		}
	}
	
	@Test
	public void testParseBogusFmt7() {
		try {
			new StringFormatter("%U%%");
			fail("Excepted exception.");
		} catch (final IllegalArgumentException e) {
			// ok.
		}
	}
	
	// packet 1
	
	@Test
	public void testParsePacket1() {
		testString("a: %u0, %u1, %u2", packet1, "a: 0, 1, 2");
	}
	
	@Test
	public void testParsePacket1a() {
		testString("a: %u0, %u1, %u0", packet1, "a: 0, 1, 0");
	}
	
	// packet 2
	
	// Endianess test
	@Test
	public void testParsePacket2() {
		testString("a: %U0", packet2, "a: 4");
	}
	
	// Endianess test
	@Test
	public void testParsePacket2a() {
		testString("a: %i2", packet2, "a: 4");
	}
	
	// packet 3
	
	// unsigned test - 32bit
	@Test
	public void testParsePacket3() {
		testString("a: %U0", packet3, "a: 4294967294");
	}
	
	// unsigned test - 8 bit
	@Test
	public void testParsePacket3a() {
		testString("a: %u3", packet3, "a: 254");
	}
	
	// signed test - 16 bit
	@Test
	public void testParsePacket3b() {
		testString("a: %i2", packet3, "a: -2");
	}
	
	// signed test - 16 bit
	@Test
	public void testParsePacket3c() {
		testString("a: %i0", packet3, "a: -1");
	}
	
	// packet length
	@Test
	public void testParsePacketFailLength() {
		testString("a: %i10", packet3, "a: <packet to short>");
	}
	
	// packet 4 - float
	
	// signed test - 16 bit
	@Test
	public void testParsePacket4() {
		testString("a: %f0\n", packet4, "a: 14.7\n");
	}
	
	// signed test - 16 bit
	@Test
	public void testParsePacket4a() {
		testString("a: %f4", packet4, "a: 9.380918E-8");
	}
	
	// signed test - 16 bit
	@Test
	public void testParsePacket4NaN() {
		testString("a: %f8", packet4, "a: " + Float.NaN);
	}
	
	// signed test - 16 bit
	@Test
	public void testParsePacket4MinInf() {
		testString("a: %f12", packet4, "a: " + Float.NEGATIVE_INFINITY);
	}
	
	// signed test - 16 bit
	@Test
	public void testParsePacket4MaxInf() {
		testString("a: %f16", packet4, "a: " + Float.POSITIVE_INFINITY);
	}
	
	// signed test - 16 bit
	@Test
	public void testParsePacket4MinValue() {
		testString("a: %f20", packet4, "a: " + Float.MIN_VALUE);
	}
	
	// signed test - 16 bit
	@Test
	public void testParsePacket4MaxValue() {
		testString("a: %f24", packet4, "a: " + Float.MAX_VALUE);
	}
	
}
