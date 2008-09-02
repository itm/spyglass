package de.uniluebeck.itm.spyglass.gui.converter;

import java.util.TreeSet;

import org.eclipse.core.databinding.conversion.IConverter;

/**
 * Converts a String representing a list of integers into an actual array.
 * 
 * @author Dariush Forouher
 * 
 */
public class StringToIntListConverter implements IConverter {
	
	@Override
	public Object convert(final Object fromObject) {
		
		final String s = (String) fromObject;
		
		final String[] parts = s.split(",");
		final TreeSet<Integer> set = new TreeSet<Integer>();
		
		for (final String p : parts) {
			if (p.matches("\\d+")) {
				final int i = Integer.parseInt(p);
				set.add(i);
			} else if (p.matches("\\d+-\\d+")) {
				final String[] q = p.split("-");
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
	
	@Override
	public Object getFromType() {
		return "";
	}
	
	@Override
	public Object getToType() {
		return new int[0];
	}
	
}
