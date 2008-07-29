package de.uniluebeck.itm.spyglass.drawing.primitive;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;

import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.gui.view.DrawingArea;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;
import de.uniluebeck.itm.spyglass.util.StringFormatter;

public class NodeObject extends DrawingObject {
	
	private final int nodeID;
	
	private String denotation;
	
	private StringFormatter description;
	
	private boolean isExtended;
	
	private int[] lineColorRGB;
	
	private int lineWidth;
	
	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 * 
	 * @param nodeID
	 *            the identifier of the node which is visualized
	 * @param denotation
	 *            the denotation of the visualization
	 */
	public NodeObject(final int nodeID, final String denotation) {
		this(nodeID, denotation, null, false);
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 * 
	 * @param nodeID
	 *            the identifier of the node which is visualized
	 * @param denotation
	 *            the denotation of the visualization
	 * @param description
	 *            a detailed description (only shown in extended mode)
	 * @param isExtended
	 *            indicates if the extended mode is active
	 */
	public NodeObject(final int nodeID, final String denotation, final StringFormatter description, final boolean isExtended) {
		this(nodeID, denotation, description, isExtended, new int[] { 255, 0, 0 }, 1);
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 * 
	 * @param nodeID
	 *            the identifier of the node which is visualized
	 * @param denotation
	 *            the denotation of the visualization
	 * @param description
	 *            a detailed description (only shown in extended mode)
	 * @param isExtended
	 *            indicates if the extended mode is active
	 * @param lineColorRGB
	 *            the line colo's RGB values
	 * @param lineWidth
	 *            the line's width
	 */
	public NodeObject(final int nodeID, final String denotation, final StringFormatter description, final boolean isExtended,
			final int[] lineColorRGB, final int lineWidth) {
		super();
		this.nodeID = nodeID;
		this.denotation = denotation;
		this.description = description;
		this.isExtended = isExtended;
		this.lineColorRGB = lineColorRGB;
		this.lineWidth = lineWidth;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Updates the parameters
	 * 
	 * @param denotation
	 *            the denotation of the visualization
	 * @param description
	 *            a detailed description (only shown in extended mode)
	 * @param isExtended
	 *            indicates if the extended mode is active
	 * @param lineColorRGB
	 *            the line colo's RGB values
	 * @param lineWidth
	 *            the line's width
	 */
	public void update(final String denotation, final StringFormatter description, final boolean isExtended, final int[] lineColorRGB,
			final int lineWidth) {
		this.denotation = denotation;
		this.description = description;
		this.isExtended = isExtended;
		this.lineColorRGB = lineColorRGB;
		this.lineWidth = lineWidth;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the denotation
	 */
	public String getDenotation() {
		return denotation;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param denotation
	 *            the denotation to set
	 */
	public void setDenotation(final String denotation) {
		this.denotation = denotation;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the description
	 */
	public StringFormatter getDescription() {
		return description;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(final StringFormatter description) {
		this.description = description;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the isExtended
	 */
	public boolean isExtended() {
		return isExtended;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param isExtended
	 *            the isExtended to set
	 */
	public void setExtended(final boolean isExtended) {
		this.isExtended = isExtended;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the lineColorRGB
	 */
	public int[] getLineColorRGB() {
		return lineColorRGB;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param lineColorRGB
	 *            the lineColorRGB to set
	 */
	public void setLineColorRGB(final int[] lineColorRGB) {
		this.lineColorRGB = lineColorRGB;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the lineWidth
	 */
	public int getLineWidth() {
		return lineWidth;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param lineWidth
	 *            the lineWidth to set
	 */
	public void setLineWidth(final int lineWidth) {
		this.lineWidth = lineWidth;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the nodeID
	 */
	public int getNodeID() {
		return nodeID;
	}
	
	@Override
	public void draw(final DrawingArea drawingArea, final GC gc) {
		// TODO Auto-generated method stub
		final Color color = new Color(null, this.getColorR(), this.getColorG(), this.getColorB());
		final Color bg = new Color(null, this.getBgColorR(), this.getBgColorG(), this.getBgColorB());
		
		gc.setForeground(color);
		gc.setBackground(bg);
		// gc.setLineWidth(this.getLineWidth());
		// gc.fillRectangle((this.getPosition().x - (this.getWidth() / 2)),
		// (this.getPosition().y - (this.getHeight() / 2)), this.getWidth(),
		// this
		// .getHeight());
		// gc.drawRectangle((this.getPosition().x - (this.getWidth() / 2)),
		// (this.getPosition().y - (this.getHeight() / 2)), this.getWidth(),
		// this
		// .getHeight());
		//		
		color.dispose();
		bg.dispose();
	}
	
	@Override
	public AbsoluteRectangle getBoundingBox() {
		// TODO Auto-generated method stub
		return null;
	}
	
}