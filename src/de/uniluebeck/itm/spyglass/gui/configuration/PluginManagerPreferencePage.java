package de.uniluebeck.itm.spyglass.gui.configuration;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
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
import de.uniluebeck.itm.spyglass.util.SpyglassLogger;

public class PluginManagerPreferencePage extends PreferencePage {
	
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
			((Plugin) arg0).setActive(selected == 0 ? true : false);
			pluginTableViewer.update(arg0, new String[] { COLUMN_ACTIVE });
		}
		
	}
	
	private class ActiveLabelProvider extends ColumnLabelProvider {
		
		@Override
		public String getText(final Object element) {
			return ((Plugin) element).isActive() + "";
		}
		
	}
	
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
	
	private class CategoryLabelProvider extends ColumnLabelProvider {
		
		@Override
		public String getText(final Object element) {
			return getCategory((Plugin) element);
		}
		
	}
	
	private class NameEditing extends EditingSupport {
		
		// private final TextCellEditor cellEditor;
		
		public NameEditing(final TableViewer viewer) {
			super(viewer);
			// cellEditor = new TextCellEditor(viewer.getTable());
		}
		
		@Override
		protected boolean canEdit(final Object arg0) {
			return true;
		}
		
		@Override
		protected CellEditor getCellEditor(final Object arg0) {
			// return cellEditor;
			return null;
		}
		
		@Override
		protected Object getValue(final Object arg0) {
			return ((Plugin) arg0).getInstanceName();
		}
		
		@Override
		protected void setValue(final Object arg0, final Object arg1) {
			// TODO unique name validation (!!!)
			// ((Plugin) arg0).getXMLConfig().setName((String) arg1);
			// pluginTableViewer.update(arg0, new String[] { COLUMN_NAME });
		}
	}
	
	private class NameLabelProvider extends ColumnLabelProvider {
		
		@Override
		public String getText(final Object element) {
			return ((Plugin) element).getInstanceName();
		}
		
	}
	
	private class NodePositionerComboContentProvider implements IStructuredContentProvider,
			PluginListChangeListener, PropertyChangeListener {
		
		@Override
		public void dispose() {
			spyglass.getPluginManager().removePluginListChangeListener(this);
		}
		
		@Override
		public Object[] getElements(final Object arg0) {
			final List<Plugin> pluginInstances = spyglass.getPluginManager().getPluginInstances(
					NodePositionerPlugin.class, true);
			return pluginInstances.toArray(new Object[pluginInstances.size()]);
		}
		
		@Override
		public void inputChanged(final Viewer v, final Object oldInput, final Object newInput) {
			if (newInput != null) {
				for (final Plugin p : spyglass.getPluginManager().getPluginInstances(
						NodePositionerPlugin.class, true)) {
					p.getXMLConfig().addPropertyChangeListener(this);
				}
				spyglass.getPluginManager().addPluginListChangeListener(this);
			}
			if (oldInput != null) {
				for (final Plugin p : spyglass.getPluginManager().getPluginInstances(
						NodePositionerPlugin.class, true)) {
					p.getXMLConfig().removePropertyChangeListener(this);
				}
				spyglass.getPluginManager().removePluginListChangeListener(this);
			}
		}
		
		@Override
		public void pluginListChanged(final Plugin p, final ListChangeEvent what) {
			npComboViewer.refresh();
		}
		
		@Override
		public void propertyChange(final PropertyChangeEvent evt) {
			npComboViewer.refresh();
		}
		
	}
	
	private class NodePositionerComboLabelProvider extends LabelProvider {
		
		@Override
		public String getText(final Object element) {
			return ((Plugin) element).getInstanceName();
		}
		
	}
	
	private class NodePositionerComboViewer extends ComboViewer {
		
		public NodePositionerComboViewer(final Composite parent, final int style) {
			super(parent, style);
		}
		
		@Override
		public void refresh() {
			super.refresh();
			selectNodePositioner();
		}
		
		private void selectNodePositioner() {
			final NodePositionerPlugin np = spyglass.getPluginManager().getNodePositioner();
			for (int i = 0; i < listGetItemCount(); i++) {
				if (getElementAt(i) == np) {
					listSetSelection(new int[] { i });
				}
			}
		}
		
	}
	
	private class PluginTableContentProvider implements IStructuredContentProvider,
			PluginListChangeListener, PropertyChangeListener {
		
		private List<Plugin> plugins;
		
		@Override
		public void dispose() {
			spyglass.getPluginManager().removePluginListChangeListener(this);
		}
		
		@Override
		public Object[] getElements(final Object arg0) {
			plugins = getPlugins();
			return plugins.toArray(new Plugin[plugins.size()]);
		}
		
		@SuppressWarnings("unchecked")
		private List<Plugin> getPlugins() {
			return spyglass.getPluginManager().getPlugins(true, NodePositionerPlugin.class);
		}
		
		@Override
		public void inputChanged(final Viewer v, final Object oldInput, final Object newInput) {
			if (newInput != null) {
				for (final Plugin p : getPlugins()) {
					p.getXMLConfig().addPropertyChangeListener(this);
				}
				spyglass.getPluginManager().addPluginListChangeListener(this);
			}
			if (oldInput != null) {
				for (final Plugin p : plugins) {
					p.getXMLConfig().removePropertyChangeListener(this);
				}
				spyglass.getPluginManager().removePluginListChangeListener(this);
			}
		}
		
		public boolean isFirstInList(final Plugin selectedPlugin) {
			
			if (selectedPlugin == null) {
				return false;
			}
			
			if (plugins.size() > 0) {
				return plugins.get(0) == selectedPlugin;
			}
			
			throw new RuntimeException("We should never reach this code block hopefully.");
			
		}
		
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
			pluginTableViewer.refresh();
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
	
	private class TypeLabelProvider extends ColumnLabelProvider {
		
		@Override
		public String getText(final Object element) {
			return getType((Plugin) element);
		}
		
	}
	
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
	
	private static final Logger log = SpyglassLogger.get(PluginManagerPreferencePage.class);
	
	private Button buttonDown;
	
	private Button buttonDeleteInstance;
	
	private final SelectionListener buttonSelectionListener = new SelectionAdapter() {
		
		@Override
		public void widgetSelected(final SelectionEvent e) {
			if (e.getSource() == buttonUp) {
				clickedButtonUp();
			} else if (e.getSource() == buttonDown) {
				clickedButtonDown();
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
	
	private final NodePositionerComboContentProvider npComboContentProvider = new NodePositionerComboContentProvider();
	
	private final NodePositionerComboLabelProvider npComboLabelProvider = new NodePositionerComboLabelProvider();
	
	private NodePositionerComboViewer npComboViewer;
	
	private final PluginTableContentProvider pluginTableContentProvider = new PluginTableContentProvider();
	
	private TableViewer pluginTableViewer;
	
	private final Spyglass spyglass;
	
	private PluginPreferenceDialog pluginPreferenceDialog;
	
	public PluginManagerPreferencePage(final Spyglass spyglass,
			final PluginPreferenceDialog pluginPreferenceDialog) {
		this.spyglass = spyglass;
		this.pluginPreferenceDialog = pluginPreferenceDialog;
		
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
		
		npComboViewer = new NodePositionerComboViewer(npSelGroup, SWT.SIMPLE | SWT.DROP_DOWN
				| SWT.READ_ONLY);
		npComboViewer.setContentProvider(npComboContentProvider);
		npComboViewer.setLabelProvider(npComboLabelProvider);
		npComboViewer.setInput(spyglass);
		npComboViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(final SelectionChangedEvent e) {
				final IStructuredSelection selection = (IStructuredSelection) e.getSelection();
				final NodePositionerPlugin selectedPlugin = (NodePositionerPlugin) selection
						.getFirstElement();
				// TODO warning if switching to node positioner without metric system (!!!)
				selectedPlugin.getXMLConfig().setActive(true);
			}
			
		});
		npComboViewer.selectNodePositioner();
		
	}
	
	private IDoubleClickListener pluginTableDoubleClickListener = new IDoubleClickListener() {
		@Override
		public void doubleClick(final DoubleClickEvent event) {
			final Plugin element = (Plugin) ((IStructuredSelection) event.getSelection())
					.getFirstElement();
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
	
	private void clickedButtonInVisible() {
		final IStructuredSelection selection = (IStructuredSelection) pluginTableViewer
				.getSelection();
		final boolean visible = !getFirstSelectedPlugin(selection).isVisible();
		final List<Plugin> list = getSelectedPlugins(selection);
		for (final Plugin p : list) {
			p.setVisible(visible);
		}
		pluginTableViewer.refresh();
		updateButtons((IStructuredSelection) pluginTableViewer.getSelection());
	}
	
	private void clickedButtonDeActivate() {
		final IStructuredSelection selection = (IStructuredSelection) pluginTableViewer
				.getSelection();
		final boolean active = !getFirstSelectedPlugin(selection).isActive();
		final List<Plugin> list = getSelectedPlugins(selection);
		for (final Plugin p : list) {
			p.setActive(active);
		}
		pluginTableViewer.refresh();
		updateButtons((IStructuredSelection) pluginTableViewer.getSelection());
	}
	
	private void clickedButtonRemoveInstance() {
		final List<Plugin> list = getSelectedPlugins((IStructuredSelection) pluginTableViewer
				.getSelection());
		final boolean confirm = MessageDialog.openConfirm(getShell(), "Confirm instance removal",
				"Are you sure you want to remove " + list.size() + " plugin instance(s)?");
		if (confirm) {
			for (final Plugin p : list) {
				spyglass.getPluginManager().removePlugin(p);
			}
		}
	}
	
	private void clickedButtonDown() {
		// final Plugin selectedPlugin = (Plugin) ((IStructuredSelection) pluginTableViewer
		// .getSelection()).getFirstElement();
		// final Plugin nextPlugin;
		// final List<Plugin> plugins = pluginTableContentProvider.plugins;
		// nextPlugin = plugins.get(plugins.indexOf(selectedPlugin) + 1);
		// spyglass.getPluginManager().togglePluginPriorities(selectedPlugin, nextPlugin);
		final List<Plugin> list = getSelectedPlugins((IStructuredSelection) pluginTableViewer
				.getSelection());
		spyglass.getPluginManager().decreasePluginPriorities(list);
		pluginTableViewer.refresh();
		updateButtons((IStructuredSelection) pluginTableViewer.getSelection());
	}
	
	private void clickedButtonUp() {
		// final Plugin selectedPlugin = (Plugin) ((IStructuredSelection) pluginTableViewer
		// .getSelection()).getFirstElement();
		// final Plugin previousPlugin;
		// final List<Plugin> plugins = pluginTableContentProvider.plugins;
		// previousPlugin = plugins.get(plugins.indexOf(selectedPlugin) - 1);
		// spyglass.getPluginManager().togglePluginPriorities(selectedPlugin, previousPlugin);
		final List<Plugin> list = getSelectedPlugins((IStructuredSelection) pluginTableViewer
				.getSelection());
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
		
		pluginTableViewer.getTable().dispose();
		pluginTableViewer = null;
		
		buttonUp.dispose();
		buttonUp = null;
		
		buttonDown.dispose();
		buttonDown = null;
		
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
