package de.uniluebeck.itm.spyglass.plugin;

import java.util.List;

import ishell.util.Logging;

import org.apache.log4j.Category;
import org.eclipse.swt.widgets.Widget;

import de.uniluebeck.itm.spyglass.configuration.PreferencesConfigurationWidget;
import de.uniluebeck.itm.spyglass.core.ConfigStore;
import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.drawing.primitive.Line;
import de.uniluebeck.itm.spyglass.drawing.primitive.Text;
import de.uniluebeck.itm.spyglass.drawing.primitive.Text.TextJustification;
import de.uniluebeck.itm.spyglass.gui.view.DrawingArea;
import de.uniluebeck.itm.spyglass.packet.Packet;
import de.uniluebeck.itm.spyglass.plugin.NodePositionerPlugin.Position;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

public class DagstuhlConnectivityPainter extends de.uniluebeck.itm.spyglass.plugin.RelationPainterPlugin {

	private static Category log = Logging.get(DagstuhlConnectivityPainter.class);

	static final int LINK_LINE_OFFSET = 1000000;
	static final int LINK_TEXT_OFFSET = 2000000;
	static final int NODE_RECT_OFFSET = 3000000;
	static final int NODE_TEXT_OFFSET = 4000000;

	//private int counter = 0;

	@Override
	public void handlePacket(Packet packet) {
		byte[] s = packet.getContent();
		int src = 0;

		String debug = new String();
		if (s != null && s.length >= 6) {
			log.debug("packet len " + s.length);
			String ss = new String();
			for (int i = 0; i<s.length; i++)
			{
				ss += " " + Integer.toHexString( 0xFF & s[i]);
				
			}
			log.debug(ss);
			
			if (s[0] != 99) {
				log.debug("received wrong packet");
				return;
			} 
				
			if (s[1]==0)
			{
				src = (int) ((0xFFFF) & (((0xFF & s[2]) << 8) + (0xFF & s[3])));
	
				short neighborCount = (short) (0xFF & s[4]);
				if (s.length != 4*neighborCount+6)
				{
					log.debug("received empty neighbour packet");
					return;
				}
					
				debug += "ID=" + Integer.toHexString(src) + " n_count=" + neighborCount;
				int[] neighbors = new int[neighborCount];
				short[] metrics = new short[neighborCount];
				short[] lqi = new short[neighborCount];
				for (int i = 0; i < neighborCount; i++) {
					neighbors[i] = (int) ((0xFFFF) &  (((0xFF & s[4 * i + 5]) << 8) + (0xFF & s[4 * i + 6])));
					metrics[i] = (short) (0xFF & s[4 * i + 7]);
					lqi[i] = (short) (0xFF & s[4 * i + 8]);
					paintConnectivity(src, neighbors[i], metrics[i], 255-lqi[i]);
					debug += " " + Integer.toHexString(neighbors[i]) + "=" + metrics[i]+ "," + metrics[i];
				}
/*				for (int i = 0; i < neighborCount; i++) {
					paintNode(neighbors[i]);
	
				}
				paintNode(src);
	
				Text t = new Text(new Integer(counter++).toString(), new Position(10, 10, 0), 99999999);
				t.setColor(0, 0, 0);
				getSubLayer().addOrUpdateDrawingObject(t);
*/
				log.debug(debug);
			}
		}
	}

	public void paintConnectivity(int n1, int n2, int metric, int lqi) {
		Position p1 = getPluginManager().getNodePositioner().getPosition(n1);
		Position p2 = getPluginManager().getNodePositioner().getPosition(n2);
		if (p1 == null) {
			log.error("paintConnectivity: p1 for " + Integer.toHexString(n1) + " was null");
			return;
		}
		if (p2 == null) {
			log.error("paintConnectivity: p2 for " + Integer.toHexString(n2) + " was null");
			return;
		}
		Line l = new Line(p1, p2);
		l.setColor(200, 200, 200);
		l.setId(n1 + n2 + LINK_LINE_OFFSET);
		//getSubLayer().addOrUpdateDrawingObject(l); // TODO

		Position p = (p1.mult(0.75)).add(p2.mult(0.25));
		Text t = new Text("("+new Integer(metric).toString()+","+new Integer(lqi).toString()+")", p, n1 * 2+ n2 + LINK_TEXT_OFFSET);
		t.setColor(0, 0, 0);
		t.setJustification(TextJustification.center);
		//getSubLayer().addOrUpdateDrawingObject(t); // TODO

	}

	public String name() {
		return "Dagstuhl";
	}
	
	public void reset()
	{
		// TODO
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
	public void setXMLConfig(PluginXMLConfig xmlConfig) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void updateQuadTree() {
		// TODO Auto-generated method stub
		
	}
}
