package de.uniluebeck.itm.spyglass.drawing.primitive;

import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.gui.view.DrawingArea;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;
import de.uniluebeck.itm.spyglass.positions.PixelRectangle;

public class Image extends DrawingObject {
	
	private final static Logger log = Logger.getLogger(Image.class);
	
	private org.eclipse.swt.graphics.Image image;
	
	private int imageSizeX;
	
	private int imageSizeY;
	
	public Image(final String imageFileName) {
		this.image = new org.eclipse.swt.graphics.Image(null, imageFileName);
	}
	
	public int getImageSizeX() {
		return imageSizeX;
	}
	
	public void setImageSizeX(final int imageSizeX) {
		setImageSizeX(imageSizeX, true);
	}
	
	public void setImageSizeX(final int imageSizeX, final boolean fireBoundingBoxChangeEvent) {
		this.imageSizeX = imageSizeX;
		updateBoundingBox(fireBoundingBoxChangeEvent);
	}
	
	public int getImageSizeY() {
		return imageSizeY;
	}
	
	public void setImageSizeY(final int imageSizeY) {
		setImageSizeY(imageSizeY, true);
	}
	
	public void setImageSizeY(final int imageSizeY, final boolean fireBoundingBoxChangeEvent) {
		this.imageSizeY = imageSizeY;
		updateBoundingBox(fireBoundingBoxChangeEvent);
	}
	
	@Override
	public void draw(final DrawingArea drawingArea, final GC gc) {
		
		final PixelRectangle rect = drawingArea.absRect2PixelRect(getBoundingBox());
		final Rectangle bounds = image.getBounds();
		
		try {
			
			gc.drawImage(image, 0, 0, bounds.width, bounds.height, rect.getUpperLeft().x, rect
					.getUpperLeft().y, rect.getWidth(), rect.getHeight());
			
		} catch (final Exception e) {
			log.error("Error while painting Image: " + e, e);
		}
		
	}
	
	public void dispose() {
		
		if (image != null) {
			image.dispose();
		}
		
	}
	
	@Override
	protected AbsoluteRectangle calculateBoundingBox() {
		return new AbsoluteRectangle(this.getPosition(), imageSizeX, imageSizeY);
	}
	
}
