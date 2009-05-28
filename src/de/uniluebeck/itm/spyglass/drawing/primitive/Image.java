package de.uniluebeck.itm.spyglass.drawing.primitive;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;

import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.GC;

import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;
import de.uniluebeck.itm.spyglass.positions.PixelRectangle;

public class Image extends DrawingObject {

	private final static Logger log = Logger.getLogger(Image.class);

	private final org.eclipse.swt.graphics.Image image;

	private int imageSizeX;

	private int imageSizeY;

	public Image(final String imageFileName) {
		this.image = new org.eclipse.swt.graphics.Image(null, imageFileName);
	}

	public synchronized int getImageSizeX() {
		return imageSizeX;
	}

	public synchronized void setImageSizeX(final int imageSizeX) {
		this.imageSizeX = imageSizeX;
		markBoundingBoxDirty();
	}

	public synchronized int getImageSizeY() {
		return imageSizeY;
	}

	public void setImageSizeY(final int imageSizeY) {
		this.imageSizeY = imageSizeY;
		markBoundingBoxDirty();
	}

	@Override
	public void draw(final GC gc) {

		// the math below can't handle these values. since such image wouldn't get displayed
		// anyway, we may as well return right now.
		if ((imageSizeX == 0) || (imageSizeY == 0)) {
			return;
		}

		// disable the advanced subsystem for performance reasons and to avoid strange drawing bugs.
		final boolean advancedSubsystem = gc.getAdvanced();
		gc.setAdvanced(false);

		// Transform from Image coordinate system to absolute coordinate system
		final AffineTransform transform = AffineTransform.getScaleInstance(imageSizeX/((double)image.getBounds().width), imageSizeY/((double)image.getBounds().height));
		transform.preConcatenate(AffineTransform.getTranslateInstance(this.getPosition().x,
										-this.getPosition().y - this.getBoundingBox().getHeight()));

		// multiply transform from drawing area. now "transform" transforms from image coordinates to pixel coordinates
		transform.preConcatenate(getDrawingArea().getTransform());

		// the inverse matrix. it transforms from pixel coordinates to image coordinates
		AffineTransform invTransform;
		try {
			invTransform = transform.createInverse();
		} catch (final NoninvertibleTransformException e) {
			throw new RuntimeException("BUG", e);
		}

		final PixelRectangle clippingArea = new PixelRectangle(gc.getClipping());

		// the pixel area that actually has to be redrawn...
		PixelRectangle imageSrcArea = getDrawingArea().absRect2PixelRect(getBoundingBox()).intersection(clippingArea);
		// ... transformed into the corresponding area in the image
		imageSrcArea.transform(invTransform);

		// this is a hack to avoid strange graphical errors. enlarge the image size if possible
		final int safetyMargin = 2;
		imageSrcArea.rectangle.x -= safetyMargin;
		imageSrcArea.rectangle.y -= safetyMargin;
		imageSrcArea.rectangle.width += 2 * safetyMargin;
		imageSrcArea.rectangle.height += 2 * safetyMargin;

		// but make sure that we don't overstep the limits of the original image
		imageSrcArea =  imageSrcArea.intersection(new PixelRectangle(image.getBounds()));

		// the destination area on the drawing area - calculated *from* the imageSrcArea to avoid numerical problems
		final PixelRectangle imageDestArea = new PixelRectangle(imageSrcArea);
		imageDestArea.transform(transform);

		gc.drawImage(image,
				imageSrcArea.getUpperLeft().x,
				imageSrcArea.getUpperLeft().y,
				imageSrcArea.getWidth(),
				imageSrcArea.getHeight(),
				imageDestArea.getUpperLeft().x,
				imageDestArea.getUpperLeft().y,
				imageDestArea.getWidth(),
				imageDestArea.getHeight());

		gc.setAdvanced(advancedSubsystem);

	}


	@Override
	public void destroy() {
		if (image != null) {
			image.dispose();
		}
		super.destroy();
	}

	@Override
	protected AbsoluteRectangle calculateBoundingBox() {
		return new AbsoluteRectangle(this.getPosition(), imageSizeX, imageSizeY);
	}
}
