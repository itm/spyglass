package de.uniluebeck.itm.spyglass.gui.configuration;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import de.uniluebeck.itm.spyglass.plugin.Plugin;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

public class AbstractPluginTypePreferencePage extends PluginPreferencePage<Plugin> {
	
	private final String pluginName;
	
	public AbstractPluginTypePreferencePage(final String pluginName) {
		super(null);
		this.pluginName = pluginName;
	}
	
	@Override
	public PluginXMLConfig getCurrentPluginConfig() {
		return null;
	}
	
	@Override
	public boolean hasUnsavedChanges() {
		return false;
	}
	
	@Override
	public void performApply() {
		
	}
	
	@Override
	public boolean performRestore() {
		return false;
	}
	
	@Override
	public boolean performRestoreDefaults() {
		return false;
	}
	
	@Override
	public boolean performSaveAsDefault() {
		return false;
	}
	
	@Override
	protected Control createContents(final Composite parent) {
		final Label label = new Label(parent, SWT.NONE);
		label.setText(pluginName + " Preference Page");
		return label;
	}
	
}
