/*
 * ---------------------------------------------------------------------- This file is part of the
 * WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de)
 * project SpyGlass is free software; you can redistribute it and/or modify it under the terms of
 * the BSD License. Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for
 * further details. ------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.gui.view;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.cloudgarden.resource.SWTResourceManager;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.plugin.globalinformation.GlobalInformationPlugin;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;

// --------------------------------------------------------------------------------
/**
 * Instances of this class manager SpyGlass's graphical user Interface
 * 
 * @author Sebastian Ebers and others
 */
public class SpyglassGuiComponent extends org.eclipse.swt.widgets.Composite {

	{
		// Register as a resource user - SWTResourceManager will
		// handle the obtaining and disposing of resources
		SWTResourceManager.registerResourceUser(this);
	}

	private static final Logger log = SpyglassLoggerFactory.getLogger(SpyglassGuiComponent.class);

	private DrawingArea canvas;

	private RulerArea unitArea;
	private RulerArea rulerH;
	private RulerArea rulerV;

	private static int defaultRulerWidth = 30;

	private GlobalInformationBar globalInformationBar;

	// --------------------------------------------------------------------------------
	/**
	 * Displays this org.eclipse.swt.widgets.Composite inside a new Shell.
	 * 
	 * @param args
	 *            the arguments
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		showGUI();
	}

	// --------------------------------------------------------------------------------
	/**
	 * Displays this org.eclipse.swt.widgets.Composite inside a new Shell.
	 * 
	 * @throws IOException
	 */
	public static void showGUI() throws IOException {
		final Display display = Display.getDefault();
		final Shell shell = new Shell(display);
		final SpyglassGuiComponent inst = new SpyglassGuiComponent(shell, SWT.NULL, new Spyglass());
		final Point size = inst.getSize();
		shell.setLayout(new FillLayout());
		shell.layout();

		if ((size.x == 0) && (size.y == 0)) {
			inst.pack();
			shell.pack();
		} else {
			final Rectangle shellBounds = shell.computeTrim(0, 0, size.x, size.y);
			shell.setSize(shellBounds.width, shellBounds.height);
		}

		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

	}

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 * 
	 * @param parent
	 *            the parent widget
	 * @param style
	 *            the style to be used
	 * @param spyglass
	 *            the <code>SpyGlass</code> instance
	 */
	public SpyglassGuiComponent(final Composite parent, final int style, final Spyglass spyglass) {
		super(parent, style);
		initGUI(spyglass);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Initializes the graphical user interface.
	 * 
	 * @param spyglass
	 *            the <code>SpyGlass</code> instance
	 */
	private void initGUI(final Spyglass spyglass) {
		try {

			this.setLayout(new FillLayout());

			final SashForm form = new SashForm(this, SWT.HORIZONTAL);
			form.setLayout(new FillLayout());

			{
				final Composite drawingAreaWrapper = new Composite(form, SWT.BORDER);
				drawingAreaWrapper.setLayout(new FillLayout());
				initDrawingArea(drawingAreaWrapper, spyglass);
			}

			{
				final Composite wrapper = new Composite(form, SWT.BORDER);
				wrapper.setLayout(new FillLayout());
				initGlobalInformationBar(wrapper, spyglass);
			}

			form.setWeights(new int[] { 85, 15 });

		} catch (final Exception e) {
			log.error(e, e);
		}
	}

	// --------------------------------------------------------------------------------
	/**
	 * Initializes the area where all nodes etc. are painted on.
	 * 
	 * @param parent
	 *            the parent composite
	 * @param spyglass
	 *            the <code>SpyGlass</code> instance
	 */
	private void initDrawingArea(final Composite parent, final Spyglass spyglass) {
		try {
			final GridLayout thisLayout = new GridLayout();
			thisLayout.makeColumnsEqualWidth = true;
			parent.setLayout(thisLayout);
			parent.setSize(678, 472);
			{
				final Composite compositeDrawingArea = new Composite(parent, SWT.NONE);
				final GridLayout composite1Layout = new GridLayout();
				// composite1Layout.makeColumnsEqualWidth = true;
				composite1Layout.marginHeight = 0;
				composite1Layout.marginWidth = 0;
				composite1Layout.numColumns = 2;
				composite1Layout.verticalSpacing = 0;
				composite1Layout.horizontalSpacing = 0;
				final GridData composite1LData = new GridData();
				composite1LData.grabExcessHorizontalSpace = true;
				composite1LData.horizontalAlignment = GridData.FILL;
				composite1LData.verticalAlignment = GridData.FILL;
				composite1LData.grabExcessVerticalSpace = true;
				compositeDrawingArea.setLayoutData(composite1LData);
				compositeDrawingArea.setLayout(composite1Layout);
				compositeDrawingArea.setBackground(SWTResourceManager.getColor(255, 255, 255));

				int rulerWidth = 0;
				if (spyglass.getConfigStore().getSpyglassConfig().getGeneralSettings().getShowRuler()) {
					rulerWidth = defaultRulerWidth;
				}

				// begin creation of ruler canvases
				{
					final GridData unitData = new GridData();
					unitData.heightHint = rulerWidth;
					unitData.widthHint = rulerWidth;
					unitArea = new RulerArea(compositeDrawingArea, RulerArea.UNIT);
					unitArea.setLayoutData(unitData);
				}

				{
					final GridData rulerHData = new GridData();
					rulerHData.heightHint = rulerWidth;
					rulerHData.horizontalAlignment = SWT.FILL;
					rulerH = new RulerArea(compositeDrawingArea, RulerArea.HORIZONTAL);
					rulerH.setLayoutData(rulerHData);
				}

				{
					final GridData rulerVData = new GridData();
					rulerVData.widthHint = rulerWidth;
					rulerVData.verticalAlignment = SWT.FILL;
					rulerV = new RulerArea(compositeDrawingArea, RulerArea.VERTICAL);
					rulerV.setLayoutData(rulerVData);
				}
				// end creation of ruler canvases
				{
					final GridData canvas1LData = new GridData();
					canvas1LData.horizontalAlignment = GridData.FILL;
					canvas1LData.grabExcessHorizontalSpace = true;
					canvas1LData.verticalAlignment = GridData.FILL;
					canvas1LData.grabExcessVerticalSpace = true;
					canvas = new DrawingArea(compositeDrawingArea, SWT.None, spyglass);
					canvas.setLayoutData(canvas1LData);

					rulerH.setDrawingArea(canvas);
					rulerV.setDrawingArea(canvas);
				}
			}
			parent.layout();
		} catch (final Exception e) {
			log.error(e, e);
		}
	}

	// --------------------------------------------------------------------------------
	/**
	 * Initializes the widgets where {@link GlobalInformationPlugin}'s can attach information
	 * 
	 * @param parent
	 *            the parent widget
	 */
	private void initGlobalInformationBar(final Composite parent, final Spyglass spyglass) {
		globalInformationBar = new GlobalInformationBar(parent, SWT.V_SCROLL, spyglass);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns the widget where all nodes etc. are painted on.
	 * 
	 * @return the widget where all nodes etc. are painted on
	 */
	public DrawingArea getDrawingArea() {
		return canvas;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns a bar where plug-ins of the type {@link GlobalInformationPlugin} can attach
	 * information
	 * 
	 * @return the globalInformationBar a bar where plug-ins of the type
	 *         {@link GlobalInformationPlugin} can attach information
	 */
	public GlobalInformationBar getGlobalInformationBar() {
		return globalInformationBar;
	}

	public void changeRulerVis() {
		log.debug("Ruler visibility changed!");
		final GridData dataD = (GridData) unitArea.getLayoutData();
		final GridData dataV = (GridData) rulerV.getLayoutData();
		final GridData dataH = (GridData) rulerH.getLayoutData();

		final int curVal = dataD.widthHint;
		if (curVal == 0) {
			dataD.widthHint = defaultRulerWidth;
			dataD.heightHint = defaultRulerWidth;
			dataV.widthHint = defaultRulerWidth;
			dataH.heightHint = defaultRulerWidth;
		} else {
			dataD.widthHint = 0;
			dataD.heightHint = 0;
			dataV.widthHint = 0;
			dataH.heightHint = 0;
		}

		unitArea.setLayoutData(dataD);
		rulerV.setLayoutData(dataV);
		rulerH.setLayoutData(dataH);
		// final Point size2 = new Point(200, 200);
		// canvas.setSize(size2);

		canvas.getParent().layout();
	}

	public RulerArea getRulerH() {
		return rulerH;
	}

	public RulerArea getRulerV() {
		return rulerV;
	}

	public RulerArea getUnitArea() {
		return unitArea;
	}
}
