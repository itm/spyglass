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
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.viewers.ObservableValueEditingSupport;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColorCellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerCell;

/**
 * Creates an observable ColorCellEditor box.
 * 
 * @author Daniel Bimschas
 * 
 */
public class ColorEditingSupport extends ObservableValueEditingSupport {

	/*
	 * private class ColorCellEditor extends CellEditor {
	 * 
	 * private Label label;
	 * 
	 * public ColorCellEditor(final Composite parent) { super(parent); }
	 * 
	 * @Override protected Control createControl(final Composite parent) { label = new Label(parent,
	 * SWT.NONE); label.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK)); return
	 * label; }
	 * 
	 * @Override protected RGB doGetValue() { return label.getBackground().getRGB(); }
	 * 
	 * @Override protected void doSetFocus() { label.setFocus(); }
	 * 
	 * @Override protected void doSetValue(final Object value) {
	 * 
	 * if (!(value instanceof RGB)) { throw new RuntimeException("Param must be of type " +
	 * RGB.class.getCanonicalName()); }
	 * 
	 * final Color oldColor = label.getBackground(); label.setBackground(new
	 * Color(Display.getDefault(), (RGB) value)); oldColor.dispose();
	 * 
	 * } }
	 */

	private CellEditor cellEditor;

	private DataBindingContext dbc;

	private String elementName;

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
	 * 
	 */
	public ColorEditingSupport(final ColumnViewer viewer, final DataBindingContext dbc, final String elementName) {
		super(viewer, dbc);
		this.dbc = dbc;
		this.elementName = elementName;
		cellEditor = new ColorCellEditor(((TableViewer) viewer).getTable());
	}

	@Override
	protected CellEditor getCellEditor(final Object element) {
		return cellEditor;
	}

	@Override
	protected IObservableValue doCreateCellEditorObservable(final CellEditor cellEditor) {
		return BeansObservables.observeValue(dbc.getValidationRealm(), cellEditor, "value");
	}

	@Override
	protected IObservableValue doCreateElementObservable(final Object element, final ViewerCell cell) {
		return BeansObservables.observeValue(dbc.getValidationRealm(), element, elementName);
	}

	@Override
	protected Binding createBinding(final IObservableValue target, final IObservableValue model) {
		/*
		 * final UpdateValueStrategy targetToModel = new
		 * UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT); targetToModel.setConverter(new
		 * RGBToArrayConverter()); final UpdateValueStrategy modelToTarget = new
		 * UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT); modelToTarget.setConverter(new
		 * ArrayToRGBConverter());
		 */
		return dbc.bindValue(target, model, null, null);
	}
}
