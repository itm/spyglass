package de.uniluebeck.itm.spyglass.plugin.dagstuhl;

import java.util.List;

import org.apache.log4j.Category;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.drawing.primitive.Line;
import de.uniluebeck.itm.spyglass.drawing.primitive.Text;
import de.uniluebeck.itm.spyglass.drawing.primitive.Text.TextJustification;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferenceDialog;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;
import de.uniluebeck.itm.spyglass.gui.view.DrawingArea;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacket;
import de.uniluebeck.itm.spyglass.plugin.HistoricalPlugin;
import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;
import de.uniluebeck.itm.spyglass.util.SpyglassLogger;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

/**
 * 
 * @deprecated ??? whats this Plugin for?
 */
@Deprecated
public class DagstuhlRoutePainter extends HistoricalPlugin {
	
	private static Category log = SpyglassLogger.get(DagstuhlRoutePainter.class);
	
	// private int counter = 0;
	
	@Override
	public void handlePacket(final SpyglassPacket packet) {
		final byte[] s = packet.getContent();
		
		String debug = new String();
		if ((s != null) && (s.length > 1)) {
			log.debug("packet len " + s.length);
			
			if (s[0] != 99) {
				log.debug("received wrong packet");
				return;
			}
			
			if (s[1] == 1) {
				
				final short count = (short) (0xFF & s[2]);
				log.debug("------------- Route received (" + count + ") -------------");
				final int[] hops = new int[count];
				
				if (s.length != count * 2 + 3) {
					log.error("route log packet had WRONG LENGTH");
				}
				
				getSubLayer().clear();
				
				debug = "Route: ";
				for (int i = 0; i < count; i++) {
					hops[i] = ((0xFFFF) & (((0xFF & s[2 * i + 3]) << 8) + (0xFF & s[2 * i + 4])));
					debug += " " + Integer.toHexString(hops[i]);
				}
				
				for (int i = 1; i < count; i++) {
					
					log.debug("Line from " + hops[i - 1] + "->" + hops[i]);
					final AbsolutePosition p1 = getPluginManager().getNodePositioner().getPosition(hops[i - 1]);
					final AbsolutePosition p2 = getPluginManager().getNodePositioner().getPosition(hops[i]);
					if (p1 == null) {
						log.error("paintRoute: p1 for " + Integer.toHexString(hops[i - 1]) + " was null");
						break;
					}
					if (p2 == null) {
						log.error("paintRoute: p2 for " + Integer.toHexString(hops[i]) + " was null");
						break;
					}
					final Line l = new Line(p1, p2);
					l.setColor(255, 0, 0);
					l.setLineWidth(5);
					l.setId(hops[i - 1] * 2 + hops[i]);
					getSubLayer().addOrUpdate(l);
					
					// Position p = (p1.mult(0.2)).add(p2.mult(0.8));
					// Text t = new Text(new Integer(metric).toString(), p, n1 *
					// 2 + n2 + LINK_TEXT_OFFSET);
					// t.setColor(0, 0, 0);
					// getSubLayer().addOrUpdateDrawingObject(t);
				}
				
				/*
				 * Text t = new Text(new Integer(counter++).toString(), new Position(100, 10, 0),
				 * 99999999); t.setColor(0, 0, 0); getSubLayer().addOrUpdateDrawingObject(t);
				 */
				log.debug(debug);
			}
			
			if (s[1] == 2) {
				log.debug("------------- Packet received -------------");
				
				final int src = (0xFFFF & (((0xFF & s[2]) << 8) + (0xFF & s[3])));
				int no = (0xFF & s[4]);
				final int count = (0xFF & s[5]);
				no = count - no;
				final int setup = (0xFFFF & (((0xFF & s[6]) << 8) + (0xFF & s[7])));
				final int delay = (0xFFFF & (((0xFF & s[8]) << 8) + (0xFF & s[9])));
				String ds;
				if (delay < 2000) {
					ds = new Integer(delay).toString();
				} else {
					ds = "?";
				}
				final AbsolutePosition p1 = getPluginManager().getNodePositioner().getPosition(src).add(new AbsolutePosition(0, -20, 0));
				final Text t = new Text(new Integer(setup).toString() + "," + ds + "," + new Integer(no).toString() + "/"
						+ new Integer(count).toString(), p1, 23);
				t.setColor(0, 0, 0);
				t.setJustification(TextJustification.center);
				getSubLayer().addOrUpdate(t);
			}
			if (s[1] == 3) {
				reset();
			}
		}
	}
	
	@Override
	public PluginPreferencePage<DagstuhlRoutePainter, PluginXMLConfig> createPreferencePage(final PluginPreferenceDialog dialog,
			final Spyglass spyglass) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static PluginPreferencePage<DagstuhlRoutePainter, PluginXMLConfig> createTypePreferencePage(final PluginPreferenceDialog dialog,
			final Spyglass spyglass) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<DrawingObject> getDrawingObjects(final DrawingArea drawingArea) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static String getHumanReadableName() {
		return "DagstuhlRoutePainter";
	}
	
	@Override
	public PluginXMLConfig getXMLConfig() {
		return new PluginXMLConfig() {
			@Override
			public String getName() {
				return "DagstuhlRoutePainter-Instance";
			}
			
			@Override
			public boolean getVisible() {
				return false;
			}
			
			@Override
			public boolean equals(final PluginXMLConfig other) {
				return false;
			}
		};
	}
	
	@Override
	protected void processPacket(final SpyglassPacket packet) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void updateQuadTree() {
		// TODO Auto-generated method stub
		
	}
}
