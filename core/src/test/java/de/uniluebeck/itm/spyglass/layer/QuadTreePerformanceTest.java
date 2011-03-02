// --------------------------------------------------------------------------------
/**
 * 
 */
package de.uniluebeck.itm.spyglass.layer;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Random;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.packet.PacketFactory;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacket;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacketException;
import de.uniluebeck.itm.spyglass.packetgenerator.samples.PayloadSample;
import de.uniluebeck.itm.spyglass.packetgenerator.samples.Position;
import de.uniluebeck.itm.spyglass.packetgenerator.samples.Sample;
import de.uniluebeck.itm.spyglass.plugin.simplenodepainter.Node;
import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;

// --------------------------------------------------------------------------------
/**
 * @author dariush
 * 
 */
public class QuadTreePerformanceTest {

	private static final Logger log = SpyglassLoggerFactory.getLogger(QuadTreePerformanceTest.class);

	private ArrayList<DrawingObject> dobs = null;

	final int OBJECTS_PER_RUN = 10000;

	final int PAUSE_AFTER_NUM_PACKETS = 1000;

	/**
	 * How often should the "moveNodes" test be repeated?
	 */
	final int RUNS = 10;

	private PacketFactory factory;

	// --------------------------------------------------------------------------------
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		factory = new PacketFactory(new Spyglass());
		createSamples();
	}

	private void createSamples() throws SpyglassPacketException, ParseException {
		dobs = new ArrayList<DrawingObject>();

		log.warn("Generating samples...");

		// borrow from the packetGenerator
		final Sample s = new PayloadSample("uint16list", "1", "0000", "1-1000", new Position("0-1000", "0-1000", "0"));

		for (int i = 0; i < OBJECTS_PER_RUN; i++) {
			final SpyglassPacket packet = factory.createInstance(s.generatePacket());
			final Node n = new Node(packet.getSenderId(), "bla");
			n.setPosition(packet.getPosition());
			dobs.add(n);
		}
	}

	// --------------------------------------------------------------------------------
	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		log.warn("Cleaning up...");
		dobs = null;
		System.gc();
	}

	/**
	 * Test method for
	 * {@link de.uniluebeck.itm.spyglass.layer.SimpleLayer#add(de.uniluebeck.itm.spyglass.drawing.DrawingObject)}
	 * .
	 */
	@Test
	public void testAddQuadTree() {
		final Layer layer = Layer.Factory.createQuadTreeLayer();

		performAddTest(layer);

	}

	/**
	 * Test method for
	 * {@link de.uniluebeck.itm.spyglass.layer.SimpleLayer#add(de.uniluebeck.itm.spyglass.drawing.DrawingObject)}
	 * .
	 */
	@Test
	public void testAddSimpleLayer() {
		final Layer layer = new SimpleLayer();

		performAddTest(layer);

	}

	@Test
	public void testMoveSimpleLayer() {
		final Layer layer = new SimpleLayer();

		performMoveTest(layer);

	}

	@Test
	public void testMoveQuadTree() {
		final Layer layer = Layer.Factory.createQuadTreeLayer();

		performMoveTest(layer);

	}

	private void performAddTest(final Layer layer) {
		long start = System.nanoTime();
		int lastLog = 0;
		for (int i = 0; i < OBJECTS_PER_RUN; i++) {
			layer.add(dobs.get(i));

			if (i / PAUSE_AFTER_NUM_PACKETS > lastLog) {
				final long stop = System.nanoTime();
				log.warn(String.format("%s: Time needed for adding 1000 nodes: %f ms", layer.getClass().getSimpleName(), (stop - start) / 1000000d));
				lastLog = i / PAUSE_AFTER_NUM_PACKETS;
				start = System.nanoTime();
			}
		}
		log.warn(String.format("Items in the layer: %d", layer.getDrawingObjects().size()));
	}

	private void performMoveTest(final Layer layer) {
		final Random r = new Random();

		// first add a couple of objects to the layer
		for (int i = 0; i < PAUSE_AFTER_NUM_PACKETS; i++) {
			layer.add(dobs.get(i));
		}

		// now move them around
		for (int k = 0; k < RUNS; k++) {

			final long start = System.nanoTime();

			for (int i = 0; i < PAUSE_AFTER_NUM_PACKETS; i++) {
				final DrawingObject d = dobs.get(i);

				// move the node to a new position
				final int x = r.nextInt(1000);
				final int y = r.nextInt(1000);
				d.setPosition(new AbsolutePosition(x, y, 0));
			}

			final long stop = System.nanoTime();
			log.warn(String.format("%s: Time needed for moving 1000 nodes: %f ms", layer.getClass().getSimpleName(), (stop - start) / 1000000d));

		}

		log.warn(String.format("Items in the layer: %d", layer.getDrawingObjects().size()));
	}

}
