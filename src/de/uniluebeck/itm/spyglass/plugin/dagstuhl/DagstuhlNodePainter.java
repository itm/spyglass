package de.uniluebeck.itm.spyglass.plugin.dagstuhl;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Category;

import de.uniluebeck.itm.spyglass.core.ConfigStore;
import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.drawing.primitive.Rectangle;
import de.uniluebeck.itm.spyglass.drawing.primitive.Text;
import de.uniluebeck.itm.spyglass.drawing.primitive.Text.TextJustification;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;
import de.uniluebeck.itm.spyglass.gui.view.DrawingArea;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacket;
import de.uniluebeck.itm.spyglass.plugin.HistoricalPlugin;
import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;
import de.uniluebeck.itm.spyglass.util.SpyglassLogger;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

/**
 * 
 * @deprecated use SimpleNodePainter instead.
 */
@Deprecated
public class DagstuhlNodePainter extends HistoricalPlugin {
	
	private static Category log = SpyglassLogger.get(DagstuhlNodePainter.class);
	
	static final int NODE_RECT_OFFSET = 100000;
	static final int NODE_TEXT_OFFSET = 200000;
	static final int COUNTER_TEXT_OFFSET = 300000;
	
	private final HashMap<Integer, Integer> counter = new HashMap<Integer, Integer>();
	
	int c = 0;
	
	@Override
	public void handlePacket(final SpyglassPacket packet) {
		final byte[] s = packet.getContent();
		int src = 0;
		
		String debug = new String();
		if ((s != null) && (s.length > 4)) {
			log.debug("packet len " + s.length);
			
			if (s[0] != 99) {
				log.debug("received wrong packet");
				return;
			}
			
			if (s[1] == 0) {
				src = ((0xFFFF) & (((0xFF & s[2]) << 8) + (0xFF & s[3])));
				
				final short neighborCount = (short) (0xFF & s[4]);
				if (s.length != 4 * neighborCount + 6) {
					return;
				}
				debug += "ID=" + Integer.toHexString(src) + " n_count=" + neighborCount;
				final int[] neighbors = new int[neighborCount];
				// short[] metrics = new short[neighborCount];
				for (int i = 0; i < neighborCount; i++) {
					neighbors[i] = ((0xFFFF) & (((0xFF & s[4 * i + 5]) << 8) + (0xFF & s[4 * i + 6])));
					// metrics[i] = (short) (0xFF & s[3 * i + 7]);
					// paintConnectivity(src, neighbors[i], metrics[i]);
					debug += " " + Integer.toHexString(neighbors[i]);
				}
				final int c = (0xFF & s[4 * neighborCount + 5]);
				log.debug("counter is " + c);
				counter.put(new Integer(src), new Integer(c));
				for (int i = 0; i < neighborCount; i++) {
					paintNode(neighbors[i]);
					
				}
				paintNode(src);
				/*
				 * Text t = new Text(new Integer(c++).toString(), new
				 * Position(10, 10, 0), 99999999); t.setColor(200, 200, 200);
				 * getSubLayer().addOrUpdateDrawingObject(t);
				 */
				log.debug(debug);
			}
		}
	}
	
	public void paintNode(final int src) {
		final AbsolutePosition p = getPluginManager().getNodePositioner().getPosition(src);
		if (p != null) {
			final Rectangle rect = new Rectangle();
			rect.setColor(0, 0, 0);
			rect.setBgColor(0, 0, 0);
			rect.setId(src + NODE_RECT_OFFSET);
			rect.setHeight(25);
			rect.setWidth(25);
			rect.setPosition(p);
			getSubLayer().addOrUpdate(rect);
			
			final Integer i = counter.get(new Integer(src));
			if (i != null) {
				final Text t2 = new Text(i.toString(), p.add(new AbsolutePosition(-13, 0, 0)), src + COUNTER_TEXT_OFFSET);
				t2.setColor(200, 200, 200);
				// t2.setBgColor(0, 0, 0);
				t2.setJustification(TextJustification.right);
				getSubLayer().addOrUpdate(t2);
			}
			
			/*
			 * Circle rect = new Circle(); rect.setColor(0, 0, 0);
			 * rect.setBgColor(0, 0, 0); rect.setId(src + NODE_RECT_OFFSET);
			 * rect.setDiameter(30); rect.setPosition(p);
			 * getSubLayer().addOrUpdateDrawingObject(rect);
			 */

			final Text t = new Text(Integer.toHexString(src), p, src + NODE_TEXT_OFFSET);
			t.setColor(255, 255, 255);
			t.setBgColor(0, 0, 0);
			t.setJustification(TextJustification.center);
			// getSubLayer().addOrUpdateDrawingObject(t); //TODO
			
		} else {
			log.debug("received packet from unknown node " + src);
		}
	}
	
	@Override
	public void reset() {
		// TODO
		counter.clear();
	}
	
	@Override
	public PluginPreferencePage<DagstuhlNodePainter, PluginXMLConfig> createPreferencePage(final ConfigStore cs) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static PluginPreferencePage<DagstuhlNodePainter, PluginXMLConfig> createTypePreferencePage(final ConfigStore cs) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<DrawingObject> getDrawingObjects(final DrawingArea drawingArea) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static String getHumanReadableName() {
		return "DagstuhlNodePainter";
	}
	
	@Override
	public PluginXMLConfig getXMLConfig() {
		return new PluginXMLConfig() {
			@Override
			public String getName() {
				return "DagstuhlNodePainter-Instance";
			}
			
			@Override
			public boolean isVisible() {
				return false;
			}
		};
	}
	
	@Override
	protected void processPacket(final SpyglassPacket packet) {
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
