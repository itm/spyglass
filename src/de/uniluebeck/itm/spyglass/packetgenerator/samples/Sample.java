package de.uniluebeck.itm.spyglass.packetgenerator.samples;

import java.text.ParseException;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * A sample is the abstract representation of a packet. it can be serialized to
 * a byte array which holds to the specification of an Spyglass packet.
 * 
 * @author dariush
 * 
 */
@Root
public abstract class Sample {
	
	/**
	 * fixed size of the packet header.
	 */
	public final short HEADER_SIZE = 19;
	
	/**
	 * Version of the packet specification
	 */
	public final byte VERSION = 2;
	
	/**
	 * Name of this sample. Only for user convenience, is not actually used.
	 */
	@Element
	private String name;
	
	/**
	 * probabiliry, that this sample is selected. must be between 0 and 1.
	 */
	@Element
	private double probability;
	
	/**
	 * Converts this sample to an byte array representing a specific packet.
	 * During this process a random generator might fill out various fields in
	 * the packet, so call to this method might generate a different packet.
	 * 
	 * details depend on the specific implementing subclass.
	 * 
	 * @return a packet as a byte array.
	 * @throws ParseException
	 *             if parts of the configuration could not be parsed.
	 */
	public abstract byte[] generatePacket() throws ParseException;
	
	@Override
	public String toString() {
		return this.name;
	}
	
	/**
	 * Return the name
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Return the probalibity.
	 * 
	 * @return
	 */
	public double getProbability() {
		return probability;
	}
	
}
