package de.uniluebeck.itm.spyglass.plugin.simplenodepainter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import de.uniluebeck.itm.spyglass.core.ConfigStore;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;

public class SimpleNodePainterPreferencePage extends PluginPreferencePage<SimpleNodePainterPlugin, SimpleNodePainterXMLConfig> {
	
	public SimpleNodePainterPreferencePage(final ConfigStore cs) {
		super(cs);
	}
	
	public SimpleNodePainterPreferencePage(final ConfigStore cs, final SimpleNodePainterPlugin plugin) {
		super(cs, plugin);
	}
	
	@Override
	protected Control createContents(final Composite parent) {
		String msg = "SimpleNodePainterPreferencePage Preference Page\n";
		msg += (type == PrefType.INSTANCE ? "Instance Name: " + plugin.getName() + "\n" + "IsActive: " + plugin.isActive() + "\n" + "IsVisible: "
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
	public SimpleNodePainterXMLConfig getFormValues() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void setFormValues(final SimpleNodePainterXMLConfig config) {
		// TODO Auto-generated method stub
		
	}
	
}