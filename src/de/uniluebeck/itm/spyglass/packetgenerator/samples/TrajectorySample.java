// --------------------------------------------------------------------------------
/**
 * 
 */
package de.uniluebeck.itm.spyglass.packetgenerator.samples;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.ParseException;
import java.util.ArrayList;

import org.simpleframework.xml.ElementList;

// --------------------------------------------------------------------------------
/**
 * 
 * 3D-Trajectory sample
 * 
 * @author dariush
 *
 */
public class TrajectorySample extends PayloadSample {

	@ElementList
	private ArrayList<TrajectorySection> sections = new ArrayList<TrajectorySection>();
	
	@Override
	public byte[] getBytePayload() throws ParseException {
		
		final ByteBuffer buf = ByteBuffer.allocate(sections.size()*8-2);
		buf.order(ByteOrder.BIG_ENDIAN);
		
		// Now fill the array with data

		for(int i=0; i<sections.size(); i++) {
			final TrajectorySection s = sections.get(i);
			buf.putShort((short) PayloadSample.getRandomIntFromList(s.pos.x));
			buf.putShort((short) PayloadSample.getRandomIntFromList(s.pos.y));
			buf.putShort((short) PayloadSample.getRandomIntFromList(s.pos.z));
			
			// last one is special case: no duration
			if (i<sections.size()-1) {
				buf.putShort((short) PayloadSample.getRandomIntFromList(s.duration));
			}
			
		}
		
		buf.compact();
		
		return buf.array();
	}

}
