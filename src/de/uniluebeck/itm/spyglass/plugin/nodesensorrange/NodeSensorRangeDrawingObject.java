// --------------------------------------------------------------------------------
/**
 * 
 */
package de.uniluebeck.itm.spyglass.plugin.nodesensorrange;

import de.uniluebeck.itm.spyglass.drawing.DrawingObject;

// --------------------------------------------------------------------------------
/**
 * Flag class for DrawingObjects painted by NodeSensorRangePlugin
 * 
 * @author bimschas
 */
public abstract class NodeSensorRangeDrawingObject extends DrawingObject {

	protected int backgroundAlpha = 255;

	public int getBackgroundAlpha() {
		return backgroundAlpha;
	}

	public void setBackgroundAlpha(final int backgroundAlpha) {
		this.backgroundAlpha = backgroundAlpha;
	}

	public abstract void setRange(NodeSensorRangeXMLConfig.NodeSensorRange range);

}
