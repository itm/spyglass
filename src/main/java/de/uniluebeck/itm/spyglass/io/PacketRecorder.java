/*----------------------------------------------------------------------------------------
 * This file is part of the
 * WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de)
 * project SpyGlass is free software; you can redistribute it and/or modify it under the terms of
 * the BSD License. Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for
 * further details.
 * ---------------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.io;

import java.io.File;

import de.uniluebeck.itm.spyglass.packet.SpyglassPacket;

// --------------------------------------------------------------------------------
/**
 * Instances of this interface can play back and record {@link SpyglassPacket}s
 * 
 * @author Sebastian Ebers
 * 
 */
public interface PacketRecorder extends PacketReader {

	// --------------------------------------------------------------------------------
	/**
	 * Returns whether the currently provided files are read from a file or handed over from iShell
	 * 
	 * @return <code>true</code> if the currently provided files are read from a file or handed over
	 *         from iShell
	 */
	public boolean isReadFromFile();

	// --------------------------------------------------------------------------------
	/**
	 * Enables or disables the readFromFile mode
	 * 
	 * @param enable
	 *            <code>true</code> if the readFromFile mode is to be enabled, <code>false</code>
	 *            otherwise
	 * @return <code>true</code> if the readFromFile mode is enabled, <code>false</code> otherwise
	 */
	public boolean setReadFromFile(final boolean enable);

	// --------------------------------------------------------------------------------
	/**
	 * Activates or deactivates the recording mode
	 * 
	 * @param enable
	 *            if <code>true</code> the recording mode will be activated, if <code>false</code>
	 *            the recording mode will be deactivated
	 * @return <code>true</code> if the recording mode was activated, <code>false</code> otherwise
	 * 
	 */
	public boolean setRecording(final boolean enable);

	// --------------------------------------------------------------------------------
	/**
	 * Returns whether the incoming packets are currently recorded
	 * 
	 * @return <code>true</code> if the incoming packets are currently recorded
	 */
	public boolean isRecord();

	// --------------------------------------------------------------------------------
	/**
	 * Sets the file which is used for recording the input
	 * 
	 * @param path
	 *            the path to the file which is used for recording the input
	 */
	public void setRecordFile(final String path);

	// --------------------------------------------------------------------------
	/**
	 * Returns the file where the recoded data has to be saved.
	 * 
	 * @return the path to the file where the recoded data has to be saved.
	 */
	public File getRecordFile();

	// --------------------------------------------------------------------------
	/**
	 * Sets the file to be used to read the previously recorded packages.
	 * 
	 * @param path
	 *            the path to the file
	 * @return <code>true</code> if an input file was set successfully
	 */
	public boolean setPlayBackFile(final String path);

	// --------------------------------------------------------------------------
	/**
	 * Returns the file to be used to read the previously recorded packages
	 * 
	 * @return the file to be used to read the previously recorded packages
	 */
	public File getPlayBackFile();

}
