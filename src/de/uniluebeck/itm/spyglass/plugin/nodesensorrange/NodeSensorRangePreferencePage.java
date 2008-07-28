package de.uniluebeck.itm.spyglass.plugin.nodesensorrange;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import de.uniluebeck.itm.spyglass.core.ConfigStore;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;

public class NodeSensorRangePreferencePage extends PluginPreferencePage<NodeSensorRangePlugin, NodeSensorRangeXMLConfig> {
	
	public NodeSensorRangePreferencePage(final ConfigStore cs) {
		super(cs);
	}
	
	public NodeSensorRangePreferencePage(final ConfigStore cs, final NodeSensorRangePlugin plugin) {
		super(cs, plugin);
	}
	
	@Override
	protected Control createContents(final Composite parent) {
		final Label label = new Label(parent, SWT.NONE);
		label.setText("NodeSensorRangePlugin Preference Page");
		return label;
	}
	
	@Override
	public boolean hasUnsavedChanges() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void performApply() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public NodeSensorRangeXMLConfig getFormValues() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void setFormValues(final NodeSensorRangeXMLConfig config) {
		// TODO Auto-generated method stub
		
	}
	
}