package de.uniluebeck.itm.spyglass.gui.configuration;

import java.util.List;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
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
import de.uniluebeck.itm.spyglass.plugin.nodepositioner.NodePositionerPlugin;

public class PluginManagerPreferencePage extends PreferencePage {
	
	private final Spyglass spyglass;
	
	public PluginManagerPreferencePage(final Spyglass spyglass) {
		this.spyglass = spyglass;
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
	
	private final ILabelProvider pluginTableLabelProvider = new LabelProvider() {
		
	};
	
	private final IContentProvider pluginTableContentProvider = new IStructuredContentProvider() {
		
		@Override
		public Object[] getElements(final Object arg0) {
			final List<Plugin> plugins = ((Spyglass) arg0).getPluginManager().getPlugins();
			return plugins.toArray(new Plugin[plugins.size()]);
		}
		
		@Override
		public void dispose() {
			// nothing to do
		}
		
		@Override
		public void inputChanged(final Viewer arg0, final Object arg1, final Object arg2) {
			// nothing to do
		}
		
	};
	
	private void addPluginManagerList(final Composite parent) {
		
		final GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = false;
		
		final GridData data = new GridData();
		data.grabExcessHorizontalSpace = true;
		
		final Group pluginsGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
		pluginsGroup.setText("Plugins");
		pluginsGroup.setLayout(layout);
		pluginsGroup.setLayoutData(data);
		
		final GridData tmpData = new GridData();
		tmpData.grabExcessHorizontalSpace = true;
		
		final GridData pluginTableData = new GridData();
		pluginTableData.grabExcessHorizontalSpace = true;
		
		final TableViewer pluginTable = new TableViewer(pluginsGroup);
		pluginTable.setContentProvider(pluginTableContentProvider);
		pluginTable.setLabelProvider(pluginTableLabelProvider);
		pluginTable.setInput(spyglass);
		pluginTable.getTable().setLayoutData(pluginTableData);
		
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
