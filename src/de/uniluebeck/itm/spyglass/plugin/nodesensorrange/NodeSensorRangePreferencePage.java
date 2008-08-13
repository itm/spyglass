package de.uniluebeck.itm.spyglass.plugin.nodesensorrange;

import org.eclipse.swt.widgets.Composite;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferenceDialog;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;

public class NodeSensorRangePreferencePage extends PluginPreferencePage<NodeSensorRangePlugin, NodeSensorRangeXMLConfig> {
	
	public NodeSensorRangePreferencePage(final PluginPreferenceDialog dialog, final Spyglass spyglass) {
		super(dialog, spyglass, NodeSensorRangePlugin.class, BasicOptions.ALL_BUT_SEMANTIC_TYPES);
	}
	
	public NodeSensorRangePreferencePage(final PluginPreferenceDialog dialog, final Spyglass spyglass, final NodeSensorRangePlugin plugin) {
		super(dialog, spyglass, plugin, BasicOptions.ALL_BUT_SEMANTIC_TYPES);
	}
	
	@Override
	protected Composite createContents(final Composite parent) {
		return super.createContents(parent); // TODO: plugin-specific options
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