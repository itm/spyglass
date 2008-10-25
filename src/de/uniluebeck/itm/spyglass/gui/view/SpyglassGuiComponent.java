/*
 * ---------------------------------------------------------------------- This file is part of the
 * WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de)
 * project SpyGlass is free software; you can redistribute it and/or modify it under the terms of
 * the BSD License. Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for
 * further details. ------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.gui.view;

import org.eclipse.swt.SWT;
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

// --------------------------------------------------------------------------------
/**
 *
 */
public class SpyglassGuiComponent extends org.eclipse.swt.widgets.Composite {
	
	{
		// Register as a resource user - SWTResourceManager will
		// handle the obtaining and disposing of resources
		SWTResourceManager.registerResourceUser(this);
	}
	
	private DrawingArea canvas;
	
	private Composite composite1;
	
	// --------------------------------------------------------------------------------
	/**
	 * Displays this org.eclipse.swt.widgets.Composite inside a new Shell.
	 */
	public static void main(final String[] args) {
		showGUI();
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Displays this org.eclipse.swt.widgets.Composite inside a new Shell.
	 */
	public static void showGUI() {
		final Display display = Display.getDefault();
		final Shell shell = new Shell(display);
		final SpyglassGuiComponent inst = new SpyglassGuiComponent(shell, SWT.NULL, new Spyglass(
				false));
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
	 * @param parent
	 * @param style
	 */
	public SpyglassGuiComponent(final org.eclipse.swt.widgets.Composite parent, final int style,
			final Spyglass spyglass) {
		super(parent, style);
		initGUI(spyglass);
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	private void initGUI(final Spyglass spyglass) {
		try {
			final GridLayout thisLayout = new GridLayout();
			thisLayout.makeColumnsEqualWidth = true;
			this.setLayout(thisLayout);
			this.setSize(678, 472);
			{
				composite1 = new Composite(this, SWT.NONE);
				final GridLayout composite1Layout = new GridLayout();
				composite1Layout.makeColumnsEqualWidth = true;
				composite1Layout.marginHeight = 0;
				composite1Layout.marginWidth = 0;
				final GridData composite1LData = new GridData();
				composite1LData.grabExcessHorizontalSpace = true;
				composite1LData.horizontalAlignment = GridData.FILL;
				composite1LData.verticalAlignment = GridData.FILL;
				composite1LData.grabExcessVerticalSpace = true;
				composite1.setLayoutData(composite1LData);
				composite1.setLayout(composite1Layout);
				composite1.setBackground(SWTResourceManager.getColor(255, 255, 255));
				{
					final GridData canvas1LData = new GridData();
					canvas1LData.horizontalAlignment = GridData.FILL;
					canvas1LData.grabExcessHorizontalSpace = true;
					canvas1LData.verticalAlignment = GridData.FILL;
					canvas1LData.grabExcessVerticalSpace = true;
					canvas = new DrawingArea(composite1, SWT.None, spyglass);
					canvas.setLayoutData(canvas1LData);
					
				}
			}
			this.layout();
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public DrawingArea getDrawingArea() {
		return canvas;
	}
	
}
