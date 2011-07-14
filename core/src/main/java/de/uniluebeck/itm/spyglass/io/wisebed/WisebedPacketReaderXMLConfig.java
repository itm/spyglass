/*
 * -------------------------------------------------------------------------------- This file is
 * part of the WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet
 * (www.swarmnet.de) project SpyGlass is free software; you can redistribute it and/or modify it
 * under the terms of the BSD License. Refer to spyglass-licence.txt file in the root of the
 * SpyGlass source tree for further details.
 * --------------------------------------------------------------------------------
 */

package de.uniluebeck.itm.spyglass.io.wisebed;

import de.uniluebeck.itm.spyglass.xmlconfig.XMLConfig;
import org.simpleframework.xml.Element;

/**
 * Configuration for Testbed-Connection
 *
 * @author jens kluttig
 */
public class WisebedPacketReaderXMLConfig extends XMLConfig {

	public static final String PROPERTYNAME_SNAA_ENDPOINT_URL = "snaaEndpointUrl";

	public static final String PROPERTYNAME_RS_ENDPOINT_URL = "rsEndpointUrl";

	public static final String PROPERTYNAME_SM_ENDPOINT_URL = "smEndpointUrl";

	public static final String PROPERTYNAME_CONTROLLER_ENDPOINT_URL = "controllerEndpointUrl";

	public static final String PROPERTYNAME_USERNAME = "username";

	public static final String PROPERTYNAME_URN_PREFIX = "urnPrefix";

	public static final String PROPERTYNAME_PASSWORD = "password";

	/**
	 * URL of the Sensor Network Authentication and Authorization (SNAA) Service
	 */
	@Element(required = false, name = PROPERTYNAME_SNAA_ENDPOINT_URL)
	private String snaaEndpointUrl;

	/**
	 * URL of the Reservation Service
	 */
	@Element(required = false, name = PROPERTYNAME_RS_ENDPOINT_URL)
	private String rsEndpointUrl;

	/**
	 * URL of the Session Management Service
	 */
	@Element(required = false, name = PROPERTYNAME_SM_ENDPOINT_URL)
	private String smEndpointUrl;

	@Element(required = false, name = PROPERTYNAME_CONTROLLER_ENDPOINT_URL)
	private String controllerEndpointUrl;

	/**
	 * Username used for authentication with the SNAA
	 */
	@Element(required = false, name = PROPERTYNAME_USERNAME)
	private String username;

	@Element(required = false, name = PROPERTYNAME_URN_PREFIX)
	private String urnPrefix;

	@Element(required = false, name = PROPERTYNAME_PASSWORD)
	private String password;

	public String getSnaaEndpointUrl() {
		return snaaEndpointUrl;
	}

	public void setSnaaEndpointUrl(String snaaEndpointUrl) {
		firePropertyChange("snaaEndpointUrl", this.snaaEndpointUrl, snaaEndpointUrl);
		this.snaaEndpointUrl = snaaEndpointUrl;
	}

	public String getRsEndpointUrl() {
		return rsEndpointUrl;
	}

	public void setRsEndpointUrl(String rsEndpointUrl) {
		firePropertyChange("rsEndpointUrl", this.rsEndpointUrl, rsEndpointUrl);
		this.rsEndpointUrl = rsEndpointUrl;
	}

	public String getSmEndpointUrl() {
		return smEndpointUrl;
	}

	public void setSmEndpointUrl(String smEndpointUrl) {
		firePropertyChange("smEndpointUrl", this.smEndpointUrl,
				smEndpointUrl
		);
		this.smEndpointUrl = smEndpointUrl;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUrnPrefix() {
		return urnPrefix;
	}

	public void setUrnPrefix(String urnPrefix) {
		this.urnPrefix = urnPrefix;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getControllerEndpointUrl() {
		return controllerEndpointUrl;
	}

	public void setControllerEndpointUrl(String controllerEndpointUrl) {
		this.controllerEndpointUrl = controllerEndpointUrl;
	}
}
