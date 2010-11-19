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
 * @author jens kluttig
 */
public class TestbedXMLConfig extends XMLConfig {
    @Element(required = false)
    private
    String snaaUrl;

    @Element(required = false)
    private
    String reservationUrl;

    @Element(required = false)
    private
    String sessionManagementUrl;

    @Element(required = false)
    private
    String controllerUrn;

    @Element(required = false)
    private
    String userName;

    @Element(required = false)
    private
    String urnPrefix;

    @Element(required = false)
    private
    String password;

    /**
     * Url of Authorization Service
     */
    public String getSnaaUrl() {
        return snaaUrl;
    }

    public void setSnaaUrl(String snaaUrl) {
        firePropertyChange("snaaUrl", this.snaaUrl, snaaUrl);
        this.snaaUrl = snaaUrl;
    }

    /**
     * Url of Reservation Service
     */
    public String getReservationUrl() {
        return reservationUrl;
    }

    public void setReservationUrl(String reservationUrl) {
        firePropertyChange("reservationUrl", this.reservationUrl, reservationUrl);
        this.reservationUrl = reservationUrl;
    }

    /**
     * Url of Session Management Service
     */
    public String getSessionManagementUrl() {
        return sessionManagementUrl;
    }

    public void setSessionManagementUrl(String sessionManagementUrl) {
        firePropertyChange("sessionManagementUrl", this.sessionManagementUrl, sessionManagementUrl);
        this.sessionManagementUrl = sessionManagementUrl;
    }

    /**
     * Username for authentication
     */
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public String getControllerUrn() {
        return controllerUrn;
    }

    public void setControllerUrn(String controllerUrn) {
        this.controllerUrn = controllerUrn;
    }
}
