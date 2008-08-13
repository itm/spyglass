package de.uniluebeck.itm.spyglass.plugin.positionpacketnodepositioner;

import org.apache.log4j.Category;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferenceDialog;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;
import de.uniluebeck.itm.spyglass.plugin.Plugin;
import de.uniluebeck.itm.spyglass.util.SpyglassLogger;

public class PositionPacketNodePositionerPreferencePage extends
		PluginPreferencePage<PositionPacketNodePositionerPlugin, PositionPacketNodePositionerXMLConfig> {
	
	private final String PREF_STORE_TTL = "ttl";
	
	private static Category log = SpyglassLogger.get(PositionPacketNodePositionerPlugin.class);
	
	public PositionPacketNodePositionerPreferencePage(final PluginPreferenceDialog dialog, final Spyglass spyglass) {
		super(dialog, spyglass, BasicOptions.ALL_BUT_VISIBLE_AND_SEMANTIC_TYPES);
	}
	
	public PositionPacketNodePositionerPreferencePage(final PluginPreferenceDialog dialog, final Spyglass spyglass,
			final PositionPacketNodePositionerPlugin plugin) {
		super(dialog, spyglass, plugin, BasicOptions.ALL_BUT_VISIBLE_AND_SEMANTIC_TYPES);
		// this.config = (PositionPacketNodePositionerXMLConfig) plugin.getXMLConfig();
	}
	
	private IntegerFieldEditor ttlFieldEditor;
	
	@Override
	protected Composite createContents(final Composite parent) {
		
		final Composite composite = super.createContents(parent);
		
		final Group optionsGroup = createGroup(composite, "Options");
		
		ttlFieldEditor = new IntegerFieldEditor(PREF_STORE_TTL, "Time to Live (sec)", optionsGroup);
		ttlFieldEditor.setEmptyStringAllowed(false);
		ttlFieldEditor.setEnabled(true, optionsGroup);
		ttlFieldEditor.setErrorMessage("You must provide a TTL parameter.");
		ttlFieldEditor.setPage(this);
		ttlFieldEditor.setValidRange(0, Integer.MAX_VALUE);
		
		return composite;
		
	}
	
	@Override
	public PositionPacketNodePositionerXMLConfig getFormValues() {
		final PositionPacketNodePositionerXMLConfig config = new PositionPacketNodePositionerXMLConfig();
		super.fillInFormValues(config);
		
		this.ttlFieldEditor.store();
		config.setTimeToLive(ttlFieldEditor.getIntValue());
		return config;
	}
	
	@Override
	public void setFormValues(final PositionPacketNodePositionerXMLConfig config) {
		super.setFormValues(config);
		
		listenForPropertyChanges = false;
		ttlFieldEditor.setStringValue(Integer.toString(config.getTimeToLive()));
		listenForPropertyChanges = true;
	}
	
	@Override
	public Class<? extends Plugin> getPluginClass() {
		return PositionPacketNodePositionerPlugin.class;
	}
	
}