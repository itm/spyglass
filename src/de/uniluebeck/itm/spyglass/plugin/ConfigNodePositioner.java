package de.uniluebeck.itm.spyglass.plugin;

import ishell.util.Logging;
import ishell.util.Settings;
import ishell.util.Settings.SettingsKey;

import java.util.HashMap;

import org.apache.log4j.Category;

import de.uniluebeck.itm.spyglass.packet.Packet;

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

}
