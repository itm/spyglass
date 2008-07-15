package de.uniluebeck.itm.spyglass.plugin;

import ishell.util.Logging;
import ishell.util.Settings;
import ishell.util.Settings.SettingsKey;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Category;
import org.eclipse.swt.widgets.Widget;

import de.uniluebeck.itm.spyglass.configuration.PreferencesConfigurationWidget;
import de.uniluebeck.itm.spyglass.core.ConfigStore;
import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.gui.view.DrawingArea;
import de.uniluebeck.itm.spyglass.packet.Packet;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

public class ConfigNodePositioner extends NodePositionerPlugin {

	private static Category log = Logging.get(NodePositionerPlugin.class);

	HashMap<Integer,Position> positions = new HashMap<Integer,Position>();

	public ConfigNodePositioner()
	{
		String n = Settings.instance().get(SettingsKey.nodes);
		String tupels[] = n.split(";");
		
		for( String tupel : tupels)
		{
			String elements[] = tupel.split(",");
			if (elements.length == 4)
			{
				log.debug("node="+Integer.toHexString(new Integer(elements[0]))+" ("+elements[1]+","+elements[2]+","+elements[3]+")");
				
				positions.put(new Integer(elements[0]), new Position(new Integer(elements[1]),new Integer(elements[2]),new Integer(elements[3])));
			}
		}
	}
	
	@Override
	public Position getPosition(int nodeId) {
		Position p = positions.get(new Integer(nodeId));
		if (p!= null)
			return p.clone();
		else return null;
	}

	@Override
	public void handlePacket(Packet packet) {
		

	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PreferencesConfigurationWidget createPreferencesWidget(
			Widget parent, ConfigStore cs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PreferencesConfigurationWidget createTypePreferencesWidget(
			Widget parent, ConfigStore cs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DrawingObject> getDrawingObjects(DrawingArea drawingArea) {
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
	protected void processPacket(Packet packet) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setXMLConfig(PluginXMLConfig xmlConfig) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void updateQuadTree() {
		// TODO Auto-generated method stub
		
	}

}
