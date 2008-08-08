package de.uniluebeck.itm.spyglass.plugin.imagepainter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferenceDialog;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;
import de.uniluebeck.itm.spyglass.plugin.Plugin;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

public class ImagePainterPreferencePage extends PluginPreferencePage<ImagePainterPlugin, ImagePainterXMLConfig> {
	
	public ImagePainterPreferencePage(final PluginPreferenceDialog dialog, final Spyglass spyglass) {
		super(dialog, spyglass);
	}
	
	public ImagePainterPreferencePage(final PluginPreferenceDialog dialog, final Spyglass spyglass, final ImagePainterPlugin plugin) {
		super(dialog, spyglass, plugin);
	}
	
	@Override
	protected Control createContents(final Composite parent) {
		String msg = "ImagePainterPlugin Preference Page\n";
		msg += (type == PrefType.INSTANCE ? "Instance Name: " + plugin.getInstanceName() + "\n" + "IsActive: " + plugin.isActive() + "\n"
				+ "IsVisible: " + plugin.isVisible() : "");
		final Label label = new Label(parent, SWT.NONE);
		label.setText(msg);
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
	public ImagePainterXMLConfig getFormValues() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void setFormValues(final ImagePainterXMLConfig config) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public Class<? extends Plugin> getPluginClass() {
		return ImagePainterPlugin.class;
	}
	
	@Override
	public Class<? extends PluginXMLConfig> getConfigClass() {
		return ImagePainterXMLConfig.class;
	}
	
}