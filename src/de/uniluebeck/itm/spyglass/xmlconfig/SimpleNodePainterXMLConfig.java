package de.uniluebeck.itm.spyglass.xmlconfig;

import java.util.HashMap;

import org.eclipse.swt.graphics.Color;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementArray;
import org.simpleframework.xml.ElementMap;

import de.uniluebeck.itm.spyglass.util.StringFormatter;

public class SimpleNodePainterXMLConfig extends PluginXMLConfig {
	
	@ElementMap(entry = "isActive", key = "nodeID", attribute = true, name = "extendedInformation")
	private HashMap<Integer, Boolean> isExtendenInformationActive;
	
	@ElementArray()
	private int[] lineColor;
	
	@Element
	private int lineWidth;
	
	@ElementMap(entry = "stringFormatter", key = "nodeID", attribute = true, valueType = StringFormatter.class)
	private HashMap<Integer, StringFormatter> stringFormatters;
	
	public SimpleNodePainterXMLConfig() {
		
	}
	
	@Override
	public void finalize() throws Throwable {
		super.finalize();
	}
	
	/**
	 * @return the isExtendenInformationActive
	 */
	public HashMap<Integer, Boolean> getIsExtendenInformationActive() {
		return isExtendenInformationActive;
	}
	
	/**
	 * @param isExtendenInformationActive
	 *            the isExtendenInformationActive to set
	 */
	public void setIsExtendenInformationActive(final HashMap<Integer, Boolean> isExtendenInformationActive) {
		this.isExtendenInformationActive = isExtendenInformationActive;
	}
	
	/**
	 * @return the lineColor
	 */
	public int[] getLineColor() {
		return lineColor;
		
	}
	
	/**
	 * @param lineColor
	 *            the lineColor to set
	 */
	public void setLineColor(final Color lineColor) {
		this.lineColor = new int[] { lineColor.getRed(), lineColor.getGreen(), lineColor.getBlue() };
	}
	
	/**
	 * @return the lineWidth
	 */
	public int getLineWidth() {
		return lineWidth;
	}
	
	/**
	 * @param lineWidth
	 *            the lineWidth to set
	 */
	public void setLineWidth(final int lineWidth) {
		this.lineWidth = lineWidth;
	}
	
	/**
	 * @return the stringFormatters
	 */
	public HashMap<Integer, StringFormatter> getStringFormatters() {
		return stringFormatters;
	}
	
	/**
	 * @param stringFormatters
	 *            the stringFormatters to set
	 */
	public void setStringFormatters(final HashMap<Integer, StringFormatter> stringFormatters) {
		this.stringFormatters = stringFormatters;
	}
	
}