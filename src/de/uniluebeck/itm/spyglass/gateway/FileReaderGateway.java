/*
 * ---------------------------------------------------------------------- This file is part of the
 * WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de)
 * project SpyGlass is free software; you can redistribute it and/or modify it under the terms of
 * the BSD License. Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for
 * further details. ------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.gateway;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;

// ------------------------------------------------------------------------------
// --
/**
 * A gateway implementation that offers access to a file.
 */
@Root
public class FileReaderGateway implements Gateway {
	private static Logger log = SpyglassLoggerFactory.getLogger(FileReaderGateway.class);

	@Element(name = "file", required = false)
	private File file = null;

	private InputStream inputStream = null;
	
	// --------------------------------------------------------------------------
	// ------
	/**
	 * 
	 */
	@Override
	public InputStream getInputStream() {
		return inputStream;
	}

	// --------------------------------------------------------------------------
	// ------
	/**
	 * 
	 */
	public File getFile() {
		return file;
	}

	// --------------------------------------------------------------------------
	// ------
	/**
	 * Sets the input file
	 * 
	 * @param file
	 *            the input file
	 */
	public void setFile(final File file) {
		this.file = file;

		// Construct the input stream from that file
		try {
			if (inputStream != null) {
				inputStream.close();
			}
			if (file != null) {
				inputStream = new FileInputStream(file);
			}
		} catch (final FileNotFoundException e) {
			log.error("File[" + file + "] not found.", e);
		} catch (final IOException e) {
			log.error("Error while setting an input file. Please close the pallication and try again.", e);
		}
	}

	/**
	 * Resets the file.
	 */
	public void reset() {
		setFile(file);
	}

}
