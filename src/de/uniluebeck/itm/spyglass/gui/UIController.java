/*
 * ---------------------------------------------------------------------- This file is part of the
 * WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de)
 * project SpyGlass is free software; you can redistribute it and/or modify it under the terms of
 * the BSD License. Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for
 * further details. ------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.gui;

import java.awt.Event;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;

import de.uniluebeck.itm.spyglass.core.EventDispatcher;
import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.core.SpyglassListener;
import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.gui.view.AppWindow;
import de.uniluebeck.itm.spyglass.gui.view.DrawingArea;
import de.uniluebeck.itm.spyglass.plugin.Drawable;
import de.uniluebeck.itm.spyglass.plugin.Plugin;
import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;
import de.uniluebeck.itm.spyglass.positions.PixelPosition;
import de.uniluebeck.itm.spyglass.positions.PixelRectangle;
import de.uniluebeck.itm.spyglass.util.SpyglassLogger;

// ------------------------------------------------------------------------------
// --
/**
 * The UI controller is the interface between the core Spyglass functionality and the graphical user
 * interface. It is bound to a specific GUI library. If the GUI must be completely replaced, the UI
 * controller must also be changed/replaced.
 */
public class UIController {
	
	private static Logger log = SpyglassLogger.get(UIController.class);
	
	private AppWindow appWindow = null;
	
	private Spyglass spyglass = null;
	
	private final Display display;
	
	/** User events will be dispatched here */
	private final EventDispatcher eventDispatcher;
	
	/**
	 * This color is used for area outside of the the map
	 */
	private final Color canvasOutOfMapColor = new Color(null, 50, 50, 50);
	
	/**
	 * This color is used as the background color
	 */
	private final Color canvasBgColor = new Color(null, 255, 255, 255);
	
	/**
	 * Number of pixels to be moved when Up/Down/Left/right is pressed.
	 */
	private final int MOVE_OFFSET = 20;
	
	/**
	 * True, while the user moves the map via mouse
	 */
	private volatile boolean mouseDragInProgress = false;
	
	/**
	 * the starting point of the movement buisness.
	 */
	private volatile PixelPosition mouseDragStartPosition = null;
	
	// --------------------------------------------------------------------------
	// ------
	/**
	 * 
	 */
	private final Runnable paintRunnable = new Runnable() {
		public void run() {
			try {
				final Canvas c = appWindow.getGui().getCanvas();
				if (!c.isDisposed()) {
					c.redraw();
				} else {
					log.info("The paintRunnable-thread stopped");
				}
			} catch (final Exception e) {
				log.error(e, e);
			}
		}
	};
	
	// --------------------------------------------------------------------------
	// ------
	/**
	 * 
	 */
	public UIController(final Spyglass spyglass, final AppWindow appWindow) {
		if ((spyglass == null) || (appWindow == null)) {
			throw new IllegalArgumentException();
		}
		
		this.spyglass = spyglass;
		this.appWindow = appWindow;
		this.eventDispatcher = new EventDispatcher(spyglass.getPluginManager(), spyglass
				.getDrawingArea());
		
		display = appWindow.getDisplay();
		
		init();
	}
	
	// --------------------------------------------------------------------------
	// ------
	/**
	 * 
	 */
	private void init() {
		// Add listeners
		appWindow.getGui().getCanvas().addPaintListener(new PaintListener() {
			public void paintControl(final PaintEvent e) {
				// log.debug("Redraw of " + new PixelRectangle(e.x, e.y, e.width, e.height));
				render(e.gc);
			}
		});
		
		/*
		 * Key listener: for moving
		 */
		appWindow.getGui().getCanvas().addKeyListener(((new KeyListener() {
			
			@Override
			public void keyPressed(final KeyEvent arg0) {
				log.debug("pressed" + arg0);
				if (arg0.keyCode == 16777219) {
					spyglass.getDrawingArea().move(-MOVE_OFFSET, 0);
				}
				if (arg0.keyCode == 16777220) {
					spyglass.getDrawingArea().move(MOVE_OFFSET, 0);
				}
				if (arg0.keyCode == 16777217) {
					spyglass.getDrawingArea().move(0, -MOVE_OFFSET);
				}
				if (arg0.keyCode == 16777218) {
					spyglass.getDrawingArea().move(0, MOVE_OFFSET);
				}
			}
			
			@Override
			public void keyReleased(final KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		})));
		
		/*
		 * Mouse wheel: used for zooming
		 */
		appWindow.getGui().getCanvas().addMouseWheelListener((new MouseWheelListener() {
			
			@Override
			public void mouseScrolled(final MouseEvent arg0) {
				if (arg0.count > 0) {
					spyglass.getDrawingArea().zoomIn(arg0.x, arg0.y);
				} else {
					spyglass.getDrawingArea().zoomOut(arg0.x, arg0.y);
				}
			}
		}));
		
		/*
		 * mouse drag and drop: used for moving the drawing area.
		 */
		appWindow.getGui().getCanvas().addMouseListener((new MouseListener() {
			
			@Override
			public void mouseDoubleClick(final MouseEvent e) {
				if (e.button == 1) {
					eventDispatcher.handleEvent(e);
				}
			}
			
			@Override
			public void mouseDown(final MouseEvent e) {
				if (e.button > 1) {
					eventDispatcher.handleEvent(e);
				}
				log.debug("mouse down: " + e);
				mouseDragInProgress = true;
				mouseDragStartPosition = new PixelPosition(e.x, e.y);
			}
			
			@Override
			public void mouseUp(final MouseEvent arg0) {
				log.debug("mouse up: " + arg0);
				mouseDragInProgress = false;
				// if moving in progress, stop it.
				// if (mouseDragInProgress) {
				//					
				// mouseDragInProgress = false;
				// final PixelPosition mouseDragStopPosition = new
				// PixelPosition(arg0.x, arg0.y);
				//					
				// final int deltaX = mouseDragStopPosition.x -
				// mouseDragStartPosition.x;
				// final int deltaY = mouseDragStopPosition.y -
				// mouseDragStartPosition.y;
				//					
				// spyglass.getDrawingArea().move(deltaX, deltaY);
				// }
			}
			
		}));
		
		/*
		 * move listener: this helps to make moving more smooth
		 */
		appWindow.getGui().getCanvas().addMouseMoveListener((new MouseMoveListener() {
			
			@Override
			public void mouseMove(final MouseEvent arg0) {
				
				// if a movement is in progress, update the drawing area by
				// appling the current
				// delta.
				if (mouseDragInProgress) {
					
					final PixelPosition mouseDragStopPosition = new PixelPosition(arg0.x, arg0.y);
					
					final int deltaX = mouseDragStopPosition.x - mouseDragStartPosition.x;
					final int deltaY = mouseDragStopPosition.y - mouseDragStartPosition.y;
					
					spyglass.getDrawingArea().move(deltaX, deltaY);
					
					mouseDragStartPosition = mouseDragStopPosition;
				}
				
			}
		}));
		
		// Trigger asynchronous redraw (must happen in the gui thread, see SWT
		// documentation)
		spyglass.addSpyglassListener(new SpyglassListener() {
			public void redraw(final EventObject e) {
				// if (log.isDebugEnabled()) {
				// log.debug("Triggering redraw event from " + e.getSource());
				// }
				
				// Asynchrony execution, waiting for operation to finish
				display.asyncExec(paintRunnable);
			}
		});
	}
	
	/**
	 * Renders the visible plug-in's.<br>
	 * The plug-ins provide objects which are drawn into the drawing area.
	 * 
	 * @param gc
	 *            the graphic context used to actually draw the provided objects
	 * @see DrawingObject
	 */
	private void render(final GC gc) {
		
		drawBackground(gc);
		
		final List<Plugin> plugins = spyglass.getPluginManager().getVisibleActivePlugins();
		// drawDebugGlobalBoundingBox(gc, plugins);
		for (final Plugin plugin : plugins) {
			if (plugin instanceof Drawable) {
				renderPlugin(gc, plugin);
			}
		}
		
		// drawDebugMarkers(gc);
		// drawDebugMarkers2(gc);
	}
	
	/**
	 * Draws a bounding box around all objects
	 */
	private void drawDebugGlobalBoundingBox(final GC gc, final List<Plugin> list) {
		final List<DrawingObject> dobs = new ArrayList<DrawingObject>();
		
		for (final Plugin plugin : list) {
			if (plugin instanceof Drawable) {
				final Drawable plugin2 = (Drawable) plugin;
				
				dobs.addAll(plugin2.getAutoZoomDrawingObjects());
			}
		}
		
		AbsoluteRectangle maxRect = null;
		
		for (final DrawingObject drawingObject : dobs) {
			final AbsoluteRectangle nextRect = drawingObject.getBoundingBox();
			if (nextRect == null) {
				continue;
			}
			
			if (maxRect == null) {
				maxRect = nextRect;
			} else {
				maxRect = maxRect.union(nextRect);
			}
		}
		if (maxRect != null) {
			final PixelRectangle pxRect = spyglass.getDrawingArea().absRect2PixelRect(maxRect);
			gc.setForeground(new Color(null, 0, 255, 0));
			gc.drawRectangle(pxRect.getUpperLeft().x, pxRect.getUpperLeft().y, pxRect.getWidth(),
					pxRect.getHeight());
		}
	}
	
	/**
	 * Draw the background.
	 * 
	 * Space which lies inside the map (-2^15 to 2^15) will be colored in <code>canvasBgColor</code>
	 * , whereas space outside this area is colored <code>canvasOutOfMapColor</code>.
	 */
	private void drawBackground(final GC gc) {
		gc.setBackground(canvasOutOfMapColor);
		gc.fillRectangle(appWindow.getGui().getCanvas().getClientArea());
		
		// TODO: move this code into DrawingArea
		
		final AbsolutePosition absPoint = new AbsolutePosition();
		absPoint.x = -32768;
		absPoint.y = -32768;
		
		final AbsoluteRectangle completeMap = new AbsoluteRectangle();
		completeMap.setUpperLeft(absPoint);
		completeMap.setHeight(2 * 32768);
		completeMap.setWidth(2 * 32768);
		
		PixelRectangle pxRect = null;
		
		final AbsoluteRectangle visibleArea = spyglass.getDrawingArea()
				.getAbsoluteDrawingRectangle();
		
		// This is a workaround, since GC has problems with huge negative numbers
		if (completeMap.contains(visibleArea)) {
			pxRect = spyglass.getDrawingArea().getDrawingRectangle();
		} else {
			pxRect = spyglass.getDrawingArea().absRect2PixelRect(completeMap);
		}
		
		gc.setBackground(canvasBgColor);
		gc.fillRectangle(pxRect.getUpperLeft().x, pxRect.getUpperLeft().y, pxRect.getWidth(),
				pxRect.getHeight());
	}
	
	/**
	 * positioning markers to calibrate the borders
	 * 
	 * @param gc
	 */
	private void drawDebugMarkers2(final GC gc) {
		final DrawingArea da = this.spyglass.getDrawingArea();
		final DrawingObject dob = new de.uniluebeck.itm.spyglass.drawing.primitive.Circle();
		dob.getPosition().x = -32768;
		dob.getPosition().y = -32768;
		dob.draw(da, gc);
		dob.setColor(0, 0, 255);
		
		final DrawingObject dob2 = new de.uniluebeck.itm.spyglass.drawing.primitive.Circle();
		dob2.getPosition().x = 32768;
		dob2.getPosition().y = -32768;
		dob2.draw(da, gc);
		dob2.setColor(0, 0, 255);
		
		final DrawingObject dob3 = new de.uniluebeck.itm.spyglass.drawing.primitive.Circle();
		dob3.getPosition().x = -32768;
		dob3.getPosition().y = 32768;
		dob3.draw(da, gc);
		dob3.setColor(0, 0, 255);
		
		final DrawingObject dob4 = new de.uniluebeck.itm.spyglass.drawing.primitive.Circle();
		dob4.getPosition().x = 32768;
		dob4.getPosition().y = 32768;
		dob4.draw(da, gc);
		dob4.setColor(0, 0, 255);
		
	}
	
	/**
	 * positioning markers to calibrate the zoom
	 * 
	 * @param gc
	 */
	private void drawDebugMarkers(final GC gc) {
		final DrawingArea da = this.spyglass.getDrawingArea();
		final DrawingObject dob = new de.uniluebeck.itm.spyglass.drawing.primitive.Circle();
		dob.getPosition().x = 10;
		dob.getPosition().y = 10;
		dob.draw(da, gc);
		
		final DrawingObject dob2 = new de.uniluebeck.itm.spyglass.drawing.primitive.Circle();
		dob2.getPosition().x = 100;
		dob2.getPosition().y = 100;
		dob2.draw(da, gc);
		
		final DrawingObject dob3 = new de.uniluebeck.itm.spyglass.drawing.primitive.Circle();
		dob3.getPosition().x = -100;
		dob3.getPosition().y = -100;
		dob3.draw(da, gc);
		
		final DrawingObject dob4 = new de.uniluebeck.itm.spyglass.drawing.primitive.Circle();
		dob4.getPosition().x = 100;
		dob4.getPosition().y = -100;
		dob4.draw(da, gc);
		
		final DrawingObject dob5 = new de.uniluebeck.itm.spyglass.drawing.primitive.Circle();
		dob5.getPosition().x = -100;
		dob5.getPosition().y = 100;
		dob5.draw(da, gc);
		
	}
	
	private void renderPlugin(final GC gc, final Plugin plugin) {
		final List<DrawingObject> dos = new LinkedList<DrawingObject>(((Drawable) plugin)
				.getDrawingObjects(this.spyglass.getDrawingArea()));
		final DrawingArea area = spyglass.getDrawingArea();
		if (dos != null) {
			for (final DrawingObject object : dos) {
				object.draw(area, gc);
			}
			// log.error("The plugin " + plugin + " did provide any drawing
			// objects!");
		} else {
			log.error("The plugin " + plugin + " did not provide any drawing objects!");
		}
	}
	
	// --------------------------------------------------------------------------
	// ------
	/**
	 * 
	 */
	public AppWindow getAppWindow() {
		return appWindow;
	}
	
	/**
	 * 
	 * @param e
	 */
	public void PreferencesButtonEvent(final Event e) {
		
	}
	
}
