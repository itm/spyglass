/*
 * --------------------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass. Copyright (C)
 * 2004-2007 by the SwarmNet (www.swarmnet.de) project SpyGlass is free
 * software; you can redistribute it and/or modify it under the terms of the BSD
 * License. Refer to spyglass-licence.txt file in the root of the SpyGlass
 * source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.mappainter;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
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

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.gui.databinding.converter.ArrayToColorConverter;
import de.uniluebeck.itm.spyglass.gui.databinding.converter.ColorToArrayConverter;
import de.uniluebeck.itm.spyglass.gui.databinding.validator.IntegerRangeValidator;
import de.uniluebeck.itm.spyglass.xmlconfig.MetricsXMLConfig;

public class MapPainterPrefComposite extends org.eclipse.swt.widgets.Composite {

	{
		// Register as a resource user - SWTResourceManager will
		// handle the obtaining and disposing of resources
		SWTResourceManager.registerResourceUser(this);
	}

	private Group group1;
	private Label label1;
	private Label label11space;
	private Text framePointsY;
	private Label label15;
	private Text framePointsX;
	private Text minValue;
	private Label label14;
	private Button button4;
	private CLabel maxValueColor;
	private Text maxValue;
	private Button button3;
	private CLabel minValueColor;
	private Label label13;
	private Label label12;
	private Group group2;
	private Button button2;
	private Text blockWidth;
	private Text blockHeight;
	private Label label10;
	private Label label9;
	private Label label7;
	private Button button1;
	private Label label8space;
	private Text text1;
	private Label label19;
	private Label label17;
	private Label label11;
	private Label label18;
	private Text kNN;
	private Label label16;
	private Label label8;
	private Label label5;
	private Text defaultValue;
	private Label label3;
	private Text height;
	private Text width;
	private Label label6;
	private Label labelspace;
	private Text lowerLeftY;
	private Label label4;
	private Label label3space;
	private Text lowerLeftX;
	private Label label2;

	private MapPainterPreferencePage page;
	private MapPainterXMLConfig config;
	private DataBindingContext dbc;
	private Binding lockBinding = null;
	private Binding lockBinding2 = null;

	/**
	 * Auto-generated main method to display this org.eclipse.swt.widgets.Composite inside a new
	 * Shell.
	 */
	public static void main(final String[] args) {
		showGUI();
	}

	/**
	 * Auto-generated method to display this org.eclipse.swt.widgets.Composite inside a new Shell.
	 */
	public static void showGUI() {
		final Display display = Display.getDefault();
		final Shell shell = new Shell(display);
		final MapPainterPrefComposite inst = new MapPainterPrefComposite(shell, SWT.NULL);
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

	public MapPainterPrefComposite(final org.eclipse.swt.widgets.Composite parent, final int style) {
		super(parent, style);
		initGUI();
	}

	private void initGUI() {
		try {
			final GridLayout thisLayout = new GridLayout(1, true);

			final GridData groupLData = new GridData();
			groupLData.horizontalAlignment = GridData.FILL;
			groupLData.verticalAlignment = GridData.FILL;
			groupLData.grabExcessHorizontalSpace = true;
			groupLData.grabExcessVerticalSpace = true;
			this.setLayoutData(groupLData);

			this.setLayout(thisLayout);
			this.setSize(931, 545);

			{
				group1 = new Group(this, SWT.NONE);
				final GridLayout group1Layout = new GridLayout();
				group1.setLayout(group1Layout);

				final GridData group1LData = new GridData();
				group1LData.horizontalAlignment = GridData.FILL;
				group1Layout.numColumns = 7;
				group1LData.verticalAlignment = GridData.FILL;
				group1LData.widthHint = 440;
				group1LData.grabExcessVerticalSpace = true;
				group1.setLayoutData(group1LData);

				group1.setText("Layout");

				GridData data;

				{
					label7 = new Label(group1, SWT.NONE);
					final GridData label7LData = new GridData();
					label7LData.horizontalSpan = 7;
					label7LData.widthHint = 92;
					label7LData.heightHint = 17;
					label7.setLayoutData(label7LData);
					label7.setText("Dimensions of map");
					label7.setFont(SWTResourceManager.getFont("Sans", 10, 1, false, false));
				}
				{
					label1 = new Label(group1, SWT.NONE);
					label1.setText("Lower left point: ");
				}
				{
					data = new GridData();
					data.horizontalAlignment = SWT.RIGHT;

					label2 = new Label(group1, SWT.NONE);
					label2.setText("x:");
					label2.setLayoutData(data);
				}
				{
					lowerLeftX = new Text(group1, SWT.BORDER);
					final GridData text1LData = new GridData();
					text1LData.heightHint = 17;
					text1LData.widthHint = 63;
					lowerLeftX.setLayoutData(text1LData);
				}
				{
					data = new GridData();
					data.widthHint = 40;

					label3space = new Label(group1, SWT.NONE);
					label3space.setText("");
					label3space.setLayoutData(data);
				}
				{

					label4 = new Label(group1, SWT.NONE);
					final GridData label4LData = new GridData();
					label4LData.horizontalAlignment = SWT.RIGHT;
					label4.setLayoutData(label4LData);
					label4.setText("y:");
				}
				{
					lowerLeftY = new Text(group1, SWT.BORDER);
					final GridData lowerLeftYLData = new GridData();
					lowerLeftYLData.heightHint = 17;
					lowerLeftYLData.widthHint = 50;
					lowerLeftY.setLayoutData(lowerLeftYLData);
				}
				{
					labelspace = new Label(group1, SWT.NONE);
					labelspace.setText("");
				}
				{
					label6 = new Label(group1, SWT.NONE);
					label6.setText("Width / Height: ");
				}
				{
					label8 = new Label(group1, SWT.NONE);
				}
				{
					width = new Text(group1, SWT.BORDER);
					final GridData widthLData = new GridData();
					widthLData.horizontalAlignment = GridData.FILL;
					widthLData.heightHint = 17;
					width.setLayoutData(widthLData);
				}
				{
					button1 = new Button(group1, SWT.PUSH | SWT.CENTER);
					final GridData button1LData = new GridData();
					button1LData.widthHint = 50;
					button1LData.heightHint = 20;
					button1LData.horizontalSpan = 2;
					button1.setLayoutData(button1LData);
					button1.setImage(SWTResourceManager.getImage("de/uniluebeck/itm/spyglass/gui/configuration/chain_small_closed_hor.png"));

					button1.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(final SelectionEvent evt) {
							/*
							 * if pressed, change image/text and change binding between both text
							 * fields (add binding / remove binding
							 */
							final boolean currentlyLocked = config.getLockNumberOfRowsNCols();

							config.setLockNumberOfRowsNCols(!currentlyLocked);

							updateScaleLinkDim();

						}

					});
				}
				{
					final GridData heightLData = new GridData();
					heightLData.horizontalAlignment = GridData.FILL;
					height = new Text(group1, SWT.BORDER);
					height.setLayoutData(heightLData);
				}
				{
					label8space = new Label(group1, SWT.NONE);
					label8space.setText("m");
				}
				{
					label9 = new Label(group1, SWT.NONE);
					final GridData label9LData = new GridData();
					label9LData.horizontalSpan = 7;
					label9.setLayoutData(label9LData);
					label9.setText("Granularity");
					label9.setFont(SWTResourceManager.getFont("Sans", 10, 1, false, false));
				}
				{
					label10 = new Label(group1, SWT.NONE);
					label10.setText("Block width / height: ");
				}
				{
					label11 = new Label(group1, SWT.NONE);
					final GridData label11LData = new GridData();
					label11LData.heightHint = 19;
					label11LData.horizontalAlignment = GridData.FILL;
					label11.setLayoutData(label11LData);
				}
				{
					blockWidth = new Text(group1, SWT.BORDER);
					final GridData text2LData = new GridData();
					text2LData.horizontalAlignment = GridData.FILL;
					blockWidth.setLayoutData(text2LData);
				}
				{
					button2 = new Button(group1, SWT.PUSH | SWT.CENTER);
					final GridData button2LData = new GridData();
					button2LData.widthHint = 50;
					button2LData.heightHint = 18;
					button2LData.horizontalSpan = 2;
					button2.setLayoutData(button2LData);
					button2.setImage(SWTResourceManager.getImage("de/uniluebeck/itm/spyglass/gui/configuration/chain_small_closed_hor.png"));

					button2.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(final SelectionEvent evt) {
							/*
							 * if pressed, change image/text and change binding between both text
							 * fields (add binding / remove binding
							 */
							final boolean currentlyLocked = config.getLockGridElementSquare();

							config.setLockGridElementSquare(!currentlyLocked);

							updateScaleLinkBlock();

						}

					});
				}
				{
					blockHeight = new Text(group1, SWT.BORDER);
					final GridData blockHeightLData = new GridData();
					blockHeightLData.horizontalAlignment = GridData.FILL;
					blockHeight.setLayoutData(blockHeightLData);
				}
				{
					label11space = new Label(group1, SWT.NONE);
					label11space.setText("m");
				}

			}
			{
				group2 = new Group(this, SWT.NONE);
				final GridLayout group2Layout = new GridLayout();
				group2Layout.numColumns = 5;
				group2.setLayout(group2Layout);
				final GridData group2LData = new GridData();
				group2LData.horizontalAlignment = GridData.FILL;
				group2LData.grabExcessHorizontalSpace = true;
				group2LData.heightHint = 228;
				group2LData.verticalAlignment = GridData.FILL;
				group2LData.widthHint = 640;
				group2LData.grabExcessVerticalSpace = true;
				group2.setLayoutData(group2LData);
				group2.setText("Data");
				{
					label12 = new Label(group2, SWT.NONE);
					label12.setText("Minimum value: ");
				}
				{
					final GridData minValueLData = new GridData();
					minValueLData.widthHint = 82;
					minValueLData.heightHint = 17;
					minValueLData.verticalAlignment = GridData.BEGINNING;
					minValueLData.horizontalAlignment = GridData.BEGINNING;
					minValue = new Text(group2, SWT.BORDER);
					minValue.setLayoutData(minValueLData);
				}
				{
					minValueColor = new CLabel(group2, SWT.BORDER);
					final GridData cLabel1LData = new GridData();
					cLabel1LData.heightHint = 23;
					cLabel1LData.horizontalAlignment = GridData.BEGINNING;
					cLabel1LData.horizontalSpan = 2;
					cLabel1LData.verticalAlignment = GridData.BEGINNING;
					cLabel1LData.widthHint = 97;
					minValueColor.setLayoutData(cLabel1LData);
				}
				{
					button3 = new Button(group2, SWT.PUSH | SWT.CENTER);
					button3.setText("Change color");
					button3.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(final SelectionEvent evt) {
							button3WidgetSelected(evt);
						}
					});
				}
				{
					label13 = new Label(group2, SWT.NONE);
					final GridData label13LData = new GridData();
					label13LData.widthHint = 111;
					label13LData.heightHint = 17;
					label13.setLayoutData(label13LData);
					label13.setText("Maximum value: ");
				}
				{
					final GridData text4LData = new GridData();
					text4LData.heightHint = 17;
					text4LData.horizontalAlignment = GridData.FILL;
					maxValue = new Text(group2, SWT.BORDER);
					maxValue.setLayoutData(text4LData);
				}
				{
					maxValueColor = new CLabel(group2, SWT.BORDER);
					final GridData cLabel2LData = new GridData();
					cLabel2LData.widthHint = 96;
					cLabel2LData.heightHint = 23;
					cLabel2LData.horizontalSpan = 2;
					maxValueColor.setLayoutData(cLabel2LData);
				}
				{
					button4 = new Button(group2, SWT.PUSH | SWT.CENTER);
					final GridData button4LData = new GridData();
					button4LData.heightHint = 29;
					button4LData.horizontalAlignment = GridData.BEGINNING;
					button4LData.verticalAlignment = GridData.BEGINNING;
					button4LData.widthHint = 102;
					button4.setLayoutData(button4LData);
					button4.setText("Change color");
					button4.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(final SelectionEvent evt) {
							button4WidgetSelected(evt);
						}
					});
				}
				{
					label3 = new Label(group2, SWT.NONE);
					label3.setText("Default value: ");
				}
				{
					defaultValue = new Text(group2, SWT.BORDER);
					final GridData defaultValueLData = new GridData();
					defaultValueLData.heightHint = 17;
					defaultValueLData.horizontalAlignment = GridData.FILL;
					defaultValue.setLayoutData(defaultValueLData);
				}
				{
					label5 = new Label(group2, SWT.NONE);
					final GridData label5LData = new GridData();
					label5LData.horizontalSpan = 3;
					label5LData.horizontalAlignment = GridData.FILL;
					label5.setLayoutData(label5LData);
				}
				{
					label14 = new Label(group2, SWT.NONE);
					label14.setText("Framepoints x/y: ");
				}
				{
					framePointsX = new Text(group2, SWT.BORDER);
					final GridData text5LData = new GridData();
					text5LData.heightHint = 17;
					text5LData.horizontalAlignment = GridData.FILL;
					framePointsX.setLayoutData(text5LData);
				}
				{
					label15 = new Label(group2, SWT.NONE);
					label15.setText("/");
				}
				{
					framePointsY = new Text(group2, SWT.BORDER);
					final GridData framePointsYLData = new GridData();
					framePointsYLData.heightHint = 17;
					framePointsYLData.horizontalAlignment = GridData.BEGINNING;
					framePointsYLData.verticalAlignment = GridData.BEGINNING;
					framePointsY.setLayoutData(framePointsYLData);
				}
				{
					label18 = new Label(group2, SWT.NONE);
					final GridData label18LData = new GridData();
					label18LData.widthHint = 17;
					label18LData.heightHint = 17;
					label18LData.verticalAlignment = GridData.BEGINNING;
					label18LData.horizontalAlignment = GridData.BEGINNING;
					label18.setLayoutData(label18LData);
					label18.setText(" ");
				}
				{
					label16 = new Label(group2, SWT.NONE);
					label16.setText("Neighbors (for kNN):");
				}
				{
					kNN = new Text(group2, SWT.BORDER);
					final GridData text1LData = new GridData();
					text1LData.widthHint = 83;
					text1LData.heightHint = 17;
					text1LData.verticalAlignment = GridData.BEGINNING;
					text1LData.horizontalAlignment = GridData.BEGINNING;
					kNN.setLayoutData(text1LData);
					kNN.setText("k");
				}
				{
					final GridData label19LData = new GridData();
					label19LData.verticalAlignment = GridData.BEGINNING;
					label19LData.horizontalAlignment = GridData.BEGINNING;
					label19LData.horizontalSpan = 3;
					label19LData.heightHint = 17;
					label19LData.grabExcessHorizontalSpace = true;
					label19 = new Label(group2, SWT.NONE);
					label19.setLayoutData(label19LData);
				}
				{
					label17 = new Label(group2, SWT.NONE);
					label17.setText("Update frequency (1/s): ");
				}
				{
					final GridData text1LData = new GridData();
					text1LData.verticalAlignment = GridData.BEGINNING;
					text1LData.horizontalAlignment = GridData.BEGINNING;
					text1LData.widthHint = 84;
					text1LData.heightHint = 17;
					text1 = new Text(group2, SWT.BORDER);
					text1.setLayoutData(text1LData);
				}
			}

			{
			}
			this.layout();
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public void setDatabinding(final DataBindingContext dbc, final MapPainterXMLConfig config, final Spyglass spyglass,
			final MapPainterPreferencePage page) {

		this.page = page;
		this.config = config;
		this.dbc = dbc;

		{
			dbc.bindValue(SWTObservables.observeText(this.lowerLeftX, SWT.Modify), BeansObservables.observeValue(dbc.getValidationRealm(), config,
					MapPainterXMLConfig.PROPERTYNAME_LOWER_LEFT_X), new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT), null);
		}
		{
			dbc.bindValue(SWTObservables.observeText(this.lowerLeftY, SWT.Modify), BeansObservables.observeValue(dbc.getValidationRealm(), config,
					MapPainterXMLConfig.PROPERTYNAME_LOWER_LEFT_Y), new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT), null);
		}
		{
			dbc.bindValue(SWTObservables.observeText(this.width, SWT.Modify), BeansObservables.observeValue(dbc.getValidationRealm(), config,
					MapPainterXMLConfig.PROPERTYNAME_WIDTH), new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT)
					.setAfterConvertValidator(new IntegerRangeValidator("Width", 1, Integer.MAX_VALUE)), null);
		}
		{

			dbc.bindValue(SWTObservables.observeText(this.height, SWT.Modify), BeansObservables.observeValue(dbc.getValidationRealm(), config,
					MapPainterXMLConfig.PROPERTYNAME_HEIGHT), new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT)
					.setAfterConvertValidator(new IntegerRangeValidator("Height", 1, Integer.MAX_VALUE)), null);
		}
		{

			dbc.bindValue(SWTObservables.observeText(this.blockWidth, SWT.Modify), BeansObservables.observeValue(dbc.getValidationRealm(), config,
					MapPainterXMLConfig.PROPERTYNAME_GRID_ELEMENT_WIDTH), new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT)
					.setAfterConvertValidator(new IntegerRangeValidator("Block width", 1, Integer.MAX_VALUE)), null);
		}
		{

			dbc.bindValue(SWTObservables.observeText(this.blockHeight, SWT.Modify), BeansObservables.observeValue(dbc.getValidationRealm(), config,
					MapPainterXMLConfig.PROPERTYNAME_GRID_ELEMENT_HEIGHT), new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT)
					.setAfterConvertValidator(new IntegerRangeValidator("Block height", 1, Integer.MAX_VALUE)), null);

		}
		{
			dbc.bindValue(SWTObservables.observeText(this.minValue, SWT.Modify), BeansObservables.observeValue(dbc.getValidationRealm(), config,
					MapPainterXMLConfig.PROPERTYNAME_MIN_VALUE), new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT), null);

		}
		{
			dbc.bindValue(SWTObservables.observeText(this.maxValue, SWT.Modify), BeansObservables.observeValue(dbc.getValidationRealm(), config,
					MapPainterXMLConfig.PROPERTYNAME_MAX_VALUE), new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT), null);

		}
		{
			dbc.bindValue(SWTObservables.observeText(this.defaultValue, SWT.Modify), BeansObservables.observeValue(dbc.getValidationRealm(), config,
					MapPainterXMLConfig.PROPERTYNAME_DEFAULT_VALUE), new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT), null);

		}
		{
			dbc.bindValue(SWTObservables.observeText(this.framePointsX, SWT.Modify), BeansObservables.observeValue(dbc.getValidationRealm(), config,
					MapPainterXMLConfig.PROPERTYNAME_NUM_FRAME_POINTS_HORIZONTAL), new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT)
					.setAfterConvertValidator(new IntegerRangeValidator("Framepoints x", 1, Integer.MAX_VALUE)), null);

		}
		{
			dbc.bindValue(SWTObservables.observeText(this.framePointsY, SWT.Modify), BeansObservables.observeValue(dbc.getValidationRealm(), config,
					MapPainterXMLConfig.PROPERTYNAME_NUM_FRAME_POINTS_VERTICAL), new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT)
					.setAfterConvertValidator(new IntegerRangeValidator("Framepoints y", 1, Integer.MAX_VALUE)), null);

		}
		{
			dbc.bindValue(SWTObservables.observeBackground(minValueColor), BeansObservables.observeValue(dbc.getValidationRealm(), config,
					MapPainterXMLConfig.PROPERTYNAME_MIN_COLOR_R_G_B), new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT)
					.setConverter(new ColorToArrayConverter()), new UpdateValueStrategy().setConverter(new ArrayToColorConverter(this.getDisplay())));

		}
		{
			dbc.bindValue(SWTObservables.observeBackground(maxValueColor), BeansObservables.observeValue(dbc.getValidationRealm(), config,
					MapPainterXMLConfig.PROPERTYNAME_MAX_COLOR_R_G_B), new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT)
					.setConverter(new ColorToArrayConverter()), new UpdateValueStrategy().setConverter(new ArrayToColorConverter(this.getDisplay())));

		}
		{
			dbc.bindValue(SWTObservables.observeText(this.kNN, SWT.Modify), BeansObservables.observeValue(dbc.getValidationRealm(), config,
					MapPainterXMLConfig.PROPERTYNAME_K), new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT)
					.setAfterConvertValidator(new IntegerRangeValidator("Neighbors (for kNN)", 1, Integer.MAX_VALUE)), null);

		}
		{
			dbc.bindValue(SWTObservables.observeText(this.text1, SWT.Modify), BeansObservables.observeValue(dbc.getValidationRealm(), config,
					MapPainterXMLConfig.PROPERTYNAME_REFRESH_FREQUENCY), new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT)
					.setAfterConvertValidator(new IntegerRangeValidator("Update frequency (1/s)", 1, 25)), null);

		}
		{
			final MetricsXMLConfig mConf = spyglass.getConfigStore().getSpyglassConfig().getGeneralSettings().getMetrics();
			dbc.bindValue(SWTObservables.observeText(this.label11space), BeansObservables.observeValue(dbc.getValidationRealm(), mConf,
					MetricsXMLConfig.PROPERTYNAME_UNIT), null, null);
			dbc.bindValue(SWTObservables.observeText(this.label3space), BeansObservables.observeValue(dbc.getValidationRealm(), mConf,
					MetricsXMLConfig.PROPERTYNAME_UNIT), null, null);
			dbc.bindValue(SWTObservables.observeText(this.label8space), BeansObservables.observeValue(dbc.getValidationRealm(), mConf,
					MetricsXMLConfig.PROPERTYNAME_UNIT), null, null);
			dbc.bindValue(SWTObservables.observeText(this.labelspace), BeansObservables.observeValue(dbc.getValidationRealm(), mConf,
					MetricsXMLConfig.PROPERTYNAME_UNIT), null, null);

		}
		updateScaleLinkBlock();
		updateScaleLinkDim();
	}

	private void updateScaleLinkDim() {
		final boolean locked = config.getLockNumberOfRowsNCols();

		if (locked) {
			height.setText(width.getText());

			if (lockBinding == null) {

				// bind the two fields together
				lockBinding = dbc
						.bindValue(SWTObservables.observeText(width, SWT.Modify), SWTObservables.observeText(height, SWT.Modify), null, null);
			}

			button1.setImage(SWTResourceManager.getImage("de/uniluebeck/itm/spyglass/gui/configuration/chain_small_closed_hor.png"));

		} else {
			if (lockBinding != null) {
				// Kill the binding (it will be automatically removed from the dbc)
				lockBinding.dispose();
				lockBinding = null;
			}

			button1.setImage(SWTResourceManager.getImage("de/uniluebeck/itm/spyglass/gui/configuration/chain_small_open_hor.png"));

		}
	}

	private void updateScaleLinkBlock() {
		final boolean locked = config.getLockGridElementSquare();

		if (locked) {
			blockHeight.setText(blockWidth.getText());

			if (lockBinding2 == null) {

				// bind the two fields together
				lockBinding2 = dbc.bindValue(SWTObservables.observeText(blockWidth, SWT.Modify), SWTObservables.observeText(this.blockHeight,
						SWT.Modify), null, null);
			}

			button2.setImage(SWTResourceManager.getImage("de/uniluebeck/itm/spyglass/gui/configuration/chain_small_closed_hor.png"));

		} else {
			if (lockBinding2 != null) {
				// Kill the binding (it will be automatically removed from the dbc)
				lockBinding2.dispose();
				lockBinding2 = null;
			}

			button2.setImage(SWTResourceManager.getImage("de/uniluebeck/itm/spyglass/gui/configuration/chain_small_open_hor.png"));

		}
	}

	private void button3WidgetSelected(final SelectionEvent evt) {
		final ColorDialog dlg = new ColorDialog(this.getShell());
		dlg.setRGB(minValueColor.getBackground().getRGB());
		final RGB color = dlg.open();
		if (color != null) {
			minValueColor.setBackground(new Color(this.getDisplay(), color));
			this.page.markFormDirty();
		}
	}

	private void button4WidgetSelected(final SelectionEvent evt) {
		final ColorDialog dlg = new ColorDialog(this.getShell());
		dlg.setRGB(maxValueColor.getBackground().getRGB());
		final RGB color = dlg.open();
		if (color != null) {
			maxValueColor.setBackground(new Color(this.getDisplay(), color));
			this.page.markFormDirty();
		}
	}

}
