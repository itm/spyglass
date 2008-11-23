// --------------------------------------------------------------------------------
/**
 * 
 */
package de.uniluebeck.itm.spyglass.packet;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;

import org.apache.log4j.Logger;
import org.simpleframework.xml.Root;

import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;

// --------------------------------------------------------------------------------
/**
 * Instances of this class are used as packet broker between iShell and Spyglass.<br>
 * iShell can push packets for Spyglass in a queue and Spyglass can read them afterwards.<br>
 * By extending the abstract class {@link PacketReader} Spyglass can use this broker as an ordinary
 * packet reader.
 * 
 * @author Sebastian Ebers
 * @author Dariush Forouher
 */
@Root
public class IShellToSpyGlassPacketBroker extends PacketReader {
	
	/**
	 * The queue where packets provided by iShell are stored
	 */
	private Deque<SpyglassPacket> queue = new ArrayDeque<SpyglassPacket>(50);
	
	private static final Logger log = SpyglassLoggerFactory
			.getLogger(IShellToSpyGlassPacketBroker.class);
	
	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	public IShellToSpyGlassPacketBroker() {
		super();
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 * 
	 * @param queue
	 *            the queue where packets provided by iShell are stored
	 */
	public IShellToSpyGlassPacketBroker(final Deque<SpyglassPacket> queue) {
		this.queue = queue;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Retrieves and removes the last packet of the packet queue, or returns <tt>null</tt> if this
	 * packet queue is empty.
	 * 
	 * @return the tail of the packet queue, or <tt>null</tt> if the packet queue is empty
	 * @exception SpyglassPacketException
	 *                if the packet to return is invalid
	 * @exception InterruptedException
	 *                if the method was interrupted while waiting on a packet.
	 */
	@Override
	public SpyglassPacket getNextPacket() throws SpyglassPacketException, InterruptedException {
		
		synchronized (queue) {
			
			SpyglassPacket packet = null;
			
			// Loop until we have a packet.
			while (packet == null) {
				
				if (queue.isEmpty()) {
					
					// the queue is empty, wait until we have something.
					queue.wait();
					
				}
				
				packet = queue.remove();
				
			}
			
			log.debug("Returning packet from queue");
			return packet;
		}
		
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Inserts the element at the front of the packet queue
	 * 
	 * @param packet
	 *            the packet to push
	 * @throws NullPointerException
	 *             if the specified packet is null
	 */
	public void push(final SpyglassPacket packet) {
		synchronized (queue) {
			log.debug("Push packet into the queue");
			queue.push(packet);
			queue.notifyAll(); // wake up all waiting threads
		}
	}
	
	@Override
	public void reset() throws IOException {
		synchronized (queue) {
			queue.clear();
		}
	}
}
