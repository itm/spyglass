package de.uniluebeck.itm.spyglass.core;

import org.apache.log4j.Category;

import de.uniluebeck.itm.spyglass.packet.Packet;
import de.uniluebeck.itm.spyglass.plugin.PluginManager;
import de.uniluebeck.itm.spyglass.util.SpyglassLogger;

public class PacketDispatcher {
	
	private static Category log = SpyglassLogger.get(InformationDispatcher.class);
	private final PluginManager pluginManager = null;
	
	public PacketDispatcher() {
		
	}
	
	@Override
	public void finalize() throws Throwable {
		
	}
	
	/**
	 * Constructor.
	 * 
	 * @param pluginManager
	 *            The plugin manager object that is being used to get all loaded
	 *            plugins.
	 */
	public PacketDispatcher(final PluginManager pluginManager) {
		
	}
	
	/**
	 * This method distributes the given Packet object to all loaded plugins by
	 * invoking the <code>handlePacket</code> method of a plugin.
	 * 
	 * @param packet
	 *            The packet object to be distributed.
	 */
	public void dispatchPacket(final Packet packet) {
		
	}
	
}