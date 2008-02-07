/* ----------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass.       
 * Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de) project    
 * SpyGlass is free software; you can redistribute it and/or modify it  
 * under the terms of the BSD License. Refer to spyglass-licence.txt    
 * file in the root of the SpyGlass source tree for further details.  
------------------------------------------------------------------------*/
package de.uniluebeck.itm.spyglass.packet;

import org.eclipse.swt.graphics.Point;

/**
 * The class represents a data packet. Every packet has at least a unique id and a position.
 */
public class Packet {
	private int id = 0;

	private Point position = new Point(0, 0);

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public Point getPosition() {
		return position;
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public void setPosition(Point position) {
		this.position = position;
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public int getId() {
		return id;
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public void setId(int id) {
		this.id = id;
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result + id;

		return result;
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null)
			return false;

		if (getClass() != obj.getClass())
			return false;

		final Packet other = (Packet) obj;

		if (id != other.id)
			return false;

		return true;
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	@Override
	public String toString() {
		return "Packet [id=" + id + ", position=[" + position.x + "," + position.y + "]]";
	}

}
