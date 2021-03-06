/*--------------------------------------------------------------import org.eclipse.core.databinding.conversion.Converter;
WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de)
 * project SpyGlass is free software; you can redistribute it and/or modify it under the terms of
 * the BSD License. Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for
 * further details.
 * ---------------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.gui.databinding.converter;

import org.eclipse.core.databinding.conversion.Converter;

/**
 * Converts an array of integers into an String representing this integer list.
 * 
 * The integers should be non-negative.
 * 
 * @author Dariush Forouher
 * 
 */
public class IntListToStringConverter extends Converter {

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	public IntListToStringConverter() {
		super(new int[0], "");
	}

	// --------------------------------------------------------------------------------
	@Override
	public Object convert(final Object fromObject) {
		if (fromObject instanceof int[]) {
			final int[] a = (int[]) fromObject;

			if (a.length == 0) {
				return "";
			} else if (a.length == 1) {
				return "" + a[0];
			}

			String s = "" + a[0];

			int iMinus = a[0];

			boolean list = false;
			// start at 2nd entry
			for (int i = 1; i < a.length; i++) {

				final int next = a[i];

				final boolean last = a.length == i + 1;

				if (iMinus == next - 1) {
					list = true;
				} else if (list) {
					s += "-" + iMinus + "," + next;
					list = false;
				} else {
					s += "," + next;
					list = false;
				}

				if (list && last) {
					s += "-" + next;
				}

				iMinus = next;
			}

			return s;
		}
		return "";
	}

}
