// --------------------------------------------------------------------------------
/**
 *
 */
package de.uniluebeck.itm.spyglass.plugin.nodesensorrange;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.ObservableValueEditingSupport;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColorCellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;


// --------------------------------------------------------------------------------
/**
 * @author bimschas
 *
 */
public class NodeSensorRangeEditingSupport extends ObservableValueEditingSupport {

	public enum Field {
		LINE_WIDTH, LINE_COLOR, BACKGROUND_COLOR, BACKGROUND_ALPHA, TYPE
	}

	private CellEditor cellEditor;

	private DataBindingContext dbc;

	private String elementName;

	public NodeSensorRangeEditingSupport(final ColumnViewer viewer, final DataBindingContext dbc, final String elementName) {

		super(viewer, dbc);

		this.dbc = dbc;
		this.elementName = elementName;

		final boolean isNumberTextField = NodeSensorRangeXMLConfig.PROPERTYNAME_LINE_WIDTH.equals(elementName)
				|| NodeSensorRangeXMLConfig.PROPERTYNAME_BACKGROUND_ALPHA.equals(elementName);
		final boolean isColorField = NodeSensorRangeXMLConfig.PROPERTYNAME_BACKGROUND_R_G_B.equals(elementName)
				|| NodeSensorRangeXMLConfig.PROPERTYNAME_COLOR_R_G_B.equals(elementName);

		if (isNumberTextField) {

			this.cellEditor = new TextCellEditor((Composite) viewer.getControl());

		} else if (isColorField) {

			this.cellEditor = new ColorCellEditor((Composite) viewer.getControl());

		} else if (NodeSensorRangeXMLConfig.PROPERTYNAME_RANGE_TYPE.equals(elementName)) {

			this.cellEditor = new ComboBoxCellEditor((Composite) viewer.getControl(), new String[] {
					"Circle", "Cone", "Rectangle" });

		}
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

}
