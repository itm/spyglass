package de.uniluebeck.itm.spyglass.xmlconfig;

import java.util.List;

public class SimpleGlobalInformationXMLConfig extends PluginXMLConfig {

	private List<Integer> semanticTypes4Neighborhoods;
	private boolean showNodeDegree;
	private boolean showNumNodes;
	private List<StringFormatterSettings> stringFormatterSettings;

	public SimpleGlobalInformationXMLConfig(){

	}

	public void finalize() throws Throwable {
		super.finalize();
	}

}