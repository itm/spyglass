/*
 * ---------------------------------------------------------------------- This file is part of the
 * WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de)
 * project SpyGlass is free software; you can redistribute it and/or modify it under the terms of
 * the BSD License. Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for
 * further details. ------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

// ------------------------------------------------------------------------------
// --
/**
 * Convenience tools
 */
public class Tools {
	private static Logger log = SpyglassLoggerFactory.getLogger(Tools.class);

	// -------------------------------------------------------------------------
	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public static Vector<String> getExternalHostIps() {
		final HashSet<String> ips = new HashSet<String>();
		final Vector<String> external = new Vector<String>();

		try {
			InetAddress i;
			NetworkInterface iface = null;

			for (final Enumeration ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements();) {
				iface = (NetworkInterface) ifaces.nextElement();
				for (final Enumeration ifaceips = iface.getInetAddresses(); ifaceips.hasMoreElements();) {
					i = (InetAddress) ifaceips.nextElement();
					if (i instanceof Inet4Address) {
						ips.add(i.getHostAddress());
					}
				}
			}

		} catch (final Throwable t) {
			log.error("Unable to retrieve external ips: " + t, t);

			try {
				log.debug("Trying different lookup scheme");

				final InetAddress li = InetAddress.getLocalHost();
				final String strli = li.getHostName();
				final InetAddress ia[] = InetAddress.getAllByName(strli);
				for (int i = 0; i < ia.length; i++) {
					ips.add(ia[i].getHostAddress());
				}
			} catch (final Throwable t2) {
				log.error("Also unable to retrieve external ips: " + t2, t2);
			}

		}

		for (final String ip : ips) {
			if (isExternalIp(ip)) {
				log.debug("Found external ip: " + ip);
				external.add(ip);
			}
		}

		return external;
	}

	// -------------------------------------------------------------------------
	/**
	 * 
	 */
	public static String getFirstLocalIp() {
		final Vector<String> ips = getExternalHostIps();
		String localHost = "127.0.0.1";

		for (final String ip : ips) {
			if (!"127.0.0.1".equals(ip)) {
				localHost = new String(ip);
			}
		}

		return localHost;
	}

	// -------------------------------------------------------------------------
	/**
	 * <pre>
	 * - 127.0.0.0 - 127.255.255.255 (localhost) 
	 * - 10.0.0.0 - 10.255.255.255 (10/8 prefix) 
	 * - 172.16.0.0 - 172.31.255.255 (172.16/12 prefix) 
	 * - 192.168.0.0 - 192.168.255.255 (192.168/16 prefix)
	 * </pre>
	 */
	public static boolean isExternalIp(final String ip) {
		boolean external = true;

		if (ip == null) {
			external = false;
		} else if (ip.startsWith("127.")) {
			external = false;
		} else if (ip.startsWith("10.")) {
			external = false;
		} else if (ip.startsWith("192.168.")) {
			external = false;
		}

		for (int i = 16; i <= 31; ++i) {
			if (ip.startsWith("172." + i + ".")) {
				external = false;
			}
		}

		log.debug("IP " + ip + " is an " + (external ? "external" : "internal") + " address");
		return external;
	}

	// -------------------------------------------------------------------------
	/**
	 * @param millis
	 */
	public static void sleep(final long millis) {
		try {
			Thread.sleep(millis);
		} catch (final Throwable e) {
		}
	}

	// -------------------------------------------------------------------------
	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public static String toString(final Collection l) {
		return toString(l, ", ");
	}

	// -------------------------------------------------------------------------
	/**
	 * 
	 */
	public static String toString(final short[] l, final int offset, final int length) {
		final LinkedList<Short> ll = new LinkedList<Short>();
		for (int i = offset; i < offset + length; ++i) {
			ll.add(l[i]);
		}

		return toString(ll, ", ");
	}

	// -------------------------------------------------------------------------
	/**
	 * 
	 */
	public static String toString(final byte[] l) {
		if (l == null) {
			return "";
		}
		return toString(l, 0, l.length);
	}

	// -------------------------------------------------------------------------
	/**
	 * 
	 */
	public static String toString(final byte[] l, final int offset, final int length) {
		if (l == null) {
			return "";
		}

		final LinkedList<String> ll = new LinkedList<String>();
		for (int i = offset; i < offset + length; ++i) {
			ll.add(toHexString(l[i]));
		}

		return toString(ll, ", ");
	}

	// -------------------------------------------------------------------------
	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public static String toString(final Collection l, final String divider) {
		final StringBuffer b = new StringBuffer();

		if (l == null) {
			return "<null>";
		}

		for (final Object o : l) {
			final String t = o != null ? o.toString() : "{null}";
			if (b.length() > 0) {
				b.append(divider);
			}

			b.append(t);
		}

		return b.toString().trim();
	}

	// -------------------------------------------------------------------------
	/**
	 * 
	 */
	public static String toHexString(final char tmp) {
		return toHexString((byte) tmp);
	}

	// -------------------------------------------------------------------------
	/**
	 * 
	 */
	public static String toHexString(final byte tmp) {
		return "0x" + Integer.toHexString(tmp & 0xFF);
	}

	// -------------------------------------------------------------------------
	/**
	 * 
	 */
	public static String toHexString(final byte[] tmp) {
		return toHexString(tmp, 0, tmp.length);
	}

	// -------------------------------------------------------------------------
	/**
	 * 
	 */
	public static String toHexString(final int i) {
		String tmp = "";
		if (i > 0xFF) {
			tmp += toHexString((byte) (i >> 8 & 0xFF)) + " ";
		} else {
			tmp += "    ";
		}

		tmp += toHexString((byte) (i & 0xFF));

		return tmp;
	}

	// -------------------------------------------------------------------------
	/**
	 * 
	 */
	public static String toHexString(final long i) {
		String tmp = "";

		if (i > 0xFF) {
			tmp += toHexString((byte) (i >> 24 & 0xFF)) + ":";
		}
		if (i > 0xFF) {
			tmp += toHexString((byte) (i >> 16 & 0xFF)) + ":";
		}
		if (i > 0xFF) {
			tmp += toHexString((byte) (i >> 8 & 0xFF)) + ":";
		}

		tmp += toHexString((byte) (i & 0xFF));

		return tmp;
	}

	// -------------------------------------------------------------------------
	/**
	 * 
	 */
	public static String toHexString(final byte[] tmp, final int offset) {
		return toHexString(tmp, offset, tmp.length - offset);
	}

	// -------------------------------------------------------------------------
	/**
	 * 
	 */
	public static String toHexString(final byte[] tmp, final int offset, final int length) {
		final StringBuffer s = new StringBuffer();
		for (int i = offset; i < offset + length; ++i) {
			if (s.length() > 0) {
				s.append(' ');
			}
			s.append("0x");
			s.append(Integer.toHexString(tmp[i] & 0xFF));
		}
		return s.toString();
	}

	// -------------------------------------------------------------------------
	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public static String toString(final HashMap m) {
		if ((m == null) || (m.size() == 0)) {
			return "";
		}

		final StringBuffer s = new StringBuffer();
		for (final Object o : m.keySet()) {
			if (s.length() > 0) {
				s.append(", ");
			}
			s.append(o.toString());
			s.append("=");
			s.append(m.get(o));
		}
		return s.toString();
	}

	// -------------------------------------------------------------------------
	/**
	 * 
	 */
	public static int toInteger(final String s, final int defaultValue) {
		int retVal = defaultValue;

		try {
			retVal = Integer.parseInt(s);
		} catch (final Throwable t) {
		}

		return retVal;
	}

	// -------------------------------------------------------------------------
	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public static String toString(final Iterator it) {
		final LinkedList l = new LinkedList();
		while (it.hasNext()) {
			l.add(it.next());
		}
		return toString(l);
	}

	// -------------------------------------------------------------------------
	/**
	 * @return
	 */
	public static StackTraceElement[] getStackTrace() {
		final Throwable ex = new Throwable();
		return ex.getStackTrace();
	}

	// -------------------------------------------------------------------------
	/**
	 * @return
	 */
	public static String toString(final StackTraceElement[] trace) {
		final StringBuffer s = new StringBuffer();
		for (final StackTraceElement t : trace) {
			s.append(t.toString());
			s.append('\n');
		}
		return s.toString();
	}

	// -------------------------------------------------------------------------
	/**
	 * 
	 */
	public static String uppercaseFirst(final String s) {
		if ((s == null) || (s.length() <= 0)) {
			return "";
		}

		String result = s.substring(0, 1).toUpperCase();

		if (s.length() > 1) {
			result += s.substring(1);
		}

		return result;
	}

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
