/*
 * ---------------------------------------------------------------------- This file is part of the
 * WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de)
 * project SpyGlass is free software; you can redistribute it and/or modify it under the terms of
 * the BSD License. Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for
 * further details. ------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.DeviceData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.gui.UIController;
import de.uniluebeck.itm.spyglass.gui.actions.ExitSpyglassAction;
import de.uniluebeck.itm.spyglass.gui.actions.LoadConfigurationAction;
import de.uniluebeck.itm.spyglass.gui.actions.OpenPreferencesAction;
import de.uniluebeck.itm.spyglass.gui.actions.PlayPlayPauseAction;
import de.uniluebeck.itm.spyglass.gui.actions.PlaySelectInputAction;
import de.uniluebeck.itm.spyglass.gui.actions.RecordRecordAction;
import de.uniluebeck.itm.spyglass.gui.actions.RecordSelectOutputAction;
import de.uniluebeck.itm.spyglass.gui.actions.StoreConfigurationAction;
import de.uniluebeck.itm.spyglass.gui.actions.ZoomAction;
import de.uniluebeck.itm.spyglass.gui.actions.ZoomCompleteMapAction;
import de.uniluebeck.itm.spyglass.gui.view.AppWindow;
import de.uniluebeck.itm.spyglass.gui.view.DrawingArea;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;

// ------------------------------------------------------------------------------
// --
/**
 * Application class for wrapping the Spyglass core class and it's user interface/GUI. It
 * instantiate and injects the core classes that are needed to run the application.
 */
public class SpyglassApp extends ApplicationWindow {
	private static Logger log = SpyglassLoggerFactory.getLogger(SpyglassApp.class);
	
	private Spyglass spyglass;
		
	private UIController uic;

	private static Shell shell;

	private AppWindow appWindow;
	
	// -------------------------------------------------------------------------
	/**
	 * @throws Exception 
	 * @throws IOException 
	 * 
	 */
	public SpyglassApp(final Shell shell) throws Exception {
		super(shell);
		spyglass = new Spyglass();
		

		this.setBlockOnOpen(true);
		addStatusLine();
		addToolBar(SWT.None);

		addMenuBar();

	}

	// -------------------------------------------------------------------------
	/**
	 * @throws Exception 
	 * @throws IOException 
	 * 
	 */
	public static void main(final String[] args) {
	
		// SWT stuff
		final DeviceData data = new DeviceData();
		data.tracking = true;
		data.debug = true;
		final Display display = new Display(data);
		shell = new Shell(display);
	
		SpyglassApp app = null;
		try {
			app = new SpyglassApp(shell);
			app.open();
			
		} catch (final Exception e) {
			log.error(e,e);
		} finally {
			if (app != null) {
				app.shutdown();
			}
		}
		
	
	}

	@Override
	protected MenuManager createMenuManager() {
		final MenuManager man = super.createMenuManager();
		
        final MenuManager fileMenu = createFileMenu();
        final MenuManager mapMenu = createMapMenu();
        final MenuManager sourceMenu = createSourceMenu();
        final MenuManager recordMenu = createRecordMenu();

        man.add(fileMenu);
        man.add(mapMenu);
        man.add(sourceMenu);
        man.add(recordMenu);
        
        return man;

	}

	@Override
	protected Control createContents(final Composite parent) {
		
		// Model
		SpyglassEnvironment.setIShellPlugin(false);

		appWindow = new AppWindow(spyglass, getShell());
		
		// Control
		uic = new UIController(spyglass, appWindow);
		
		new ToolbarHandler(getToolBarManager(), spyglass, appWindow);

		return parent;
	}

	@Override
	protected void configureShell(final Shell shell) {
		super.configureShell(shell);
		
		shell.setText("Spyglass");
		shell.setSize(SpyglassEnvironment.getWindowSizeX(), 
				SpyglassEnvironment.getWindowSizeY());
		
		shell.addControlListener(new ControlAdapter() {

			@Override
			public void controlResized(final ControlEvent e) {
				log.info("Shell resized.");
				try {
					SpyglassEnvironment.setWindowSizeX(shell.getSize().x);
					SpyglassEnvironment.setWindowSizeY(shell.getSize().y);
				} catch (final IOException e1) {
					log.error(e1,e1);
				}
				
			}
			
		});
		
	}
	
	public DrawingArea getDrawingArea() {
		return appWindow.getGui().getDrawingArea();
	}
	
	public void shutdown() {

		if (uic != null) {
			uic.shutdown();
		}

		if (spyglass != null) {
			spyglass.shutdown();
		}
		
	}
	
	private MenuManager createSourceMenu() {
		final MenuManager sourceMenu = new MenuManager("&Source");
		
		sourceMenu.add(new PlaySelectInputAction(shell, spyglass));
		sourceMenu.add(new PlayPlayPauseAction(spyglass));
		
		return sourceMenu;
	}

	
	private MenuManager createRecordMenu() {
		final MenuManager recordMenu = new MenuManager("&Record");
		
		recordMenu.add(new RecordSelectOutputAction(spyglass));
		recordMenu.add(new RecordRecordAction(spyglass));
		
		return recordMenu;
	}

	private MenuManager createMapMenu() {
		final MenuManager mapMenu = new MenuManager("&Map");
		
		mapMenu.add(new ZoomAction(this, ZoomAction.Type.ZOOM_IN));
		mapMenu.add(new ZoomAction(this, ZoomAction.Type.ZOOM_OUT));
		mapMenu.add(new ZoomCompleteMapAction(this, spyglass));
		
		return mapMenu;
	}

	private MenuManager createFileMenu() {
		final MenuManager fileMenu = new MenuManager("&File");
		
		fileMenu.add(new LoadConfigurationAction(spyglass));
		fileMenu.add(new StoreConfigurationAction(spyglass));
		fileMenu.add(new Separator());
		fileMenu.add(new OpenPreferencesAction(shell, spyglass));
		fileMenu.add(new Separator());
		fileMenu.add(new ExitSpyglassAction(this));
		
		return fileMenu;
	}
	
}
