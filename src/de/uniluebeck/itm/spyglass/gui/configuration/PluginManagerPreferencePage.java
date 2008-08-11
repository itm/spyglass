package de.uniluebeck.itm.spyglass.gui.configuration;

import java.util.List;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.plugin.Plugin;
import de.uniluebeck.itm.spyglass.plugin.PluginListChangeListener;
import de.uniluebeck.itm.spyglass.plugin.nodepositioner.NodePositionerPlugin;

public class PluginManagerPreferencePage extends PreferencePage {
	
	private class PluginTableCellModifier implements ICellModifier {
		
		@Override
		public boolean canModify(final Object arg0, final String arg1) {
			return false;
		}
		
		private int findColumnIndex(final String property) {
			
			int columnIndex = Integer.MIN_VALUE, i = 0;
			for (final String p : (String[]) pluginTableViewer.getColumnProperties()) {
				if (p.equals(property)) {
					columnIndex = i;
				}
				i++;
			}
			
			assert columnIndex != Integer.MIN_VALUE;
			
			return columnIndex;
		}
		
		@Override
		public Object getValue(final Object element, final String property) {
			
			final int columnIndex = findColumnIndex(property);
			
			final Object result = null;
			
			switch (columnIndex) {
				case 0:
					break;
				case 1:
					break;
				case 2:
					break;
				case 3:
					break;
				case 4:
					break;
			}
			return result;
		}
		
		@Override
		public void modify(final Object element, final String property, final Object value) {
			
			final int columnIndex = findColumnIndex(property);
			
			switch (columnIndex) {
				case 0:
					break;
				case 1:
					break;
				case 2:
					break;
				case 3:
					break;
				case 4:
					break;
			}
			
		}
		
	}
	
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
		
	}
	
	private class PluginTableLabelProvider implements ITableLabelProvider {
		@Override
		public void addListener(final ILabelProviderListener arg0) {
			// nothing to do
		}
		
		@Override
		public void dispose() {
			// nothing to do
		}
		
		@Override
		public Image getColumnImage(final Object arg0, final int columnIndex) {
			// nothing to do
			return null;
		}
		
		@Override
		public String getColumnText(final Object element, final int columnIndex) {
			
			final Plugin p = (Plugin) element;
			
			switch (columnIndex) {
				case 0:
					return getCategory(p);
				case 1:
					return getType(p);
				case 2:
					return p.getInstanceName();
				case 3:
					return p.getXMLConfig().isActive() + "";
				default:
					return p.getXMLConfig().isVisible() + "";
			}
		}
		
		private String getType(final Plugin p) {
			return p.getClass().getSimpleName();
		}
		
		@SuppressWarnings("unchecked")
		private String getCategory(final Plugin p) {
			Class<? extends Plugin> clazz = p.getClass();
			while (!clazz.getSuperclass().equals(Plugin.class)) {
				clazz = (Class<? extends Plugin>) clazz.getSuperclass();
			}
			return clazz.getSimpleName();
		}
		
		@Override
		public boolean isLabelProperty(final Object arg0, final String arg1) {
			return true;
		}
		
		@Override
		public void removeListener(final ILabelProviderListener arg0) {
			// nothing to do
		}
	}
	
	private static final String COLUMN_CATEGORY = "Category";
	
	private static final String COLUMN_TYPE = "Type";
	
	private static final String COLUMN_NAME = "Name";
	
	private static final String COLUMN_ACTIVE = "Active";
	
	private static final String COLUMN_VISIBLE = "Visible";
	
	private static final String[] columNames = new String[] { COLUMN_CATEGORY, COLUMN_TYPE, COLUMN_NAME, COLUMN_ACTIVE, COLUMN_VISIBLE };
	
	private TableViewer pluginTableViewer;
	
	private final PluginTableCellModifier pluginTableCellModifier = new PluginTableCellModifier();
	
	private final PluginTableContentProvider pluginTableContentProvider = new PluginTableContentProvider();
	
	private final PluginTableLabelProvider pluginTableLabelProvider = new PluginTableLabelProvider();
	
	private final Spyglass spyglass;
	
	private Table pluginTable;
	
	public PluginManagerPreferencePage(final Spyglass spyglass) {
		this.spyglass = spyglass;
		
		noDefaultAndApplyButton();
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
		
		createTable(pluginsGroup);
		createTableViewer();
		
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
		
		final Button buttonUp = new Button(buttonComposite, SWT.PUSH);
		buttonUp.setText("Up");
		buttonUp.setLayoutData(buttonUpData);
		
		final Button buttonDown = new Button(buttonComposite, SWT.PUSH);
		buttonDown.setText("Down");
		buttonDown.setLayoutData(buttonDownData);
		
	}
	
	private void createTable(final Composite parent) {
		
		final int style = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL;
		
		pluginTable = new Table(parent, style);
		pluginTable.setLinesVisible(true);
		pluginTable.setHeaderVisible(true);
		
		TableColumn column;
		
		column = new TableColumn(pluginTable, SWT.CENTER, 0);
		column.setText(COLUMN_CATEGORY);
		
		column = new TableColumn(pluginTable, SWT.CENTER, 1);
		column.setText(COLUMN_TYPE);
		
		column = new TableColumn(pluginTable, SWT.CENTER, 2);
		column.setText(COLUMN_NAME);
		
		column = new TableColumn(pluginTable, SWT.CENTER, 3);
		column.setText(COLUMN_ACTIVE);
		
		column = new TableColumn(pluginTable, SWT.CENTER, 4);
		column.setText(COLUMN_VISIBLE);
		
	}
	
	private void createTableViewer() {
		
		// final CellEditor[] pluginTableEditors = new CellEditor[columNames.length];
		// pluginTableEditors[0] = null;
		// pluginTableEditors[1] = null;
		// pluginTableEditors[2] = new TextCellEditor();
		// pluginTableEditors[3] = new CheckboxCellEditor();
		// pluginTableEditors[4] = new CheckboxCellEditor();
		
		pluginTableViewer = new TableViewer(pluginTable);
		// pluginTableViewer.setUseHashlookup(true);
		pluginTableViewer.setColumnProperties(columNames);
		// pluginTableViewer.setCellEditors(pluginTableEditors);
		// pluginTableViewer.setCellModifier(pluginTableCellModifier);
		pluginTableViewer.setContentProvider(pluginTableContentProvider);
		pluginTableViewer.setLabelProvider(pluginTableLabelProvider);
		pluginTableViewer.setInput(spyglass);
		
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
		pluginTable.dispose();
		pluginTable = null;
		pluginTableViewer = null;
	}
	
	private String[] getActiveNPs() {
		
		final List<Plugin> pluginInstances = spyglass.getPluginManager().getPluginInstances(NodePositionerPlugin.class);
		final String[] activeNPs = new String[pluginInstances.size()];
		
		int i = 0;
		for (final Plugin p : pluginInstances) {
			activeNPs[i++] = p.getInstanceName();
		}
		
		return activeNPs;
		
	}
	
}
