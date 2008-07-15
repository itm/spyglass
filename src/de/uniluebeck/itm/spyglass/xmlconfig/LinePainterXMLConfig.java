package de.uniluebeck.itm.spyglass.xmlconfig;
import java.util.HashMap;

import org.eclipse.swt.graphics.Color;

import de.uniluebeck.itm.spyglass.util.StringFormatter;

public class LinePainterXMLConfig extends PluginXMLConfig {

	private Color lineColor;
	private int lineWidth;
	private HashMap<Integer,StringFormatter> stringFormatters;

	public LinePainterXMLConfig(){

	}

	public void finalize() throws Throwable {
		super.finalize();
	}

}