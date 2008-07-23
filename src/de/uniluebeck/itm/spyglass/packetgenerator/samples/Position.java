package de.uniluebeck.itm.spyglass.packetgenerator.samples;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 * A Node Position description.
 * 
 * koordinates can be made up of ranges, like
 * 
 * x: 10-20,40-56,
 * y: 15,16,17
 * z: 0
 * 
 * @author dariush
 *
 */
@Root
public class Position {
	

	/**
	 * x koordinate. 
	 */
	@Attribute
	String x;

	/**
	 * y koordinate. 
	 */
	@Attribute
	String y;

	/**
	 * z koordinate. 
	 */
	@Attribute
	String z;
	
}
