package de.uniluebeck.itm.spyglass.plugin;

import java.util.List;

import org.eclipse.swt.widgets.Widget;

import de.uniluebeck.itm.spyglass.core.ConfigStore;
import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.gui.configuration.PreferencesConfigurationWidget;
import de.uniluebeck.itm.spyglass.gui.view.DrawingArea;
import de.uniluebeck.itm.spyglass.packet.Packet;
import de.uniluebeck.itm.spyglass.xmlconfig.NodeSensorRangeXMLConfig;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

public class NodeSensorRangePlugin extends BackgroundPainterPlugin {
	
	private NodeSensorRangeXMLConfig xmlConfig;
	
	public NodeSensorRangePlugin() {
		
	}
	
	@Override
	public void finalize() throws Throwable {
		super.finalize();
	}
	
	@Override
	public float getTimeout() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public boolean isVisible() {
		// TODO Auto-generated method stub
		return false;
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
	
	public List<DrawingObject> getDrawingObjects(final DrawingArea drawingArea) {
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