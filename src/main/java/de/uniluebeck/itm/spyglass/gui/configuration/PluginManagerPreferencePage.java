/*
 * --------------------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass. Copyright (C)
 * 2004-2007 by the SwarmNet (www.swarmnet.de) project SpyGlass is free
 * software; you can redistribute it and/or modify it under the terms of the BSD
 * License. Refer to spyglass-licence.txt file in the root of the SpyGlass
 * source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.gui.configuration;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.plugin.Plugin;
import de.uniluebeck.itm.spyglass.plugin.PluginListChangeListener;
import de.uniluebeck.itm.spyglass.plugin.nodepositioner.NodePositionerPlugin;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;

// --------------------------------------------------------------------------------
/**
 * Preference page to display all currently active plug-ins. Allows to:
 * 
 * <ul>
 * <li>manage the drawing order of the plug-ins</li>
 * <li>one-click activating and deactivating of plug-ins</li>
 * <li>changing the active NodePositioner plug-in instance</li>
 * </ul>
 * 
 * @author Daniel Bimschas
 */
public class PluginManagerPreferencePage extends PreferencePage {

	// --------------------------------------------------------------------------------
	/**
	 * Used for editing support of column 'active' of the plug-in table.
	 * 
	 * @author Daniel Bimschas
	 */
	private class ActiveEditing extends EditingSupport {

		private final ComboBoxCellEditor cellEditor;

		public ActiveEditing(final TableViewer viewer) {
			super(viewer);
			cellEditor = new ComboBoxCellEditor(viewer.getTable(), new String[] { "true", "false" });
		}

		@Override
		protected boolean canEdit(final Object arg0) {
			return true;
		}

		@Override
		protected CellEditor getCellEditor(final Object arg0) {
			return cellEditor;
		}

		@Override
		protected Object getValue(final Object arg0) {
			return ((Plugin) arg0).isActive() ? 0 : 1;
		}

		@Override
		protected void setValue(final Object arg0, final Object arg1) {

			final int selected = ((Integer) arg1);
			final boolean selectedTrue = selected == 0;

			((Plugin) arg0).setActive(selectedTrue);
			pluginTableViewer.update(arg0, new String[] { COLUMN_ACTIVE });
		}
	}

	// --------------------------------------------------------------------------------
	/**
	 * Label provider for column 'active' of the plug-in table.
	 * 
	 * @author Daniel Bimschas
	 */
	private class ActiveLabelProvider extends ColumnLabelProvider {

		@Override
		public String getText(final Object element) {
			return ((Plugin) element).isActive() + "";
		}

	}

	// --------------------------------------------------------------------------------
	/**
	 * Editing support class for column 'category' of the plug-in table.
	 * 
	 * @author Daniel Bimschas
	 */
	private class CategoryEditing extends EditingSupport {

		public CategoryEditing(final TableViewer viewer) {
			super(viewer);
		}

		@Override
		protected boolean canEdit(final Object arg0) {
			return false;
		}

		@Override
		protected CellEditor getCellEditor(final Object arg0) {
			return null;
		}

		@Override
		protected Object getValue(final Object arg0) {
			return getCategory((Plugin) arg0);
		}

		@Override
		protected void setValue(final Object arg0, final Object arg1) {
			// not allowed to edit so nothing is to do
		}

	}

	// --------------------------------------------------------------------------------
	/**
	 * Label provider for the column 'category' of the plug-in table.
	 * 
	 * @author Daniel Bimschas
	 */
	private class CategoryLabelProvider extends ColumnLabelProvider {

		@Override
		public String getText(final Object element) {
			return getCategory((Plugin) element);
		}

	}

	// --------------------------------------------------------------------------------
	/**
	 * Editing support for the column 'name' of the plug-in table.
	 * 
	 * @author Daniel Bimschas
	 */
	private class NameEditing extends EditingSupport {

		// private final TextCellEditor cellEditor;

		// private Plugin plugin;

		public NameEditing(final TableViewer viewer) {
			super(viewer);
			// cellEditor = new TextCellEditor(viewer.getTable());
			// cellEditor.addPropertyChangeListener(new IPropertyChangeListener() {
			// @Override
			// public void propertyChange(final org.eclipse.jface.util.PropertyChangeEvent event) {
			// final String name = (String) cellEditor.getValue();
			// for (final Plugin p : spyglass.getPluginManager().getPlugins()) {
			// if ((p != plugin) && name.equals(p.getXMLConfig().getName())) {
			// setValid(false);
			// setErrorMessage("Plugin names must be unique!");
			// }
			// }
			// setValid(true);
			// }
			// });
		}

		@Override
		protected boolean canEdit(final Object arg0) {
			// return true;
			return false;
		}

		@Override
		protected CellEditor getCellEditor(final Object arg0) {
			// plugin = (Plugin) arg0;
			// return cellEditor;
			return null;
		}

		@Override
		protected Object getValue(final Object arg0) {
			return ((Plugin) arg0).getInstanceName();
		}

		@Override
		protected void setValue(final Object arg0, final Object arg1) {
			// ((Plugin) arg0).getXMLConfig().setName(((String) arg1));
			// pluginTableViewer.update(arg0, new String[] { COLUMN_NAME });
		}
	}

	// --------------------------------------------------------------------------------
	/**
	 * Label provider for the column 'name' of the plug-in table.
	 * 
	 * @author Daniel Bimschas
	 */
	private class NameLabelProvider extends ColumnLabelProvider {

		@Override
		public String getText(final Object element) {
			return ((Plugin) element).getInstanceName();
		}

	}

	// --------------------------------------------------------------------------------
	/**
	 * Content provider for the ComboBox which allows for selecting the active NodePositioner
	 * plug-in instance.
	 * 
	 * @author Daniel Bimschas
	 */
	private class NodePositionerComboContentProvider implements IStructuredContentProvider, PluginListChangeListener, PropertyChangeListener {

		// --------------------------------------------------------------------------------
		/**
		 * Constructor that sets up listeners to the list of plug-ins and to all active
		 * NodePositioner plug-in instances
		 */
		public NodePositionerComboContentProvider() {
			spyglass.getPluginManager().addPluginListChangeListener(this);
		}

		@Override
		public void dispose() {
			// as the constructor added listeners, we now have to remove them
			spyglass.getPluginManager().removePluginListChangeListener(this);
		}

		@Override
		public Object[] getElements(final Object arg0) {
			final List<Plugin> pluginInstances = spyglass.getPluginManager().getPluginInstances(NodePositionerPlugin.class, true);
			return pluginInstances.toArray(new Object[pluginInstances.size()]);
		}

		@Override
		public void inputChanged(final Viewer v, final Object oldInput, final Object newInput) {
			// remove as listener from old input, add the new input
			if (oldInput != null) {
				addOrRemoveListeners(false);
			}
			if (newInput != null) {
				addOrRemoveListeners(true);
			}
		}

		// --------------------------------------------------------------------------------
		/**
		 * Adds or removes as PropertyChangeListener to all NodePositioner plug-in instances.
		 * 
		 * @param add
		 *            if <code>true</code> this content provider is added, if <code>false</code> it
		 *            is removed
		 */
		private void addOrRemoveListeners(final boolean add) {
			for (final Plugin p : spyglass.getPluginManager().getPluginInstances(NodePositionerPlugin.class, true)) {
				if (add) {
					p.getXMLConfig().addPropertyChangeListener(this);
				} else {
					p.getXMLConfig().removePropertyChangeListener(this);
				}
			}
		}

		@Override
		public void pluginListChanged(final Plugin p, final ListChangeEvent what) {
			// if npComboViewer is still null it means that user hasn't yet visited this
			// preference page and therefore createContents has not yet been called
			if (npComboViewer != null) {
				npComboViewer.refresh();
			}
		}

		@Override
		public void propertyChange(final PropertyChangeEvent evt) {
			npComboViewer.refresh();
		}

	}

	// --------------------------------------------------------------------------------
	/**
	 * Label provider for the ComboBox displaying the NodePositioner plug-in instances.
	 * 
	 * @author Daniel Bimschas
	 */
	private class NodePositionerComboLabelProvider extends LabelProvider {

		@Override
		public String getText(final Object element) {
			return ((Plugin) element).getInstanceName();
		}

	}

	// --------------------------------------------------------------------------------
	/**
	 * Viewer for the ComboBox displaying the NodePositioner plug-in instances.
	 * 
	 * @author Daniel Bimschas
	 */
	private class NodePositionerComboViewer extends ComboViewer {

		public NodePositionerComboViewer(final Composite parent, final int style) {
			super(parent, style);
		}

		@Override
		public void refresh() {
			super.refresh();
			selectActiveNodePositioner();
		}

		// --------------------------------------------------------------------------------
		/**
		 * Assures that the currently activated NodePositioner is selected in the ComboBox.
		 */
		private void selectActiveNodePositioner() {
			final NodePositionerPlugin np = spyglass.getPluginManager().getNodePositioner();
			for (int i = 0; i < listGetItemCount(); i++) {
				if (getElementAt(i) == np) {
					listSetSelection(new int[] { i });
				}
			}
		}

	}

	// --------------------------------------------------------------------------------
	/**
	 * Content provider for the table that displays all plug-in instances (except of
	 * NodePositioners).
	 * 
	 * @author Daniel Bimschas
	 */
	private class PluginTableContentProvider implements IStructuredContentProvider, PluginListChangeListener, PropertyChangeListener {

		private List<Plugin> plugins;

		// --------------------------------------------------------------------------------
		/**
		 * Constructor that registers this content provider as listener to the list of plug-in
		 * instances and the properties of the displayed plug-ins.
		 */
		public PluginTableContentProvider() {
			spyglass.getPluginManager().addPluginListChangeListener(this);
		}

		@Override
		public void dispose() {
			// as we have registered in the constructor, we now have to remove ourselves
			spyglass.getPluginManager().removePluginListChangeListener(this);
		}

		@Override
		public Object[] getElements(final Object arg0) {
			plugins = getPlugins();
			return plugins.toArray(new Plugin[plugins.size()]);
		}

		/**
		 * Returns all active plug-ins in increasing priority
		 * 
		 * @return all active plug-ins in increasing priority
		 */
		@SuppressWarnings("unchecked")
		private List<Plugin> getPlugins() {
			final List<Plugin> retVal = new LinkedList<Plugin>();
			final List<Plugin> plugins = spyglass.getPluginManager().getPlugins(true, NodePositionerPlugin.class);
			for (int i = plugins.size() - 1; i >= 0; i--) {
				retVal.add(plugins.get(i));
			}
			return retVal;
		}

		@Override
		public void inputChanged(final Viewer v, final Object oldInput, final Object newInput) {
			if (oldInput != null) {
				addOrRemoveListeners(false);
			}
			if (newInput != null) {
				addOrRemoveListeners(true);
			}
		}

		// --------------------------------------------------------------------------------
		/**
		 * Adds or removes as PropertyChangeListener to all plug-in instances excluding
		 * NodePositioner plug-in instances.
		 * 
		 * @param add
		 *            if <code>true</code> this content provider is added, if <code>false</code> it
		 *            is removed
		 */
		private void addOrRemoveListeners(final boolean add) {
			for (final Plugin p : getPlugins()) {
				if (add) {
					p.getXMLConfig().addPropertyChangeListener(this);
				} else {
					p.getXMLConfig().removePropertyChangeListener(this);
				}
			}
		}

		// --------------------------------------------------------------------------------
		/**
		 * Checks if <code>selectedPlugin</code> is the first plug-in displayed in the table.
		 * 
		 * @param selectedPlugin
		 * @return <code>true</code> if the plug-in is the first in list, <code>false</code>
		 *         otherwise
		 */
		public boolean isFirstInList(final Plugin selectedPlugin) {

			if (selectedPlugin == null) {
				return false;
			}

			if (plugins.size() > 0) {
				return plugins.get(0) == selectedPlugin;
			}

			throw new RuntimeException("We should never reach this code block hopefully.");

		}

		// --------------------------------------------------------------------------------
		/**
		 * Checks if <code>selectedPlugin</code> is the last plug-in displayed in the table.
		 * 
		 * @param selectedPlugin
		 * @return <code>true</code> if the plug-in is the last in list, <code>false</code>
		 *         otherwise
		 */
		public boolean isLastInList(final Plugin selectedPlugin) {

			if (selectedPlugin == null) {
				return false;
			}

			if (plugins.size() > 0) {
				return plugins.get(plugins.size() - 1) == selectedPlugin;
			}

			throw new RuntimeException("We should never reach this code block hopefully.");

		}

		@Override
		public void pluginListChanged(final Plugin p, final ListChangeEvent what) {
			// if pluginTableViewer is still null it means that user hasn't yet visited this
			// preference page and therefore createContents has not yet been called
			if (pluginTableViewer != null) {
				pluginTableViewer.refresh();
			}
		}

		@Override
		public void propertyChange(final PropertyChangeEvent event) {
			for (final Plugin p : plugins) {
				if (p.getXMLConfig() == event.getSource()) {
					pluginTableViewer.refresh(p);
				}
			}
		}
	}

	// --------------------------------------------------------------------------------
	/**
	 * Editing support for the column 'type' of the plug-in table.
	 * 
	 * @author Daniel Bimschas
	 */
	private class TypeEditing extends EditingSupport {

		public TypeEditing(final TableViewer viewer) {
			super(viewer);
		}

		@Override
		protected boolean canEdit(final Object arg0) {
			return false;
		}

		@Override
		protected CellEditor getCellEditor(final Object arg0) {
			return null;
		}

		@Override
		protected Object getValue(final Object arg0) {
			return getType((Plugin) arg0);
		}

		@Override
		protected void setValue(final Object arg0, final Object arg1) {
			// not allowed to edit so nothing is to do
		}

	}

	// --------------------------------------------------------------------------------
	/**
	 * Label provider for the column 'type' of the plug-in table.
	 * 
	 * @author Daniel Bimschas
	 */
	private class TypeLabelProvider extends ColumnLabelProvider {

		@Override
		public String getText(final Object element) {
			return getType((Plugin) element);
		}

	}

	// --------------------------------------------------------------------------------
	/**
	 * Editing support for the column 'visible' of the plug-in table.
	 * 
	 * @author Daniel Bimschas
	 */
	private class VisibleEditing extends EditingSupport {

		private final ComboBoxCellEditor cellEditor;

		public VisibleEditing(final TableViewer viewer) {
			super(viewer);
			cellEditor = new ComboBoxCellEditor(viewer.getTable(), new String[] { "true", "false" });
		}

		@Override
		protected boolean canEdit(final Object arg0) {
			return true;
		}

		@Override
		protected CellEditor getCellEditor(final Object arg0) {
			return cellEditor;
		}

		@Override
		protected Object getValue(final Object arg0) {
			return ((Plugin) arg0).isVisible() ? 0 : 1;
		}

		@Override
		protected void setValue(final Object arg0, final Object arg1) {
			final int selected = ((Integer) arg1);
			((Plugin) arg0).setVisible(selected == 0 ? true : false);
			pluginTableViewer.update(arg0, new String[] { COLUMN_VISIBLE });
		}

	}

	// --------------------------------------------------------------------------------
	/**
	 * Label provider for the column 'visible' of the plug-in table.
	 * 
	 * @author Daniel Bimschas
	 */
	private class VisibleLabelProvider extends ColumnLabelProvider {

		@Override
		public String getText(final Object element) {
			return ((Plugin) element).isVisible() + "";
		}

	}

	private static final String COLUMN_ACTIVE = "Active";

	private static final String COLUMN_CATEGORY = "Category";

	private static final String COLUMN_NAME = "Name";

	private static final String COLUMN_TYPE = "Type";

	private static final String COLUMN_VISIBLE = "Visible";

	private static final Logger log = SpyglassLoggerFactory.getLogger(PluginManagerPreferencePage.class);

	private Button buttonDown;

	private Button buttonDeleteInstance;

	private Button buttonResetInstance;

	private final SelectionListener buttonSelectionListener = new SelectionAdapter() {

		@Override
		public void widgetSelected(final SelectionEvent e) {
			if (e.getSource() == buttonUp) {
				clickedButtonUp();
			} else if (e.getSource() == buttonDown) {
				clickedButtonDown();
			} else if (e.getSource() == buttonResetInstance) {
				clickedButtonReset();
			} else if (e.getSource() == buttonDeleteInstance) {
				clickedButtonRemoveInstance();
			} else if (e.getSource() == buttonDeActivate) {
				clickedButtonDeActivate();
			} else if (e.getSource() == buttonInVisible) {
				clickedButtonInVisible();
			}
		}

	};

	private Button buttonUp;

	private final NodePositionerComboContentProvider npComboContentProvider;

	private final NodePositionerComboLabelProvider npComboLabelProvider;

	private NodePositionerComboViewer npComboViewer;

	private final PluginTableContentProvider pluginTableContentProvider;

	private TableViewer pluginTableViewer;

	private final Spyglass spyglass;

	private PluginPreferenceDialog pluginPreferenceDialog;

	private PluginListChangeListener pluginListChangeListener = new PluginListChangeListener() {
		@Override
		public void pluginListChanged(final Plugin p, final ListChangeEvent what) {
			pluginTableViewer.refresh();
		}
	};

	public PluginManagerPreferencePage(final Spyglass spyglass, final PluginPreferenceDialog pluginPreferenceDialog) {

		this.spyglass = spyglass;
		this.pluginPreferenceDialog = pluginPreferenceDialog;

		npComboContentProvider = new NodePositionerComboContentProvider();
		npComboLabelProvider = new NodePositionerComboLabelProvider();
		pluginTableContentProvider = new PluginTableContentProvider();

		noDefaultAndApplyButton();

	}

	private void addNodePositionerSelectionGroup(final Composite composite) {

		final GridLayout npSelLayout = new GridLayout(2, false);

		final GridData npSelData = new GridData();
		npSelData.horizontalAlignment = GridData.FILL;
		npSelData.grabExcessHorizontalSpace = true;

		final Group npSelGroup = new Group(composite, SWT.SHADOW_ETCHED_IN);
		npSelGroup.setText("Node Positioner Selection");
		npSelGroup.setLayout(npSelLayout);
		npSelGroup.setLayoutData(npSelData);

		final Label npSelLabel = new Label(npSelGroup, SWT.NONE);
		npSelLabel.setText("Active Node Positioner");

		npComboViewer = new NodePositionerComboViewer(npSelGroup, SWT.SIMPLE | SWT.DROP_DOWN | SWT.READ_ONLY);
		npComboViewer.setContentProvider(npComboContentProvider);
		npComboViewer.setLabelProvider(npComboLabelProvider);
		npComboViewer.setInput(spyglass);
		npComboViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(final SelectionChangedEvent e) {

				final IStructuredSelection selection = (IStructuredSelection) e.getSelection();
				final NodePositionerPlugin selectedPlugin = (NodePositionerPlugin) selection.getFirstElement();

				selectedPlugin.getXMLConfig().setActive(true);
			}

		});
		npComboViewer.selectActiveNodePositioner();

	}

	private IDoubleClickListener pluginTableDoubleClickListener = new IDoubleClickListener() {
		@Override
		public void doubleClick(final DoubleClickEvent event) {
			final Plugin element = (Plugin) ((IStructuredSelection) event.getSelection()).getFirstElement();
			pluginPreferenceDialog.selectPreferencePage(element);
		}
	};

	private Button buttonDeActivate;

	private Button buttonInVisible;

	private void addPluginManagerList(final Composite parent) {

		final GridLayout pluginsGroupLayout = new GridLayout(2, false);

		final GridData pluginsGroupData = new GridData();
		pluginsGroupData.grabExcessHorizontalSpace = true;
		pluginsGroupData.grabExcessVerticalSpace = true;
		pluginsGroupData.horizontalAlignment = GridData.FILL;
		pluginsGroupData.verticalAlignment = GridData.FILL;

		final Group pluginsGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
		pluginsGroup.setText("Plugins");
		pluginsGroup.setLayout(pluginsGroupLayout);
		pluginsGroup.setLayoutData(pluginsGroupData);

		final GridData pluginTableData = new GridData();
		pluginTableData.grabExcessHorizontalSpace = true;
		pluginTableData.grabExcessVerticalSpace = true;
		pluginTableData.horizontalAlignment = GridData.FILL;
		pluginTableData.verticalAlignment = GridData.FILL;
		pluginTableData.heightHint = 100;
		pluginTableData.widthHint = 500;

		createPluginTableViewer(pluginsGroup);

		pluginTableViewer.getTable().setLayoutData(pluginTableData);
		pluginTableViewer.addDoubleClickListener(pluginTableDoubleClickListener);

		final GridLayout buttonCompositeLayout = new GridLayout();
		buttonCompositeLayout.numColumns = 1;

		final GridData buttonCompositeData = new GridData();
		buttonCompositeData.verticalAlignment = SWT.TOP;

		final Composite buttonComposite = new Composite(pluginsGroup, SWT.NONE);
		buttonComposite.setLayout(buttonCompositeLayout);
		buttonComposite.setLayoutData(buttonCompositeData);

		final GridData buttonUpData = new GridData();
		buttonUpData.widthHint = 130;

		final GridData buttonDownData = new GridData();
		buttonDownData.widthHint = 130;

		final GridData buttonResetInstanceData = new GridData();
		buttonResetInstanceData.widthHint = 130;

		final GridData buttonRemoveInstanceData = new GridData();
		buttonRemoveInstanceData.widthHint = 130;

		final GridData buttonDeActivateInstanceData = new GridData();
		buttonDeActivateInstanceData.widthHint = 130;

		final GridData buttonInVisibleInstanceData = new GridData();
		buttonInVisibleInstanceData.widthHint = 130;

		buttonUp = new Button(buttonComposite, SWT.PUSH);
		buttonUp.setText("Up");
		buttonUp.setLayoutData(buttonUpData);
		buttonUp.setEnabled(false);
		buttonUp.addSelectionListener(buttonSelectionListener);

		buttonDown = new Button(buttonComposite, SWT.PUSH);
		buttonDown.setText("Down");
		buttonDown.setLayoutData(buttonDownData);
		buttonDown.setEnabled(false);
		buttonDown.addSelectionListener(buttonSelectionListener);

		buttonResetInstance = new Button(buttonComposite, SWT.PUSH);
		buttonResetInstance.setText("Reset Instance");
		buttonResetInstance.setLayoutData(buttonResetInstanceData);
		buttonResetInstance.setEnabled(false);
		buttonResetInstance.addSelectionListener(buttonSelectionListener);

		buttonDeleteInstance = new Button(buttonComposite, SWT.PUSH);
		buttonDeleteInstance.setText("Delete Instance");
		buttonDeleteInstance.setLayoutData(buttonRemoveInstanceData);
		buttonDeleteInstance.setEnabled(false);
		buttonDeleteInstance.addSelectionListener(buttonSelectionListener);

		buttonDeActivate = new Button(buttonComposite, SWT.PUSH);
		buttonDeActivate.setText("Make Inactive");
		buttonDeActivate.setLayoutData(buttonDeActivateInstanceData);
		buttonDeActivate.setEnabled(false);
		buttonDeActivate.addSelectionListener(buttonSelectionListener);

		buttonInVisible = new Button(buttonComposite, SWT.PUSH);
		buttonInVisible.setText("Make Invisible");
		buttonInVisible.setLayoutData(buttonInVisibleInstanceData);
		buttonInVisible.setEnabled(false);
		buttonInVisible.addSelectionListener(buttonSelectionListener);

	}

	private void clickedButtonReset() {
		final List<Plugin> list = getSelectedPlugins((IStructuredSelection) pluginTableViewer.getSelection());
		for (final Plugin p : list) {
			p.reset();
		}
	}

	private void clickedButtonInVisible() {
		final IStructuredSelection selection = (IStructuredSelection) pluginTableViewer.getSelection();
		final boolean visible = !getFirstSelectedPlugin(selection).isVisible();
		final List<Plugin> list = getSelectedPlugins(selection);
		for (final Plugin p : list) {
			p.setVisible(visible);
		}
		pluginTableViewer.refresh();
		updateButtons((IStructuredSelection) pluginTableViewer.getSelection());
	}

	private void clickedButtonDeActivate() {
		final IStructuredSelection selection = (IStructuredSelection) pluginTableViewer.getSelection();
		final boolean active = !getFirstSelectedPlugin(selection).isActive();
		final List<Plugin> list = getSelectedPlugins(selection);
		for (final Plugin p : list) {
			p.setActive(active);
		}
		pluginTableViewer.refresh();
		updateButtons((IStructuredSelection) pluginTableViewer.getSelection());
	}

	private void clickedButtonRemoveInstance() {
		final List<Plugin> list = getSelectedPlugins((IStructuredSelection) pluginTableViewer.getSelection());
		final boolean confirm = MessageDialog.openConfirm(getShell(), "Confirm instance removal", "Are you sure you want to remove " + list.size()
				+ " plugin instance(s)?");
		if (confirm) {
			for (final Plugin p : list) {
				spyglass.getPluginManager().removePlugin(p);
			}
		}
	}

	private void clickedButtonDown() {
		final List<Plugin> list = getSelectedPlugins((IStructuredSelection) pluginTableViewer.getSelection());
		spyglass.getPluginManager().decreasePluginPriorities(list);
		pluginTableViewer.refresh();
		updateButtons((IStructuredSelection) pluginTableViewer.getSelection());
	}

	private void clickedButtonUp() {
		final List<Plugin> list = getSelectedPlugins((IStructuredSelection) pluginTableViewer.getSelection());
		spyglass.getPluginManager().increasePluginPriorities(list);
		pluginTableViewer.refresh();
		updateButtons((IStructuredSelection) pluginTableViewer.getSelection());
	}

	@Override
	protected Control createContents(final Composite parent) {

		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, true));
		composite.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, true));

		addNodePositionerSelectionGroup(composite);
		addPluginManagerList(composite);

		return composite;
	}

	private void createPluginTableViewer(final Composite parent) {

		final int style = SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION;

		pluginTableViewer = new TableViewer(parent, style);

		TableViewerColumn column;

		column = new TableViewerColumn(pluginTableViewer, SWT.NONE);
		column.getColumn().setWidth(130);
		column.getColumn().setText(COLUMN_CATEGORY);
		column.setLabelProvider(new CategoryLabelProvider());
		column.setEditingSupport(new CategoryEditing(pluginTableViewer));

		column = new TableViewerColumn(pluginTableViewer, SWT.NONE);
		column.getColumn().setWidth(130);
		column.getColumn().setText(COLUMN_TYPE);
		column.setLabelProvider(new TypeLabelProvider());
		column.setEditingSupport(new TypeEditing(pluginTableViewer));

		column = new TableViewerColumn(pluginTableViewer, SWT.NONE);
		column.getColumn().setWidth(140);
		column.getColumn().setText(COLUMN_NAME);
		column.setLabelProvider(new NameLabelProvider());
		column.setEditingSupport(new NameEditing(pluginTableViewer));

		column = new TableViewerColumn(pluginTableViewer, SWT.NONE);
		column.getColumn().setWidth(50);
		column.getColumn().setText(COLUMN_ACTIVE);
		column.getColumn().setResizable(false);
		column.setLabelProvider(new ActiveLabelProvider());
		column.setEditingSupport(new ActiveEditing(pluginTableViewer));

		column = new TableViewerColumn(pluginTableViewer, SWT.NONE);
		column.getColumn().setWidth(50);
		column.getColumn().setText(COLUMN_VISIBLE);
		column.getColumn().setResizable(false);
		column.setLabelProvider(new VisibleLabelProvider());
		column.setEditingSupport(new VisibleEditing(pluginTableViewer));

		pluginTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(final SelectionChangedEvent e) {
				final IStructuredSelection selection = (IStructuredSelection) e.getSelection();
				updateButtons(selection);
			}

		});

		pluginTableViewer.getTable().setLinesVisible(true);
		pluginTableViewer.getTable().setHeaderVisible(true);

		pluginTableViewer.setContentProvider(pluginTableContentProvider);
		pluginTableViewer.setInput(spyglass);

	}

	@SuppressWarnings("unchecked")
	private List<Plugin> getSelectedPlugins(final IStructuredSelection selection) {
		final List<Plugin> list = new ArrayList<Plugin>();
		for (final Iterator it = selection.iterator(); it.hasNext();) {
			list.add((Plugin) it.next());
		}
		return list;
	}

	@Override
	public void dispose() {

		spyglass.getPluginManager().removePluginListChangeListener(pluginListChangeListener);

		pluginTableViewer.getTable().dispose();
		pluginTableViewer = null;

		buttonUp.dispose();
		buttonUp = null;

		buttonDown.dispose();
		buttonDown = null;

		buttonResetInstance.dispose();
		buttonResetInstance = null;

		buttonDeleteInstance.dispose();
		buttonDeleteInstance = null;

	}

	@SuppressWarnings("unchecked")
	private String getCategory(final Plugin p) {
		Class<? extends Plugin> clazz = p.getClass();
		while (!clazz.getSuperclass().equals(Plugin.class)) {
			clazz = (Class<? extends Plugin>) clazz.getSuperclass();
		}
		return clazz.getSimpleName();
	}

	private String getType(final Plugin p) {
		try {
			return (String) p.getClass().getMethod("getHumanReadableName").invoke(p);
		} catch (final Exception e) {
			log.error("", e);
			return "__ERROR__";
		}
	}

	private void updateButtons(final IStructuredSelection selection) {

		final Plugin first = getFirstSelectedPlugin(selection);
		final Plugin last = getLastSelectedPlugin(selection);
		final boolean enableUp = !pluginTableContentProvider.isFirstInList(first);
		final boolean enableDown = !pluginTableContentProvider.isLastInList(last);
		final boolean notEmpty = !pluginTableViewer.getSelection().isEmpty();

		buttonUp.setEnabled(enableUp);
		buttonDown.setEnabled(enableDown);
		buttonResetInstance.setEnabled(notEmpty);
		buttonDeleteInstance.setEnabled(notEmpty);
		buttonDeActivate.setEnabled(notEmpty);
		buttonInVisible.setEnabled(notEmpty);

		if (notEmpty) {
			buttonDeActivate.setText(first.isActive() ? "Make Inactive" : "Make Active");
			buttonInVisible.setText(first.isVisible() ? "Make Invisible" : "Make Visible");
		}

	}

	private Plugin getFirstSelectedPlugin(final IStructuredSelection selection) {
		if (selection.isEmpty()) {
			return null;
		}
		return (Plugin) selection.getFirstElement();
	}

	@SuppressWarnings("unchecked")
	private Plugin getLastSelectedPlugin(final IStructuredSelection selection) {
		Object o = null;
		final Iterator it = selection.iterator();
		while (it.hasNext()) {
			o = it.next();
		}
		return (Plugin) o;
	}

}
