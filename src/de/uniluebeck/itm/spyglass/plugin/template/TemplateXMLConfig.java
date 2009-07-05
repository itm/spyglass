/*
 * -------------------------------------------------------------------------------- This file is
 * part of the WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet
 * (www.swarmnet.de) project SpyGlass is free software; you can redistribute it and/or modify it
 * under the terms of the BSD License. Refer to spyglass-licence.txt file in the root of the
 * SpyGlass source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.template;

import java.beans.PropertyChangeEvent;

import org.simpleframework.xml.Element;

import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

// --------------------------------------------------------------------------------
/**
 * This is the configuration class of plugin TemplatePlugin.
 * 
 * It has (additionally to the ones defined in PluginXMLConfig) one parameter, "someParameter".
 * 
 * A note to thread-safety: objects of this class may be used by different threads, so have to put
 * some memory barriers in place to make sure that changes propagate correctly to other threads. In
 * simple cases like this declaring a variable volatile may be sufficient. but as it can be seen in
 * more complex plugins synchronized blocks may become necessary.
 * 
 * @author Dariush Forouher
 */
public class TemplateXMLConfig extends PluginXMLConfig {

	/**
	 * The name of a {@link PropertyChangeEvent} which is fired whenever the call of
	 * {@link TemplateXMLConfig#setSomeParameter(int)} yields a change
	 */
	public static final String PROPERTYNAME_SOME_PARAMETER = "someParameter";

	@Element(required = false)
	private volatile int someParameter = 42;

	// --------------------------------------------------------------------------------
	/**
	 * @return the someParameter
	 */
	public int getSomeParameter() {
		return someParameter;
	}

	// --------------------------------------------------------------------------------
	/**
	 * @param someParameter
	 *            the someParameter to set
	 */
	public void setSomeParameter(final int someParameter) {
		final int oldValue = this.someParameter;
		this.someParameter = someParameter;
		firePropertyChange(PROPERTYNAME_SOME_PARAMETER, oldValue, someParameter);
	}

}