package de.uniluebeck.itm.spyglass.plugin.simplenodepainter;

import java.util.HashMap;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.jface.databinding.viewers.ObservableSetContentProvider;
import org.eclipse.jface.databinding.viewers.ObservableValueEditingSupport;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.cloudgarden.resource.SWTResourceManager;

import de.uniluebeck.itm.spyglass.gui.converter.ArrayToColorConverter;
import de.uniluebeck.itm.spyglass.gui.converter.ColorToArrayConverter;
import de.uniluebeck.itm.spyglass.plugin.simplenodepainter.WrappedSet.ObservableEntry;
import de.uniluebeck.itm.spyglass.util.SpyglassLogger;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

public class OptionsComposite extends org.eclipse.swt.widgets.Composite {
	
	private class StringFormatterEditingSupport extends ObservableValueEditingSupport {
		
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
		protected IObservableValue doCreateElementObservable(final Object element,
				final ViewerCell cell) {
			return BeansObservables.observeValue(dbc.getValidationRealm(), element, "value");
		}
		
	}
	
	{
		// Register as a resource user - SWTResourceManager will
		// handle the obtaining and disposing of resources
		SWTResourceManager.registerResourceUser(this);
	}
	
	private static final Logger log = SpyglassLogger.getLogger(OptionsComposite.class);
	private Group group1;
	private Label label1;
	private Button delEntry;
	private Button addEntry;
	private TableViewer table;
	private Text defaultStringFmt;
	private Label label3;
	private Button showExtInf;
	private CLabel colorExample;
	private Button lineColor;
	private Label label2;
	private Text lineWidth;
	private TableViewerColumn columnFormatString;
	private TableViewerColumn columnTypes;
	
	/**
	 * Reference to the set backing the table. All edits have to go through this set, so that
	 * changeListeners are being noticed.
	 */
	private IObservableSet tableData;
	
	SimpleNodePainterPreferencePage page;
	
	/**
	 * Auto-generated main method to display this org.eclipse.swt.widgets.Composite inside a new
	 * Shell.
	 */
	public static void main(final String[] args) {
		showGUI();
	}
	
	/**
	 * Auto-generated method to display this org.eclipse.swt.widgets.Composite inside a new Shell.
	 */
	public static void showGUI() {
		final Display display = Display.getDefault();
		final Shell shell = new Shell(display);
		final OptionsComposite inst = new OptionsComposite(shell, SWT.NULL);
		final Point size = inst.getSize();
		shell.setLayout(new FillLayout());
		shell.layout();
		if ((size.x == 0) && (size.y == 0)) {
			inst.pack();
			shell.pack();
		} else {
			final Rectangle shellBounds = shell.computeTrim(0, 0, size.x, size.y);
			shell.setSize(shellBounds.width, shellBounds.height);
		}
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
	public OptionsComposite(final org.eclipse.swt.widgets.Composite parent, final int style) {
		super(parent, style);
		initGUI();
	}
	
	private void initGUI() {
		try {
			final FillLayout thisLayout = new FillLayout(org.eclipse.swt.SWT.HORIZONTAL);
			this.setLayout(thisLayout);
			final GridData gridData = new GridData();
			gridData.horizontalAlignment = GridData.FILL;
			gridData.verticalAlignment = GridData.FILL;
			gridData.grabExcessHorizontalSpace = true;
			gridData.grabExcessVerticalSpace = true;
			this.setLayoutData(gridData);
			
			this.setSize(613, 273);
			{
				group1 = new Group(this, SWT.NONE);
				final GridLayout group1Layout = new GridLayout();
				group1Layout.numColumns = 3;
				group1Layout.makeColumnsEqualWidth = false;
				group1.setLayout(group1Layout);
				group1.setText("Options");
				{
					label1 = new Label(group1, SWT.NONE);
					label1.setText("Line width: ");
				}
				{
					final GridData lineWidthLData = new GridData();
					lineWidthLData.grabExcessHorizontalSpace = true;
					lineWidthLData.horizontalAlignment = GridData.FILL;
					lineWidthLData.horizontalSpan = 2;
					lineWidthLData.heightHint = 17;
					lineWidth = new Text(group1, SWT.BORDER);
					lineWidth.setLayoutData(lineWidthLData);
				}
				{
					label2 = new Label(group1, SWT.NONE);
					label2.setText("Line color: ");
				}
				{
					final GridData colorExampleLData = new GridData();
					colorExampleLData.widthHint = 50;
					colorExampleLData.heightHint = 19;
					colorExample = new CLabel(group1, SWT.BORDER);
					colorExample.setLayoutData(colorExampleLData);
				}
				{
					lineColor = new Button(group1, SWT.PUSH | SWT.CENTER);
					lineColor.setText("Change color");
					lineColor.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(final SelectionEvent evt) {
							lineColorWidgetSelected(evt);
						}
					});
				}
				{
					showExtInf = new Button(group1, SWT.CHECK | SWT.LEFT);
					final GridData showExtInfLData = new GridData();
					showExtInfLData.horizontalSpan = 3;
					showExtInf.setLayoutData(showExtInfLData);
					showExtInf.setText("Show extended information by default");
					
				}
				{
					label3 = new Label(group1, SWT.NONE);
					label3.setText("Common string formatter");
				}
				{
					final GridData defaultStringFmtLData = new GridData();
					defaultStringFmtLData.horizontalSpan = 2;
					defaultStringFmtLData.horizontalAlignment = GridData.FILL;
					defaultStringFmtLData.verticalAlignment = GridData.BEGINNING;
					defaultStringFmtLData.grabExcessHorizontalSpace = true;
					defaultStringFmt = new Text(group1, SWT.BORDER);
					defaultStringFmt.setLayoutData(defaultStringFmtLData);
					
				}
				{
					final GridData tableLData = new GridData();
					tableLData.horizontalSpan = 3;
					tableLData.grabExcessHorizontalSpace = true;
					tableLData.horizontalAlignment = GridData.FILL;
					tableLData.verticalAlignment = GridData.FILL;
					tableLData.grabExcessVerticalSpace = true;
					tableLData.heightHint = 27;
					table = new TableViewer(group1, SWT.FULL_SELECTION);
					table.getControl().setLayoutData(tableLData);
					
				}
				{
					columnTypes = new TableViewerColumn(table, SWT.NONE);
					columnTypes.getColumn().setWidth(50);
					columnTypes.getColumn().setText("Type");
					
					// Sort by semantic tyoe
					table.getTable().setSortColumn(columnTypes.getColumn());
					table.getTable().setSortDirection(SWT.DOWN);
					table.getTable().setLinesVisible(true);
					table.getTable().setHeaderVisible(true);
					
				}
				{
					columnFormatString = new TableViewerColumn(table, SWT.NONE);
					columnFormatString.getColumn().setWidth(200);
					columnFormatString.getColumn().setText("Format string");
					
				}
				{
					addEntry = new Button(group1, SWT.PUSH | SWT.CENTER);
					final GridData addEntryLData = new GridData();
					addEntryLData.verticalAlignment = GridData.BEGINNING;
					addEntryLData.horizontalAlignment = GridData.END;
					addEntry.setLayoutData(addEntryLData);
					addEntry.setText("Add");
					addEntry.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(final SelectionEvent evt) {
							addEntryWidgetSelected(evt);
						}
					});
				}
				{
					delEntry = new Button(group1, SWT.PUSH | SWT.CENTER);
					delEntry.setText("Delete");
					delEntry.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(final SelectionEvent evt) {
							delEntryWidgetSelected(evt);
						}
					});
				}
			}
			this.layout();
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
	
	private void lineColorWidgetSelected(final SelectionEvent evt) {
		log.debug("lineColor.widgetSelected, event=" + evt);
		final ColorDialog dlg = new ColorDialog(this.getShell());
		dlg.setRGB(colorExample.getBackground().getRGB());
		final RGB color = dlg.open();
		if (color != null) {
			colorExample.setBackground(new Color(this.getDisplay(), color));
			this.page.markFormDirty();
		}
	}
	
	public void setDatabinding(final DataBindingContext dbc, final PluginXMLConfig config,
			final SimpleNodePainterPreferencePage page) {
		
		this.page = page;
		
		// line width
		
		final IObservableValue modelObservable = BeansObservables.observeValue(dbc
				.getValidationRealm(), config, "lineWidth");
		dbc.bindValue(SWTObservables.observeText(this.lineWidth, SWT.Modify), modelObservable,
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT), null);
		
		// extended inf
		
		final IObservableValue observableExtInf = BeansObservables.observeValue(dbc
				.getValidationRealm(), config, "extendedDefaultValue");
		dbc.bindValue(SWTObservables.observeSelection(this.showExtInf), observableExtInf,
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT), null);
		
		// line color
		
		final IObservableValue observableColor = BeansObservables.observeValue(dbc
				.getValidationRealm(), config, "lineColorRGB");
		dbc.bindValue(SWTObservables.observeBackground(colorExample), observableColor,
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT)
						.setConverter(new ColorToArrayConverter()), new UpdateValueStrategy()
						.setConverter(new ArrayToColorConverter(this.getDisplay())));
		
		// default string fmt
		
		final IObservableValue modelDefStrFmt = BeansObservables.observeValue(dbc
				.getValidationRealm(), config, "defaultStringFormatter");
		dbc.bindValue(SWTObservables.observeText(this.defaultStringFmt, SWT.Modify),
				modelDefStrFmt, new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT), null);
		
		// table
		
		columnFormatString.setEditingSupport(new StringFormatterEditingSupport(table, dbc));
		
		final ObservableSetContentProvider contentProvider = new ObservableSetContentProvider();
		table.setContentProvider(contentProvider);
		
		final IObservableMap typeMap = BeansObservables.observeMap(contentProvider
				.getKnownElements(), ObservableEntry.class, "key");
		final IObservableMap fmtStringMap = BeansObservables.observeMap(contentProvider
				.getKnownElements(), ObservableEntry.class, "value");
		
		final IObservableMap[] columnMaps = new IObservableMap[] { typeMap, fmtStringMap };
		table.setLabelProvider(new ObservableMapLabelProvider(columnMaps));
		
	}
	
	private void addEntryWidgetSelected(final SelectionEvent evt) {
		final InputDialog dlg = new InputDialog(this.getShell(), "Enter a semantic type",
				"Please enter a semantic type (0-255)", "", new IInputValidator() {
					
					@Override
					public String isValid(final String newText) {
						try {
							final int i = Integer.parseInt(newText);
							
							if ((i < 0) || (i > 255)) {
								return "Please enter a number between 0 and 255";
							} else {
								return null;
							}
						} catch (final NumberFormatException e) {
							return "Please enter an integer";
						}
						
					}
					
				});
		dlg.setBlockOnOpen(true);
		final int ret = dlg.open();
		if (ret == Window.OK) {
			final int type = Integer.parseInt(dlg.getValue());
			
			final ObservableEntry<Integer, String> ne = new WrappedSet.ObservableEntry<Integer, String>(
					type, "");
			tableData.add(ne);
			
		}
		
		// this is a hack and will probably not work every time.
		table.refresh();
	}
	
	public void connectTableWithData(final DataBindingContext dbc,
			final HashMap<Integer, String> tempStringFormatterTable) {
		
		// the hashmap is cloned inside the getter-method.
		// tempStringFormatterTable = config.getStringFormatters();
		
		// Wrap Hashmap into an Set, so that JFace Databinding can handle it.
		final Set<ObservableEntry<Integer, String>> entrySet = new WrappedSet<Integer, String>(
				tempStringFormatterTable);
		tableData = new WrappedObservableSet(dbc.getValidationRealm(), entrySet, null);
		table.setInput(tableData);
	}
	
	private void delEntryWidgetSelected(final SelectionEvent evt) {
		final IStructuredSelection selection = (IStructuredSelection) table.getSelection();
		for (final Object o : selection.toList()) {
			tableData.remove(o);
		}
	}
}
