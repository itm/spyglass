package de.uniluebeck.itm.spyglass.plugin.imagepainter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import de.uniluebeck.itm.spyglass.core.ConfigStore;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

public class ImagePainterPluginPreferences extends PluginPreferencePage<ImagePainterPlugin> {
	
	public ImagePainterPluginPreferences(final ConfigStore cs) {
		super(cs);
	}
	
	public ImagePainterPluginPreferences(final ConfigStore cs, final ImagePainterPlugin plugin) {
		super(cs, plugin);
	}
	
	@Override
	public PluginXMLConfig getCurrentPluginConfig() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	protected Control createContents(final Composite parent) {
		final Label label = new Label(parent, SWT.NONE);
		label.setText("ImagePainterPlugin Preference Page");
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
	public boolean performRestore() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean performRestoreDefaults() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean performSaveAsDefault() {
		// TODO Auto-generated method stub
		return false;
	}
	
}