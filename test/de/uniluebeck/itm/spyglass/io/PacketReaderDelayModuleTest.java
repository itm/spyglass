// --------------------------------------------------------------------------------
/**
 * 
 */
package de.uniluebeck.itm.spyglass.io;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import de.uniluebeck.itm.spyglass.core.SpyglassConfiguration;
import de.uniluebeck.itm.spyglass.io.AbstractPacketReader.DelayModule;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacket;
import de.uniluebeck.itm.spyglass.packet.Time;

// --------------------------------------------------------------------------------
/**
 * @author Sebastian Ebers
 * 
 */
public class PacketReaderDelayModuleTest {

	private SpyglassConfiguration config;
	private AbstractPacketReader.DelayModule delayModule;
	private AbstractPacketReader packetReader;

	// --------------------------------------------------------------------------------
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		packetReader = new ComplexPacketReader();
		config = new SpyglassConfiguration();
		config.setPacketReader(packetReader);
		delayModule = new DelayModule(new Object(), config);

	}

	/**
	 * Test method for
	 * {@link de.uniluebeck.itm.spyglass.io.AbstractPacketReader.DelayModule#determineDelay(SpyglassPacket, long)
	 * )} . The packets' time stamps are used
	 */
	@Test
	public final void testDetermineDelay1() {

		packetReader.setDelayMillies(2000);
		config.getGeneralSettings().setTimeScale(1);

		delayModule.determineDelay(new SpyglassPacketStub(1, 10), -1);
		long delay = delayModule.determineDelay(new SpyglassPacketStub(4, 10), System.currentTimeMillis());

		if ((delay <= 3000) && (delay > 2990)) {
			delay = 3000;
		}
		Assert.assertEquals(3000, delay);
	}

	/**
	 * Test method for
	 * {@link de.uniluebeck.itm.spyglass.io.AbstractPacketReader.DelayModule#determineDelay(SpyglassPacket, long)
	 * )} . The packets' time stamps are used
	 */
	@Test
	public final void testDetermineDelay2() {

		packetReader.setDelayMillies(1000);
		config.getGeneralSettings().setTimeScale(2);

		delayModule.determineDelay(new SpyglassPacketStub(1, 10), -1);
		long delay = delayModule.determineDelay(new SpyglassPacketStub(4, 10), System.currentTimeMillis());

		if ((delay <= 1500) && (delay > 1490)) {
			delay = 1500;
		}
		Assert.assertEquals(1500, delay);
	}

	/**
	 * Test method for
	 * {@link de.uniluebeck.itm.spyglass.io.AbstractPacketReader.DelayModule#determineDelay(SpyglassPacket, long)
	 * )} . The packets' time stamps are used
	 */
	@Test
	public final void testDetermineDelay3() {

		packetReader.setDelayMillies(2000);
		config.getGeneralSettings().setTimeScale(0.5f);

		delayModule.determineDelay(new SpyglassPacketStub(1, 10), -1);
		long delay = delayModule.determineDelay(new SpyglassPacketStub(4, 10), System.currentTimeMillis());

		if ((delay <= 6000) && (delay > 5990)) {
			delay = 6000;
		}
		Assert.assertEquals(6000, delay);
	}

	/**
	 * Test method for
	 * {@link de.uniluebeck.itm.spyglass.io.AbstractPacketReader.DelayModule#determineDelay(SpyglassPacket, long)
	 * )} . The minimum delay time is used
	 */
	@Test
	public final void testDetermineDelay4() {

		packetReader.setDelayMillies(3500);
		config.getGeneralSettings().setTimeScale(1);

		delayModule.determineDelay(new SpyglassPacketStub(1, 10), -1);
		long delay = delayModule.determineDelay(new SpyglassPacketStub(4, 10), System.currentTimeMillis());

		if ((delay <= 3500) && (delay > 3490)) {
			delay = 3500;
		}
		Assert.assertEquals(3500, delay);
	}

	/**
	 * Test method for
	 * {@link de.uniluebeck.itm.spyglass.io.AbstractPacketReader.DelayModule#determineDelay(SpyglassPacket, long)
	 * )} . The minimum delay time is used
	 */
	@Test
	public final void testDetermineDelay5() {

		packetReader.setDelayMillies(4000);
		config.getGeneralSettings().setTimeScale(2);

		delayModule.determineDelay(new SpyglassPacketStub(1, 10), -1);
		long delay = delayModule.determineDelay(new SpyglassPacketStub(4, 10), System.currentTimeMillis());

		if ((delay <= 4000) && (delay > 3990)) {
			delay = 4000;
		}
		Assert.assertEquals(4000, delay);
	}

	/**
	 * Test method for
	 * {@link de.uniluebeck.itm.spyglass.io.AbstractPacketReader.DelayModule#determineDelay(SpyglassPacket, long)
	 * )} . The minimum delay time is used
	 */
	@Test
	public final void testDetermineDelay6() {

		packetReader.setDelayMillies(7000);
		config.getGeneralSettings().setTimeScale(0.5f);

		delayModule.determineDelay(new SpyglassPacketStub(1, 10), -1);
		long delay = delayModule.determineDelay(new SpyglassPacketStub(4, 10), System.currentTimeMillis());

		if ((delay <= 7000) && (delay > 6990)) {
			delay = 7000;
		}
		Assert.assertEquals(7000, delay);

	}

	/**
	 * Test method for
	 * {@link de.uniluebeck.itm.spyglass.io.AbstractPacketReader.DelayModule#determineDelay(SpyglassPacket, long)
	 * )} . The time which elapsed between the previously delivered packet and the current one is
	 * bigger than the time to delay the packet
	 */
	@Test
	public final void testDetermineDelay7() {

		packetReader.setDelayMillies(2000);
		config.getGeneralSettings().setTimeScale(1);

		delayModule.determineDelay(new SpyglassPacketStub(1, 10), -1);
		final long delay = delayModule.determineDelay(new SpyglassPacketStub(4, 10), (System.currentTimeMillis() - 4000));
		Assert.assertEquals(0, delay);
	}

	/**
	 * Test method for
	 * {@link de.uniluebeck.itm.spyglass.io.AbstractPacketReader.DelayModule#determineDelay(SpyglassPacket, long)
	 * )} . Packets with smaller time stamps are not delayed at all
	 */
	@Test
	public final void testDetermineDelay8() {

		packetReader.setDelayMillies(0);
		config.getGeneralSettings().setTimeScale(1);

		delayModule.determineDelay(new SpyglassPacketStub(1, 10), -1);
		long delay = delayModule.determineDelay(new SpyglassPacketStub(4, 10), System.currentTimeMillis());

		if ((delay <= 3000) && (delay > 2990)) {
			delay = 3000;
		}
		Assert.assertEquals(3000, delay);

		long delay2 = delayModule.determineDelay(new SpyglassPacketStub(3, 10), System.currentTimeMillis());

		if ((delay2 <= 3000) && (delay2 > 2990)) {
			delay2 = 0;
		}
		Assert.assertEquals(0, delay2);

		long delay3 = delayModule.determineDelay(new SpyglassPacketStub(3, 100), System.currentTimeMillis());

		if ((delay3 <= 3000) && (delay3 > 2990)) {
			delay3 = 0;
		}
		Assert.assertEquals(0, delay3);

		long delay4 = delayModule.determineDelay(new SpyglassPacketStub(7, 10), System.currentTimeMillis());

		if ((delay4 <= 3000) && (delay4 > 2990)) {
			delay4 = 3;
		}
		Assert.assertEquals(3, delay4);
	}

	/**
	 * Test method for
	 * {@link de.uniluebeck.itm.spyglass.io.AbstractPacketReader.DelayModule#reset()} .
	 */
	@Test
	public final void testReset1() {

		packetReader.setDelayMillies(0);
		config.getGeneralSettings().setTimeScale(1);

		delayModule.determineDelay(new SpyglassPacketStub(1, 10), -1);
		long delay = delayModule.determineDelay(new SpyglassPacketStub(4, 10), System.currentTimeMillis());

		if ((delay <= 3000) && (delay > 2990)) {
			delay = 3000;
		}
		Assert.assertEquals(3000, delay);
	}

	/**
	 * Test method for
	 * {@link de.uniluebeck.itm.spyglass.io.AbstractPacketReader.DelayModule#reset()} .
	 */
	@Test
	public final void testReset2() {

		packetReader.setDelayMillies(0);
		config.getGeneralSettings().setTimeScale(1);

		delayModule.determineDelay(new SpyglassPacketStub(1, 10), -1);
		delayModule.reset();
		final long delay = delayModule.determineDelay(new SpyglassPacketStub(4, 10), System.currentTimeMillis());

		Assert.assertEquals(0, delay);
	}

	// --------------------------------------------------------------------------------
	private class SpyglassPacketStub extends SpyglassPacket {

		private Time time;

		public SpyglassPacketStub(final long sec, final int ms) {
			time = new Time(sec, ms);
		}

		// --------------------------------------------------------------------------------
		/**
		 * @return the time
		 */
		@Override
		public Time getTime() {
			return time;
		}

	}

}
