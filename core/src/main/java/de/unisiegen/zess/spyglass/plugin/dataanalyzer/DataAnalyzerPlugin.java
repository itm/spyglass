package de.unisiegen.zess.spyglass.plugin.dataanalyzer;

import java.util.List;
import java.util.Map;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferenceDialog;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;
import de.uniluebeck.itm.spyglass.gui.view.GlobalInformationWidget;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacket;
import de.uniluebeck.itm.spyglass.plugin.GlobalInformation;
import de.uniluebeck.itm.spyglass.plugin.Plugin;
import de.uniluebeck.itm.spyglass.plugin.positionpacketnodepositioner.PositionData;
import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

/**
 */
public class DataAnalyzerPlugin extends Plugin implements GlobalInformation {
	/**
	 * Constructor
	 */
	public DataAnalyzerPlugin() {
		super(true);
	}

	public static String getHumanReadableName() {
		return "Data Analyzer";
	}

	@Override
	public String toString() {
		return DataAnalyzerPlugin.getHumanReadableName() + "." + this.getInstanceName();
	}

	@Override
	public PluginPreferencePage<? extends Plugin, ? extends PluginXMLConfig> createPreferencePage(
			PluginPreferenceDialog dialog, Spyglass spyglass) throws Exception {
		return null;
	}

	@Override
	public PluginXMLConfig getXMLConfig() {
		return null;
	}

	@Override
	protected void processPacket(SpyglassPacket packet) throws Exception {

	}

	@Override
	protected void resetPlugin() {		
	}

	@Override
	public void setWidget(GlobalInformationWidget widget) {
		widget.setShow(false);
	}

}


