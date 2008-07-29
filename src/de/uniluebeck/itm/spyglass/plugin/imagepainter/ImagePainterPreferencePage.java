package de.uniluebeck.itm.spyglass.plugin.imagepainter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import de.uniluebeck.itm.spyglass.core.ConfigStore;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;

public class ImagePainterPreferencePage extends PluginPreferencePage<ImagePainterPlugin, ImagePainterXMLConfig> {
	
	public ImagePainterPreferencePage(final ConfigStore cs) {
		super(cs);
	}
	
	public ImagePainterPreferencePage(final ConfigStore cs, final ImagePainterPlugin plugin) {
		super(cs, plugin);
	}
	
	@Override
	protected Control createContents(final Composite parent) {
		String msg = "ImagePainterPlugin Preference Page\n";
		msg += (type == PrefType.INSTANCE ? "Instance Name: " + plugin.getInstanceName() + "\n" + "IsActive: " + plugin.isActive() + "\n" + "IsVisible: "
				+ plugin.isVisible() : "");
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
	
}