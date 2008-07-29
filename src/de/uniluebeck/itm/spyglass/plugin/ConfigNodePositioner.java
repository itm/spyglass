// package de.uniluebeck.itm.spyglass.plugin;
//
// import ishell.util.Settings;
// import ishell.util.Settings.SettingsKey;
//
// import java.util.HashMap;
//
// import org.apache.log4j.Category;
//
// import de.uniluebeck.itm.spyglass.core.ConfigStore;
// import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;
// import de.uniluebeck.itm.spyglass.packet.Packet;
// import de.uniluebeck.itm.spyglass.packet.SpyglassPacket;
// import de.uniluebeck.itm.spyglass.plugin.nodepositioner.NodePositionerPlugin;
// import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;
// import de.uniluebeck.itm.spyglass.util.SpyglassLogger;
// import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;
//
// /**
// *
// * @deprecated TODO: what is this Plugin for?
// */
// @Deprecated
// public class ConfigNodePositioner extends NodePositionerPlugin {
//	
// private static Category log = SpyglassLogger.get(NodePositionerPlugin.class);
//	
// HashMap<Integer, AbsolutePosition> positions = new HashMap<Integer,
// AbsolutePosition>();
//	
// public ConfigNodePositioner() {
// final String n = Settings.instance().get(SettingsKey.nodes);
// final String tupels[] = n.split(";");
//		
// for (final String tupel : tupels) {
// final String elements[] = tupel.split(",");
// if (elements.length == 4) {
// log.debug("node=" + Integer.toHexString(new Integer(elements[0])) + " (" +
// elements[1] + "," + elements[2] + "," + elements[3] + ")");
//				
// positions.put(new Integer(elements[0]), new AbsolutePosition(new
// Integer(elements[1]), new Integer(elements[2]), new Integer(
// elements[3])));
// }
// }
// }
//	
// @Override
// public AbsolutePosition getPosition(final int nodeId) {
// final AbsolutePosition p = positions.get(new Integer(nodeId));
// if (p != null) {
// return p.clone();
// } else {
// return null;
// }
// }
//	
// @Override
// public void handlePacket(final SpyglassPacket packet) {
//		
// }
//	
// @Override
// public String getInstanceName() {
// return "unnamedCCNP";
// }
//	
// @Override
// public PluginPreferencePage<ConfigNodePositioner, PluginXMLConfig>
// createPreferencePage(final ConfigStore cs) {
// // TODO Auto-generated method stub
// return null;
// }
//	
// public static PluginPreferencePage<ConfigNodePositioner, PluginXMLConfig>
// createTypePreferencePage(final ConfigStore cs) {
// // TODO Auto-generated method stub
// return null;
// }
//	
// public static String getHumanReadableName() {
// return "ConfigNodePositioner";
// }
//	
// @Override
// public PluginXMLConfig getXMLConfig() {
// // TODO Auto-generated method stub
// return null;
// }
//	
// @Override
// protected void processPacket(final Packet packet) {
// // TODO Auto-generated method stub
//		
// }
//	
// @Override
// public void reset() {
// // TODO Auto-generated method stub
//		
// }
//	
// @Override
// public void setXMLConfig(final PluginXMLConfig xmlConfig) {
// // TODO Auto-generated method stub
//		
// }
//	
// @Override
// protected void updateQuadTree() {
// // TODO Auto-generated method stub
//		
// }
//	
// @Override
// public int getNumNodes() {
// // TODO Auto-generated method stub
// return 0;
// }
//	
// @Override
// public boolean offersMetric() {
// // TODO Auto-generated method stub
// return false;
// }
//	
// }
