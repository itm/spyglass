package de.uniluebeck.itm.spyglass.xmlconfig;
import java.util.HashMap;

import org.eclipse.swt.graphics.Color;

import de.uniluebeck.itm.spyglass.util.StringFormatter;

public class SimpleNodePainterXMLConfig extends PluginXMLConfig {

	private HashMap<Integer,Boolean> isExtendenInformationActive;
	private Color lineColor;
	private int lineWidth;
	private HashMap<Integer,StringFormatter> stringFormatters;

	public SimpleNodePainterXMLConfig(){

	}

	public void finalize() throws Throwable {
		super.finalize();
	}

}