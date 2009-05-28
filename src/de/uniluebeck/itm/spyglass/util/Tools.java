/*
 * ---------------------------------------------------------------------- This file is part of the
 * WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de)
 * project SpyGlass is free software; you can redistribute it and/or modify it under the terms of
 * the BSD License. Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for
 * further details. ------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.util;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

// ------------------------------------------------------------------------------
/**
 * Convenience tools
 */
public class Tools {

	private static Logger log = SpyglassLoggerFactory.getLogger(Tools.class);

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

	// --------------------------------------------------------------------------------
	/**
	 * Returns if a file is writable
	 * 
	 * @param file
	 *            the file to be checked
	 * @param create
	 *            indicates whether the file is to be created if it does not exist, yet
	 * @return <code>true</code> if the file is writable, <code>false</code> otherwise
	 */
	public static boolean isWritable(final File file, final boolean create) {
		// check if it is a file at all (this should always be the case but we want to make sure)
		if (!file.exists()) {
			try {
				if (create) {
					file.createNewFile();
				}
			} catch (final IOException e) {
				log.error("The recording file could not be created", e);
			}
		}
		if (!file.isFile()) {
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					MessageDialog.openError(null, "Invalid file", "No valid file for recording specified.\r\nPlease choose a different one");
				}
			});
			return false;
		}

		// if it is a file, it has to be writable
		else if (!file.canWrite()) {
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					MessageDialog.openError(null, "Write protection activated",
							"The file cannot be written.\r\nPlease disable write protection or choose a different file!");
				}
			});
			return false;
		}
		return true;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Converts a decimal value to hex
	 * 
	 * @param dec
	 *            the decimal value to be converted
	 * @return the converted decimal value
	 */
	public static String convertDecToHex(final double dec) {

		final String[] cArr = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F" };

		int number = (int) dec;
		double frac = dec - number;

		String result = "";
		if (number == 0) {
			result = "0";
		} else {
			while (number > 0) {
				result = cArr[(number % 16)] + result;
				number /= 16;
			}
		}

		if (frac != 0) {
			result += ".";
			int i = 10;
			while ((frac > 0) && (i-- != 0)) {
				final double tmp = frac * 16;
				result += cArr[((int) tmp)];
				frac = (tmp - (int) tmp);
			}
		}

		return result;

	}

	// --------------------------------------------------------------------------------
	/**
	 * Converts a hex value to decimal
	 * 
	 * @param hex
	 *            the hex value to be converted
	 * @return the converted hex value
	 */
	public static double convertHexToDec(final String hex) {

		final String characters = "0123456789ABCDEF";

		final String[] val = hex.replace(".", ":").split(":");
		final String pre = val[0];

		long number = 0;
		for (int i = 0; i < pre.length(); i++) {
			number = (number * 16) + characters.indexOf(pre.charAt(i));
		}

		double frac = 0;
		if (val.length == 2) {
			final String post = val[1];
			for (int i = post.length() - 1; i >= 0; i--) {
				frac = (frac / 16) + characters.indexOf(post.charAt(i));
			}
			frac /= 16;
		}

		return number + frac;
	}
}
