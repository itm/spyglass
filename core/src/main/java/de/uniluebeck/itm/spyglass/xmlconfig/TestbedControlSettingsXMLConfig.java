/*
 * -------------------------------------------------------------------------------- This file is
 * part of the WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet
 * (www.swarmnet.de) project SpyGlass is free software; you can redistribute it and/or modify it
 * under the terms of the BSD License. Refer to spyglass-licence.txt file in the root of the
 * SpyGlass source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.xmlconfig;

import de.uniluebeck.itm.spyglass.testbedControl.testbedControler;
import org.simpleframework.xml.Element;

// --------------------------------------------------------------------------------
/**
 * Instances of this class contain the configuration parameters of the general settings
 * 
 * @author Sebastian Ebers, Dariush Forouher
 * 
 */
public class TestbedControlSettingsXMLConfig extends XMLConfig {

	/**
	 * if <code>true</code> a ruler is shown in the graphical user interface
	 */
	@Element(required = false)
	private volatile boolean showRuler = true;
        
        @Element(required = false)
	private volatile String FlashProgramImage = "";
        
        @Element(required = false)
	private volatile String FlashProgramKey = "";
        
        @Element(required = false)
	private volatile String OTAPFlashProgramImage = "";
        
        @Element(required = false)
	private volatile String OTAPFlashProgramKey = "";
        
        @Element(required = false)
	private volatile String SentMessage = "";
        
        @Element(required = false)
	private volatile String NodeKey = "";
        
        @Element(required = false)
	private volatile String NodeID = "";
        
        @Element(required = false)
	private volatile String GWID = "";
        
        @Element(required = false)
	private volatile String Flash = "";
                
                
        @Element(required = false)
	private volatile String OTAPFlash = "";
                        
                        
        @Element(required = false)
	private volatile String AddNode = "";
        
        @Element(required = false)
	private volatile String RemoveNode = "";
        
        
        @Element(required = false)
	private volatile String Send = "";
        
        @Element(required = false)
	private volatile String Reset = "";


	// --------------------------------------------------------------------------------
	/**
	 * @return the showRuler
	 */
	public boolean getShowRuler() {
		return showRuler;
	}

	// --------------------------------------------------------------------------------
	/**
	 * @param showRuler
	 *            the showRuler to set
	 */
	public void setShowRuler(final boolean showRuler) {
		final boolean oldValue = this.showRuler;
		this.showRuler = showRuler;
		firePropertyChange("showRuler", oldValue, showRuler);
	}
        
        	// --------------------------------------------------------------------------------
	/**
	 * @return the FlashProgramImage
	 */
	public String getFlashProgramImage() {
		return FlashProgramImage;
	}

	// --------------------------------------------------------------------------------
	/**
	 * @param FlashProgramImage
	 *            the FlashProgramImage to set
	 */
	public void setFlashProgramImage(final String FlashProgramImage) {
		final String oldValue = this.FlashProgramImage;
		this.FlashProgramImage = FlashProgramImage;
		firePropertyChange("FlashProgramImage", oldValue, FlashProgramImage);
	}
        
        	// --------------------------------------------------------------------------------
	/**
	 * @return the FlashProgramKey
	 */
	public String getFlashProgramKey() {
		return FlashProgramKey;
	}

	// --------------------------------------------------------------------------------
	/**
	 * @param FlashProgramKey
	 *            the FlashProgramKey to set
	 */
	public void setFlashProgramKey(final String FlashProgramKey) {
		final String oldValue = this.FlashProgramKey;
		this.FlashProgramKey = FlashProgramKey;
		firePropertyChange("FlashProgramKey", oldValue, FlashProgramKey);
	}
        
                	// --------------------------------------------------------------------------------
	/**
	 * @return the OTAPFlashProgramImage
	 */
	public String getOTAPFlashProgramImage() {
		return OTAPFlashProgramImage;
	}

	// --------------------------------------------------------------------------------
	/**
	 * @param FlashProgramImage
	 *            the FlashProgramImage to set
	 */
	public void setOTAPFlashProgramImage(final String OTAPFlashProgramImage) {
		final String oldValue = this.OTAPFlashProgramImage;
		this.OTAPFlashProgramImage = OTAPFlashProgramImage;
		firePropertyChange("OTAPFlashProgramImage", oldValue, OTAPFlashProgramImage);
	}
        
        	// --------------------------------------------------------------------------------
	/**
	 * @return the OTAPFlashProgramKey
	 */
	public String getOTAPFlashProgramKey() {
		return OTAPFlashProgramKey;
	}

	// --------------------------------------------------------------------------------
	/**
	 * @param OTAPFlashProgramKey
	 *            the OTAPFlashProgramKey to set
	 */
	public void setOTAPFlashProgramKey(final String OTAPFlashProgramKey) {
		final String oldValue = this.OTAPFlashProgramKey;
		this.OTAPFlashProgramKey = OTAPFlashProgramKey;
		firePropertyChange("OTAPFlashProgramKey", oldValue, OTAPFlashProgramKey);
	}
        
        // --------------------------------------------------------------------------------
	/**
	 * @return the SentMessage
	 */
	public String getSentMessage() {
		return SentMessage;
	}

	// --------------------------------------------------------------------------------
	/**
	 * @param SentMessage
	 *            the SentMessage to set
	 */
	public void setSentMessage(final String SentMessage) {
		final String oldValue = this.SentMessage;
		this.SentMessage = SentMessage;
		firePropertyChange("SentMessage", oldValue, SentMessage);
	}

	// --------------------------------------------------------------------------------
                // --------------------------------------------------------------------------------
	/**
	 * @return the NodeID
	 */
	public String getNodeID() {
		return NodeID;
	}

	// --------------------------------------------------------------------------------
	/**
	 * @param NodeID
	 *            the NodeID to set
	 */
	public void setNodeID(final String NodeID) {
		final String oldValue = this.NodeID;
		this.NodeID = NodeID;
		firePropertyChange("NodeID", oldValue, NodeID);
	}

	// --------------------------------------------------------------------------------

	/**
	 * @return the NodeKey
	 */
	public String getNodeKey() {
		return NodeKey;
	}

	// --------------------------------------------------------------------------------
	/**
	 * @param NodeKey
	 *            the NodeKey to set
	 */
	public void setNodeKey(final String NodeKey) {
		final String oldValue = this.NodeKey;
		this.NodeKey = NodeKey;
		firePropertyChange("NodeKey", oldValue, NodeKey);
	}

	// --------------------------------------------------------------------------------
                // --------------------------------------------------------------------------------
	/**
	 * @return the GWID
	 */
	public String getGWID() {
		return GWID;
	}

	// --------------------------------------------------------------------------------
	/**
	 * @param GWID
	 *            the SentMessage to set
	 */
	public void setGWID(final String GWID) {
		final String oldValue = this.GWID;
		this.GWID = GWID;
		firePropertyChange("GWID", oldValue, GWID);
	}

	// --------------------------------------------------------------------------------
        
        	/**
	 * 


	// --------------------------------------------------------------------------------
	/**
	 * Overwrites the object with the provided other one
	 * 
	 * @param o
	 *            the object which contains the objects new parameter values
	 */
	public void overwriteWith(final TestbedControlSettingsXMLConfig o) {
		super.overwriteWith(o);
		//this.metrics.overwriteWith(o.getMetrics());
	}

}
