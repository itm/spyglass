package de.uniluebeck.itm.spyglass.core;

import java.util.EventObject;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.events.MouseEvent;

import de.uniluebeck.itm.spyglass.gui.view.DrawingArea;
import de.uniluebeck.itm.spyglass.plugin.Plugin;
import de.uniluebeck.itm.spyglass.plugin.PluginManager;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;

// --------------------------------------------------------------------------------
/**
 * Forwards mouse events to the plug-ins.<br>
 * 
 * @author Sebastian Ebers
 * 
 */
public class EventDispatcher {

	private static final Logger log = SpyglassLoggerFactory.getLogger(EventDispatcher.class);

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
	public void finalize() {
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
		// this has to be done in reverse order since the "normal" order is used for painting which
		// means that the topmost element is actually the last element in the list
		for (int i = plugins.size() - 1; i >= 0; i--) {
			
			try {
				// for the moment only mouse events are dispatched to plugins
				if ((e instanceof MouseEvent) && plugins.get(i).handleEvent((MouseEvent) e, drawingArea)) {
					return;
				}
			} catch (final Exception e1) {
				log.error("Plugin "+plugins.get(i)+" threw an exception while handling an event",e1);
			}
		}
	}

}