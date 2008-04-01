package de.uniluebeck.itm.spyglass.plugin;

import ishell.util.Logging;

import org.apache.log4j.Category;

import de.uniluebeck.itm.spyglass.drawing.primitive.Line;
import de.uniluebeck.itm.spyglass.drawing.primitive.Text;
import de.uniluebeck.itm.spyglass.drawing.primitive.Text.TextJustification;
import de.uniluebeck.itm.spyglass.packet.Packet;
import de.uniluebeck.itm.spyglass.plugin.NodePositionerPlugin.Position;

public class DagstuhlRoutePainter extends de.uniluebeck.itm.spyglass.plugin.RelationPainterPlugin {

	private static Category log = Logging.get(DagstuhlRoutePainter.class);


	//private int counter = 0;

	@Override
	public void handlePacket(Packet packet) {
		byte[] s = packet.getContent();

		String debug = new String();
		if (s != null && s.length > 1) {
			log.debug("packet len " + s.length);

			if (s[0] != 99) {
				log.debug("received wrong packet");
				return;
			} 
				
			if (s[1]==1)
			{
				
				short count = (short) (0xFF & s[2]);
				log.debug("------------- Route received ("+count+") -------------" );
				int[] hops = new int[count];

				if (s.length != count * 2 +3)
					log.error("route log packet had WRONG LENGTH");
				
				
				getSubLayer().clear();
				
				debug = "Route: ";
				for (int i = 0; i < count; i++) {
					hops[i] = (int) ((0xFFFF) & (((0xFF & s[2 * i + 3]) << 8) + (0xFF & s[2 * i + 4])));
					debug += " " + Integer.toHexString(hops[i]);
				}
	
				for ( int i = 1; i < count; i++)
				{
					
					log.debug("Line from "+hops[i-1]+"->"+hops[i]);
					Position p1 = getPluginManager().getNodePositioner().getPosition(hops[i-1]);
					Position p2 = getPluginManager().getNodePositioner().getPosition(hops[i]);
					if (p1 == null) {
						log.error("paintRoute: p1 for " + Integer.toHexString(hops[i-1]) + " was null");
						break;
					}
					if (p2 == null) {
						log.error("paintRoute: p2 for " + Integer.toHexString(hops[i]) + " was null");
						break;
					}
					Line l = new Line(p1, p2);
					l.setColor(255, 0, 0);
					l.setLineWidth(5);
					l.setId(hops[i-1] * 2 + hops[i]);
					getSubLayer().addOrUpdateDrawingObject(l);
	
					//Position p = (p1.mult(0.2)).add(p2.mult(0.8));
					//Text t = new Text(new Integer(metric).toString(), p, n1 * 2 + n2 + LINK_TEXT_OFFSET);
					//t.setColor(0, 0, 0);
					//getSubLayer().addOrUpdateDrawingObject(t);
				}
				
				/*
				Text t = new Text(new Integer(counter++).toString(), new Position(100, 10, 0), 99999999);
				t.setColor(0, 0, 0);
				getSubLayer().addOrUpdateDrawingObject(t);*/
				log.debug(debug);
			}

			if (s[1]==2)
			{
				log.debug("------------- Packet received -------------" );

				int src = (int) (0xFFFF&(((0xFF & s[2]) << 8) + (0xFF & s[3])));
				int no = (int) (0xFF & s[4]);
				int count = (int) (0xFF & s[5]);
				no = count - no;
				int setup =  (int) (0xFFFF&(((0xFF & s[6]) << 8) + (0xFF & s[7])));
				int delay =  (int) (0xFFFF&(((0xFF & s[8]) << 8) + (0xFF & s[9])));
				String ds;
				if (delay < 2000)
					ds = new Integer(delay).toString();
				else
					ds = "?";
				Position p1 = getPluginManager().getNodePositioner().getPosition(src).add(new Position(0, -20, 0));
				Text t = new Text(new Integer(setup).toString()+","+ds+","+new Integer(no).toString()+"/"+new Integer(count).toString(), p1, 23);
				t.setColor(0, 0, 0);
				t.setJustification(TextJustification.center);
				getSubLayer().addOrUpdateDrawingObject(t);
			}
			if (s[1]==3)
			{
				reset();
			}
		}
	}


	public String name() {
		return "RoutePainter";
	}
}
