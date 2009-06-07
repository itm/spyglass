package de.uniluebeck.itm.spyglass.gui.actions;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.resource.ImageDescriptor;

import de.uniluebeck.itm.spyglass.SpyglassApp;
import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.gui.view.DrawingArea;
import de.uniluebeck.itm.spyglass.plugin.Drawable;
import de.uniluebeck.itm.spyglass.plugin.Plugin;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;

/**
 * Auto Zoom.
 *
 * This action modifies zoom and position to make all visible DrawingObjects fit exactly into the
 * drawing area.
 *
 * This action has two constructors since unfortunatly using it in SpyglassStandalone differs slightly
 * to Spyglass4iShell
 *
 * @author Dariush Forouher
 *
 */
public class ZoomCompleteMapAction extends Action {

	protected static final Logger log = SpyglassLoggerFactory.getLogger(ZoomCompleteMapAction.class);

	protected final ImageDescriptor imageDescriptor = getImageDescriptor("zoom_complete_map.png");

	protected final Spyglass spyglass;

	protected DrawingArea drawingArea;

	protected final SpyglassApp app;

	public ZoomCompleteMapAction(final Spyglass spyglass, final DrawingArea drawingArea) {
		this.spyglass = spyglass;
		this.drawingArea = drawingArea;
		this.app = null;
	}

	public ZoomCompleteMapAction(final SpyglassApp da, final Spyglass spyglass) {
		this.app = da;
		this.spyglass = spyglass;
		this.drawingArea = null;
	}

	@Override
	public void run() {
		log.debug("Pressed button ZOOM_COMPLETE_MAP.");

		if ((app != null) && (drawingArea == null)) {
			drawingArea = app.getDrawingArea();
		}

		// 10 seems a good value for now.
		for (int i = 0; i < 10; i++) {

			final AbsoluteRectangle maxRect1 = calcBBox();

			final AbsoluteRectangle maxRect = maxRect1;
			if ((maxRect != null) && (maxRect.getHeight() > 0) && (maxRect.getWidth() > 0)) {

				drawingArea.autoZoom(maxRect);

				// Note: This code depends on the assumtion that the bounding boxes are
				// updated synchronously when calling this method.
			}

		}
	}

	@Override
	public String getText() {
		return "Zoom Complete Map";
	}

	@Override
	public String getToolTipText() {
		return "Zoom Complete Map";
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return imageDescriptor;
	}

	/**
	 * This method gets the bounding boxes of all visible drawingObjects (which are applicable
	 * for AutoZoom and scrollbars) and merges them.
	 */
	private AbsoluteRectangle calcBBox() {
		final List<Plugin> list = spyglass.getPluginManager().getVisibleActivePlugins();

		final List<DrawingObject> dobs = new ArrayList<DrawingObject>();

		for (final Plugin plugin : list) {
			if (plugin instanceof Drawable) {
				final Drawable plugin2 = (Drawable) plugin;

				dobs.addAll(plugin2.getAutoZoomDrawingObjects());
			}
		}

		AbsoluteRectangle maxRect1 = null;

		for (final DrawingObject drawingObject : dobs) {
			final AbsoluteRectangle nextRect = drawingObject.getBoundingBox();
			if (nextRect == null) {
				continue;
			}

			if (maxRect1 == null) {
				maxRect1 = nextRect;
			} else {
				maxRect1 = maxRect1.union(nextRect);
			}
		}
		return maxRect1;
	}
}
