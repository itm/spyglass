package de.uniluebeck.itm.spyglass.gui.configuration;

import ishell.util.Logging;

import java.util.List;

import org.apache.log4j.Category;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.plugin.Plugin;
import de.uniluebeck.itm.spyglass.plugin.PluginListChangeListener;
import de.uniluebeck.itm.spyglass.plugin.nodepositioner.NodePositionerPlugin;

public class PluginManagerPreferencePage extends PreferencePage {
	
	private class PluginTableContentProvider implements IStructuredContentProvider, PluginListChangeListener {
		
		@Override
		public void dispose() {
			spyglass.getPluginManager().removePluginListChangeListener(this);
		}
		
		@Override
		public Object[] getElements(final Object arg0) {
			final List<Plugin> plugins = ((Spyglass) arg0).getPluginManager().getPlugins();
			return plugins.toArray(new Plugin[plugins.size()]);
		}
		
		@Override
		public void inputChanged(final Viewer v, final Object oldInput, final Object newInput) {
			if (newInput != null) {
				spyglass.getPluginManager().addPluginListChangeListener(this);
			}
			if (oldInput != null) {
				spyglass.getPluginManager().removePluginListChangeListener(this);
			}
		}
		
		@Override
		public void pluginListChanged(final Plugin p, final ListChangeEvent what) {
			pluginTableViewer.update(p, null);
		}
		
		public boolean isFirstInList(final Plugin selectedPlugin) {
			
			if (selectedPlugin == null) {
				return true;
			}
			
			final List<Plugin> plugins = spyglass.getPluginManager().getPlugins();
			if (plugins.size() > 0) {
				return plugins.get(0) == selectedPlugin;
			}
			
			throw new RuntimeException("We should never reach this code block hopefully.");
			
		}
		
		public boolean isLastInList(final Plugin selectedPlugin) {
			
			if (selectedPlugin == null) {
				return true;
			}
			
			final List<Plugin> plugins = spyglass.getPluginManager().getPlugins();
			if (plugins.size() > 0) {
				return plugins.get(plugins.size() - 1) == selectedPlugin;
			}
			
			throw new RuntimeException("We should never reach this code block hopefully.");
			
		}
		
	}
	
	private static final String COLUMN_CATEGORY = "Category";
	
	private static final String COLUMN_TYPE = "Type";
	
	private static final String COLUMN_NAME = "Name";
	
	private static final String COLUMN_ACTIVE = "Active";
	
	private static final String COLUMN_VISIBLE = "Visible";
	
	private static final String[] columnNames = new String[] { COLUMN_CATEGORY, COLUMN_TYPE, COLUMN_NAME, COLUMN_ACTIVE, COLUMN_VISIBLE };
	
	private TableViewer pluginTableViewer;
	
	private final PluginTableContentProvider pluginTableContentProvider = new PluginTableContentProvider();
	
	private final Spyglass spyglass;
	
	private Button buttonUp;
	
	private Button buttonDown;
	
	public PluginManagerPreferencePage(final Spyglass spyglass) {
		this.spyglass = spyglass;
		
		noDefaultAndApplyButton();
	}
	
	private static final Category log = Logging.get(PluginManagerPreferencePage.class);
	
	private String getType(final Plugin p) {
		try {
			return (String) p.getClass().getMethod("getHumanReadableName").invoke(p);
		} catch (final Exception e) {
			log.error("", e);
			return "__ERROR__";
		}
	}
	
	@SuppressWarnings("unchecked")
	private String getCategory(final Plugin p) {
		Class<? extends Plugin> clazz = p.getClass();
		while (!clazz.getSuperclass().equals(Plugin.class)) {
			clazz = (Class<? extends Plugin>) clazz.getSuperclass();
		}
		return clazz.getSimpleName();
	}
	
	private void addNodePositionerSelectionGroup(final Composite composite) {
		
		final GridLayout npSelLayout = new GridLayout();
		npSelLayout.numColumns = 2;
		
		final Group npSelGroup = new Group(composite, SWT.SHADOW_ETCHED_IN);
		npSelGroup.setText("Node Positioner Selection");
		npSelGroup.setLayout(npSelLayout);
		
		final Label npSelLabel = new Label(npSelGroup, SWT.NONE);
		npSelLabel.setText("Active Node Positioner");
		
		final Combo activeNPComboBox = new Combo(npSelGroup, SWT.SIMPLE | SWT.DROP_DOWN | SWT.READ_ONLY);
		activeNPComboBox.setItems(getActiveNPs());
		
	}
	
	private final SelectionListener buttonSelectionListener = new SelectionAdapter() {
		
		@Override
		public void widgetSelected(final SelectionEvent e) {
			if (e.getSource() == buttonUp) {
				clickedButtonUp();
			} else if (e.getSource() == buttonDown) {
				clickedButtonDown();
			}
		}
		
	};
	
	private void clickedButtonDown() {
		final Plugin selectedPlugin = (Plugin) ((IStructuredSelection) pluginTableViewer.getSelection()).getFirstElement();
		final Plugin nextPlugin;
		final List<Plugin> plugins = spyglass.getPluginManager().getPlugins();
		nextPlugin = plugins.get(plugins.indexOf(selectedPlugin) + 1);
		spyglass.getPluginManager().togglePluginPriorities(selectedPlugin, nextPlugin);
		pluginTableViewer.refresh();
		updateButtons(selectedPlugin);
	}
	
	private void clickedButtonUp() {
		final Plugin selectedPlugin = (Plugin) ((IStructuredSelection) pluginTableViewer.getSelection()).getFirstElement();
		final Plugin previousPlugin;
		final List<Plugin> plugins = spyglass.getPluginManager().getPlugins();
		previousPlugin = plugins.get(plugins.indexOf(selectedPlugin) - 1);
		spyglass.getPluginManager().togglePluginPriorities(selectedPlugin, previousPlugin);
		pluginTableViewer.refresh();
		updateButtons(selectedPlugin);
	}
	
	private void addPluginManagerList(final Composite parent) {
		
		final GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = false;
		
		final GridData data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		
		final Group pluginsGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
		pluginsGroup.setText("Plugins");
		pluginsGroup.setLayout(layout);
		pluginsGroup.setLayoutData(data);
		
		final GridData pluginTableData = new GridData();
		pluginTableData.grabExcessHorizontalSpace = true;
		pluginTableData.grabExcessVerticalSpace = true;
		pluginTableData.heightHint = 300;
		pluginTableData.widthHint = 500;
		
		// createTable(pluginsGroup);
		createTableViewer(pluginsGroup);
		
		pluginTableViewer.getTable().setLayoutData(pluginTableData);
		
		final GridLayout buttonCompositeLayout = new GridLayout();
		buttonCompositeLayout.numColumns = 1;
		
		final GridData buttonCompositeData = new GridData();
		buttonCompositeData.verticalAlignment = SWT.TOP;
		
		final Composite buttonComposite = new Composite(pluginsGroup, SWT.NONE);
		buttonComposite.setLayout(buttonCompositeLayout);
		buttonComposite.setLayoutData(buttonCompositeData);
		
		final GridData buttonUpData = new GridData();
		buttonUpData.widthHint = 70;
		
		final GridData buttonDownData = new GridData();
		buttonDownData.widthHint = 70;
		
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
		
	}
	
	private class CategoryLabelProvider extends ColumnLabelProvider {
		
		@Override
		public String getText(final Object element) {
			return getCategory((Plugin) element);
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
	
	private class TypeLabelProvider extends ColumnLabelProvider {
		
		@Override
		public String getText(final Object element) {
			return getType((Plugin) element);
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
	
	private class NameLabelProvider extends ColumnLabelProvider {
		
		@Override
		public String getText(final Object element) {
			return ((Plugin) element).getInstanceName();
		}
		
	}
	
	private class NameEditing extends EditingSupport {
		
		private final TextCellEditor cellEditor;
		
		public NameEditing(final TableViewer viewer) {
			super(viewer);
			cellEditor = new TextCellEditor(viewer.getTable());
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
			return ((Plugin) arg0).getInstanceName();
		}
		
		@Override
		protected void setValue(final Object arg0, final Object arg1) {
			// TODO unique name validation (!!!)
			((Plugin) arg0).getXMLConfig().setName((String) arg1);
			pluginTableViewer.update(arg0, new String[] { COLUMN_NAME });
		}
	}
	
	private class ActiveLabelProvider extends ColumnLabelProvider {
		
		@Override
		public String getText(final Object element) {
			return ((Plugin) element).isActive() + "";
		}
		
	}
	
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
			// TODO unique name validation (!!!)
			final int selected = ((Integer) arg1);
			((Plugin) arg0).setActive(selected == 0 ? true : false);
			pluginTableViewer.update(arg0, new String[] { COLUMN_ACTIVE });
		}
		
	}
	
	private class VisibleLabelProvider extends ColumnLabelProvider {
		
		@Override
		public String getText(final Object element) {
			return ((Plugin) element).isVisible() + "";
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
			// TODO unique name validation (!!!)
			final int selected = ((Integer) arg1);
			((Plugin) arg0).setVisible(selected == 0 ? true : false);
			pluginTableViewer.update(arg0, new String[] { COLUMN_VISIBLE });
		}
		
	}
	
	private void createTableViewer(final Composite parent) {
		
		final int style = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION;
		
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
		column.setLabelProvider(new ActiveLabelProvider());
		column.setEditingSupport(new ActiveEditing(pluginTableViewer));
		
		column = new TableViewerColumn(pluginTableViewer, SWT.NONE);
		column.getColumn().setWidth(50);
		column.getColumn().setText(COLUMN_VISIBLE);
		column.setLabelProvider(new VisibleLabelProvider());
		column.setEditingSupport(new VisibleEditing(pluginTableViewer));
		
		pluginTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(final SelectionChangedEvent e) {
				final IStructuredSelection selection = (IStructuredSelection) e.getSelection();
				updateButtons((Plugin) selection.getFirstElement());
			}
			
		});
		
		pluginTableViewer.getTable().setLinesVisible(true);
		pluginTableViewer.getTable().setHeaderVisible(true);
		
		pluginTableViewer.setContentProvider(pluginTableContentProvider);
		pluginTableViewer.setInput(spyglass);
		
	}
	
	private void updateButtons(final Plugin selectedPlugin) {
		buttonUp.setEnabled(!pluginTableContentProvider.isFirstInList(selectedPlugin));
		buttonDown.setEnabled(!pluginTableContentProvider.isLastInList(selectedPlugin));
	}
	
	@Override
	protected Control createContents(final Composite parent) {
		
		final GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		
		final GridData data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		
		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(layout);
		composite.setLayoutData(data);
		
		addNodePositionerSelectionGroup(composite);
		addPluginManagerList(composite);
		
		return composite;
	}
	
	@Override
	public void dispose() {
		
		pluginTableViewer.getTable().dispose();
		pluginTableViewer = null;
		
		buttonUp.dispose();
		buttonUp = null;
		
		buttonDown.dispose();
		buttonDown = null;
		
	}
	
	private String[] getActiveNPs() {
		
		final List<Plugin> pluginInstances = spyglass.getPluginManager().getPluginInstances(NodePositionerPlugin.class, true);
		final String[] activeNPs = new String[pluginInstances.size()];
		
		int i = 0;
		for (final Plugin p : pluginInstances) {
			activeNPs[i++] = p.getInstanceName();
		}
		
		return activeNPs;
		
	}
	
}
