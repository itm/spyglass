package de.uniluebeck.itm.spyglass.packetgenerator;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import de.uniluebeck.itm.spyglass.packetgenerator.samples.Sample;
import de.uniluebeck.itm.spyglass.packetgenerator.sinks.Sink;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;

/**
 * The Main Generator class. it runs in a loop and creates and distributes packets.
 * 
 * @author dariush
 * 
 */
@Root
public class PacketGenerator {
	
	private static Logger log = SpyglassLoggerFactory.getLogger(PacketGenerator.class);
	
	/**
	 * The generator.
	 */
	@Element
	private final SampleChooser sampleChooser = new SampleChooser();
	
	/**
	 * the number of packets generated per second.
	 */
	@Element
	private int packetsPerSecond = 1;
	
	/**
	 * the list of sinks, to which the packets are send to.
	 */
	@ElementList
	private final ArrayList<Sink> sinks = new ArrayList<Sink>();
	
	/**
	 * this methods sole existence is to convince, that the mentioned variable must not be final!
	 */
	private void methodToWorkaroundEclipse() {
		packetsPerSecond = packetsPerSecond;
	}
	
	/**
	 * Is the generator paused?
	 */
	private volatile boolean paused = false;
	
	/**
	 * When false, then the generator will shutdown within a short amount of time.
	 */
	private volatile boolean running = true;
	
	/**
	 * pause the generator
	 */
	public void pause() {
		log.info("Pausing the packet generator.");
		this.paused = true;
	}
	
	/**
	 * Resume the generator
	 */
	public void resume() {
		log.info("Resuming the packet generator.");
		this.paused = false;
	}
	
	/**
	 * Stopp the generator
	 */
	public void shutdown() {
		log.info("Shutting down the packet generator.");
		this.running = false;
		
	}
	
	/**
	 * @return true if the Generator is currently paused.
	 */
	public boolean isPaused() {
		return this.paused;
	}
	
	/**
	 * Start the generator. this method will not terminate until an error happens or the generator
	 * is killed by calling shutdown().
	 */
	public void run() throws Exception {
		
		log.info("Initalizing PacketGenerator.");
		for (final Sink sink : this.sinks) {
			sink.init();
		}
		
		while (running) {
			if (!paused) {
				final Sample sample = this.sampleChooser.getRandomSample();
				for (final Sink sink : this.sinks) {
					sink.sendPacket(sample.generatePacket());
				}
			}
			try {
				Thread.sleep(1000 / this.packetsPerSecond);
			} catch (final InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		for (final Sink sink : this.sinks) {
			sink.shutdown();
		}
		log.info("PacketGenerator shutted down.");
	}
	
}
