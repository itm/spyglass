package de.uniluebeck.itm.spyglass.gui.databinding.converter;

import java.util.TreeSet;

import org.eclipse.core.databinding.conversion.Converter;

/**
 * Converts a String representing a list of integers into an actual array.
 * 
 * Be warned: The case of ranges with negative integers is totally untested!
 * 
 * @author Dariush Forouher
 * 
 */
public class StringToIntListConverter extends Converter {
	
	public StringToIntListConverter() {
		super("", new int[0]);
	}
	
	@Override
	public Object convert(final Object fromObject) {
		
		final String s = (String) fromObject;
		
		// "-1" is a placeholder for "all plug-ins" so it has to be handled separately
		if (s.equals("-1")) {
			return new int[] { -1 };
		}
		
		final String[] parts = s.split(",");
		final TreeSet<Integer> set = new TreeSet<Integer>();
		
		for (final String p : parts) {
			if (p.matches("-?\\d+")) {
				final int i = Integer.parseInt(p);
				set.add(i);
			} else if (p.matches("-?\\d+--?\\d+")) {
				final String[] q;
				if (p.charAt(0) == '-') {
					q = p.substring(1).split("-", 2);
					// put the removed "-" back
					q[0] = "-" + q[0];
					q[1] = "-" + q[1];
				} else {
					q = p.split("-", 2);
				}
				final int start = Integer.parseInt(q[0]);
				final int stop = Integer.parseInt(q[1]);
				for (int j = start; j <= stop; j++) {
					set.add(j);
				}
			}
		}
		
		final int[] list = new int[set.size()];
		int c = 0;
		for (final Integer integer : set) {
			list[c++] = integer;
		}
		return list;
	}
}
