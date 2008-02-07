/* ----------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass.       
 * Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de) project    
 * SpyGlass is free software; you can redistribute it and/or modify it  
 * under the terms of the BSD License. Refer to spyglass-licence.txt    
 * file in the root of the SpyGlass source tree for further details.  
------------------------------------------------------------------------*/
package de.uniluebeck.itm.spyglass.util;

// --------------------------------------------------------------------------------
/** Helper class to deal with time differences. The object can be "touched" and similar to the touch command,
 * it updates its internal timestamp to the current time. The difference between this "touch"-time and the current
 * time can be queried with different units (milliseconds, seconds, etc.). In addition, a timeout value can be specified in
 * milliseconds. Whenever the difference between the "touch"-time and the current time is greater than this time out value,
 * isTimeout() returns true
 */
public class TimeDiff {
	private long lastTouch = System.currentTimeMillis();

	private long timeOutMillis = 0;

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public TimeDiff() {
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public TimeDiff(long timeOutMillis) {
		setTimeOutMillis(timeOutMillis);
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public void touch() {
		lastTouch = System.currentTimeMillis();
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public long absoluteMillis() {
		return lastTouch;
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public boolean isTimeout() {
		return ms() >= timeOutMillis;
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public boolean noTimeout() {
		return !isTimeout();
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public void setTimeOutMillis(long timeOutMillis) {
		this.timeOutMillis = timeOutMillis;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public long ms() {
		return System.currentTimeMillis() - lastTouch;
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public long s() {
		return Math.round(((double) ms()) / 1000.0);
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public long m() {
		return Math.round(((double) s()) / 60.0);
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public long h() {
		return Math.round(((double) m()) / 60.0);
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public long d() {
		return Math.round(((double) h()) / 24.0);
	}

}