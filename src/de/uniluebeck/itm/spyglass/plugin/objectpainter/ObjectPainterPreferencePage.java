package de.uniluebeck.itm.spyglass.plugin.objectpainter;

import org.eclipse.swt.widgets.Composite;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferenceDialog;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;

public class ObjectPainterPreferencePage extends PluginPreferencePage<ObjectPainterPlugin, ObjectPainterXMLConfig> {
	
	public ObjectPainterPreferencePage(final PluginPreferenceDialog dialog, final Spyglass spyglass) {
		super(dialog, spyglass, ObjectPainterPlugin.class, BasicOptions.ALL);
	}
	
	public ObjectPainterPreferencePage(final PluginPreferenceDialog dialog, final Spyglass spyglass, final ObjectPainterPlugin plugin) {
		super(dialog, spyglass, plugin, BasicOptions.ALL);
	}
	
	@Override
	protected Composite createContents(final Composite parent) {
		return super.createContents(parent); // TODO: plugin-specific options
	}
	
	@Override
	public ObjectPainterXMLConfig getFormValues() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void setFormValues(final ObjectPainterXMLConfig config) {
		// TODO Auto-generated method stub
		
	}
	
}