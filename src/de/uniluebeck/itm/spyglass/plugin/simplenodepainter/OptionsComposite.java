/* 
 * ----------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the
 * SwarmNet (www.swarmnet.de) project SpyGlass is free software;
 * you can redistribute it and/or modify it under the terms of the BSD License.
 * Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for further details.
 * ------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.simplenodepainter;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.cloudgarden.resource.SWTResourceManager;

import de.uniluebeck.itm.spyglass.gui.databinding.StringFormatter;
import de.uniluebeck.itm.spyglass.gui.databinding.converter.ArrayToColorConverter;
import de.uniluebeck.itm.spyglass.gui.databinding.converter.ColorToArrayConverter;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

//--------------------------------------------------------------------------------
/**
 * Instances of this class are widgets edit configurations for {@link SimpleNodePainterPlugin}s
 * 
 * @author Sebastian Ebers
 * 
 */
public class OptionsComposite extends org.eclipse.swt.widgets.Composite {

	{
		// Register as a resource user - SWTResourceManager will
		// handle the obtaining and disposing of resources
		SWTResourceManager.registerResourceUser(this);
	}

	private static final Logger log = SpyglassLoggerFactory.getLogger(OptionsComposite.class);
	private Group group1;
	private Label label1;
	private Button showExtInf;
	private Button nodeIDsAsHex;
	private CLabel colorExample;
	private Button lineColor;
	private Label label2;
	private Text lineWidth;

	private SimpleNodePainterPreferencePage page;

	/** The string formatter configuration widget */
	StringFormatter stringFormatter = new StringFormatter();

	/**
	 * Auto-generated method to display this org.eclipse.swt.widgets.Composite inside a new Shell.
	 */
	public static void showGUI() {
		final Display display = Display.getDefault();
		final Shell shell = new Shell(display);
		final OptionsComposite inst = new OptionsComposite(shell, SWT.NULL);
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
	 *            the style of the widget to construct
	 */
	public OptionsComposite(final org.eclipse.swt.widgets.Composite parent, final int style) {
		super(parent, style);
		initGUI();
	}

	private void initGUI() {
		try {
			final FillLayout thisLayout = new FillLayout(org.eclipse.swt.SWT.HORIZONTAL);
			this.setLayout(thisLayout);
			final GridData gridData = new GridData();
			gridData.horizontalAlignment = GridData.FILL;
			gridData.verticalAlignment = GridData.FILL;
			gridData.grabExcessHorizontalSpace = true;
			gridData.grabExcessVerticalSpace = true;
			this.setLayoutData(gridData);

			this.setSize(613, 273);
			{
				group1 = new Group(this, SWT.NONE);
				final GridLayout group1Layout = new GridLayout();
				group1Layout.numColumns = 3;
				group1Layout.makeColumnsEqualWidth = false;
				group1.setLayout(group1Layout);
				group1.setText("Options");
				{
					label1 = new Label(group1, SWT.NONE);
					label1.setText("Line width: ");
				}
				{
					final GridData lineWidthLData = new GridData();
					lineWidthLData.grabExcessHorizontalSpace = true;
					lineWidthLData.horizontalAlignment = GridData.FILL;
					lineWidthLData.horizontalSpan = 2;
					lineWidthLData.heightHint = 17;
					lineWidth = new Text(group1, SWT.BORDER);
					lineWidth.setLayoutData(lineWidthLData);
				}
				{
					label2 = new Label(group1, SWT.NONE);
					label2.setText("Line color: ");
				}
				{
					final GridData colorExampleLData = new GridData();
					colorExampleLData.widthHint = 50;
					colorExampleLData.heightHint = 19;
					colorExample = new CLabel(group1, SWT.BORDER);
					colorExample.setLayoutData(colorExampleLData);
				}
				{
					lineColor = new Button(group1, SWT.PUSH | SWT.CENTER);
					lineColor.setText("Change color");
					lineColor.addSelectionListener(new SelectionAdapter() {
						@SuppressWarnings("synthetic-access")
						@Override
						public void widgetSelected(final SelectionEvent evt) {
							lineColorWidgetSelected(evt);
						}
					});
				}
				{
					showExtInf = new Button(group1, SWT.CHECK | SWT.LEFT);
					final GridData showExtInfLData = new GridData();
					showExtInfLData.horizontalSpan = 3;
					showExtInf.setLayoutData(showExtInfLData);
					showExtInf.setText("Show extended information by default");

				}

				{
					nodeIDsAsHex = new Button(group1, SWT.CHECK | SWT.LEFT);
					final GridData nidashData = new GridData();
					nidashData.horizontalSpan = 3;
					nodeIDsAsHex.setLayoutData(nidashData);
					nodeIDsAsHex.setText("Display node identifiers as hex values");

				}

				stringFormatter.addStringFormatterFields(group1, 3);

			}
			this.layout();
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	private void lineColorWidgetSelected(final SelectionEvent evt) {
		log.debug("lineColor.widgetSelected, event=" + evt);
		final ColorDialog dlg = new ColorDialog(this.getShell());
		dlg.setRGB(colorExample.getBackground().getRGB());
		final RGB color = dlg.open();
		if (color != null) {
			colorExample.setBackground(new Color(this.getDisplay(), color));
			this.page.markFormDirty();
		}
	}

	// --------------------------------------------------------------------------------
	/**
	 * Sets the parameters necessary for data binding
	 * 
	 * @param dbc
	 *            the data binding context
	 * @param config
	 *            the configuration
	 * @param page
	 *            the plug-in's preference page
	 */
	public void setDatabinding(final DataBindingContext dbc, final PluginXMLConfig config, final SimpleNodePainterPreferencePage page) {

		this.page = page;

		// line width

		final IObservableValue modelObservable = BeansObservables.observeValue(dbc.getValidationRealm(), config,
				SimpleNodePainterXMLConfig.PROPERTYNAME_LINE_WIDTH);
		dbc.bindValue(SWTObservables.observeText(this.lineWidth, SWT.Modify), modelObservable, new UpdateValueStrategy(
				UpdateValueStrategy.POLICY_CONVERT), null);

		// extended inf

		final IObservableValue observableExtInf = BeansObservables.observeValue(dbc.getValidationRealm(), config,
				SimpleNodePainterXMLConfig.PROPERTYNAME_EXTENDED_DEFAULT_VALUE);
		dbc.bindValue(SWTObservables.observeSelection(this.showExtInf), observableExtInf,
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT), null);

		// node indertifier representation

		final IObservableValue observableNodeRepr = BeansObservables.observeValue(dbc.getValidationRealm(), config,
				SimpleNodePainterXMLConfig.PROPERTYNAME_NODE_IDS_AS_HEX);
		dbc.bindValue(SWTObservables.observeSelection(this.nodeIDsAsHex), observableNodeRepr, new UpdateValueStrategy(
				UpdateValueStrategy.POLICY_CONVERT), null);

		// line color

		final IObservableValue observableColor = BeansObservables.observeValue(dbc.getValidationRealm(), config,
				SimpleNodePainterXMLConfig.PROPERTYNAME_LINE_COLOR_R_G_B);
		dbc.bindValue(SWTObservables.observeBackground(colorExample), observableColor, new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT)
				.setConverter(new ColorToArrayConverter()), new UpdateValueStrategy().setConverter(new ArrayToColorConverter(this.getDisplay())));

		stringFormatter.setDataBinding(dbc, config);

	}

}
