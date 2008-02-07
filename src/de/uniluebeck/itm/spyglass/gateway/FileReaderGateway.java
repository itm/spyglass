/* ----------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass.       
 * Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de) project    
 * SpyGlass is free software; you can redistribute it and/or modify it  
 * under the terms of the BSD License. Refer to spyglass-licence.txt    
 * file in the root of the SpyGlass source tree for further details.  
------------------------------------------------------------------------*/
package de.uniluebeck.itm.spyglass.gateway;

import ishell.util.Logging;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.log4j.Category;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

// --------------------------------------------------------------------------------
/**
 * A gateway implementation that offers access to a file.
 */
@Root
public class FileReaderGateway implements Gateway {
	private static Category log = Logging.get(FileReaderGateway.class);

	private File file = null;

	private InputStream inputStream = null;

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	@Override
	public InputStream getInputStream() {
		return inputStream;
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	@Element
	public File getFile() {
		return file;
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	@Element
	public void setFile(File file) {
		this.file = file;

		// Construct the input stream from that file
		try {
			inputStream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			log.error("File[" + file + "] not found" + e, e);
		}

	}

}
