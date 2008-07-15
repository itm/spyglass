package de.uniluebeck.itm.spyglass.xmlconfig;

import java.util.List;

public abstract class PluginXMLConfig {

	private boolean isActice;
	private boolean isVisible;
	private String name;
	private int priority;
	private int[] semanticTypes;

	public PluginXMLConfig(){

	}

	public void finalize() throws Throwable {

	}

}