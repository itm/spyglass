package de.uniluebeck.itm.spyglass.plugin;

import java.util.List;

import org.eclipse.swt.widgets.Widget;

import de.uniluebeck.itm.spyglass.core.ConfigStore;
import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.gui.configuration.PreferencesConfigurationWidget;
import de.uniluebeck.itm.spyglass.gui.view.DrawingArea;
import de.uniluebeck.itm.spyglass.layer.Layer;
import de.uniluebeck.itm.spyglass.layer.SubLayer;
import de.uniluebeck.itm.spyglass.packet.Packet;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

/**
 * Temporary class which all old Plugins inherit. It adds a compatibillity layer
 * to the Plugin interface so that the old Plugins can run without much change.
 * 
 * @author dariush
 * @deprecated
 */
@Deprecated
public class HistoricalPlugin extends Plugin implements Drawable {
	
	private final Layer layer = new SubLayer();
	
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
		return this.layer.getDrawingObjects();
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
	
	protected Layer getSubLayer() {
		return layer;
	}
	
	public float getTimeout() {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
