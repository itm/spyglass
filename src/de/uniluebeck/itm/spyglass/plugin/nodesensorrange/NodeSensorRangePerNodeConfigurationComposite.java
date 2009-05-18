// --------------------------------------------------------------------------------
/**
 * 
 */
package de.uniluebeck.itm.spyglass.plugin.nodesensorrange;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.jface.databinding.viewers.ObservableSetContentProvider;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import de.uniluebeck.itm.spyglass.gui.databinding.ColorEditingSupport;
import de.uniluebeck.itm.spyglass.gui.databinding.ComboBoxEditingSupport;
import de.uniluebeck.itm.spyglass.gui.databinding.WrappedObservableSet;
import de.uniluebeck.itm.spyglass.plugin.nodesensorrange.NodeSensorRangeXMLConfig.Config;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;

// --------------------------------------------------------------------------------
/**
 * @author bimschas
 * 
 */
public class NodeSensorRangePerNodeConfigurationComposite {

	private static final Logger log = SpyglassLoggerFactory.getLogger(NodeSensorRangePerNodeConfigurationComposite.class);

	private TableViewerColumn columnNodeId;

	private TableViewerColumn columnLineWidth;

	private TableViewerColumn columnLineColor;

	private TableViewerColumn columnBackgroundColor;

	private TableViewerColumn columnBackgroundAlpha;

	private TableViewerColumn columnType;

	/**
	 * Reference to the set backing the table. All edits have to go through this set, so that
	 * changeListeners are being noticed.
	 */
	private IObservableSet tableData;

	private TableViewer table;

	private Composite parent;

	private Button addEntry;

	private Composite buttonComposite;

	private Button delEntry;

	public void addPerNodeConfigurationTable(final Composite parent) {

		this.parent = parent;

		{
			final GridData tableLData = new GridData();
			tableLData.horizontalSpan = 2;
			tableLData.grabExcessHorizontalSpace = true;
			tableLData.horizontalAlignment = GridData.FILL;
			tableLData.verticalAlignment = GridData.FILL;
			tableLData.grabExcessVerticalSpace = true;
			tableLData.heightHint = 100;

			table = new TableViewer(parent, SWT.FULL_SELECTION);
			table.getControl().setLayoutData(tableLData);
			table.getTable().setLinesVisible(true);
			table.getTable().setHeaderVisible(true);

		}
		{
			columnNodeId = new TableViewerColumn(table, SWT.LEFT);
			columnNodeId.getColumn().setWidth(80);
			columnNodeId.getColumn().setText("Node ID");

			// sort by node ID
			table.getTable().setSortColumn(columnNodeId.getColumn());
			table.getTable().setSortDirection(SWT.DOWN);
		}
		{
			columnLineWidth = new TableViewerColumn(table, SWT.LEFT);
			columnLineWidth.getColumn().setWidth(60);
			columnLineWidth.getColumn().setText("Line Width");
			columnLineWidth.setLabelProvider(new NodeSensorRangeTextLabelProvider(NodeSensorRangeXMLConfig.PROPERTYNAME_LINE_WIDTH));
			columnLineWidth.setEditingSupport(new NodeSensorRangeTextEditingSupport(table, NodeSensorRangeXMLConfig.PROPERTYNAME_LINE_WIDTH,
					int.class));
		}
		{
			columnLineColor = new TableViewerColumn(table, SWT.LEFT);
			columnLineColor.getColumn().setWidth(170);
			columnLineColor.getColumn().setText("Line Color");
			columnLineColor.setLabelProvider(new NodeSensorRangeColorLabelProvider(NodeSensorRangeXMLConfig.PROPERTYNAME_COLOR_R_G_B));
			// editing support is set later on data binding
		}
		{
			columnBackgroundColor = new TableViewerColumn(table, SWT.LEFT);
			columnBackgroundColor.getColumn().setWidth(170);
			columnBackgroundColor.getColumn().setText("Background Color");
			columnBackgroundColor.setLabelProvider(new NodeSensorRangeColorLabelProvider(NodeSensorRangeXMLConfig.PROPERTYNAME_BACKGROUND_R_G_B));
			// editing support is set later on data binding
		}
		{
			columnBackgroundAlpha = new TableViewerColumn(table, SWT.LEFT);
			columnBackgroundAlpha.getColumn().setWidth(105);
			columnBackgroundAlpha.getColumn().setText("Background Alpha");
			columnBackgroundAlpha.setLabelProvider(new NodeSensorRangeTextLabelProvider(NodeSensorRangeXMLConfig.PROPERTYNAME_BACKGROUND_ALPHA));
			columnBackgroundAlpha.setEditingSupport(new NodeSensorRangeTextEditingSupport(table,
					NodeSensorRangeXMLConfig.PROPERTYNAME_BACKGROUND_ALPHA, int.class));
		}
		{
			columnType = new TableViewerColumn(table, SWT.LEFT);
			columnType.getColumn().setWidth(50);
			columnType.getColumn().setText("Type");
			// columnType.setLabelProvider(new
			// NodeSensorRangeTextLabelProvider(NodeSensorRangeXMLConfig.PROPERTYNAME_RANGE_TYPE));
			// editing support is set later on data binding
		}
		{
			buttonComposite = new Composite(parent, SWT.NONE);
			buttonComposite.setLayout(new GridLayout(2, false));
			final GridData data = new GridData();
			buttonComposite.setLayoutData(data);
		}
		{
			addEntry = new Button(buttonComposite, SWT.PUSH | SWT.CENTER);
			final GridData addEntryLData = new GridData();
			addEntryLData.verticalAlignment = GridData.BEGINNING;
			addEntryLData.horizontalAlignment = GridData.END;
			addEntryLData.widthHint = 100;
			addEntry.setLayoutData(addEntryLData);
			addEntry.setText("Add");
			addEntry.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(final SelectionEvent evt) {
					clickedButtonAddEntry(evt);
				}
			});
		}
		{
			final GridData data = new GridData();
			data.widthHint = 100;
			delEntry = new Button(buttonComposite, SWT.PUSH | SWT.CENTER);
			delEntry.setLayoutData(data);
			delEntry.setText("Delete");
			delEntry.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(final SelectionEvent evt) {
					clickedButtonDelEntry(evt);
				}
			});
		}

	}

	private class NodeSensorRangeTextEditingSupport extends EditingSupport {

		private CellEditor cellEditor;

		private String getterMethodName;

		private String setterMethodName;

		private Class<?> clazz;

		public NodeSensorRangeTextEditingSupport(final TableViewer table, final String elementName, final Class<?> clazz) {
			super(table);
			this.clazz = clazz;
			this.getterMethodName = "get" + elementName.substring(0, 1).toUpperCase() + elementName.substring(1);
			this.setterMethodName = "set" + elementName.substring(0, 1).toUpperCase() + elementName.substring(1);
		}

		@Override
		protected boolean canEdit(final Object element) {
			return element instanceof NodeSensorRangeXMLConfig.Config;
		}

		@Override
		protected CellEditor getCellEditor(final Object element) {
			if (cellEditor == null) {
				cellEditor = new TextCellEditor(table.getTable(), SWT.SINGLE);
			}
			return cellEditor;
		}

		@Override
		protected Object getValue(final Object element) {
			try {
				final Method method;
				method = element.getClass().getMethod(getterMethodName, new Class<?>[] {});
				return method.invoke(element).toString();
			} catch (final Exception e) {
				log.error("", e);
			}
			return null;
		}

		@Override
		protected void setValue(final Object element, final Object value) {
			try {
				final Method method;
				method = element.getClass().getMethod(setterMethodName, new Class<?>[] { clazz });
				Object setterParam;
				if (((value instanceof String) && clazz.equals(int.class)) || clazz.equals(Integer.class)) {
					setterParam = Integer.parseInt((String) value);
				} else {
					setterParam = clazz.cast(value);
				}
				method.invoke(element, setterParam);
			} catch (final Exception e) {
				log.error("", e);
			}
		}

	}

	private class NodeSensorRangeTextLabelProvider extends ColumnLabelProvider {

		private String getterMethodName;

		public NodeSensorRangeTextLabelProvider(final String elementName) {
			this.getterMethodName = "get" + elementName.substring(0, 1).toUpperCase() + elementName.substring(1);
		}

		@Override
		public void update(final ViewerCell cell) {
			try {
				final Object element = cell.getElement();
				final Method method;
				method = element.getClass().getMethod(getterMethodName, new Class<?>[] {});
				cell.setText(method.invoke(element).toString());
			} catch (final Exception e) {
				log.error("", e);
			}
		}
	}

	private class NodeSensorRangeColorLabelProvider extends ColumnLabelProvider {

		private String getterMethodName;
		private Color color;

		public NodeSensorRangeColorLabelProvider(final String elementName) {
			this.getterMethodName = "get" + elementName.substring(0, 1).toUpperCase() + elementName.substring(1);
		}

		@Override
		public Color getBackground(final Object element) {
			return color;
		}

		@Override
		public void update(final ViewerCell cell) {
			try {
				final Object element = cell.getElement();
				final Method method;
				method = element.getClass().getMethod(getterMethodName, new Class<?>[] {});
				final RGB rgb = (RGB) method.invoke(element);
				color = new Color(Display.getDefault(), rgb);
				cell.getControl().setBackground(color);
			} catch (final Exception e) {
				log.error("", e);
			}
		}
	};

	// --------------------------------------------------------------------------------
	/**
	 * Activates data binding
	 * 
	 * @param dbc
	 *            the {@link DataBindingContext}
	 * @param config
	 *            the configuration which represents the model
	 */
	public void setDataBinding(final DataBindingContext dbc, final NodeSensorRangeXMLConfig config) {

		columnLineColor.setEditingSupport(new ColorEditingSupport(table, dbc, NodeSensorRangeXMLConfig.PROPERTYNAME_COLOR_R_G_B));
		columnBackgroundColor.setEditingSupport(new ColorEditingSupport(table, dbc, NodeSensorRangeXMLConfig.PROPERTYNAME_BACKGROUND_R_G_B));
		columnType.setEditingSupport(new ComboBoxEditingSupport(table, dbc, NodeSensorRangeXMLConfig.PROPERTYNAME_RANGE_TYPE, new String[] {
				NodeSensorRangeXMLConfig.PROPERTYVALUE_RANGE_TYPE_CIRCLE, NodeSensorRangeXMLConfig.PROPERTYVALUE_RANGE_TYPE_CONE,
				NodeSensorRangeXMLConfig.PROPERTYVALUE_RANGE_TYPE_RECTANGLE }));

		final ObservableSetContentProvider contentProvider = new ObservableSetContentProvider();
		table.setContentProvider(contentProvider);

		final IObservableMap backgroundColorMap = BeansObservables.observeMap(contentProvider.getKnownElements(),
				NodeSensorRangeXMLConfig.Config.class, NodeSensorRangeXMLConfig.PROPERTYNAME_BACKGROUND_R_G_B);

		final IObservableMap backgroundAlphaMap = BeansObservables.observeMap(contentProvider.getKnownElements(),
				NodeSensorRangeXMLConfig.Config.class, NodeSensorRangeXMLConfig.PROPERTYNAME_BACKGROUND_ALPHA);

		final IObservableMap lineColorMap = BeansObservables.observeMap(contentProvider.getKnownElements(), NodeSensorRangeXMLConfig.Config.class,
				NodeSensorRangeXMLConfig.PROPERTYNAME_COLOR_R_G_B);

		final IObservableMap lineWidthMap = BeansObservables.observeMap(contentProvider.getKnownElements(), NodeSensorRangeXMLConfig.Config.class,
				NodeSensorRangeXMLConfig.PROPERTYNAME_LINE_WIDTH);

		final IObservableMap typeMap = BeansObservables.observeMap(contentProvider.getKnownElements(), NodeSensorRangeXMLConfig.Config.class,
				NodeSensorRangeXMLConfig.PROPERTYNAME_RANGE_TYPE);

		final IObservableMap nodeIdMap = BeansObservables.observeMap(contentProvider.getKnownElements(), NodeSensorRangeXMLConfig.Config.class,
				NodeSensorRangeXMLConfig.PROPERTYNAME_NODE_ID);

		final IObservableMap[] columnMaps = new IObservableMap[] { nodeIdMap, lineWidthMap, lineColorMap, backgroundColorMap, backgroundAlphaMap,
				typeMap };

		table.setLabelProvider(new ObservableMapLabelProvider(columnMaps));
		tableData = new WrappedObservableSet(dbc.getValidationRealm(), config.getPerNodeConfigs(), null);
		table.setInput(tableData);

	}

	private void clickedButtonDelEntry(final SelectionEvent evt) {
		// TODO Auto-generated method stub

	}

	private void clickedButtonAddEntry(final SelectionEvent evt) {
		final InputDialog dlg = new InputDialog(parent.getShell(), "Enter a node ID", "Please enter a node ID", "", new IInputValidator() {

			@Override
			public String isValid(final String newText) {
				try {
					if (Integer.parseInt(newText) < 0) {
						return "Please enter a positive number";
					}
					return null;

				} catch (final NumberFormatException e) {
					return "Please enter an integer";
				}

			}

		});
		dlg.setBlockOnOpen(true);
		final int ret = dlg.open();
		if (ret == Window.OK) {
			final Config cfg = new Config();
			cfg.setNodeId(Integer.parseInt(dlg.getValue()));
			tableData.add(cfg);

		}

		// this is a hack and will probably not work every time.
		table.refresh();
	}
}
