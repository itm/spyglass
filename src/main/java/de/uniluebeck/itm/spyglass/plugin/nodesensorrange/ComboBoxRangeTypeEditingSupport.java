/*
 * ----------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the
 * SwarmNet (www.swarmnet.de) project SpyGlass is free software;
 * you can redistribute it and/or modify it under the terms of the BSD License.
 * Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for further details.
 * ------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.nodesensorrange;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.ObservableValueEditingSupport;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Combo;

import de.uniluebeck.itm.spyglass.util.SpyglassLogger;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;

//--------------------------------------------------------------------------------
/**
 * Creates an observable {@link CCombo} box.
 *
 * @author Sebastian Ebers
 *
 */
public class ComboBoxRangeTypeEditingSupport extends ObservableValueEditingSupport {

	private CellEditor cellEditor;
	private DataBindingContext dbc;
	private String elementName = "value";
	private static final Logger log = SpyglassLoggerFactory.getLogger(ComboBoxRangeTypeEditingSupport.class);
	private String getterMethodName;

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 *
	 * @param viewer
	 *            the column viewer
	 * @param dbc
	 *            the data binding context
	 * @param elementName
	 *            the name of the model's element
	 * @param values
	 *            the values to be shown in the {@link Combo} box
	 *
	 */
	public ComboBoxRangeTypeEditingSupport(final ColumnViewer viewer, final DataBindingContext dbc, final String elementName, final String[] values) {
		super(viewer, dbc);
		this.dbc = dbc;
		this.elementName = elementName;
		cellEditor = new ComboBoxCellEditor(((TableViewer) viewer).getTable(), values, SWT.READ_ONLY);
		setGetterMethodName();
	}

	// --------------------------------------------------------------------------------
	/**
	 */
	private void setGetterMethodName() {
		getterMethodName = "get" + elementName.substring(0, 1).toUpperCase() + elementName.substring(1);
	}

	@Override
	protected CellEditor getCellEditor(final Object element) {

		/*
		 * Before the CellEditor is returned, the CCombo's index is set according to the object
		 * currently visible in the cell's row. Since only one CellEditor is used for the complete
		 * table, the selection index complies with the last selection - no matter in which row it
		 * took place! To obtain the expected behavior, the selection index is set up manually.
		 */

		try {

			// determine index of the combo box's string which matches the currently selected one in
			// the model ...
			final String selectedElement = element.getClass().getMethod(getterMethodName, new Class<?>[] {}).invoke(element).toString();
			final String[] items = ((CCombo) cellEditor.getControl()).getItems();
			for (int i = 0; i < items.length; i++) {
				if (items[i].equals(selectedElement)) {
					// ... and set the combo box's selection appropriately
					((CCombo) cellEditor.getControl()).select(i);
				}
			}
		} catch (final Exception e) {
			// setting up the correct selection index might fail but it has only limited impact on
			// the functionality. Additionally there is nothing the user can do so no window is to
			// be displayed to display the error message to the user.
			((SpyglassLogger) log).error("An exception occured while trying to determine the selected index of the combo box.", e, false);
		}
		return cellEditor;
	}

	@Override
	protected IObservableValue doCreateCellEditorObservable(final CellEditor cellEditor) {
		return SWTObservables.observeSelection(cellEditor.getControl());

	}

	@Override
	protected IObservableValue doCreateElementObservable(final Object element, final ViewerCell cell) {
		return BeansObservables.observeValue(dbc.getValidationRealm(), element, elementName);
	}

	@Override
	protected Binding createBinding(final IObservableValue target, final IObservableValue model) {
		return dbc.bindValue(target, model, new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT).setConverter(new NodeSensorRangeTypeConverter(
				String.class, Enum.class)), new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT).setConverter(new NodeSensorRangeTypeConverter(
				Enum.class, String.class)));
	}


}
