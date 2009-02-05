package de.uniluebeck.itm.spyglass.gui.databinding;

import java.util.HashMap;
import java.util.Set;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.jface.databinding.viewers.ObservableSetContentProvider;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.uniluebeck.itm.spyglass.gui.databinding.WrappedSet.ObservableEntry;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginWithStringFormatterXMLConfig;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

public class StringFormatter {

	private Button delEntry;

	private Button addEntry;

	private TableViewerColumn columnTypes;

	private TableViewerColumn columnFormatString;

	/**
	 * Reference to the set backing the table. All edits have to go through this set, so that
	 * changeListeners are being noticed.
	 */
	private IObservableSet tableData;

	private TableViewer table;

	private Text defaultStringFmt;

	private Composite parent;

	private Composite buttonComposite;

	private Label label3;

	public void addStringFormatterFields(final Composite parent, final int gridHorizontalSpan) {

		this.parent = parent;

		{
			label3 = new Label(parent, SWT.NONE);
			label3.setText("Common string formatter");
		}
		{
			final GridData defaultStringFmtLData = new GridData();
			defaultStringFmtLData.horizontalSpan = gridHorizontalSpan - 1;
			defaultStringFmtLData.horizontalAlignment = GridData.FILL;
			defaultStringFmtLData.verticalAlignment = GridData.BEGINNING;
			defaultStringFmtLData.grabExcessHorizontalSpace = true;
			defaultStringFmt = new Text(parent, SWT.BORDER);
			defaultStringFmt.setLayoutData(defaultStringFmtLData);

		}
		{
			final GridData tableLData = new GridData();
			tableLData.horizontalSpan = gridHorizontalSpan;
			tableLData.grabExcessHorizontalSpace = true;
			tableLData.horizontalAlignment = GridData.FILL;
			tableLData.verticalAlignment = GridData.FILL;
			tableLData.grabExcessVerticalSpace = true;
			tableLData.heightHint = 27;
			table = new TableViewer(parent, SWT.FULL_SELECTION);
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
			buttonComposite = new Composite(parent, SWT.NONE);
			buttonComposite.setLayout(new GridLayout(2, false));
			final GridData data = new GridData();
			data.horizontalSpan = gridHorizontalSpan;
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
					addEntryWidgetSelected(evt);
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
					delEntryWidgetSelected(evt);
				}
			});
		}
	}

	public void setDataBinding(final DataBindingContext dbc, final PluginXMLConfig config) {

		// default string fmt

		final IObservableValue modelDefStrFmt = BeansObservables.observeValue(dbc.getValidationRealm(), config,
				PluginWithStringFormatterXMLConfig.PROPERTYNAME_DEFAULT_STRING_FORMATTER);
		dbc.bindValue(SWTObservables.observeText(this.defaultStringFmt, SWT.Modify), modelDefStrFmt, new UpdateValueStrategy(
				UpdateValueStrategy.POLICY_CONVERT), null);

		// table

		columnFormatString.setEditingSupport(new StringFormatterEditingSupport(table, dbc));

		final ObservableSetContentProvider contentProvider = new ObservableSetContentProvider();
		table.setContentProvider(contentProvider);

		final IObservableMap typeMap = BeansObservables.observeMap(contentProvider.getKnownElements(), ObservableEntry.class, "key");
		final IObservableMap fmtStringMap = BeansObservables.observeMap(contentProvider.getKnownElements(), ObservableEntry.class, "value");

		final IObservableMap[] columnMaps = new IObservableMap[] { typeMap, fmtStringMap };
		table.setLabelProvider(new ObservableMapLabelProvider(columnMaps));

	}

	private void addEntryWidgetSelected(final SelectionEvent evt) {
		final InputDialog dlg = new InputDialog(parent.getShell(), "Enter a semantic type", "Please enter a semantic type (-1 - 255)", "",
				new IInputValidator() {

					@Override
					public String isValid(final String newText) {
						try {
							final int i = Integer.parseInt(newText);

							if ((i < -1) || (i > 255)) {
								return "Please enter a number between -1 and 255";
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
			final int type = Integer.parseInt(dlg.getValue());

			final ObservableEntry<Integer, String> ne = new WrappedSet.ObservableEntry<Integer, String>(type, "");
			tableData.add(ne);

		}

		// this is a hack and will probably not work every time.
		table.refresh();
	}

	private void delEntryWidgetSelected(final SelectionEvent evt) {
		final IStructuredSelection selection = (IStructuredSelection) table.getSelection();
		for (final Object o : selection.toList()) {
			tableData.remove(o);
		}
	}

	public void connectTableWithData(final DataBindingContext dbc, final HashMap<Integer, String> tempStringFormatterTable) {

		// the hashmap is cloned inside the getter-method.
		// tempStringFormatterTable = config.getStringFormatters();

		// Wrap Hashmap into an Set, so that JFace Databinding can handle it.
		final Set<ObservableEntry<Integer, String>> entrySet = new WrappedSet<Integer, String>(tempStringFormatterTable);
		tableData = new WrappedObservableSet(dbc.getValidationRealm(), entrySet, null);
		table.setInput(tableData);
	}

}
