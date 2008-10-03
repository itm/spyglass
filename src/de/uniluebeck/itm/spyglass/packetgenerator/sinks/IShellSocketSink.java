package de.uniluebeck.itm.spyglass.packetgenerator.sinks;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.simpleframework.xml.Element;

import de.uniluebeck.itm.spyglass.util.SpyglassLogger;

/**
 * This Sink sends packets over an TCP-Socket via the protocol specified by iShell.
 * 
 * Ishell should be able to receive this packets and forward it to the Spyglass plugin.
 * 
 * @author dariush
 * 
 */
public class IShellSocketSink extends Sink {
	
	private static Logger log = SpyglassLogger.getLogger(IShellSocketSink.class);
	
	/**
	 * Magic byte for the iShell communication protocol.
	 */
	private final byte PROTOCOL_MAGIC_STX = 0x02;
	
	/**
	 * Magic byte for the iShell communication protocol.
	 */
	private final byte PROTOCOL_MAGIC_ETX = 0x03;
	
	/**
	 * Magic byte for the iShell communication protocol.
	 */
	private final byte PROTOCOL_MAGIC_DLE = 0x10;
	
	/**
	 * The packet type designated to spyglass packets.
	 */
	private final byte ISHELL_PACKET_TYPE_SPYGLASS = (byte) 0x91;
	
	/**
	 * The packet type designated to spyglass packets.
	 */
	private final byte PROTOCOL_MAGIC_MESSAGE_TYPE = (byte) 0x0;
	
	/**
	 * 
	 * The interfaces to bind on (0.0.0.0) for all ifs.
	 */
	@Element
	private String ip;
	
	/**
	 * The port to bind on
	 */
	@Element
	private int port;
	
	/**
	 * The source node, from which the packet comes from. Note that this node ID is only used by
	 * iShell is of no interest to Spyglass.
	 */
	@Element
	private final short iShellSourceNode = 1;
	
	/**
	 * The listenSocket.
	 */
	private ServerSocket listenSocket;
	
	/**
	 * List of all currently connected clients on the socket. serialized since the socketlistener
	 * may add new clients to this list asynchronously.
	 */
	private final List<Socket> clients = Collections.synchronizedList(new ArrayList<Socket>());
	
	/**
	 * Reference to the listenerThread.
	 */
	private Thread listenerThread;
	
	/**
	 * true as long as the socket shall remain open.
	 */
	private volatile boolean alive = true;
	
	/**
	 * This Thread listens on the socket and opens connections to new clients if they arrive.
	 * 
	 * @author dariush
	 * 
	 */
	private class SocketListener extends Thread {
		
		@Override
		public void run() {
			log.info("Server listening at port " + port);
			
			while (alive) {
				try {
					final Socket clientSocket = listenSocket.accept();
					log.debug("New client");
					synchronized (clients) {
						clients.add(clientSocket);
					}
				} catch (final IOException e) {
					// Only whine about it if this was unexpected.
					if (alive == true) {
						log.error("Could not access the socket", e);
					}
				}
			}
			log.info("SockenListener Thread terminated.");
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uniluebeck.itm.spyglass.packetgenerator.Sink#sendPacket(byte[])
	 */
	@Override
	public void sendPacket(final byte[] packet) throws Exception {
		
		if (this.clients.size() > 0) {
			log.debug("Sending a packet...");
		}
		
		// Iterate over all clients and send each the packet.
		synchronized (clients) {
			final Iterator<Socket> it = this.clients.iterator();
			while (it.hasNext()) {
				final Socket s = it.next();
				try {
					sendPacketOverSocket(s, packet);
				} catch (final IOException e) {
					log.debug("Error, socket closed. Dropping client.");
					it.remove();
				}
			}
		}
	}
	
	/**
	 * Takes a Paket and sends it over the given Socket. Before sending the packet is wrapped in
	 * another packet so that iShell can handle it.
	 * 
	 * @param s
	 *            an open socket.
	 * @param packet
	 *            some bytes.
	 * @throws IOException
	 *             if the socket makes problems
	 */
	private void sendPacketOverSocket(final Socket s, final byte[] packet) throws IOException {
		
		log.debug("Sending a packet to " + s);
		
		// enough for the worstcase, isn't usually needed.
		final ByteBuffer buf = ByteBuffer.allocate(2 * packet.length + 20);
		
		buf.order(ByteOrder.BIG_ENDIAN);
		
		// Introduction
		buf.put(PROTOCOL_MAGIC_DLE);
		buf.put(PROTOCOL_MAGIC_STX);
		buf.put(PROTOCOL_MAGIC_MESSAGE_TYPE);
		buf.putShort(iShellSourceNode);
		buf.put(PROTOCOL_MAGIC_DLE);
		buf.put(PROTOCOL_MAGIC_DLE);
		buf.put(PROTOCOL_MAGIC_STX);
		buf.put(ISHELL_PACKET_TYPE_SPYGLASS);
		
		for (int i = 0; i < packet.length; i++) {
			final byte b = packet[i];
			
			// The magic DLE byte needs to be escaped *twice* (yes, four times.
			// this is no bug.)
			if (b == PROTOCOL_MAGIC_DLE) {
				buf.put(b);
				buf.put(b);
				buf.put(b);
				buf.put(b);
			} else {
				buf.put(b);
			}
		}
		
		// Finale
		buf.put(PROTOCOL_MAGIC_DLE);
		buf.put(PROTOCOL_MAGIC_DLE);
		buf.put(PROTOCOL_MAGIC_ETX);
		buf.put(PROTOCOL_MAGIC_DLE);
		buf.put(PROTOCOL_MAGIC_ETX);
		
		// Heartbeat packet: ishell expects a dedicated heartbeat message about once in three
		// seconds
		// for simplicity send it after every real message
		buf.put(PROTOCOL_MAGIC_DLE);
		buf.put(PROTOCOL_MAGIC_STX);
		buf.put((byte) 0x01);
		buf.put(PROTOCOL_MAGIC_DLE);
		buf.put(PROTOCOL_MAGIC_ETX);
		
		s.getOutputStream().write(buf.array(), 0, buf.arrayOffset() + buf.position());
		s.getOutputStream().flush();
	}
	
	@Override
	public void finalize() {
		// Kill the thread.
		this.listenerThread.interrupt();
	}
	
	@Override
	public void init() throws Exception {
		listenSocket = new ServerSocket(port, 0, InetAddress.getByName(ip));
		
		listenerThread = new SocketListener();
		listenerThread.start();
		
	}
	
	@Override
	public void shutdown() {
		alive = false;
		try {
			listenSocket.close();
		} catch (final IOException e1) {
			//
		}
		while (this.listenerThread.isAlive()) {
			try {
				Thread.sleep(100);
			} catch (final InterruptedException e) {
				//
			}
		}
		
	}
}
