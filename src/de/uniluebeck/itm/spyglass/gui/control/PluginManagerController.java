// --------------------------------------------------------------------------------
/**
 *
 */
package de.uniluebeck.itm.spyglass.gui.control;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.gui.view.AppWindow;
import de.uniluebeck.itm.spyglass.gui.view.TransformChangedEvent;
import de.uniluebeck.itm.spyglass.gui.view.TransformChangedListener;
import de.uniluebeck.itm.spyglass.gui.view.TransformChangedEvent.Type;
import de.uniluebeck.itm.spyglass.plugin.Drawable;
import de.uniluebeck.itm.spyglass.plugin.Plugin;
import de.uniluebeck.itm.spyglass.plugin.PluginListChangeListener;
import de.uniluebeck.itm.spyglass.plugin.PluginManager;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;

// --------------------------------------------------------------------------------
/**
 * Handles events from the model, specifically all stuff surrounding plugins
 * and their drawing objects.
 *
 * For each plugin it holds a PluginController to do the main work.
 *
 * @author Dariush Forouher
 *
 */
public class PluginManagerController implements PluginListChangeListener, TransformChangedListener, PropertyChangeListener, DisposeListener {

	protected static Logger log = SpyglassLoggerFactory.getLogger(PluginManagerController.class);

	private Spyglass spyglass;
	private AppWindow appWindow;

	/** Default number of milliseconds to wait between checking for new boundingBox changes */
	private static final int DEFAULT_REDRAW_PERIOD = 100;

	/** Number of milliseconds to wait between checking for new boundingBox changes
	 * when there is currently a preference dialog open */
	private static final int REDUCED_REDRAW_PERIOD = 200;

	/**
	 * List of pluginControllers. Each controller is responsible for handling the drawingobjects
	 * of one plugin.
	 */
	protected final Map<Plugin,PluginController> pluginControllers = Collections.synchronizedMap(new HashMap<Plugin,PluginController>());


	public PluginManagerController(final AppWindow appWindow, final Spyglass spyglass) {
		this.spyglass = spyglass;
		this.appWindow = appWindow;
		registerPluginManager(spyglass.getPluginManager());

		spyglass.getConfigStore().getSpyglassConfig().addPropertyChangeListener("pluginManager", this);

		appWindow.getGui().getDrawingArea().addTransformChangedListener(this);
		appWindow.getGui().getDrawingArea().addDisposeListener(this);

		appWindow.getDisplay().timerExec(DEFAULT_REDRAW_PERIOD, new Runnable() {

			@Override
			public void run() {
				if (appWindow.getGui().getDrawingArea().isDisposed()) {
					return;
				}

				updateBoundingBoxes();

				// HACK: If there is a preference dialog open,
				// use a reduced frequency to increase smoothness
				// of the view
				final int period = spyglass.isThrottleBBoxUpdates() ?
						REDUCED_REDRAW_PERIOD : DEFAULT_REDRAW_PERIOD;

				appWindow.getDisplay().timerExec(period, this);
			}

		});


	}

	/**
	 * Registers Listener to the PluginManager and creats PluginControllers for Plugins
	 * inside the PluginManager.
	 *
	 * Note that at this point we assume that the PluginManager has already fully initialized
	 * all his Plugins (i.e. it is in a "running" state).
	 */
	protected void registerPluginManager(final PluginManager manager) {

		for (final Plugin p : manager.getPlugins()) {

			// sanity check
			if (p.getState() != Plugin.State.ALIVE) {
				throw new IllegalArgumentException("Plugin is not alive!");
			}

			if (p instanceof Drawable) {

				final PluginController c = new PluginController(appWindow.getDisplay(), appWindow.getGui().getDrawingArea(),p);
				pluginControllers.put(p,c);

			}
		}

		spyglass.getPluginManager().addPluginListChangeListener(this);
	}

	protected void unregisterPluginManager(final PluginManager manager) {
		synchronized (pluginControllers) {
			for (final PluginController pc: pluginControllers.values()) {
				pc.disconnect();
			}
			pluginControllers.clear();
		}

		manager.removePluginListChangeListener(this);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Listens for changes of the plug-in manager's list
	 */
	@Override
	public void pluginListChanged(final Plugin p, final ListChangeEvent what) {
		if (p instanceof Drawable) {

			switch (what) {
				case NEW_PLUGIN:

					if (p.getState() != Plugin.State.ALIVE) {
						throw new IllegalArgumentException("Plugin is not alive!");
					}

					final PluginController c = new PluginController(appWindow.getDisplay(), appWindow.getGui().getDrawingArea(), p);
					pluginControllers.put(p,c);

					break;
				case PLUGIN_REMOVED:
					if (p.getState() != Plugin.State.ZOMBIE) {
						throw new IllegalArgumentException("Plugin is not dead yet!");
					}

					pluginControllers.remove(p).disconnect();

					break;
				case NEW_NODE_POSITIONER:
					break;
				case PRIORITY_CHANGED:
					break;
			}
		}
	}

	// ----------------------------------------------------------------
	/**
	 * Listener for change of visibility of ruler
	 */
	@Override
	public void propertyChange(final PropertyChangeEvent evt) {
		final PluginManager oldManager = (PluginManager) evt.getOldValue();
		unregisterPluginManager(oldManager);

		final PluginManager newManager = (PluginManager) evt.getNewValue();
		registerPluginManager(newManager);

	}

	/**
	 * Update the bounding boxes of all plugins
	 */
	public void updateBoundingBoxes() {
		final Collection<PluginController> list;
		synchronized (pluginControllers) {
			list = pluginControllers.values();
		}
		for (final PluginController pc : list) {
			pc.updateBoundingBoxes();
		}
	}

	// ----------------------------------------------------------------
	/**
	 * Listens for transformation of the drawing area
	 */
	@Override
	public void handleEvent(final TransformChangedEvent e) {

		// On ZOOM we have to flush all outstanding bounding box changes. Otherwise
		// the repaint (which will follow soon after this listener finishes)
		// would work with out-of-date bounding boxes.
		if (e.type == Type.ZOOM_MOVE) {
			updateBoundingBoxes();
		}

	}

	// --------------------------------------------------------------------------------
	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.DisposeListener#widgetDisposed(org.eclipse.swt.events.DisposeEvent)
	 */
	@Override
	public void widgetDisposed(final DisposeEvent e) {
		unregisterPluginManager(spyglass.getPluginManager());
	}

}
