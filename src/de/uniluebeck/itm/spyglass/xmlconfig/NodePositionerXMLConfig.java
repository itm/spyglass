package de.uniluebeck.itm.spyglass.xmlconfig;

import org.simpleframework.xml.Element;

public class NodePositionerXMLConfig extends PluginXMLConfig {

	@Element
	private int timeToLive;

	public NodePositionerXMLConfig() {

	}

	@Override
	public void finalize() throws Throwable {
		super.finalize();
	}

}