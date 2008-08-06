/*
 * ---------------------------------------------------------------------- This file is part of the
 * WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de)
 * project SpyGlass is free software; you can redistribute it and/or modify it under the terms of
 * the BSD License. Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for
 * further details. ------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.gui;

import java.awt.Event;
import java.util.EventObject;
import java.util.List;

import org.apache.log4j.Category;
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
import org.eclipse.swt.widgets.Display;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.core.SpyglassListener;
import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.gui.view.AppWindow;
import de.uniluebeck.itm.spyglass.gui.view.DrawingArea;
import de.uniluebeck.itm.spyglass.plugin.Drawable;
import de.uniluebeck.itm.spyglass.plugin.Plugin;
import de.uniluebeck.itm.spyglass.positions.PixelPosition;
import de.uniluebeck.itm.spyglass.util.SpyglassLogger;

// ------------------------------------------------------------------------------
// --
/**
 * The UI controller is the interface between the core Spyglass functionality and the graphical user
 * interface. It is bound to a specific GUI library. If the GUI must be completely replaced, the UI
 * controller must also be changed/replaced.
 */
public class UIController {
	
	private static Category log = SpyglassLogger.get(UIController.class);
	
	private AppWindow appWindow = null;
	
	private Spyglass spyglass = null;
	
	private final Display display;
	
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
			appWindow.getGui().getCanvas().redraw();
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
			public void mouseDoubleClick(final MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseDown(final MouseEvent arg0) {
				log.debug("mouse down: " + arg0);
				mouseDragInProgress = true;
				mouseDragStartPosition = new PixelPosition(arg0.x, arg0.y);
			}
			
			@Override
			public void mouseUp(final MouseEvent arg0) {
				log.debug("mouse up: " + arg0);
				
				// if moving in progress, stop it.
				if (mouseDragInProgress) {
					
					mouseDragInProgress = false;
					final PixelPosition mouseDragStopPosition = new PixelPosition(arg0.x, arg0.y);
					
					final int deltaX = mouseDragStopPosition.x - mouseDragStartPosition.x;
					final int deltaY = mouseDragStopPosition.y - mouseDragStartPosition.y;
					
					spyglass.getDrawingArea().move(deltaX, deltaY);
				}
			}
			
		}));
		
		/*
		 * move listener: this helps to make moving more smooth
		 */
		appWindow.getGui().getCanvas().addMouseMoveListener((new MouseMoveListener() {
			
			@Override
			public void mouseMove(final MouseEvent arg0) {
				
				// if a movement is in progress, update the drawing area by appling the current
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
	 * 
	 */
	private void render(final GC gc) {
		gc.setBackground(canvasBgColor);
		gc.fillRectangle(appWindow.getGui().getCanvas().getClientArea());
		
		for (final Plugin plugin : spyglass.getPluginManager().getActivePlugins()) {
			
			if (plugin instanceof Drawable) {
				
				renderPlugin(gc, plugin);
				
			}
			
		}
		
		drawDebugMarkers(gc);
		
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
		final List<DrawingObject> dos = ((Drawable) plugin).getDrawingObjects(this.spyglass.getDrawingArea());
		if (dos != null) {
			for (final DrawingObject object : dos) {
				object.draw(this.spyglass.getDrawingArea(), gc);
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
	
	public void fireRedrawEvent() {
		
	}
	
	/**
	 * 
	 * @param e
	 */
	public void PreferencesButtonEvent(final Event e) {
		
	}
	
}
