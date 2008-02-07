package de.uniluebeck.itm.spyglass.plugin;

import ishell.util.Logging;

import java.util.Random;

import org.apache.log4j.Category;

import de.uniluebeck.itm.spyglass.packet.Packet;

// --------------------------------------------------------------------------------
/**
 * 
 */
public class RandomNodePositioner extends NodePositionerPlugin {
	private static Category log = Logging.get(RandomNodePositioner.class);

	private Random r = new Random();

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	@Override
	public Position getPosition(int nodeId) {
		Position p = new Position(Math.abs(r.nextFloat() * 800), Math.abs(r.nextFloat() * 800));
		log.debug("Random position: " + p);
		return p;
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	@Override
	public void handlePacket(Packet packet) {
		//
	}

}
