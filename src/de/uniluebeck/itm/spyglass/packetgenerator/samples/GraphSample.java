package de.uniluebeck.itm.spyglass.packetgenerator.samples;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.ParseException;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.load.Commit;

import de.uniluebeck.itm.spyglass.plugin.mappainter.AbstractDataStore;
import de.uniluebeck.itm.spyglass.plugin.mappainter.MetricPoint;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;


/**
 * This type of sample allows abstract specifications of all headerelements. the payload must be
 * supplied as a hex string.
 *
 * @author dariush
 */
public class GraphSample extends PayloadSample {

	private static Logger log = SpyglassLoggerFactory.getLogger(GraphSample.class);

	private final Random r = new Random();

	private GraphStore graph = new GraphStore();

	@Element(required=false)
	private int numberOfNodes = 100;

	@Element(required=false)
	private int neighborCount = 5;

	/**
	 * the velocity of the nodes.
	 */
	@Element(required=false)
	private Position velocity = new Position("0","0","0");

	// --------------------------------------------------------------------------------
	/**
	 * @author dariush
	 *
	 */
	public final class GraphStore extends AbstractDataStore<NodePoint> {
		public NodePoint getRandomElement() {

			final int ele = r.nextInt(this.size());
			final Iterator<NodePoint> it = iterator();
			NodePoint returnElement = it.next();
			for(int i=0; i<ele-1; i++) {
				returnElement = it.next();
			}

			return returnElement;
		}
	}

	public class NodePoint extends MetricPoint {

		short nodeID;
		byte syntaxType;
		byte semanticType;

		// --------------------------------------------------------------------------------
		/* (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(final MetricPoint o) {
			if (o instanceof NodePoint) {
				return nodeID-((NodePoint)o).nodeID;
			} else {
				return 0;
			}
		}
	}

	private Runnable moveDaemon = new Runnable() {

		@Override
		public void run() {
			log.info("Started moving daemon...");
			try {
				while (!Thread.currentThread().isInterrupted()) {
					synchronized (graph) {
						final NodePoint p = graph.getRandomElement();
						final int deltaX = getRandomIntFromList(velocity.x);
						final int deltaY = getRandomIntFromList(velocity.y);
						final int deltaZ = getRandomIntFromList(velocity.z);
						p.position.x += deltaX;
						p.position.y += deltaY;
						p.position.z += deltaZ;

					}
					Thread.sleep(1000/numberOfNodes);
				}
			} catch (final ParseException e) {
				log.error("Could not parse int-list",e);
				return;
			} catch (final InterruptedException e) {
				log.error("Got interrupted",e);
				Thread.currentThread().interrupt();
				return;
			}

		}

	};

	@Commit
	public void commit() throws ParseException {

		log.debug("Generating graph");
		synchronized (graph) {
			for(int i=0; i<numberOfNodes; i++) {
				final NodePoint p = new NodePoint();
				p.nodeID = (short) i;
				p.semanticType = this.getRandomSemanticType();
				p.syntaxType = this.getByteSyntaxType();
				p.position = this.getRandomPosition();
				graph.add(p);
			}
		}

		final Thread t = new Thread(moveDaemon);
		t.setDaemon(true);
		t.start();
	}

	// --------------------------------------------------------------------------------
	/* (non-Javadoc)
	 * @see de.uniluebeck.itm.spyglass.packetgenerator.samples.Sample#generatePacket()
	 */
	@Override
	public byte[] generatePacket() throws ParseException {
		synchronized (graph) {
			final NodePoint p = graph.getRandomElement();

			final byte[] payload = getBytePayload(p);

			return generatePacketInternal(p.nodeID, p.syntaxType, p.semanticType, p.position, payload);
		}

	}

	// --------------------------------------------------------------------------------
	/**
	 * @param numberOfNodes the numberOfNodes to set
	 */
	public void setNumberOfNodes(final int numberOfNodes) {
		this.numberOfNodes = numberOfNodes;
	}

	// --------------------------------------------------------------------------------
	/**
	 * @return the numberOfNodes
	 */
	public int getNumberOfNodes() {
		return numberOfNodes;
	}

	public byte[] getBytePayload(final NodePoint p) throws ParseException {

		final ByteBuffer buf = ByteBuffer.allocate(getNeighborCount()*2);
		buf.order(ByteOrder.BIG_ENDIAN);

		// Now fill the array with data
		synchronized (graph) {
			final List<NodePoint> list = graph.kNN(p.position, getNeighborCount()+1);
			NodePoint p2 = list.get(r.nextInt(list.size()));
			while(p==p2) {
				p2 = list.get(r.nextInt(list.size()));
			}
			// first entry is the node id
			buf.putShort(p2.nodeID);
			// second is the distance between the two nodes (as a float)
			final float distance = (float)p2.position.getEuclideanDistance(p.position);
			buf.putFloat(distance);
		}

		buf.compact();

		return buf.array();
	}

	// --------------------------------------------------------------------------------
	/**
	 * @return the k
	 */
	public int getNeighborCount() {
		return neighborCount;
	}

	// --------------------------------------------------------------------------------
	/**
	 * @param k the k to set
	 */
	public void setNeighborCount(final int k) {
		neighborCount = k;
	}
}
