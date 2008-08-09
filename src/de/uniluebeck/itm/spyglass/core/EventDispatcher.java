package de.uniluebeck.itm.spyglass.core;

import java.util.EventObject;
import java.util.List;

import org.eclipse.swt.events.MouseEvent;

import de.uniluebeck.itm.spyglass.gui.view.DrawingArea;
import de.uniluebeck.itm.spyglass.plugin.Plugin;
import de.uniluebeck.itm.spyglass.plugin.PluginManager;

public class EventDispatcher {
	
	private final PluginManager pluginManager;
	private final DrawingArea drawingArea;
	
	public EventDispatcher(final PluginManager pluginManager, final DrawingArea drawingArea) {
		this.pluginManager = pluginManager;
		this.drawingArea = drawingArea;
	}
	
	@Override
	public void finalize() throws Throwable {
		
	}
	
	/**
	 * 
	 * @param e
	 */
	public void handleEvent(final EventObject e) {
		final List<Plugin> plugins = pluginManager.getVisiblePlugins();
		for (final Plugin p : plugins) {
			if ((e instanceof MouseEvent) && p.handleEvent((MouseEvent) e, drawingArea)) {
				return;
			}
		}
	}
	
}