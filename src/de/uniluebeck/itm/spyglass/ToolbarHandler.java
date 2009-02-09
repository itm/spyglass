// --------------------------------------------------------------------------------
/**
 * 
 */
package de.uniluebeck.itm.spyglass;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.gui.actions.OpenPreferencesAction;
import de.uniluebeck.itm.spyglass.gui.actions.PlayPlayPauseAction;
import de.uniluebeck.itm.spyglass.gui.actions.PlayResetAction;
import de.uniluebeck.itm.spyglass.gui.actions.PlaySelectInputAction;
import de.uniluebeck.itm.spyglass.gui.actions.RecordRecordAction;
import de.uniluebeck.itm.spyglass.gui.actions.RecordSelectOutputAction;
import de.uniluebeck.itm.spyglass.gui.actions.ZoomAction;
import de.uniluebeck.itm.spyglass.gui.actions.ZoomCompleteMapAction;
import de.uniluebeck.itm.spyglass.gui.actions.ZoomAction.Type;
import de.uniluebeck.itm.spyglass.gui.view.AppWindow;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;

// --------------------------------------------------------------------------------
/**
 * This class creates and manages the toolbar
 * 
 * NOTE: The way the toolbar is created seems a bit fishy. Maybe this is really the
 * right thing to do, but likely there is a saner way to do this.
 * 
 * NOTE: this class contains some framework for handling mouse-events of the zoom
 * buttons. This is because the Action-Interface doesn't allow to listen to MouseEvents.
 * Again there is probable a smarter way to do this, but again I don't know it.
 * 
 * @author Dariush Forouher
 *
 */
public class ToolbarHandler {

	private static Logger log = SpyglassLoggerFactory.getLogger(ToolbarHandler.class);

	/**
	 * The coolbar the toolbar is based on
	 */
	private CoolBar coolbar;

	private ToolBar toolbar;

	private Spyglass spyglass;
	private AppWindow appWindow;
	
	/**
	 * the manager used for the toolbar
	 */
	private ToolBarManager man;

	private ZoomAction zoomInAction;
	private ZoomAction zoomOutAction;

	private ToolItem zoomInItem = null;
	private ToolItem zoomOutItem = null;

	public ToolbarHandler(final CoolBar coolbar, final Spyglass spyglass, final AppWindow appWindow) {
		this.coolbar = coolbar;
		this.spyglass = spyglass;
		this.appWindow = appWindow;
		
		man = new ToolBarManager();
		
		man.createControl(coolbar);
		
		addIcons();
	}

	public ToolbarHandler(final ToolBarManager man, final Spyglass spyglass, final AppWindow appWindow) {
		this.spyglass = spyglass;
		this.appWindow = appWindow;
		
		this.man = man;

		this.toolbar = man.getControl();

		addIcons();
	}
	

	/**
	 * Add icons to the given coolbar
	 */
	private void addIcons() {
		
		man.add(new PlaySelectInputAction(man.getControl().getShell(), spyglass));
		man.add(new PlayPlayPauseAction(spyglass));
		man.add(new PlayResetAction(spyglass));
		man.add(new RecordSelectOutputAction(spyglass));
		man.add(new RecordRecordAction(spyglass));
		zoomInAction = new ZoomAction(appWindow.getGui().getDrawingArea(), Type.ZOOM_IN);
		man.add(zoomInAction);
		zoomOutAction = new ZoomAction(appWindow.getGui().getDrawingArea(), Type.ZOOM_OUT);
		man.add(zoomOutAction);
		man.add(new ZoomCompleteMapAction(spyglass, appWindow.getGui().getDrawingArea()));
		man.add(new OpenPreferencesAction(man.getControl().getShell(), spyglass));
		
		man.update(false);

		// TODO: this can be done smarter
		if (coolbar != null) {
			final Point p = man.getControl().computeSize(SWT.DEFAULT, SWT.DEFAULT);
			final CoolItem item = new CoolItem (coolbar, SWT.NONE);
			item.setPreferredSize (p);
		}

		// save the items for ZoomIn/Out for later
		zoomInItem = man.getControl().getItems()[5];
		zoomOutItem = man.getControl().getItems()[6];
	
		man.getControl().addListener(SWT.MouseDown, listener);
		man.getControl().addListener(SWT.MouseUp, listener);
				
		log.debug("Created toolbar");
	}

	/**
	 * This listener handles Mouse Down/Up events and passes them on
	 * to the zoomActions
	 */
	Listener listener = new Listener() {
	
		@Override
		public void handleEvent(final Event event) {
			
			if (event.button!=1) {
				return;
			}
	
			if (event.type==SWT.MouseDown) {
				if (zoomInItem.getBounds().contains(event.x, event.y)) {
					zoomInAction.setChecked(true);
				}
				else if (zoomOutItem.getBounds().contains(event.x, event.y)) {
					zoomOutAction.setChecked(true);
				}
				
			} else if (event.type==SWT.MouseUp) {
				if (zoomInAction.isChecked()) {
					zoomInAction.setChecked(false);
				}
				else if (zoomOutAction.isChecked()) {
					zoomOutAction.setChecked(false);
				}
			}
		}
		
	};

	public void dispose() {
		if (man != null) {
			man.removeAll();
			man.dispose();
		}
		if (coolbar != null) {
			coolbar.dispose();
		}
	}
}
