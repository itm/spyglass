/*
 * -------------------------------------------------------------------------------- This file is
 * part of the WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet
 * (www.swarmnet.de) project SpyGlass is free software; you can redistribute it and/or modify it
 * under the terms of the BSD License. Refer to spyglass-licence.txt file in the root of the
 * SpyGlass source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.nodepositioner;

import de.uniluebeck.itm.spyglass.plugin.Plugin;
import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;

// --------------------------------------------------------------------------------
/**
 * 
 */
public abstract class NodePositionerPlugin extends Plugin {
	
	// --------------------------------------------------------------------------------
	/**
	 * TODO: if possible, use AbsolutePosition instead?!?
	 */
	public static class Position extends AbsolutePosition {
		
		// --------------------------------------------------------------------------------
		/**
		 * 
		 */
		public Position() {
		}
		
		// --------------------------------------------------------------------------------
		/**
		 * 
		 */
		public Position(final int x, final int y, final int z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
		
		// --------------------------------------------------------------------------------
		/**
		 * 
		 */
		public Position(final int x, final int y) {
			this(x, y, 0);
		}
		
		// --------------------------------------------------------------------------------
		/**
		 * 
		 */
		@Override
		public String toString() {
			return "[" + x + ", " + y + ", " + z + "]";
		}
		
		// --------------------------------------------------------------------------------
		/**
		 * 
		 */
		public Position mult(final double d) {
			return new Position((int) (x * d), (int) (y * d), (int) (z * d));
		}
		
		// --------------------------------------------------------------------------------
		/**
		 * 
		 */
		public Position add(final Position p) {
			return new Position(x + p.x, y + p.y, z + p.z);
		}
		
		// --------------------------------------------------------------------------------
		/**
		 * 
		 */
		@Override
		public Position clone() {
			return new Position(this.x, this.y, this.z);
		}
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param nodeId
	 * @return Position of the node with the supplied nodeID
	 */
	public abstract Position getPosition(int nodeId);
	
	// --------------------------------------------------------------------------------
	/**
	 * @return
	 */
	public abstract int getNumNodes();
	
	// --------------------------------------------------------------------------------
	/**
	 * @return
	 */
	public abstract boolean offersMetric();
	
	public static String getHumanReadableName() {
		return "NodePositioner";
	}
	
	@Override
	public String toString() {
		return NodePositionerPlugin.getHumanReadableName() + "." + this.getName();
	}
	
}
