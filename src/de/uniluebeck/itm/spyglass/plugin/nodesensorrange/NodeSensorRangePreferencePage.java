package de.uniluebeck.itm.spyglass.plugin.nodesensorrange;

import org.eclipse.swt.widgets.Composite;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferenceDialog;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;
import de.uniluebeck.itm.spyglass.plugin.Plugin;

public class NodeSensorRangePreferencePage extends
		PluginPreferencePage<NodeSensorRangePlugin, NodeSensorRangeXMLConfig> {
	
	public NodeSensorRangePreferencePage(final PluginPreferenceDialog dialog,
			final Spyglass spyglass) {
		super(dialog, spyglass, BasicOptions.ALL_BUT_SEMANTIC_TYPES);
	}
	
	public NodeSensorRangePreferencePage(final PluginPreferenceDialog dialog,
			final Spyglass spyglass, final NodeSensorRangePlugin plugin) {
		super(dialog, spyglass, plugin, BasicOptions.ALL_BUT_SEMANTIC_TYPES);
	}
	
	@Override
	protected Composite createContents(final Composite parent) {
		return super.createMS2Warning(parent); // TODO: plugin-specific options
	}
	
	@Override
	public Class<? extends Plugin> getPluginClass() {
		return NodeSensorRangePlugin.class;
	}
	
}