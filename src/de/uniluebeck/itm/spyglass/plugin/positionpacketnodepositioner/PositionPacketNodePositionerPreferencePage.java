package de.uniluebeck.itm.spyglass.plugin.positionpacketnodepositioner;

import org.apache.log4j.Category;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferenceDialog;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;
import de.uniluebeck.itm.spyglass.util.SpyglassLogger;

public class PositionPacketNodePositionerPreferencePage extends
		PluginPreferencePage<PositionPacketNodePositionerPlugin, PositionPacketNodePositionerXMLConfig> {
	
	private final String PREF_STORE_TTL = "ttl";
	
	private static Category log = SpyglassLogger.get(PositionPacketNodePositionerPlugin.class);
	
	public PositionPacketNodePositionerPreferencePage(final PluginPreferenceDialog dialog, final Spyglass spyglass) {
		super(dialog, spyglass, PositionPacketNodePositionerPlugin.class, BasicOptions.ALL_BUT_VISIBLE_AND_SEMANTIC_TYPES);
	}
	
	public PositionPacketNodePositionerPreferencePage(final PluginPreferenceDialog dialog, final Spyglass spyglass,
			final PositionPacketNodePositionerPlugin plugin) {
		super(dialog, spyglass, plugin, BasicOptions.ALL_BUT_VISIBLE_AND_SEMANTIC_TYPES);
		// this.config = (PositionPacketNodePositionerXMLConfig) plugin.getXMLConfig();
	}
	
	private StringFieldEditor ttlFieldEditor;
	
	@Override
	protected Composite createContents(final Composite parent) {
		
		final Composite composite = super.createContents(parent);
		
		final Group optionsGroup = createGroup(composite, "Options");
		
		ttlFieldEditor = new StringFieldEditor(PREF_STORE_TTL, "Time to Live (sec)", optionsGroup);
		ttlFieldEditor.setEmptyStringAllowed(false);
		ttlFieldEditor.setEnabled(true, optionsGroup);
		ttlFieldEditor.setErrorMessage("You must provide a TTL parameter.");
		ttlFieldEditor.setPage(this);
		ttlFieldEditor.setTextLimit(Integer.toString(Integer.MAX_VALUE).length());
		ttlFieldEditor.setValidateStrategy(StringFieldEditor.VALIDATE_ON_KEY_STROKE);
		ttlFieldEditor.setPreferenceStore(this.prefStore);
		
		return composite;
		
	}
	
	@Override
	public PositionPacketNodePositionerXMLConfig getFormValues() {
		final PositionPacketNodePositionerXMLConfig config = new PositionPacketNodePositionerXMLConfig();
		super.fillInFormValues(config);
		
		config.setTimeToLive(this.prefStore.getInt(PREF_STORE_TTL));
		return config;
	}
	
	@Override
	public void setFormValues(final PositionPacketNodePositionerXMLConfig config) {
		super.setFormValues(config);
		
		listenForPropertyChanges = false;
		this.prefStore.setValue(PREF_STORE_TTL, config.getTimeToLive());
		ttlFieldEditor.load();
		listenForPropertyChanges = true;
	}
	
}