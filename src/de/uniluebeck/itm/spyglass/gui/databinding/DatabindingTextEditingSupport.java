/*
 * --------------------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass. Copyright (C)
 * 2004-2007 by the SwarmNet (www.swarmnet.de) project SpyGlass is free
 * software; you can redistribute it and/or modify it under the terms of the BSD
 * License. Refer to spyglass-licence.txt file in the root of the SpyGlass
 * source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.gui.databinding;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.ObservableValueEditingSupport;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class DatabindingTextEditingSupport extends ObservableValueEditingSupport {

	protected CellEditor cellEditor;
	protected DataBindingContext dbc;
	protected String elementName = "value";
	protected IValidator afterConvertValidator;
	protected IValidator afterGetValidator;
	protected IConverter converter;

	public DatabindingTextEditingSupport(final ColumnViewer viewer, final DataBindingContext dbc, final String elementName,
			final IConverter converter, final IValidator afterConvertValidator, final IValidator afterGetValidator) {
		super(viewer, dbc);
		this.dbc = dbc;
		this.cellEditor = new TextCellEditor((Composite) viewer.getControl());
		this.afterConvertValidator = afterConvertValidator;
		this.afterGetValidator = afterGetValidator;
		this.converter = converter;

		this.elementName = elementName;
	}

	public DatabindingTextEditingSupport(final ColumnViewer viewer, final DataBindingContext dbc, final IConverter converter,
			final IValidator afterConvertValidator, final IValidator afterGetValidator) {
		super(viewer, dbc);
		this.dbc = dbc;
		this.cellEditor = new TextCellEditor((Composite) viewer.getControl());
		this.afterConvertValidator = afterConvertValidator;
		this.afterGetValidator = afterGetValidator;
		this.converter = converter;
	}

	@Override
	protected CellEditor getCellEditor(final Object element) {
		return cellEditor;
	}

	@Override
	protected IObservableValue doCreateCellEditorObservable(final CellEditor cellEditor) {
		return SWTObservables.observeText(cellEditor.getControl(), SWT.Modify);
	}

	@Override
	protected IObservableValue doCreateElementObservable(final Object element, final ViewerCell cell) {
		return BeansObservables.observeValue(dbc.getValidationRealm(), element, elementName);
	}

	@Override
	protected Binding createBinding(final IObservableValue target, final IObservableValue model) {
		final UpdateValueStrategy s = new UpdateValueStrategy().setAfterConvertValidator(afterConvertValidator).setAfterGetValidator(
				afterGetValidator).setConverter(converter);

		return dbc.bindValue(target, model, s, null);

	}
}