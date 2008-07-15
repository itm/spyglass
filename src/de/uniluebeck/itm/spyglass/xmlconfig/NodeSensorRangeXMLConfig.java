package de.uniluebeck.itm.spyglass.xmlconfig;
import java.util.HashMap;

import de.uniluebeck.itm.spyglass.plugin.sensorrange.NodeSensorRange;

public class NodeSensorRangeXMLConfig extends PluginXMLConfig {

	private NodeSensorRange defalutNodeSensorRange;
	private HashMap<Integer,NodeSensorRange> nodeRanges;

	public NodeSensorRangeXMLConfig(){

	}

	public void finalize() throws Throwable {
		super.finalize();
	}

}