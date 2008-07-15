package de.uniluebeck.itm.spyglass.xmlconfig;
import de.uniluebeck.itm.spyglass.packet.AbsolutePosition;
import org.eclipse.swt.graphics.Color;

public class MapPainterXMLConfig extends PluginXMLConfig {

	private float framePointDefaultValue;
	private int gridElementHeight;
	private int gridElementWidth;
	private AbsolutePosition gridLowerLeftPoint;
	private boolean lockGridElementSquare;
	private boolean lockNumberOfRowsNCols;
	private Color maxColor;
	private float maxValue;
	private Color minColor;
	private float minValue;
	private int numCols;
	private int numFramePointsHorizontal;
	private int numFramePointsVertical;
	private int numRows;
	private int refreshInterval;

	public MapPainterXMLConfig(){

	}

	public void finalize() throws Throwable {
		super.finalize();
	}

}