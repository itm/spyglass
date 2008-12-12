// --------------------------------------------------------------------------------
/**
 * 
 */
package de.uniluebeck.itm.spyglass.packetgenerator.samples;

import org.simpleframework.xml.Element;

public class TrajectorySection {
	
	@Element
	public Position pos;
	
	@Element
	public String duration;
}