/*
 * ---------------------------------------------------------------------- This file is part of the
 * WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de)
 * project SpyGlass is free software; you can redistribute it and/or modify it under the terms of
 * the BSD License. Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for
 * further details. ------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.util;

import java.util.LinkedList;
import java.util.List;

// ------------------------------------------------------------------------------
/**
 * Convenience tools
 */
public class Tools {

	// --------------------------------------------------------------------------------
	/**
	 * Converts an array of <code>int</code> values to a list of {@link Integer} values.
	 *
	 * @param intList
	 *            the array of <code>int</code> values.
	 * @return a list of {@link Integer} values
	 */
	public static List<Integer> intArrayToIntegerList(final int[] intList) {
		final List<Integer> list = new LinkedList<Integer>();
		if (intList != null) {
			for (int i = 0; i < intList.length; i++) {
				list.add(intList[i]);
			}
		}
		return list;
	}

}
