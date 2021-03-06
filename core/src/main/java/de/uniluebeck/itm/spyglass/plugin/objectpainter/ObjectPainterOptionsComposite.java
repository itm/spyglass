/*
 * --------------------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass. Copyright (C)
 * 2004-2007 by the SwarmNet (www.swarmnet.de) project SpyGlass is free
 * software; you can redistribute it and/or modify it under the terms of the BSD
 * License. Refer to spyglass-licence.txt file in the root of the SpyGlass
 * source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.objectpainter;

import java.io.File;
import java.text.NumberFormat;
import java.text.ParseException;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.conversion.Converter;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.cloudgarden.resource.SWTResourceManager;

import de.uniluebeck.itm.spyglass.SpyglassEnvironment;
import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.gui.databinding.converter.ArrayToColorConverter;
import de.uniluebeck.itm.spyglass.gui.databinding.converter.ColorToArrayConverter;
import de.uniluebeck.itm.spyglass.gui.databinding.validator.FileReadableValidator;
import de.uniluebeck.itm.spyglass.gui.databinding.validator.IntegerRangeValidator;
import de.uniluebeck.itm.spyglass.xmlconfig.MetricsXMLConfig;

/**
 * This code was edited or generated using CloudGarden's Jigloo SWT/Swing GUI Builder, which is free
 * for non-commercial use. If Jigloo is being used commercially (ie, by a corporation, company or
 * business for any purpose whatever) then you should purchase a license for each developer using
 * Jigloo. Please visit www.cloudgarden.com for details. Use of Jigloo implies acceptance of these
 * licensing terms. A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR THIS MACHINE, SO JIGLOO OR THIS
 * CODE CANNOT BE USED LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
public class ObjectPainterOptionsComposite extends Composite {

	{
		// Register as a resource user - SWTResourceManager will
		// handle the obtaining and disposing of resources
		SWTResourceManager.registerResourceUser(this);
	}

	private Text imageFileText;
	private Label imageDimesionsLabel;
	private Text imageSizeWidthText;
	private Text imageSizeHeightText;
	private Label label1;
	private Text inteval;
	private Label label10;
	private Label label9;
	private Label label8;
	private Label label7;
	private Combo combo1;
	private Button button1;
	private CLabel colorExample;
	private Label label6;
	private Button drawLineCheckbox;
	private Label label5;
	private Label label2;
	private Label label4;
	private Button keepProportionsButton;
	private Label label3;
	private Label label3a;

	ObjectPainterPreferencePage page;

	private double imageAspectRatio;

	/**
	 * Is true while the algorith sets width/height (neccessary to avoid stack overflow ;)
	 */
	boolean aspectSettingInProgress = false;

	// --------------------------------------------------------------------------------
	/**
	 * @param parent
	 */
	public ObjectPainterOptionsComposite(final Composite parent) {
		super(parent, SWT.NONE);
		initGUI();
	}

	private void initGUI() {

		final GridData groupData2 = new GridData();
		groupData2.horizontalAlignment = GridData.FILL;
		groupData2.grabExcessHorizontalSpace = true;
		this.setLayoutData(groupData2);
		final GridLayout thisLayout = new GridLayout(1, true);
		this.setLayout(thisLayout);
		this.setSize(660, 411);
		{
			final GridData groupData = new GridData();
			groupData.horizontalAlignment = GridData.FILL;
			groupData.grabExcessHorizontalSpace = true;
			final Group group = new Group(this, SWT.NONE);
			group.setLayoutData(groupData);
			final GridLayout groupLayout = new GridLayout();
			group.setLayout(groupLayout);
			groupLayout.numColumns = 7;
			group.setText("Image options");

			{
				final GridData imageFileData = new GridData();
				imageFileData.widthHint = 71;
				imageFileData.heightHint = 17;
				final Label imageFileLabel = new Label(group, SWT.NONE);
				imageFileLabel.setLayoutData(imageFileData);
				imageFileLabel.setText("Image file: ");

				final GridLayout imageFileCompositeLayout = new GridLayout();
				imageFileCompositeLayout.numColumns = 2;
				final Composite imageFileComposite = new Composite(group, SWT.NONE);
				imageFileComposite.setLayout(imageFileCompositeLayout);
				final GridData imageFileCompositeData = new GridData();
				imageFileCompositeData.horizontalSpan = 6;
				imageFileCompositeData.horizontalAlignment = GridData.FILL;
				imageFileCompositeData.grabExcessHorizontalSpace = true;
				imageFileComposite.setLayoutData(imageFileCompositeData);
				{
					final GridData imageFileTextData = new GridData();
					imageFileTextData.grabExcessHorizontalSpace = true;
					imageFileTextData.horizontalAlignment = GridData.FILL;
					imageFileText = new Text(imageFileComposite, SWT.BORDER);
					imageFileText.setLayoutData(imageFileTextData);
					imageFileText.addModifyListener(new ModifyListener() {
						public void modifyText(final ModifyEvent evt) {
							if (new File(imageFileText.getText()).canRead()) {
								final Image i = new Image(null, imageFileText.getText());
								imageAspectRatio = (i.getBounds().width) / ((double) i.getBounds().height);

								imageDimesionsLabel.setText(String.format("%d/%d px (ratio %.2f)", i.getBounds().height, i.getBounds().width,
										imageAspectRatio));

								i.dispose();
							} else {
								imageDimesionsLabel.setText("???/??? px");
							}
						}
					});

					final GridData imageFileButtonData = new GridData();
					imageFileButtonData.widthHint = 80;
					final Button imageFileButton = new Button(imageFileComposite, SWT.PUSH);
					imageFileButton.setText("Change...");
					imageFileButton.setLayoutData(imageFileButtonData);
					imageFileButton.addSelectionListener(new SelectionListener() {
						@Override
						public void widgetDefaultSelected(final SelectionEvent e) {
							widgetSelected(e);
						}

						@Override
						public void widgetSelected(final SelectionEvent e) {
							final FileDialog fileDialog = new FileDialog(getShell(), SWT.OPEN);
							fileDialog.setFilterExtensions(new String[] { "*.jpg;*.png;*.gif", "*" });
							fileDialog.setFilterPath(SpyglassEnvironment.getImageWorkingDirectory());
							final String file = fileDialog.open();
							if (file != null) {
								imageFileText.setText(file);
							}
						}
					});
				}

				final GridData lowerLeftLabelData = new GridData();
				lowerLeftLabelData.horizontalSpan = 2;
				final Label lowerLeftLabel = new Label(group, SWT.NONE);
				lowerLeftLabel.setLayoutData(lowerLeftLabelData);
				lowerLeftLabel.setText("Dimensions (width/height): ");
			}
			{
				final GridData lowerLeftXTextData = new GridData();
				lowerLeftXTextData.horizontalAlignment = GridData.FILL;
				imageDimesionsLabel = new Label(group, SWT.NONE);
				imageDimesionsLabel.setLayoutData(lowerLeftXTextData);
				imageDimesionsLabel.setText("???");
			}

		}
		{
			final GridData groupData = new GridData();
			groupData.horizontalAlignment = GridData.FILL;
			groupData.verticalAlignment = GridData.FILL;
			groupData.grabExcessHorizontalSpace = true;
			groupData.grabExcessVerticalSpace = true;
			final Group group = new Group(this, SWT.NONE);
			group.setLayoutData(groupData);
			final GridLayout groupLayout = new GridLayout();
			group.setLayout(groupLayout);
			groupLayout.numColumns = 6;
			group.setText("Drawing options");

			// 1st row
			{
				// image size
				final GridData imageSizeLabelData = new GridData();
				imageSizeLabelData.horizontalSpan = 6;
				final Label imageSizeLabel = new Label(group, SWT.NONE);
				imageSizeLabel.setLayoutData(imageSizeLabelData);
				imageSizeLabel.setText("Scale image to");
				imageSizeLabel.setFont(SWTResourceManager.getFont("Sans", 10, 1, false, false));
			}
			// 2nd row
			{
				final GridData data = new GridData();
				data.horizontalAlignment = SWT.RIGHT;

				label4 = new Label(group, SWT.NONE);
				final GridData label4LData = new GridData();
				// label4LData.widthHint = 113;
				label4LData.heightHint = 17;
				label4.setLayoutData(label4LData);
				label4.setText("Width:");
				label4.setLayoutData(data);
			}
			{
				final GridData imageSizeWidthTextData = new GridData();
				imageSizeWidthTextData.heightHint = 17;
				imageSizeWidthTextData.widthHint = 78;
				imageSizeWidthText = new Text(group, SWT.BORDER);
				imageSizeWidthText.setLayoutData(imageSizeWidthTextData);
				imageSizeWidthText.addModifyListener(new ModifyListener() {
					public void modifyText(final ModifyEvent evt) {
						imageSizeWidthTextModifyText(evt);
					}
				});
			}
			{
				// The text will be automatically generated (metric unit from xml config)
				label3a = new Label(group, SWT.NONE);
				label3a.setText("");
			}
			{
				label2 = new Label(group, SWT.NONE);
				final GridData label2LData = new GridData();
				label2LData.horizontalAlignment = SWT.RIGHT;
				// label2LData.widthHint = 7;
				label2LData.heightHint = 17;
				label2.setLayoutData(label2LData);
				label2.setText("Height:");
			}
			{

				final GridData imageSizeHeightTextData = new GridData();
				imageSizeHeightTextData.heightHint = 17;
				imageSizeHeightTextData.widthHint = 77;
				imageSizeHeightText = new Text(group, SWT.BORDER);
				imageSizeHeightText.setLayoutData(imageSizeHeightTextData);
				imageSizeHeightText.addModifyListener(new ModifyListener() {
					public void modifyText(final ModifyEvent evt) {
						imageSizeHeightTextModifyText(evt);
					}
				});
			}
			{
				label3 = new Label(group, SWT.NONE);
				final GridData label3LData = new GridData();
				label3LData.heightHint = 17;
				label3LData.grabExcessHorizontalSpace = true;
				label3LData.horizontalAlignment = GridData.FILL;
				label3.setLayoutData(label3LData);
				label3.setText("m");

			}

			// {
			// label1 = new Label(group, SWT.NONE);
			// final GridData label1LData = new GridData();
			// label1LData.widthHint = 41;
			// label1LData.heightHint = 17;
			// label1.setLayoutData(label1LData);
			// label1.setText("");
			// }

			// 3rd row
			{
				keepProportionsButton = new Button(group, SWT.CHECK | SWT.LEFT);
				final GridData keepProportionsButtonLData = new GridData();
				keepProportionsButtonLData.horizontalSpan = 6;
				keepProportionsButtonLData.horizontalAlignment = GridData.FILL;
				keepProportionsButtonLData.grabExcessHorizontalSpace = true;
				keepProportionsButton.setLayoutData(keepProportionsButtonLData);
				keepProportionsButton.setText("Keep proportions");
			}

			// 4th row
			{
				label5 = new Label(group, SWT.NONE);
				final GridData label5LData = new GridData();
				label5LData.horizontalSpan = 5;
				label5LData.widthHint = 101;
				label5LData.heightHint = 17;
				label5.setLayoutData(label5LData);
				label5.setText("Trajectory");
				label5.setFont(SWTResourceManager.getFont("Sans", 10, 1, false, false));
			}

			// 5th row
			{
				drawLineCheckbox = new Button(group, SWT.CHECK | SWT.LEFT);
				final GridData button1LData = new GridData();
				button1LData.heightHint = 22;
				button1LData.horizontalAlignment = GridData.FILL;
				button1LData.grabExcessHorizontalSpace = true;
				button1LData.horizontalSpan = 6;
				drawLineCheckbox.setLayoutData(button1LData);
				drawLineCheckbox.setText("Draw trajectory as line");
			}

			// 6th row
			{
				label6 = new Label(group, SWT.NONE);
				label6.setText("Line color: ");
			}
			{
				final GridData cLabel1LData = new GridData();
				cLabel1LData.widthHint = 76;
				cLabel1LData.heightHint = 23;
				colorExample = new CLabel(group, SWT.BORDER);
				colorExample.setLayoutData(cLabel1LData);
			}
			{
				button1 = new Button(group, SWT.PUSH | SWT.CENTER);
				final GridData button1LData1 = new GridData();
				button1LData1.horizontalSpan = 4;
				button1LData1.grabExcessHorizontalSpace = true;
				button1LData1.widthHint = 78;
				button1LData1.heightHint = 29;
				button1.setLayoutData(button1LData1);
				button1.setText("Change");
				button1.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(final SelectionEvent evt) {
						button1WidgetSelected(evt);
					}
				});
			}

			// 7th row
			{
				label8 = new Label(group, SWT.NONE);
				label8.setText("Trajectory");
				final GridData label8LData = new GridData();
				label8LData.widthHint = 101;
				label8LData.heightHint = 17;
				label8LData.horizontalSpan = 6;
				label8.setLayoutData(label8LData);
				label8.setFont(SWTResourceManager.getFont("Sans", 10, 1, false, false));
			}

			// 8th row
			{
				final GridData data = new GridData();
				data.horizontalSpan = 2;

				label7 = new Label(group, SWT.NONE);
				label7.setText("Packet type: ");
				label7.setLayoutData(data);
			}
			{
				final GridData combo1LData = new GridData();
				combo1LData.horizontalSpan = 4;
				combo1 = new Combo(group, SWT.NONE);
				combo1.setLayoutData(combo1LData);
				combo1.add("TrajectoryPacket2D");
				combo1.add("TrajectoryPacket3D");
			}

			// 9th row
			{
				final GridData data = new GridData();
				data.horizontalSpan = 2;

				label9 = new Label(group, SWT.NONE);
				label9.setText("Display update interval: ");
				label9.setLayoutData(data);
			}
			{
				final GridData text1LData = new GridData();
				text1LData.widthHint = 72;
				text1LData.heightHint = 17;
				inteval = new Text(group, SWT.BORDER);
				inteval.setLayoutData(text1LData);
			}
			{
				final GridData data = new GridData();
				data.horizontalSpan = 3;

				label10 = new Label(group, SWT.NONE);
				label10.setText("milliseconds");
				label10.setLayoutData(data);
			}
		}

	}

	public void setDatabinding(final DataBindingContext dbc, final ObjectPainterXMLConfig config, final Spyglass spyglass,
			final ObjectPainterPreferencePage page) {

		this.page = page;

		IObservableValue obsModel;
		ISWTObservableValue obsWidget;
		UpdateValueStrategy usTargetToModel;

		{
			obsWidget = SWTObservables.observeText(imageFileText, SWT.Modify);
			obsModel = BeansObservables.observeValue(dbc.getValidationRealm(), config, ObjectPainterXMLConfig.PROPERTYNAME_IMAGE_FILE_NAME);
			usTargetToModel = new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT).setAfterGetValidator(new FileReadableValidator());
			dbc.bindValue(obsWidget, obsModel, usTargetToModel, null);
		}

		{
			obsWidget = SWTObservables.observeText(imageSizeWidthText, SWT.Modify);
			obsModel = BeansObservables.observeValue(dbc.getValidationRealm(), config, ObjectPainterXMLConfig.PROPERTYNAME_IMAGE_SIZE_X);
			usTargetToModel = new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT).setAfterConvertValidator(new IntegerRangeValidator("Width",
					0, Integer.MAX_VALUE));
			dbc.bindValue(obsWidget, obsModel, usTargetToModel, null);
		}
		{
			obsWidget = SWTObservables.observeText(imageSizeHeightText, SWT.Modify);
			obsModel = BeansObservables.observeValue(dbc.getValidationRealm(), config, ObjectPainterXMLConfig.PROPERTYNAME_IMAGE_SIZE_Y);
			usTargetToModel = new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT).setAfterConvertValidator(new IntegerRangeValidator(
					"Height", 0, Integer.MAX_VALUE));
			dbc.bindValue(obsWidget, obsModel, usTargetToModel, null);
		}
		{
			obsWidget = SWTObservables.observeSelection(this.drawLineCheckbox);
			obsModel = BeansObservables.observeValue(dbc.getValidationRealm(), config, ObjectPainterXMLConfig.PROPERTYNAME_DRAW_LINE);
			usTargetToModel = new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT);
			dbc.bindValue(obsWidget, obsModel, usTargetToModel, null);
		}
		{
			obsWidget = SWTObservables.observeSelection(keepProportionsButton);
			obsModel = BeansObservables.observeValue(dbc.getValidationRealm(), config, ObjectPainterXMLConfig.PROPERTYNAME_KEEP_PROPORTIONS);
			usTargetToModel = new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT);
			dbc.bindValue(obsWidget, obsModel, usTargetToModel, null);
		}
		{
			final MetricsXMLConfig mConf = spyglass.getConfigStore().getSpyglassConfig().getGeneralSettings().getMetrics();
			dbc
					.bindValue(SWTObservables.observeText(this.label3), BeansObservables.observeValue(dbc.getValidationRealm(), mConf, "unit"), null,
							null);
		}
		{
			final MetricsXMLConfig mConf = spyglass.getConfigStore().getSpyglassConfig().getGeneralSettings().getMetrics();
			dbc.bindValue(SWTObservables.observeText(this.label3a), BeansObservables.observeValue(dbc.getValidationRealm(), mConf, "unit"), null,
					null);
		}
		// TODO:
		{
			final IObservableValue observableColor = BeansObservables.observeValue(dbc.getValidationRealm(), config,
					ObjectPainterXMLConfig.PROPERTYNAME_LINE_COLOR);
			dbc.bindValue(SWTObservables.observeBackground(colorExample), observableColor,
					new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT).setConverter(new ColorToArrayConverter()), new UpdateValueStrategy()
							.setConverter(new ArrayToColorConverter(this.getDisplay())));
		}
		{
			final IObservableValue observable = BeansObservables.observeValue(dbc.getValidationRealm(), config,
					ObjectPainterXMLConfig.PROPERTYNAME_PACKET_TYPE_3D);
			dbc.bindValue(SWTObservables.observeSelection(combo1), observable, new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT)
					.setConverter(new Converter("", true) {

						@Override
						public Object convert(final Object fromObject) {
							return fromObject.equals("TrajectoryPacket3D");
						}

					}), new UpdateValueStrategy().setConverter(new Converter(true, "") {

				@Override
				public Object convert(final Object fromObject) {
					return ((Boolean) fromObject) ? "TrajectoryPacket3D" : "TrajectoryPacket2D";
				}
			}));
		}
		{
			obsWidget = SWTObservables.observeText(inteval, SWT.Modify);
			obsModel = BeansObservables.observeValue(dbc.getValidationRealm(), config, ObjectPainterXMLConfig.PROPERTYNAME_UPDATE_INTERVAL);
			usTargetToModel = new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT).setAfterConvertValidator(new IntegerRangeValidator(
					"Display update interval", 50, 10000));
			dbc.bindValue(obsWidget, obsModel, usTargetToModel, null);
		}

	}

	private void imageSizeHeightTextModifyText(final ModifyEvent evt) {
		if (aspectSettingInProgress || !this.keepProportionsButton.getSelection()) {
			return;
		}

		final String s = this.imageSizeHeightText.getText();
		aspectSettingInProgress = true;
		try {
			final int i = NumberFormat.getNumberInstance().parse(s).intValue();
			final int w = (int) (i / imageAspectRatio);
			this.imageSizeWidthText.setText(w + "");

		} catch (final ParseException e) {
			// this is no valid integer yet
		} finally {
			aspectSettingInProgress = false;
		}
		return;

	}

	private void imageSizeWidthTextModifyText(final ModifyEvent evt) {

		if (aspectSettingInProgress || !this.keepProportionsButton.getSelection()) {
			return;
		}

		final String s = this.imageSizeWidthText.getText();
		aspectSettingInProgress = true;

		try {
			final int i = NumberFormat.getNumberInstance().parse(s).intValue();
			final int h = (int) (i * imageAspectRatio);
			this.imageSizeHeightText.setText(h + "");

		} catch (final ParseException e) {
			// this is no valid integer yet
			return;
		} finally {
			aspectSettingInProgress = false;
		}
	}

	private void button1WidgetSelected(final SelectionEvent evt) {
		final ColorDialog dlg = new ColorDialog(this.getShell());
		dlg.setRGB(colorExample.getBackground().getRGB());
		final RGB color = dlg.open();
		if (color != null) {
			colorExample.setBackground(new Color(this.getDisplay(), color));
			this.page.markFormDirty();
		}
	}
}
