package de.uniluebeck.itm.spyglass.plugin.positionpacketnodepositioner;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import de.uniluebeck.itm.spyglass.core.ConfigStore;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;

public class PositionPacketNodePositionerPreferencePage extends
		PluginPreferencePage<PositionPacketNodePositionerPlugin, PositionPacketNodePositionerXMLConfig> {
	
	public PositionPacketNodePositionerPreferencePage(final ConfigStore cs) {
		super(cs);
	}
	
	public PositionPacketNodePositionerPreferencePage(final ConfigStore cs, final PositionPacketNodePositionerPlugin plugin) {
		super(cs, plugin);
	}
	
	@Override
	protected Control createContents(final Composite parent) {
		String msg = "PositionPacketNodePositionerPlugin Preference Page\n";
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
	public PositionPacketNodePositionerXMLConfig getFormValues() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void setFormValues(final PositionPacketNodePositionerXMLConfig config) {
		// TODO Auto-generated method stub
		
	}
	
}