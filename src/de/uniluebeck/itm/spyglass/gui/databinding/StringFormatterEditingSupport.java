package de.uniluebeck.itm.spyglass.gui.databinding;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.ObservableValueEditingSupport;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class StringFormatterEditingSupport extends ObservableValueEditingSupport {
	
	private CellEditor cellEditor;
	private DataBindingContext dbc;
	
	public StringFormatterEditingSupport(final ColumnViewer viewer, final DataBindingContext dbc) {
		
		super(viewer, dbc);
		this.dbc = dbc;
		cellEditor = new TextCellEditor((Composite) viewer.getControl());
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
		return BeansObservables.observeValue(dbc.getValidationRealm(), element, "value");
	}
	
}