/*
 * --------------------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass. Copyright (C)
 * 2004-2007 by the SwarmNet (www.swarmnet.de) project SpyGlass is free
 * software; you can redistribute it and/or modify it under the terms of the BSD
 * License. Refer to spyglass-licence.txt file in the root of the SpyGlass
 * source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.vectorsequencepainter;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.cloudgarden.resource.SWTResourceManager;

import de.uniluebeck.itm.spyglass.gui.databinding.converter.ArrayToColorConverter;
import de.uniluebeck.itm.spyglass.gui.databinding.converter.ColorToArrayConverter;
import de.uniluebeck.itm.spyglass.gui.databinding.validator.IntegerRangeValidator;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

public class VectorSequencePainterOptionsComposite extends Composite {

	private Group group;
	private Text lineWidth;
	private CLabel lineColor;
	private Button buttonLineColor;
	private VectorSequencePainterPreferencePage page;
	private Text ttl;
	private Text dimension;

	{
		// Register as a resource user - SWTResourceManager will
		// handle the obtaining and disposing of resources
		SWTResourceManager.registerResourceUser(this);
	}

	public VectorSequencePainterOptionsComposite(final Composite parent) {
		super(parent, SWT.NONE);
		initGUI();
	}

	private void initGUI() {

		GridData data;
		Label label;

		setLayout(new GridLayout());
		data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		setLayoutData(data);

		{
			data = new GridData(SWT.TOP, SWT.LEFT, true, true);
			data.horizontalAlignment = GridData.FILL;
			data.verticalAlignment = GridData.FILL;

			group = new Group(this, SWT.NONE);
			group.setLayoutData(data);
			group.setLayout(new GridLayout(3, false));
			group.setText("Display");

			{
				label = new Label(group, SWT.NONE);
				label.setText("Line width");

				data = new GridData();
				data.widthHint = 40;
				data.horizontalSpan = 2;

				lineWidth = new Text(group, SWT.BORDER);
				lineWidth.setLayoutData(data);

				label = new Label(group, SWT.NONE);
				label.setText("Line color");

				data = new GridData();
				data.widthHint = 50;
				data.heightHint = 19;

				lineColor = new CLabel(group, SWT.BORDER);
				lineColor.setLayoutData(data);

				data = new GridData();

				buttonLineColor = new Button(group, SWT.PUSH | SWT.CENTER);
				buttonLineColor.setText("Change color");
				buttonLineColor.setLayoutData(data);
				buttonLineColor.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(final SelectionEvent evt) {
						final ColorDialog dlg = new ColorDialog(getShell());
						dlg.setRGB(lineColor.getBackground().getRGB());
						final RGB color = dlg.open();
						if (color != null) {
							lineColor.setBackground(new Color(getDisplay(), color));
							page.markFormDirty();
						}
					}
				});

				label = new Label(group, SWT.NONE);
				label.setText("Time to live");

				data = new GridData();
				data.widthHint = 40;
				data.horizontalSpan = 2;

				ttl = new Text(group, SWT.BORDER);
				ttl.setLayoutData(data);

				label = new Label(group, SWT.NONE);
				label.setText("Dimension");

				data = new GridData();
				data.widthHint = 40;
				data.horizontalSpan = 2;

				dimension = new Text(group, SWT.BORDER);
				dimension.setLayoutData(data);

			}

		}

	}

	public void setDatabinding(final DataBindingContext dbc, final VectorSequencePainterXMLConfig config,
			final VectorSequencePainterPreferencePage page) {

		this.page = page;

		IObservableValue obsModel;
		ISWTObservableValue obsWidget;
		UpdateValueStrategy usTargetToModel;
		UpdateValueStrategy usModelToTarget;

		{
			obsWidget = SWTObservables.observeText(lineWidth, SWT.Modify);
			obsModel = BeansObservables.observeValue(dbc.getValidationRealm(), config, VectorSequencePainterXMLConfig.PROPERTYNAME_LINE_WIDTH);
			usTargetToModel = new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT);
			usTargetToModel.setAfterConvertValidator(new IntegerRangeValidator("Line Width", 1, Integer.MAX_VALUE));
			dbc.bindValue(obsWidget, obsModel, usTargetToModel, null);
		}
		{
			obsWidget = SWTObservables.observeBackground(lineColor);
			obsModel = BeansObservables.observeValue(dbc.getValidationRealm(), config, VectorSequencePainterXMLConfig.PROPERTYNAME_LINE_COLOR_R_G_B);
			usTargetToModel = new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT);
			usTargetToModel.setConverter(new ColorToArrayConverter());
			usModelToTarget = new UpdateValueStrategy();
			usModelToTarget.setConverter(new ArrayToColorConverter(this.getDisplay()));
			dbc.bindValue(obsWidget, obsModel, usTargetToModel, usModelToTarget);
		}
		{
			obsWidget = SWTObservables.observeText(ttl, SWT.Modify);
			obsModel = BeansObservables.observeValue(dbc.getValidationRealm(), config, PluginXMLConfig.PROPERTYNAME_TIMEOUT);
			usTargetToModel = new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT);
			usTargetToModel.setAfterConvertValidator(new IntegerRangeValidator("Time To Live", 0, Integer.MAX_VALUE));
			dbc.bindValue(obsWidget, obsModel, usTargetToModel, null);
		}
		{
			obsWidget = SWTObservables.observeText(dimension, SWT.Modify);
			obsModel = BeansObservables.observeValue(dbc.getValidationRealm(), config, VectorSequencePainterXMLConfig.PROPERTYNAME_DIMENSION);
			usTargetToModel = new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT);
			usTargetToModel.setAfterConvertValidator(new IntegerRangeValidator("Dimension", 2, 3));
			dbc.bindValue(obsWidget, obsModel, usTargetToModel, null);
		}

	}
}
