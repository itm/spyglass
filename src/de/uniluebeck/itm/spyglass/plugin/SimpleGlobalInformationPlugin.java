package de.uniluebeck.itm.spyglass.plugin;

import org.eclipse.swt.widgets.Widget;
import org.simpleframework.xml.Element;

import de.uniluebeck.itm.spyglass.core.ConfigStore;
import de.uniluebeck.itm.spyglass.gui.configuration.PreferencesConfigurationWidget;
import de.uniluebeck.itm.spyglass.packet.Packet;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;
import de.uniluebeck.itm.spyglass.xmlconfig.SimpleGlobalInformationXMLConfig;

public class SimpleGlobalInformationPlugin extends GlobalInformationPlugin {
	
	@Element(name = "parameters")
	private final SimpleGlobalInformationXMLConfig xmlConfig;
	
	public SimpleGlobalInformationPlugin() {
		xmlConfig = new SimpleGlobalInformationXMLConfig();
	}
	
	@Override
	public void finalize() throws Throwable {
		super.finalize();
	}
	
	/**
	 * 
	 * @param widget
	 */
	@Override
	public void addWidget(final Widget widget) {
		
	}
	
	@Override
	public Widget getWidget() {
		return null;
	}
	
	@Override
	public PreferencesConfigurationWidget createPreferencesWidget(final Widget parent, final ConfigStore cs) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public PreferencesConfigurationWidget createTypePreferencesWidget(final Widget parent, final ConfigStore cs) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String getHumanReadableName() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public PluginXMLConfig getXMLConfig() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void handlePacket(final Packet packet) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public String name() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	protected void processPacket(final Packet packet) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void setXMLConfig(final PluginXMLConfig xmlConfig) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void updateQuadTree() {
		// TODO Auto-generated method stub
		
	}
	
}