/* 
 * ----------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the
 * SwarmNet (www.swarmnet.de) project SpyGlass is free software;
 * you can redistribute it and/or modify it under the terms of the BSD License.
 * Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for further details.
 * ------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.gui.databinding;

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
import org.eclipse.swt.widgets.Combo;

import de.uniluebeck.itm.spyglass.gui.databinding.converter.StatisticOperationConverter;

//--------------------------------------------------------------------------------
/**
 * Creates an observable {@link Combo} box.
 * 
 * @author Sebastian Ebers
 * 
 */
public class ComboBoxEditingSupport extends ObservableValueEditingSupport {

	private CellEditor cellEditor;
	private DataBindingContext dbc;
	private String elementName = "value";

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
	public ComboBoxEditingSupport(final ColumnViewer viewer, final DataBindingContext dbc, final String elementName, final String[] values) {
		super(viewer, dbc);
		this.dbc = dbc;
		cellEditor = new ComboBoxCellEditor(((TableViewer) viewer).getTable(), values, SWT.READ_ONLY);
		this.elementName = elementName;
	}

	@Override
	protected CellEditor getCellEditor(final Object element) {
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
		return dbc.bindValue(target, model, new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT).setConverter(new StatisticOperationConverter(
				String.class, Enum.class)), new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT).setConverter(new StatisticOperationConverter(
				Enum.class, String.class)));
	}

}
