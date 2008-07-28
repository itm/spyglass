package de.uniluebeck.itm.spyglass.plugin.mappainter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import de.uniluebeck.itm.spyglass.core.ConfigStore;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;

public class MapPainterPreferencePage extends PluginPreferencePage<MapPainterPlugin, MapPainterXMLConfig> {
	
	public MapPainterPreferencePage(final ConfigStore cs) {
		super(cs);
	}
	
	public MapPainterPreferencePage(final ConfigStore cs, final MapPainterPlugin plugin) {
		super(cs, plugin);
	}
	
	@Override
	protected Control createContents(final Composite parent) {
		final Label label = new Label(parent, SWT.NONE);
		label.setText("MapPainterPlugin Preference Page");
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
	public MapPainterXMLConfig getFormValues() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void setFormValues(final MapPainterXMLConfig config) {
		// TODO Auto-generated method stub
		
	}
	
}