/*
 * -------------------------------------------------------------------------------- This file is
 * part of the WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet
 * (www.swarmnet.de) project SpyGlass is free software; you can redistribute it and/or modify it
 * under the terms of the BSD License. Refer to spyglass-licence.txt file in the root of the
 * SpyGlass source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.positionstaticnodepositioner;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.simpleframework.xml.Element;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferenceDialog;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacket;
import de.uniluebeck.itm.spyglass.plugin.NodePositionEvent;
import de.uniluebeck.itm.spyglass.plugin.NodePositionListener;
import de.uniluebeck.itm.spyglass.plugin.PluginManager;
import de.uniluebeck.itm.spyglass.plugin.nodepositioner.NodePositionerPlugin;
import de.uniluebeck.itm.spyglass.plugin.positionpacketnodepositioner.PositionData;
import de.uniluebeck.itm.spyglass.plugin.simplenodepainter.SimpleNodePainterPlugin;
import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

//import org.w3c.dom.Element;
/**
 * Default node positioner. it reads the position information from a XML file.
 * l
 * @author Kinan Hakim
 * 
 */
public class PositionStaticNodePositionerPlugin extends NodePositionerPlugin implements PropertyChangeListener {

    private static Logger log = SpyglassLoggerFactory.getLogger(PositionStaticNodePositionerPlugin.class);
    @Element(name = "parameters")
    private final PositionStaticNodePositionerXMLConfig config;
    /**
     * Time for scheduling packet removals
     */
    private Timer timer = null;
    /**
     * Hashmap containing the position information.
     * 
     * Set concurrency level to "2", since only two threads (PacketHandler and the TimeoutTimer)
     * will modify the map.
     */
    private final Map<Integer, PositionData> nodeMap = new ConcurrentHashMap<Integer, PositionData>(16, 0.75f, 2);

    /**
     * Constructor
     */
    public PositionStaticNodePositionerPlugin() {
        config = new PositionStaticNodePositionerXMLConfig();
    }


    @Override
    public void init(final PluginManager manager) throws Exception {
        super.init(manager);




        config.addPropertyChangeListener(this);
        loadFile();

        timer = new Timer("PositionStaticNodePositioner NodeTimeout-Timer");

        // Check every second for old nodes
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                // Only do something while the plugin is active
                if (config.getActive()) {
                    //removeOldNodes();
                    //loadFile();
                    
                }
            }
        }, 1000, 100);

        //nodeMap from the XML file


    }

    @Override
    public AbsolutePosition getPosition(final int nodeId) {
        log.info("PSNP " + this.getInstanceName() + " was asked for position of node ID: " + nodeId);

        final PositionData d = nodeMap.get(nodeId);
        return d != null ? d.position : null;
    }

    /**
     * Remove nodes which have timed out.
     */
    private void removeOldNodes() {
        // log.debug("PPNP " + this.getInstanceName() + " removes old nodes...");
        if (config.getTimeout() == 0) {
            return;
        }

        final List<NodePositionEvent> list = new ArrayList<NodePositionEvent>();

        final Iterator<Integer> it = nodeMap.keySet().iterator();
        while (it.hasNext()) {
            final int id = it.next();
            final PositionData data = nodeMap.get(id);
            if (data != null) {
                final long time = data.lastSeen;
                if (System.currentTimeMillis() - time > config.getTimeout() * 1000) {

                    final AbsolutePosition oldPos = data.position;

                    // remove the element from our map
                    it.remove();

                    log.debug("Removed node " + id + " after timeout.");
                    list.add(new NodePositionEvent(id, NodePositionEvent.Change.REMOVED, oldPos, null));

                }
            }
        }

        for (final NodePositionEvent nodePositionEvent : list) {
            pluginManager.fireNodePositionEvent(nodePositionEvent);
        }

    }

    @Override
    public PluginPreferencePage<PositionStaticNodePositionerPlugin, PositionStaticNodePositionerXMLConfig> createPreferencePage(
            final PluginPreferenceDialog dialog, final Spyglass spyglass) {
        return new PositionStaticNodePositionerPreferencePage(dialog, spyglass, this);
    }

    public static PluginPreferencePage<PositionStaticNodePositionerPlugin, PositionStaticNodePositionerXMLConfig> createTypePreferencePage(
            final PluginPreferenceDialog dialog, final Spyglass spyglass) {
        return new PositionStaticNodePositionerPreferencePage(dialog, spyglass);
    }

    public static String getHumanReadableName() {
        return "PositionStaticNodePositioner";
    }

    @Override
    public PluginXMLConfig getXMLConfig() {
        return this.config;
    }

    /**
     * Contrary to the usual convention for packet handling, a NodePositioner must handle packets
     * synchronously.
     */
    @Override
    public void handlePacket(final SpyglassPacket packet) {
        /*log.debug("PPNP " + this.getInstanceName() + " handles a packet...");
        final int id = packet.getSenderId();
        
        // check if we already know about this node
        final PositionData oldData = nodeMap.get(id);
        
        final AbsolutePosition newPos = packet.getPosition().clone();
        
        // Just overwrite the old PositionDataS object
        this.nodeMap.put(id, new PositionData(newPos, System.currentTimeMillis()));
        
        // TODO: Optimization: combine multiple events into one object
        
        // only send events when the position really changes.
        if (oldData == null) {
        pluginManager.fireNodePositionEvent(new NodePositionEvent(id, NodePositionEvent.Change.ADDED, null, newPos));
        } else if (!oldData.position.equals(newPos)) {
        pluginManager.fireNodePositionEvent(new NodePositionEvent(id, NodePositionEvent.Change.MOVED, oldData.position, newPos));
        }*/
    }

    @Override
    protected void processPacket(final SpyglassPacket packet) {
        // is never called.
    }

    @Override
    protected void resetPlugin() {
        this.nodeMap.clear();
    }

    @Override
    public int getNumNodes() {
        return this.nodeMap.size();
    }

    @Override
    public boolean offersMetric() {
        return true;
    }

    @Override
    public List<Integer> getNodeList() {
        return new ArrayList<Integer>(this.nodeMap.keySet());
    }

    // --------------------------------------------------------------------------------
	/*
     * (non-Javadoc)
     * 
     * @see de.uniluebeck.itm.spyglass.plugin.Plugin#shutdown()
     */
    @Override
    public void shutdown() throws Exception {
        super.shutdown();
        this.timer.cancel();
    }

    @Override
    public void addNodes(final Map<Integer, PositionData> oldNodeMap) {
        nodeMap.putAll(oldNodeMap);
        final Iterator<Integer> it = nodeMap.keySet().iterator();
        while (it.hasNext()) {
            final int id = it.next();
            final PositionData pos = nodeMap.get(id);
            if (pos != null) {
                pluginManager.fireNodePositionEvent(new NodePositionEvent(id, NodePositionEvent.Change.MOVED, pos.position, pos.position));
            }
        }
    }

    // --------------------------------------------------------------------------------
	/*
     * (non-Javadoc)
     * 
     * @see de.uniluebeck.itm.spyglass.plugin.nodepositioner.NodePositionerPlugin#getNodeMap()
     */
    @Override
    public Map<Integer, PositionData> getNodeMap() {
        return this.nodeMap;
    }

    public void loadFile() {


        try {
            //File toplogyFile = new File(topologyFileText);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(config.getTopologyFileName());
            doc.getDocumentElement().normalize();
            System.out.println("Root element " + doc.getDocumentElement().getNodeName());
            NodeList nodeLst = doc.getElementsByTagName("node");
            System.out.println("Information of all nodes");

            for (int s = 0; s < nodeLst.getLength(); s++) {

                Node fstNode = nodeLst.item(s);

                if (fstNode.getNodeType() == Node.ELEMENT_NODE) {

                    org.w3c.dom.Element fstElmnt = (org.w3c.dom.Element) fstNode;
                    org.w3c.dom.NodeList posElmntLst = fstElmnt.getElementsByTagName("position");
                    Node fstPos = posElmntLst.item(0);
                    org.w3c.dom.Element comElmnt = (org.w3c.dom.Element) fstPos;
                    org.w3c.dom.NodeList corElmntLst = comElmnt.getElementsByTagName("x");
                    org.w3c.dom.Element corElmnt = (org.w3c.dom.Element) corElmntLst.item(0);
                    org.w3c.dom.NodeList cor = corElmnt.getChildNodes();
                    System.out.println("Position X : " + ((Node) cor.item(0)).getNodeValue());
                    String pos_x = (String) (((Node) cor.item(0)).getNodeValue());


                    fstElmnt = (org.w3c.dom.Element) fstNode;
                    posElmntLst = fstElmnt.getElementsByTagName("position");
                    fstPos = posElmntLst.item(0);
                    comElmnt = (org.w3c.dom.Element) fstPos;
                    corElmntLst = comElmnt.getElementsByTagName("y");
                    corElmnt = (org.w3c.dom.Element) corElmntLst.item(0);
                    cor = corElmnt.getChildNodes();
                    System.out.println("Position Y: " + ((Node) cor.item(0)).getNodeValue());
                    String pos_y = (String) (((Node) cor.item(0)).getNodeValue());
                    fstElmnt = (org.w3c.dom.Element) fstNode;
                    posElmntLst = fstElmnt.getElementsByTagName("position");
                    fstPos = posElmntLst.item(0);
                    comElmnt = (org.w3c.dom.Element) fstPos;
                    corElmntLst = comElmnt.getElementsByTagName("z");
                    corElmnt = (org.w3c.dom.Element) corElmntLst.item(0);
                    cor = corElmnt.getChildNodes();
                    System.out.println("Position Z: " + ((Node) cor.item(0)).getNodeValue());
                    String pos_z = (String) (((Node) cor.item(0)).getNodeValue());

                    final AbsolutePosition newPos = new AbsolutePosition(Integer.parseInt(pos_x), Integer.parseInt(pos_y), Integer.parseInt(pos_z));
                    final PositionData pos = new PositionData(newPos, System.currentTimeMillis());

                    org.w3c.dom.NodeList pirElmntLst = fstElmnt.getElementsByTagName("pir-angle");
                    org.w3c.dom.Element pirElmnt = (org.w3c.dom.Element) pirElmntLst.item(0);
                    org.w3c.dom.NodeList pir = pirElmnt.getChildNodes();
                    System.out.println("Pir : " + ((Node) pir.item(0)).getNodeValue());


                    org.w3c.dom.NodeList idElmntLst = fstElmnt.getElementsByTagName("id");
                    org.w3c.dom.Element idElmnt = (org.w3c.dom.Element) idElmntLst.item(0);
                    org.w3c.dom.NodeList id = idElmnt.getChildNodes();
                    System.out.println("Id : " + ((Node) id.item(0)).getNodeValue());

                    String Id = (String) (((Node) id.item(0)).getNodeValue());
                    this.nodeMap.put(Integer.parseInt(Id), pos);

                    pluginManager.fireNodePositionEvent(new NodePositionEvent(Integer.parseInt(Id), NodePositionEvent.Change.ADDED, null, newPos));
                    
                    /*
                    Iterator plugIns = pluginManager.getActivePlugins().listIterator();
                    
                    while(plugIns.hasNext()){
                        
                        Object it = plugIns.next(); 
                        if(it instanceof SimpleNodePainterPlugin){
                            SimpleNodePainterPlugin simpleNodePainterPlugin = (SimpleNodePainterPlugin)it;
                            simpleNodePainterPlugin.simulatePacket(Integer.parseInt(Id));
                        }
                        
                    }
                     * */
                  

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void propertyChange(final PropertyChangeEvent e) {
        
        loadFile();
    }
}