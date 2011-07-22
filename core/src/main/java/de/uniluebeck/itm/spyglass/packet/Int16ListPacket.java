/*
 * --------------------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass.
 * Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de) project SpyGlass is free
 * software; you can redistribute it and/or modify it under the terms of the BSD License.
 * Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for further
 * details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.packet;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;

/**
 * Represents a Packet with the syntaxtype Int16ListPacket
 * 
 * @author Nils Glombitza, ITM Uni Luebeck
 * @author Dariush Forouher
 */
public class Int16ListPacket extends IntListPacket {

	public Int16ListPacket() {}
	
	public Int16ListPacket(final byte[] buf) throws SpyglassPacketException {
		deserialize(buf);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Instances of this class are sections in a trajectory
	 * 
	 */
	public class TrajectorySection {
		/** The trajectory's starting point */
		public AbsolutePosition start;
		/** The trajectory's ending point */
		public AbsolutePosition end;
		/** The trajectory's duration between starting and ending point */
		public int duration;
	}

	/**
	 * Syntaxtype of this Packet
	 */
	public static final SyntaxTypes SYNTAXTYPE = SyntaxTypes.ISENSE_SPYGLASS_PACKET_INT16;

	/**
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @see SpyglassPacket#deserialize(byte[])
	 */
	@Override
	void deserialize(final byte[] buf) throws SpyglassPacketException {
		super.deserialize(buf);
		if (getSyntaxType() != Int16ListPacket.SYNTAXTYPE) {
			throw new SpyglassPacketException("Wrong Syntaxtype");
		}
		values = new Integer[(buf.length - SpyglassPacket.EXPECTED_PACKET_SIZE) / 2];
		for (int i = 0; (i * 2 + SpyglassPacket.EXPECTED_PACKET_SIZE) < buf.length; i++) {
			final int pos = i * 2 + SpyglassPacket.EXPECTED_PACKET_SIZE;
			values[i] = deserializeInt16(buf[pos], buf[pos + 1]);
		}
	}

	/**
	 * Interprets the payload as a list of 3D coordinates and returns them as a list.
	 * 
	 * Note: The packet itself has no intrinsic knowledge if it is in fact a coordinates list
	 * packet. It is up to the caller to guess the type of the payload (e.g. by looking at the
	 * semantic type).
	 * 
	 * @throws ParseException
	 *             when the payload has a wrong length to be such a packet.
	 * @return a list of coordinates.
	 */
	public List<AbsolutePosition> getCoordinates3D() throws ParseException {
		final List<AbsolutePosition> ret = new ArrayList<AbsolutePosition>();

		if (values.length % 3 != 0) {
			throw new ParseException("The payload has a wrong length (is it really a 3D coordinates list?)", -1);
		}

		for (int i = 0; i < values.length; i = i + 3) {
			final AbsolutePosition p = new AbsolutePosition();
			p.x = values[i];
			p.y = values[i + 1];
			p.z = values[i + 2];
			ret.add(p);
		}

		return ret;
	}

	/**
	 * Interprets the payload as a list of 2D coordinates and returns them as a list.
	 * 
	 * Note: The packet itself has no intrinsic knowledge if it is in fact a coordinates list
	 * packet. It is up to the caller to guess the type of the payload (e.g. by looking at the
	 * semantic type).
	 * 
	 * @throws ParseException
	 *             when the payload has a wrong length to be such a packet.
	 * @return a list of coordinates.
	 */
	public List<AbsolutePosition> getCoordinates2D() throws ParseException {
		final List<AbsolutePosition> ret = new ArrayList<AbsolutePosition>();

		if (values.length % 2 != 0) {
			throw new ParseException("The payload has a wrong length (is it really a 2D coordinates list?)", -1);
		}

		for (int i = 0; i < values.length; i = i + 2) {
			final AbsolutePosition p = new AbsolutePosition();
			p.x = values[i];
			p.y = values[i + 1];
			ret.add(p);
		}

		return ret;
	}

	/**
	 * Interprets the payload as a list of 3D trajectory packet and returns them as a list.
	 * 
	 * Note: The packet itself has no intrinsic knowledge if it is in fact such a packet. It is up
	 * to the caller to guess the type of the payload (e.g. by looking at the semantic type).
	 * 
	 * @throws ParseException
	 *             when the payload has a wrong length to be such a packet.
	 * @return a list of trajectory sections.
	 */
	public List<TrajectorySection> getTrajectory3D() throws ParseException {
		final List<TrajectorySection> ret = new ArrayList<TrajectorySection>();

		if ((values.length < 7) || ((values.length - 3) % 4 != 0)) {
			throw new ParseException("The payload has a wrong length (is it really a 3D trajectory packet?)", -1);
		}

		for (int i = 0; i < values.length - 3; i = i + 4) {
			final TrajectorySection s = new TrajectorySection();
			final AbsolutePosition start = new AbsolutePosition();
			start.x = values[i];
			start.y = values[i + 1];
			start.z = values[i + 2];
			s.start = start;

			s.duration = values[i + 3];

			final AbsolutePosition end = new AbsolutePosition();
			end.x = values[i + 4];
			end.y = values[i + 5];
			end.z = values[i + 6];
			s.end = end;

			ret.add(s);
		}

		return ret;
	}

	/**
	 * Interprets the payload as a list of 2D trajectory packet and returns them as a list.
	 * 
	 * Note: The packet itself has no intrinsic knowledge if it is in fact such a packet. It is up
	 * to the caller to guess the type of the payload (e.g. by looking at the semantic type).
	 * 
	 * @throws ParseException
	 *             when the payload has a wrong length to be such a packet.
	 * @return a list of trajectory sections.
	 */
	public List<TrajectorySection> getTrajectory2D() throws ParseException {
		final List<TrajectorySection> ret = new ArrayList<TrajectorySection>();

		if ((values.length < 5) || ((values.length - 2) % 3 != 0)) {
			throw new ParseException("The payload has a wrong length (is it really a 2D trajectory packet?)", -1);
		}

		for (int i = 0; i < values.length - 2; i = i + 3) {
			final TrajectorySection s = new TrajectorySection();
			final AbsolutePosition start = new AbsolutePosition();
			start.x = values[i];
			start.y = values[i + 1];
			s.start = start;

			s.duration = values[i + 2];

			final AbsolutePosition end = new AbsolutePosition();
			end.x = values[i + 3];
			end.y = values[i + 4];
			s.end = end;

			ret.add(s);
		}

		return ret;
	}
}
