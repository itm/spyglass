// --------------------------------------------------------------------------------
/**
 *
 */
package de.uniluebeck.itm.spyglass.plugin.nodesensorrange;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.jface.databinding.viewers.ObservableSetContentProvider;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableItem;

import de.uniluebeck.itm.spyglass.gui.databinding.ColorEditingSupport;
import de.uniluebeck.itm.spyglass.gui.databinding.DatabindingTextEditingSupport;
import de.uniluebeck.itm.spyglass.gui.databinding.WrappedObservableSet;
import de.uniluebeck.itm.spyglass.gui.databinding.validator.IntegerRangeValidator;
import de.uniluebeck.itm.spyglass.plugin.nodesensorrange.NodeSensorRangeXMLConfig.Config;
import de.uniluebeck.itm.spyglass.plugin.nodesensorrange.NodeSensorRangeXMLConfig.NodeSensorRange;
import de.uniluebeck.itm.spyglass.plugin.nodesensorrange.NodeSensorRangeXMLConfig.RANGE_TYPE;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;

// --------------------------------------------------------------------------------
/**
 * @author bimschas
 * 
 */
public class NodeSensorRangePerNodeConfigurationComposite {

	private static final Logger log = SpyglassLoggerFactory.getLogger(NodeSensorRangePerNodeConfigurationComposite.class);

	private NodeSensorRangePreferencePage page;

	private TableViewerColumn columnNodeId;

	private TableViewerColumn columnLineWidth;

	private TableViewerColumn columnLineColor;

	private TableViewerColumn columnBackgroundColor;

	private TableViewerColumn columnBackgroundAlpha;

	private TableViewerColumn columnType;

	private TableViewerColumn columnButton;

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
			// editing support is set later on data binding
		}
		{
			columnLineColor = new TableViewerColumn(table, SWT.LEFT);
			columnLineColor.getColumn().setWidth(170);
			columnLineColor.getColumn().setText("Line Color");
			// editing support is set later on data binding
		}
		{
			columnBackgroundColor = new TableViewerColumn(table, SWT.LEFT);
			columnBackgroundColor.getColumn().setWidth(170);
			columnBackgroundColor.getColumn().setText("Background Color");
			// editing support is set later on data binding
		}
		{
			columnBackgroundAlpha = new TableViewerColumn(table, SWT.LEFT);
			columnBackgroundAlpha.getColumn().setWidth(105);
			columnBackgroundAlpha.getColumn().setText("Background Alpha");
			// editing support is set later on data binding
		}
		{
			columnType = new TableViewerColumn(table, SWT.LEFT);
			columnType.getColumn().setWidth(50);
			columnType.getColumn().setText("Type");
			// editing support is set later on data binding
		}
		{
			columnButton = new TableViewerColumn(table, SWT.NONE);
			columnButton.getColumn().setWidth(50);
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

	// --------------------------------------------------------------------------------
	/**
	 * Snagged this code from https://bugs.eclipse.org/bugs/show_bug.cgi?id=36977 credit to Tom
	 * Schindl
	 * 
	 * @author Dariush Forouher
	 */
	private final class ButtonCellLabelProvider extends CellLabelProvider {

		@Override
		public void update(final ViewerCell cell) {

			final TableItem item = (TableItem) cell.getItem();
			if (item.getData("EDITOR") != null) {
				final TableEditor editor = (TableEditor) item.getData("EDITOR");
				editor.dispose();
			}

			final TableEditor editor = new TableEditor(item.getParent());
			final Composite comp = new Composite(item.getParent(), SWT.NONE);
			comp.setBackground(item.getParent().getBackground());
			comp.setBackgroundMode(SWT.INHERIT_DEFAULT);
			final RowLayout l = new RowLayout();
			l.marginHeight = 0;
			l.marginWidth = 0;
			l.marginTop = 0;
			l.marginBottom = 0;

			comp.setLayout(l);
			final Button button = new Button(comp, SWT.PUSH);
			button.setText("Options");
			button.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(final SelectionEvent e) {
					final TableItem item = editor.getItem();
					final NodeSensorRangeXMLConfig.Config config = (Config) item.getData();

					final RANGE_TYPE selectedRangeType = config.getRangeType();

					final NodeSensorRange defaultRange = config.getRange();
					final NodeRangeDialog dialog = NodeRangeDialog.createDialog(table.getTable().getShell(), selectedRangeType, defaultRange);

					if (Window.OK == dialog.open()) {
						config.setRange(dialog.range);
					}
				}
			});

			// dispose the button when the TableItem is disposed.
			item.addDisposeListener(new DisposeListener() {

				@Override
				public void widgetDisposed(final DisposeEvent e) {
					comp.dispose();
				}

			});

			editor.grabHorizontal = true;
			editor.setEditor(comp, item, 6); // number of the column starting from zero!!!
		}

	}

	private class NodeSensorRangeColorLabelProvider extends ColumnLabelProvider {

		private String getterMethodName;
		private Color color;

		public NodeSensorRangeColorLabelProvider(final String elementName) {
			this.getterMethodName = "get" + elementName.substring(0, 1).toUpperCase() + elementName.substring(1);
		}

		@Override
		public Color getForeground(final Object element) {
			try {
				final Method method;
				method = element.getClass().getMethod(getterMethodName, new Class<?>[] {});
				final RGB rgb = (RGB) method.invoke(element);
				color = new Color(Display.getDefault(), rgb);
				return color;
			} catch (final Exception e) {
				throw new RuntimeException("Bug", e);
			}
		}

		@Override
		public Color getBackground(final Object element) {
			try {
				final Method method;
				method = element.getClass().getMethod(getterMethodName, new Class<?>[] {});
				final RGB rgb = (RGB) method.invoke(element);
				color = new Color(Display.getDefault(), rgb);
				return color;
			} catch (final Exception e) {
				throw new RuntimeException("Bug", e);
			}
		}

		@Override
		public String getText(final Object element) {
			return "Color";
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
	public void setDataBinding(final DataBindingContext dbc, final NodeSensorRangePreferencePage page) {

		this.page = page;

		columnLineWidth.setEditingSupport(new DatabindingTextEditingSupport(table, dbc, NodeSensorRangeXMLConfig.PROPERTYNAME_LINE_WIDTH, null,
				new IntegerRangeValidator(0, 2000), null));
		columnLineColor.setEditingSupport(new ColorEditingSupport(table, dbc, NodeSensorRangeXMLConfig.PROPERTYNAME_COLOR_R_G_B));
		columnBackgroundColor.setEditingSupport(new ColorEditingSupport(table, dbc, NodeSensorRangeXMLConfig.PROPERTYNAME_BACKGROUND_R_G_B));
		columnBackgroundAlpha.setEditingSupport(new DatabindingTextEditingSupport(table, dbc, NodeSensorRangeXMLConfig.PROPERTYNAME_BACKGROUND_ALPHA,
				null, new IntegerRangeValidator(0, 255), null));

		columnType.setEditingSupport(new ComboBoxRangeTypeEditingSupport(table, dbc, NodeSensorRangeXMLConfig.PROPERTYNAME_RANGE_TYPE, new String[] {
				"Circle", "Cone", "Rectangle" }));

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

		// we need to map something. it doesn't matter what
		final IObservableMap buttonMap = BeansObservables.observeMap(contentProvider.getKnownElements(), NodeSensorRangeXMLConfig.Config.class,
				NodeSensorRangeXMLConfig.PROPERTYNAME_NODE_ID);

		final IObservableMap[] columnMaps = new IObservableMap[] { nodeIdMap, lineWidthMap, lineColorMap, backgroundColorMap, backgroundAlphaMap,
				typeMap, buttonMap };

		table.setLabelProvider(new ObservableMapLabelProvider(columnMaps));

		// after we have defined a generic label provider for the entire table, we can now set
		// individual label providers
		// for specific columns.

		columnBackgroundColor.setLabelProvider(new NodeSensorRangeColorLabelProvider(NodeSensorRangeXMLConfig.PROPERTYNAME_BACKGROUND_R_G_B));
		columnLineColor.setLabelProvider(new NodeSensorRangeColorLabelProvider(NodeSensorRangeXMLConfig.PROPERTYNAME_COLOR_R_G_B));

		columnButton.setLabelProvider(new ButtonCellLabelProvider());

	}

	public void connectTableWithData(final DataBindingContext dbc, final Set<Config> set) {

		tableData = new WrappedObservableSet(dbc.getValidationRealm(), set, null);
		table.setInput(tableData);

		table.refresh();

		log.info("set new input");
	}

	private void clickedButtonDelEntry(final SelectionEvent evt) {
		final IStructuredSelection selection = (IStructuredSelection) table.getSelection();
		for (final Object o : selection.toList()) {
			tableData.remove(o);
		}
		page.markFormDirty();
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
			final int nodeId = Integer.parseInt(dlg.getValue());
			// check if there's already a config for this nodeId and simply ignore if there is
			for (final Iterator<Config> iterator = tableData.iterator(); iterator.hasNext();) {
				final Config type = iterator.next();
				if (type.getNodeId() == nodeId) {
					return;
				}
			}
			final Config cfg = new Config();
			cfg.setNodeId(nodeId);
			tableData.add(cfg);
			page.markFormDirty();
		}

		// this is a hack and will probably not work every time.
		table.refresh();
	}
}
