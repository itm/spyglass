/* ----------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass.       
 * Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de) project    
 * SpyGlass is free software; you can redistribute it and/or modify it  
 * under the terms of the BSD License. Refer to spyglass-licence.txt    
 * file in the root of the SpyGlass source tree for further details.  
------------------------------------------------------------------------*/
package de.uniluebeck.itm.spyglass.plugin;

// --------------------------------------------------------------------------------
/**
 * 
 */
public abstract class NodePositionerPlugin extends Plugin {

	// --------------------------------------------------------------------------------
	/**
	 *
	 */
	public static class Position {
		public double x;

		public double y;

		public double z;

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
		public Position(double x, double y, double z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}

		// --------------------------------------------------------------------------------
		/**
		 *
		 */
		public Position(double x, double y) {
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
		public Position mult(double d) {
			return new Position(x*d, y*d, z*d);
		}

		// --------------------------------------------------------------------------------
		/**
		 *
		 */
		public Position add(Position p) {
			return new Position(x + p.x, y + p.y, z + p.z);
		}

		// --------------------------------------------------------------------------------
		/**
		 *
		 */
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

	public int getNumNodes(){
		return 0;
	}

	public boolean offersMetric(){
		return false;
	}

}
