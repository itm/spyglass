/*
 * ---------------------------------------------------------------------- This file is part of the
 * WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de)
 * project SpyGlass is free software; you can redistribute it and/or modify it under the terms of
 * the BSD License. Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for
 * further details. ------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.core.SpyglassExceptionHandler;
import de.uniluebeck.itm.spyglass.gui.actions.*;
import de.uniluebeck.itm.spyglass.gui.control.UIController;
import de.uniluebeck.itm.spyglass.gui.view.AppWindow;
import de.uniluebeck.itm.spyglass.gui.view.DrawingArea;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;
import de.uniluebeck.itm.tr.util.Logging;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.*;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.DeviceData;
import org.eclipse.swt.tools.internal.Sleak;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import java.io.File;
import java.io.IOException;

// ------------------------------------------------------------------------------

/**
 * Application class for wrapping the Spyglass core class and it's user interface/GUI. It instantiate and injects the
 * core classes that are needed to run the application.
 */
public class SpyglassApp extends ApplicationWindow {

	private static final Logger log = SpyglassLoggerFactory.getLogger(SpyglassApp.class);

	public static Spyglass spyglass;

	private AppWindow appWindow;

	/**
	 * Enables SWT Object tracking. This allows tracking memory leaks (e.g. missing dispose)
	 */
	public static final boolean ENABLE_SLEAK = false;

	// -------------------------------------------------------------------------

	/**
	 * Constructor
	 *
	 * @throws Exception
	 */
	private SpyglassApp() throws Exception {
		super(null);

		Display.setAppName(Constants.SPYGLASS_APP_NAME);
		Display.setAppVersion(Constants.SPYGLASS_APP_VERSION);

		// Model
		SpyglassEnvironment.setIShellPlugin(false);
		spyglass = new Spyglass();

		addMenuBar();

		this.setBlockOnOpen(true);
		addStatusLine();

	}

	// -------------------------------------------------------------------------

	/**
	 * The stand-alone application's entry point
	 *
	 * @param args an array of arguments
	 */
	public static void main(final String[] args) {

		tryToSetLoggingDefaults();

		log.debug("java.library.path=" + System.getProperty("java.library.path"));
		log.debug("java.class.path=" + System.getProperty("java.class.path"));

		// Set an exception handler which will handle uncaught exceptions
		Window.setExceptionHandler(new SpyglassExceptionHandler());

		if (ENABLE_SLEAK) {

			// create a customized Device. Since a Device is a singleton object
			// (at least with currently...) we don't have to save it. Newly created
			// Shells will use it automatically.
			final DeviceData data = new DeviceData();
			data.tracking = true;
			data.debug = true;
			new Display(data);

			// Open sleak
			final Sleak sleak = new Sleak();
			sleak.open();

		}

		SpyglassApp app = null;
		try {
			app = new SpyglassApp();
			app.addToolBar(SWT.None);
			app.addMenuBar();

			app.open();

		} catch (final Exception e) {
			log.error(e, e);
		} finally {
			if (app != null) {
				app.shutdown();
			}
		}

	}

	private static void tryToSetLoggingDefaults() {

		final File spyglassConfigDir = new File(SystemUtils.USER_HOME + File.separator + ".spyglass");
		final File logFile = new File(spyglassConfigDir, "spyglass.log");

		if (!spyglassConfigDir.exists() && !spyglassConfigDir.mkdirs()) {
			System.err.println("Can not create directory \"" + spyglassConfigDir.getAbsolutePath() + "\"!");
			System.exit(1);
		}

		final String defaultPatternLayout = "%-23d{yyyy-MM-dd HH:mm:ss,SSS} | %-30.30t | %-30.30c{1} | %-5p | %m%n";
		final PatternLayout patternLayout = new PatternLayout(defaultPatternLayout);

		try {

			Logging.setLoggingDefaults(
					Level.INFO,
					new FileAppender(patternLayout, logFile.getAbsolutePath()),
					new ConsoleAppender(patternLayout)
			);

		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	// -------------------------------------------------------------------------
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

	// -------------------------------------------------------------------------
	@Override
	protected Control createContents(final Composite parent) {

		appWindow = new AppWindow(spyglass, getShell());

		// Control
		UIController.connect(spyglass, appWindow);

		new ToolbarHandler(getToolBarManager(), spyglass, appWindow);

		spyglass.start();

		return parent;
	}

	// -------------------------------------------------------------------------
	@Override
	protected void configureShell(final Shell shell) {

		super.configureShell(shell);

		shell.setText(Constants.SPYGLASS_APP_NAME);
		shell.setSize(SpyglassEnvironment.getWindowSizeX(), SpyglassEnvironment.getWindowSizeY());

		shell.addControlListener(new ControlAdapter() {

			@SuppressWarnings("synthetic-access")
			@Override
			public void controlResized(final ControlEvent e) {
				try {
					SpyglassEnvironment.setWindowSizeX(shell.getSize().x);
					SpyglassEnvironment.setWindowSizeY(shell.getSize().y);
				} catch (final IOException e1) {
					log.error("Error while saving the size of the Spyglass window", e1);
				}

			}

		}
		);

	}

	// -------------------------------------------------------------------------

	/**
	 * Returns the area where plug-ins can place their objects to be displayed
	 *
	 * @return the area where plug-ins can place their objects to be displayed
	 */
	public DrawingArea getDrawingArea() {
		return appWindow.getGui().getDrawingArea();
	}

	// -------------------------------------------------------------------------

	/**
	 * Shuts the application down
	 */
	public void shutdown() {

		if (spyglass != null) {
			spyglass.shutdown();
		}

	}

	// -------------------------------------------------------------------------

	/**
	 * Creates the part of the menu where the source can be selected
	 *
	 * @return the part of the menu where the source can be selected
	 */
	private MenuManager createSourceMenu() {
		final MenuManager sourceMenu = new MenuManager("&Source");

		sourceMenu.add(new PlaySelectInputAction(getShell(), spyglass));
		sourceMenu.add(new PlayPlayPauseAction(spyglass));

		return sourceMenu;
	}

	// -------------------------------------------------------------------------

	/**
	 * Creates the part of the menu where the recording options can be selected
	 *
	 * @return the part of the menu where the recording options can be selected
	 */
	private MenuManager createRecordMenu() {
		final MenuManager recordMenu = new MenuManager("&Record");

		recordMenu.add(new RecordSelectOutputAction(spyglass));
		recordMenu.add(new RecordRecordAction(spyglass));

		return recordMenu;
	}

	// -------------------------------------------------------------------------

	/**
	 * Creates the part of the menu where the manipulations of the {@link DrawingArea} can be performed
	 *
	 * @return the part of the menu where the manipulations of the {@link DrawingArea} can be performed
	 */
	private MenuManager createMapMenu() {
		final MenuManager mapMenu = new MenuManager("&Map");

		mapMenu.add(new ZoomAction(this, ZoomAction.Type.ZOOM_IN));
		mapMenu.add(new ZoomAction(this, ZoomAction.Type.ZOOM_OUT));
		mapMenu.add(new ZoomCompleteMapAction(this, spyglass));

		return mapMenu;
	}

	// -------------------------------------------------------------------------

	/**
	 * Creates the file part of the menu
	 *
	 * @return the file part of the menu
	 */
	private MenuManager createFileMenu() {
		final MenuManager fileMenu = new MenuManager("&File");

		fileMenu.add(new LoadConfigurationAction(spyglass));
		fileMenu.add(new StoreConfigurationAction(spyglass));
		fileMenu.add(new Separator());
		fileMenu.add(new OpenPreferencesAction(spyglass, getShell()));
		fileMenu.add(new Separator());
		fileMenu.add(new ExitSpyglassAction(this));

		return fileMenu;
	}

}
