/*
 * --------------------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass. Copyright (C)
 * 2004-2007 by the SwarmNet (www.swarmnet.de) project SpyGlass is free
 * software; you can redistribute it and/or modify it under the terms of the BSD
 * License. Refer to spyglass-licence.txt file in the root of the SpyGlass
 * source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.packetgenerator.samples;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Random;
import java.util.TreeSet;

import org.simpleframework.xml.Element;

import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;

/**
 * This type of sample allows abstract specifications of all headerelements. the payload must be
 * supplied as a hex string.
 * 
 * @author dariush
 */
public class PayloadSample extends Sample {

	/**
	 * The syntaxType. See the translate("Pflichtenheft") for a complete list.
	 */
	@Element(required = false)
	private String syntaxType = "int16List";

	/**
	 * a list of semantic types
	 */
	@Element(required = false)
	private String semanticTypes = "1";

	/**
	 * the payload in hex
	 */
	@Element(required = false)
	private String payload = null;

	/**
	 * a list of node ids.
	 */
	@Element(required = false)
	private String nodeIDs = "1";

	/**
	 * the position of the node.
	 */
	@Element(required = false)
	private Position position = new Position("0", "0", "0");

	/**
	 * Optional timestamp of the node if unspecified, current systemtime will be used.
	 */
	@Element(required = false)
	private long timestamp = -1;

	public PayloadSample() {
		super();
	}

	public PayloadSample(final String syntaxType, final String semanticTypes, final String payload, final String nodeIDs, final Position position) {
		super();
		this.syntaxType = syntaxType;
		this.semanticTypes = semanticTypes;
		this.payload = payload;
		this.nodeIDs = nodeIDs;
		this.position = position;
	}

	public String getSyntaxType() {
		return syntaxType;
	}

	public String getSemanticTypes() {
		return semanticTypes;
	}

	public String getPayload() {
		return payload;
	}

	public String getNodeIDs() {
		return nodeIDs;
	}

	public Position getPosition() {
		return position;
	}

	/**
	 * Transforms the Payload from an hex string to an byte array.
	 * 
	 * May be overwritten by any subclass to some fancier thing (like building the payload from an
	 * abstract description)
	 */
	protected byte[] getBytePayload() throws ParseException {
		final int length = this.payload.length() / 2;
		final byte[] array = new byte[length];

		for (int i = 0; i < this.payload.length(); i += 2) {
			final String byteString = payload.substring(i, i + 2);
			array[i / 2] = (byte) Integer.parseInt(byteString, 16);
		}
		return array;
	}

	/**
	 * returns a random x position based on the range given in the config.
	 */
	protected short getRandomXPosition() throws ParseException {
		return (short) PayloadSample.getRandomIntFromList(this.position.x);
	}

	/**
	 * returns a random y position based on the range given in the config.
	 */
	protected short getRandomYPosition() throws ParseException {
		return (short) PayloadSample.getRandomIntFromList(this.position.y);
	}

	/**
	 * returns a random z position based on the range given in the config.
	 */
	protected short getRandomZPosition() throws ParseException {
		return (short) PayloadSample.getRandomIntFromList(this.position.z);
	}

	/**
	 * returns a random semantic type based on the range given in the config.
	 */
	protected byte getRandomSemanticType() throws ParseException {
		return (byte) PayloadSample.getRandomIntFromList(this.semanticTypes);
	}

	/**
	 * returns a random node id based on the range given in the config.
	 */
	protected short getRandomNodeID() throws ParseException {
		return (short) PayloadSample.getRandomIntFromList(this.nodeIDs);
	}

	/**
	 * Parses a Integer-List and returns a ranom number from that list.
	 * 
	 * Valid examples for intList are:
	 * 
	 * 5 4,7,1,4 14-16,57,1-10
	 * 
	 * 
	 * @param intList
	 * @return
	 * @throws ParseException
	 */
	protected static int getRandomIntFromList(final String intList) throws ParseException {
		final TreeSet<Integer> intSet = new TreeSet<Integer>();
		;
		final String[] parts = intList.split(",");
		for (int i = 0; i < parts.length; i++) {
			final String s = parts[i];
			if (s.matches("-?\\d+")) {
				intSet.add(new Integer(s));
			} else if (s.matches("\\d+-\\d+")) {
				final String[] nums = s.split("-", 2);
				final int start = Integer.parseInt(nums[0]);
				final int stop = Integer.parseInt(nums[1]);

				if (start > stop) {
					throw new ParseException("Could not parse: " + s + " (second value is smaller than the first one.)", -1);
				}

				for (int j = start; j <= stop; j++) {
					intSet.add(j);
				}
			} else {
				throw new ParseException("Could not parse: " + s, -1);
			}
		}

		final int randomID = new Random().nextInt(intSet.size());
		return new ArrayList<Integer>(intSet).get(randomID);
	}

	/**
	 * Maps the syntaxType to the corresponding number.
	 * 
	 * @return
	 * @throws ParseException
	 */
	protected byte getByteSyntaxType() throws ParseException {
		if (this.syntaxType.equalsIgnoreCase("std")) {
			return 0;
		} else if (this.syntaxType.equalsIgnoreCase("uint8list")) {
			return 1;
		} else if (this.syntaxType.equalsIgnoreCase("uint16list")) {
			return 2;
		} else if (this.syntaxType.equalsIgnoreCase("int16list")) {
			return 3;
		} else if (this.syntaxType.equalsIgnoreCase("uint32list")) {
			return 4;
		} else if (this.syntaxType.equalsIgnoreCase("int64list")) {
			return 5;
		} else if (this.syntaxType.equalsIgnoreCase("floatlist")) {
			return 6;
		} else if (this.syntaxType.equalsIgnoreCase("variable")) {
			return 7;
		} else {
			throw new ParseException("Could not parse: " + this.syntaxType, -1);
		}
	}

	@Override
	public byte[] generatePacket() throws ParseException {

		final byte[] payload = this.getBytePayload();

		final AbsolutePosition position = getRandomPosition();

		// Return the Byte array
		return generatePacketInternal(this.getRandomNodeID(), getByteSyntaxType(), getRandomSemanticType(), position, payload);
	}

	protected byte[] generatePacketInternal(final short nodeID, final byte syntaxType, final byte semType, final AbsolutePosition position,
			final byte[] payload) throws ParseException {

		// compute the packet length
		final short packetLength = (short) (payload.length + HEADER_SIZE);

		// this byte array will contain the packet
		final byte[] backendArray = new byte[packetLength];

		// create ByteBuffer for more convinient access
		final ByteBuffer buf = ByteBuffer.wrap(backendArray);

		// should already be default. but better safe than sorry...
		buf.order(ByteOrder.BIG_ENDIAN);

		// Now fill the array with data

		buf.putShort((short) (packetLength - 2)); // PacketLength with the
		// legth-field
		// itself
		buf.put(VERSION); // Version
		buf.put(syntaxType); // SyntaxType
		buf.put(semType); // SemanticType
		buf.putShort(nodeID); // Node ID

		// Timestamp
		final long time;
		if (timestamp >= 0) {
			time = timestamp;
		} else {
			time = System.currentTimeMillis();
		}

		buf.putInt((int) (time / 1000));
		buf.putShort((short) (time % 1000));

		buf.putShort((short) position.x); // Coordinates
		buf.putShort((short) position.y);
		buf.putShort((short) position.z);

		buf.put(payload); // the Payload

		// Return the Byte array
		return backendArray;
	}

	protected AbsolutePosition getRandomPosition() throws ParseException {
		return new AbsolutePosition(this.getRandomXPosition(), this.getRandomYPosition(), this.getRandomZPosition());
	}
}
