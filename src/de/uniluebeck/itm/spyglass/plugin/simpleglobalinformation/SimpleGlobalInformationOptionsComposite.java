/* 
 * ----------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the
 * SwarmNet (www.swarmnet.de) project SpyGlass is free software;
 * you can redistribute it and/or modify it under the terms of the BSD License.
 * Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for further details.
 * ------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.simpleglobalinformation;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.cloudgarden.resource.SWTResourceManager;

import de.uniluebeck.itm.spyglass.gui.databinding.converter.IntListToStringConverter;
import de.uniluebeck.itm.spyglass.gui.databinding.converter.StringToIntListConverter;
import de.uniluebeck.itm.spyglass.gui.databinding.validator.IntegerRangeValidator;
import de.uniluebeck.itm.spyglass.gui.databinding.validator.StringToIntListValidator;

// --------------------------------------------------------------------------------
/**
 * Instances of this class are widgets edit configurations for {@link SimpleGlobalInformationPlugin}
 * s
 * 
 * @author Sebastian Ebers
 * 
 */
public class SimpleGlobalInformationOptionsComposite extends Composite {
	{
		// Register as a resource user - SWTResourceManager will
		// handle the obtaining and disposing of resources
		SWTResourceManager.registerResourceUser(this);
	}

	private Button showNumNodes, showAVGNodeDegree;
	private Text semanticTypes;

	private SGIStringFormatter stringFormatter = new SGIStringFormatter();

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 * 
	 * @param parent
	 *            the parent widget
	 * @param style
	 *            the style of the widget to construct
	 */
	public SimpleGlobalInformationOptionsComposite(final Composite parent, final int style) {
		super(parent, style);
		initGUI();
	}

	// --------------------------------------------------------------------------------
	/**
	 * @return the stringFormatter
	 */
	SGIStringFormatter getStringFormatter() {
		return stringFormatter;
	}

	private void initGUI() {

		GridData data;

		setLayout(new GridLayout());
		data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;
		// data.heightHint = 400;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		setLayoutData(data);

		{
			data = new GridData(SWT.TOP, SWT.LEFT, true, true);
			data.horizontalAlignment = GridData.FILL;
			data.verticalAlignment = GridData.FILL;
			data.grabExcessVerticalSpace = true;

			final Group groupSF = new Group(this, SWT.NONE);
			groupSF.setLayoutData(data);
			groupSF.setLayout(new GridLayout(3, false));
			groupSF.setText("Statistics");

			stringFormatter.addStringFormatterFields(groupSF, 3);

		}
		{
			data = new GridData(SWT.TOP, SWT.LEFT, true, true);
			data.horizontalAlignment = GridData.FILL;
			// data.verticalAlignment = GridData.FILL;
			// data.grabExcessHorizontalSpace = true;

			final Group groupPredef = new Group(this, SWT.NONE);
			groupPredef.setLayoutData(data);
			groupPredef.setLayout(new GridLayout(3, false));
			groupPredef.setText("Predefined options");

			showNumNodes = new Button(groupPredef, SWT.CHECK);
			showNumNodes.setText("Show the number of active nodes");

			new Label(groupPredef, SWT.NONE);
			new Label(groupPredef, SWT.NONE);

			showAVGNodeDegree = new Button(groupPredef, SWT.CHECK);
			showAVGNodeDegree.setText("Show average node degree");
			showAVGNodeDegree.addSelectionListener(new SelectionAdapter() {
				@SuppressWarnings("synthetic-access")
				@Override
				public void widgetSelected(final SelectionEvent e) {
					final boolean isSelected = ((Button) e.widget).getSelection();
					if (!isSelected && !StringToIntListValidator.isValid(semanticTypes.getText())) {
						semanticTypes.setText("-1");
					}
				}
			});

			new Label(groupPredef, SWT.NONE).setText("Semantic types: ");
			semanticTypes = new Text(groupPredef, SWT.BORDER);

			final GridData semanticTypesLData = new GridData();
			semanticTypesLData.heightHint = 17;
			semanticTypesLData.grabExcessHorizontalSpace = true;
			semanticTypesLData.horizontalAlignment = GridData.FILL;
			semanticTypes.setLayoutData(semanticTypesLData);

		}

		// make sure that the initial value of the semantic types field will not be erroneous if
		// semantic types are not needed at all!
		{
			if (!showAVGNodeDegree.getSelection() && !StringToIntListValidator.isValid(semanticTypes.getText())) {
				semanticTypes.setText("-1");
			}
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
	 */
	public void setDatabinding(final DataBindingContext dbc, final SimpleGlobalInformationXMLConfig config) {

		stringFormatter.setDataBinding(dbc);

		// show the total number of available nodes

		final IObservableValue observableshowNN = BeansObservables.observeValue(dbc.getValidationRealm(), config, "showNumNodes");
		dbc.bindValue(SWTObservables.observeSelection(this.showNumNodes), observableshowNN, new UpdateValueStrategy(
				UpdateValueStrategy.POLICY_CONVERT), null);

		// semantic types
		{
			final IObservableValue modelObservable2 = BeansObservables.observeValue(dbc.getValidationRealm(), config, "semanticTypes4Neighborhoods");
			final UpdateValueStrategy strFromModel = new UpdateValueStrategy();
			strFromModel.setConverter(new IntListToStringConverter());
			final UpdateValueStrategy strToModel = new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT);
			strToModel.setConverter(new StringToIntListConverter());
			strToModel.setAfterConvertValidator(new IntegerRangeValidator("Semantic types", -1, 255));
			strToModel.setAfterGetValidator(new StringToIntListValidator("Semantic types"));
			dbc.bindValue(SWTObservables.observeText(this.semanticTypes, SWT.Modify), modelObservable2, strToModel, strFromModel);
		}

		// show the average node degree
		final IObservableValue observableshowAVGND = BeansObservables.observeValue(dbc.getValidationRealm(), config, "showNodeDegree");
		dbc.bindValue(SWTObservables.observeSelection(this.showAVGNodeDegree), observableshowAVGND, new UpdateValueStrategy(
				UpdateValueStrategy.POLICY_CONVERT), null);

		// if the average node degree is not to be shown, disable the semantic types field
		{
			dbc.bindValue(SWTObservables.observeSelection(this.showAVGNodeDegree), SWTObservables.observeEnabled(this.semanticTypes), null,
					new UpdateValueStrategy(UpdateValueStrategy.POLICY_NEVER));

			semanticTypes.setEnabled(showAVGNodeDegree.getSelection());
		}

	}
}
