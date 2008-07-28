package de.uniluebeck.itm.spyglass.plugin.objectpainter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import de.uniluebeck.itm.spyglass.core.ConfigStore;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;

public class ObjectPainterPreferencePage extends PluginPreferencePage<ObjectPainterPlugin, ObjectPainterXMLConfig> {
	
	public ObjectPainterPreferencePage(final ConfigStore cs) {
		super(cs);
	}
	
	public ObjectPainterPreferencePage(final ConfigStore cs, final ObjectPainterPlugin plugin) {
		super(cs, plugin);
	}
	
	@Override
	protected Control createContents(final Composite parent) {
		final Label label = new Label(parent, SWT.NONE);
		label.setText("ObjectPainterPlugin Preference Page");
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
	public ObjectPainterXMLConfig getFormValues() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void setFormValues(final ObjectPainterXMLConfig config) {
		// TODO Auto-generated method stub
		
	}
	
}