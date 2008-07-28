package de.uniluebeck.itm.spyglass.gui.configuration;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import de.uniluebeck.itm.spyglass.plugin.Plugin;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

public class AbstractPluginTypePreferencePage extends PluginPreferencePage<Plugin, PluginXMLConfig> {
	
	private final String pluginName;
	
	public AbstractPluginTypePreferencePage(final String pluginName) {
		super(null);
		this.pluginName = pluginName;
	}
	
	@Override
	public boolean hasUnsavedChanges() {
		return false;
	}
	
	@Override
	public void performApply() {
		
	}
	
	@Override
	protected Control createContents(final Composite parent) {
		final Label label = new Label(parent, SWT.NONE);
		label.setText(pluginName + " Preference Page");
		return label;
	}
	
	@Override
	public PluginXMLConfig getFormValues() {
		// nothing to do, since this is abstract (!)
		throw new RuntimeException("This method should never be called");
	}
	
	@Override
	public void setFormValues(final PluginXMLConfig config) {
		// nothing to do, since this is abstract (!)
	}
	
}
