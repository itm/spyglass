// --------------------------------------------------------------------------------
/**
 *
 */
package de.uniluebeck.itm.spyglass.gui.view;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.plugin.Drawable;
import de.uniluebeck.itm.spyglass.plugin.Plugin;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;
import de.uniluebeck.itm.spyglass.positions.PixelRectangle;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;

// --------------------------------------------------------------------------------
/**
 * This class is responsible for redrawing the drawingArea when it
 * sends out a PaintEvent.
 *
 * @author Dariush Forouher
 *
 */
public class DrawingAreaRenderer implements PaintListener, DisposeListener {

	protected static Logger log = SpyglassLoggerFactory.getLogger(DrawingAreaRenderer.class);

	private Spyglass spyglass;
	private DrawingArea drawingArea;


	/**
	 * This color is used for area outside of the the map
	 */
	private final Color canvasOutOfMapColor = new Color(null, 50, 50, 50);

	/**
	 * This color is used as the background color
	 */
	private final Color canvasBgColor = new Color(null, 255, 255, 255);

	private final static boolean ENABLE_DRAW_PROFILING = false;

	public DrawingAreaRenderer(final DrawingArea drawingArea, final Spyglass spyglass) {
		this.spyglass = spyglass;
		this.drawingArea = drawingArea;

		// Add paint listener to the canvas
		drawingArea.addPaintListener(this);
		drawingArea.addDisposeListener(this);

		drawingArea.setBackground(canvasBgColor);

	}
	// --------------------------------------------------------------------------------
	/**
	 * Renders the visible plug-ins.<br>
	 * The plug-ins provide objects which are drawn into the drawing area.
	 *
	 * @see DrawingObject
	 */
	public void paintControl(final PaintEvent e) {

		e.gc.setBackground(canvasOutOfMapColor);

		final PixelRectangle world = drawingArea.absRect2PixelRect(DrawingArea.getGlobalBoundingBox());
		final PixelRectangle canvas = drawingArea.getDrawingRectangle();

		final int edgeN = world.getUpperLeft().y;
		if (edgeN > 0) {
			e.gc.fillRectangle(0, 0, canvas.getWidth(), edgeN);
		}
		final int edgeE = canvas.getWidth() - world.getWidth() - world.getUpperLeft().x;
		if (edgeE > 0) {
			e.gc.fillRectangle(world.getWidth() + world.getUpperLeft().x, 0, edgeE, canvas.getHeight());
		}
		final int edgeS = canvas.getHeight() - world.getHeight() - world.getUpperLeft().y;
		if (edgeS > 0) {
			e.gc.fillRectangle(0, world.getHeight() + world.getUpperLeft().y, canvas.getWidth(), edgeS);
		}
		final int edgeW = world.getUpperLeft().x;
		if (edgeW > 0) {
			e.gc.fillRectangle(0, 0, edgeW, canvas.getHeight());
		}

		e.gc.setBackground(canvasBgColor);

		final long time = System.nanoTime();

		final PixelRectangle pxArea = new PixelRectangle(e.gc.getClipping().x, e.gc.getClipping().y, e.gc.getClipping().width,
				e.gc.getClipping().height);

		final AbsoluteRectangle area = drawingArea.pixelRect2AbsRect(pxArea);

		final List<Plugin> plugins = spyglass.getPluginManager().getVisibleActivePlugins();

		for (final Plugin plugin : plugins) {
			if (plugin instanceof Drawable) {
				renderPlugin(e.gc, (Drawable) plugin, area);
			}
		}

		if (ENABLE_DRAW_PROFILING) {
			final long time2 = System.nanoTime();
			final double pxCount = pxArea.getHeight() * pxArea.getWidth();
			final boolean clipping = !drawingArea.getClientArea().equals(pxArea.rectangle);
			if (clipping) {
				log.debug(String.format("Partial redraw (%.0f px). Time: %.03f ms (%.0f ns per pixel).", pxCount, (time2 - time) / 1000000d,
						((time2 - time) / pxCount)));
			} else {
				log.debug(String.format("Complete redraw. Time: %.03f ms (%.0f ns per pixel).", (time2 - time) / 1000000d,
						((time2 - time) / pxCount)));
			}			}
	}

	// --------------------------------------------------------------------------------
	/**
	 * Draw all drawing objects inside the bounding box <code>area</code> from the plug-in
	 * <code>plug-in</code> on <code>gc</code>.
	 *
	 * @param gc
	 *            a GC
	 * @param plugin
	 *            a plug-in which is capable of drawing objects
	 * @param area
	 *            Only drawing objects inside this area will be redrawn.
	 * @see Drawable
	 */
	protected void renderPlugin(final GC gc, final Drawable plugin, final AbsoluteRectangle area) {

		if (ENABLE_DRAW_PROFILING) {
			log.debug("Rendering plugin "+plugin+" on "+area);
		}

		final List<DrawingObject> dos = new LinkedList<DrawingObject>(plugin.getDrawingObjects(area));
		for (final DrawingObject object : dos) {

			switch (object.getState()) {
				case ALIVE:
					if (ENABLE_DRAW_PROFILING) {
						log.debug("Rendering DOB "+object);
					}
					object.drawObject(gc);
					break;
				case INFANT:
					log.debug(String.format("Plugin %s contains an unitialized drawing object in its layer: %s (skipping it)", plugin, object));
					break;
				case ZOMBIE:
					log.error(String.format("Plugin %s contains a zombie drawing object in its layer: %s", plugin, object));
			}
		}
	}
	// --------------------------------------------------------------------------------
	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.DisposeListener#widgetDisposed(org.eclipse.swt.events.DisposeEvent)
	 */
	@Override
	public void widgetDisposed(final DisposeEvent e) {
		canvasBgColor.dispose();
		canvasOutOfMapColor.dispose();
	}



}
