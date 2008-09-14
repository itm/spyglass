package de.uniluebeck.itm.spyglass.core;

import java.util.EventObject;
import java.util.List;

import org.eclipse.swt.events.MouseEvent;

import de.uniluebeck.itm.spyglass.gui.view.DrawingArea;
import de.uniluebeck.itm.spyglass.plugin.Plugin;
import de.uniluebeck.itm.spyglass.plugin.PluginManager;

// --------------------------------------------------------------------------------
/**
 * Forwards mouse events to the plug-ins.<br>
 * 
 * @author Sebastian Ebers
 * 
 */
public class EventDispatcher {
	
	private PluginManager pluginManager;
	private DrawingArea drawingArea;
	
	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 * 
	 * @param pluginManager
	 *            the instance which manages the plug-ins
	 * @param drawingArea
	 *            the area where the plug-ins' contents are visualized
	 */
	public EventDispatcher(final PluginManager pluginManager, final DrawingArea drawingArea) {
		this.pluginManager = pluginManager;
		this.drawingArea = drawingArea;
	}
	
	@Override
	public void finalize() throws Throwable {
		pluginManager = null;
		drawingArea = null;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Forwards a event to the first plug-in which can handle it.
	 * 
	 * @param e
	 *            the event to be forwarded
	 */
	public void handleEvent(final EventObject e) {
		final List<Plugin> plugins = pluginManager.getVisibleActivePlugins();
		for (final Plugin p : plugins) {
			if ((e instanceof MouseEvent) && p.handleEvent((MouseEvent) e, drawingArea)) {
				return;
			}
		}
	}
	
}