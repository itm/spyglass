/*
 * ----------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the
 * SwarmNet (www.swarmnet.de) project SpyGlass is free software;
 * you can redistribute it and/or modify it under the terms of the BSD License.
 * Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for further details.
 * ------------------------------------------------------------------------
 */

package de.uniluebeck.itm.spyglass.plugin.simpleglobalinformation;

import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.observable.set.ISetChangeListener;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.jface.databinding.viewers.ObservableSetContentProvider;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IElementComparer;
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

import de.uniluebeck.itm.spyglass.gui.databinding.ComboBoxEditingSupport;
import de.uniluebeck.itm.spyglass.gui.databinding.StringFormatterEditingSupport;
import de.uniluebeck.itm.spyglass.gui.databinding.WrappedObservableSet;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;

// --------------------------------------------------------------------------------
/**
 * Instances of this class create widgets providing data bound tables to create, update and delete
 * {@link StatisticalInformationEvaluator}
 * 
 * @author Sebastian Ebers
 * 
 */
public class SGIStringFormatter {

	private static final Logger log = SpyglassLoggerFactory.getLogger(SGIStringFormatter.class);

	private Button delEntry;

	private Button addEntry;

	private TableViewerColumn columnTypes, columnDescription, columnExpressionString, columnStatisticType;

	/**
	 * Reference to the set backing the table. All edits have to go through this set, so that
	 * changeListeners are being noticed.
	 */
	private IObservableSet tableData;

	private TableViewer table;

	private Composite parent;

	private Composite buttonComposite;

	// --------------------------------------------------------------------------------
	/**
	 * Adds fields to configure the {@link StatisticalInformationEvaluator}
	 * 
	 * @param parent
	 *            the parent widget
	 * @param gridHorizontalSpan
	 *            the horizontal span of the underlying grid of the widget
	 */
	public void addStringFormatterFields(final Composite parent, final int gridHorizontalSpan) {

		this.parent = parent;

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
			columnDescription = new TableViewerColumn(table, SWT.NONE);
			columnDescription.getColumn().setWidth(200);
			columnDescription.getColumn().setText("Description");
		}
		{
			columnExpressionString = new TableViewerColumn(table, SWT.NONE);
			columnExpressionString.getColumn().setWidth(100);
			columnExpressionString.getColumn().setText("Expression");

		}
		{
			columnStatisticType = new TableViewerColumn(table, SWT.NONE);
			columnStatisticType.getColumn().setWidth(100);
			columnStatisticType.getColumn().setText("Operation");
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
				@SuppressWarnings("synthetic-access")
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
				@SuppressWarnings("synthetic-access")
				@Override
				public void widgetSelected(final SelectionEvent evt) {
					delEntryWidgetSelected(evt);
				}
			});
		}
	}

	// --------------------------------------------------------------------------------
	/**
	 * Activates data binding
	 * 
	 * @param dbc
	 *            the {@link DataBindingContext}
	 */
	public void setDataBinding(final DataBindingContext dbc) {

		// table

		columnExpressionString.setEditingSupport(new StringFormatterEditingSupport(table, dbc, "expression"));
		columnDescription.setEditingSupport(new StringFormatterEditingSupport(table, dbc, "description"));

		columnStatisticType.setEditingSupport(new ComboBoxEditingSupport(table, dbc, "operation",
				new String[] { "SUM", "MIN", "MAX", "AVG", "MEDIAN" }));

		final ObservableSetContentProvider contentProvider = new ObservableSetContentProvider();
		table.setContentProvider(contentProvider);

		final IObservableMap typeMap = BeansObservables.observeMap(contentProvider.getKnownElements(), StatisticalInformationEvaluator.class,
				"semanticType");
		final IObservableMap fmtStringMap = BeansObservables.observeMap(contentProvider.getKnownElements(), StatisticalInformationEvaluator.class,
				"expression");

		final IObservableMap descriptionMap = BeansObservables.observeMap(contentProvider.getKnownElements(), StatisticalInformationEvaluator.class,
				"description");

		final IObservableMap operationMap = BeansObservables.observeMap(contentProvider.getKnownElements(), StatisticalInformationEvaluator.class,
				"operation");

		final IObservableMap[] columnMaps = new IObservableMap[] { typeMap, descriptionMap, fmtStringMap, operationMap };
		table.setLabelProvider(new ObservableMapLabelProvider(columnMaps));

	}

	// --------------------------------------------------------------------------------
	/**
	 * Adds a statistical information evaluator to the table
	 * 
	 * @param evt
	 *            a selection event
	 */
	private void addEntryWidgetSelected(@SuppressWarnings("unused") final SelectionEvent evt) {
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

			final StatisticalInformationEvaluator r = new StatisticalInformationEvaluator(type);

			if (tableData.contains(r)) {
				final String message = "A configuration with the same semantic type and description already exists. The new configuration will"
						+ " not be inserted";
				log.debug(message);
				MessageDialog.openError(null, "Duplicate element", message);
			} else {
				tableData.add(r);
				// this is a hack and will probably not work every time.
				table.refresh();
			}

		}

	}

	// --------------------------------------------------------------------------------
	/**
	 * Deletes the statistical information evaluator which was selected in the table
	 * 
	 * @param evt
	 *            a selection event (which is not used at all, since the table provides all
	 *            information itself)
	 */
	private void delEntryWidgetSelected(@SuppressWarnings("unused") final SelectionEvent evt) {
		final IStructuredSelection selection = (IStructuredSelection) table.getSelection();
		for (final Object o : selection.toList()) {
			tableData.remove(o);
		}
		table.refresh();
	}

	// --------------------------------------------------------------------------------
	/**
	 * Creates a connection between the view's table and data from the model
	 * 
	 * @param dbc
	 *            the data binding context
	 * @param statisticalInformationEvaluators
	 *            a set containing the statistical information evaluators
	 * @param setChangeListener
	 *            a listener which listen to changes of the set of statistical information
	 *            evaluators
	 */
	public void connectTableWithData(final DataBindingContext dbc, final Set<StatisticalInformationEvaluator> statisticalInformationEvaluators,
			final ISetChangeListener setChangeListener) {
		final IElementComparer comp = new IElementComparer() {
			// --------------------------------------------------------------------------------
			@Override
			public boolean equals(final Object a, final Object b) {
				StatisticalInformationEvaluator e1 = null;
				StatisticalInformationEvaluator e2 = null;
				if (a instanceof StatisticalInformationEvaluator) {
					e1 = (StatisticalInformationEvaluator) a;
				}
				if (b instanceof StatisticalInformationEvaluator) {
					e2 = (StatisticalInformationEvaluator) b;
				}
				if ((e1 != null) && (e2 != null)) {
					return ((e1.getSemanticType() == e2.getSemanticType()) && e1.getDescription().equals(e2.getDescription()));
				}
				if ((e1 != null) && (e2 != null)) {
					// return ((e1.getSemanticType() == e2.getSemanticType()) &&
					// e1.getDescription().equals(e2.getDescription()));
					return ((e1.getSemanticType() == e2.getSemanticType()));
				}
				return e1 == e2;
			}

			@Override
			public int hashCode(final Object element) {
				return element.hashCode();
			}
		};
		tableData = new WrappedObservableSet(dbc.getValidationRealm(), statisticalInformationEvaluators, comp);
		table.setInput(tableData);
		tableData.addSetChangeListener(setChangeListener);
	}
}
