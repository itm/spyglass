package de.uniluebeck.itm.spyglass.plugin.positionpacketnodepositioner;

import org.apache.log4j.Category;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferenceDialog;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;
import de.uniluebeck.itm.spyglass.plugin.Plugin;
import de.uniluebeck.itm.spyglass.util.SpyglassLogger;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

public class PositionPacketNodePositionerPreferencePage extends
		PluginPreferencePage<PositionPacketNodePositionerPlugin, PositionPacketNodePositionerXMLConfig> {
	
	private static Category log = SpyglassLogger.get(PositionPacketNodePositionerPlugin.class);
	
	public PositionPacketNodePositionerPreferencePage(final PluginPreferenceDialog dialog, final Spyglass spyglass) {
		super(dialog, spyglass);
	}
	
	public PositionPacketNodePositionerPreferencePage(final PluginPreferenceDialog dialog, final Spyglass spyglass,
			final PositionPacketNodePositionerPlugin plugin) {
		super(dialog, spyglass, plugin);
	}
	
	private StringFieldEditor instanceNameFieldEditor;
	
	private CheckboxCellEditor activeCellEditor;
	
	private StringFieldEditor ttlFieldEditor;
	
	private PositionPacketNodePositionerXMLConfig config;
	
	private boolean unsavedChanges = false;
	
	private boolean listenForPropertyChanges = true;
	
	final IPropertyChangeListener propertyChangeListener = new IPropertyChangeListener() {
		@Override
		public void propertyChange(final PropertyChangeEvent event) {
			if (listenForPropertyChanges) {
				log.debug("Property change received.");
				unsavedChanges = !config.equals(getFormValues());
			}
		}
	};
	
	@Override
	protected Control createContents(final Composite parent) {
		
		final Composite composite = createComposite(parent);
		final Group basicGroup = createGroup(composite, "Basic");
		final Group optionsGroup = createGroup(composite, "Options");
		
		instanceNameFieldEditor = new StringFieldEditor("instanceName", "Instance Name", basicGroup);
		instanceNameFieldEditor.setEmptyStringAllowed(false);
		instanceNameFieldEditor.setErrorMessage("You must provide a unique instance name.");
		instanceNameFieldEditor.setEnabled(true, basicGroup);
		instanceNameFieldEditor.setPage(this);
		instanceNameFieldEditor.setTextLimit(100);
		instanceNameFieldEditor.setValidateStrategy(StringFieldEditor.VALIDATE_ON_KEY_STROKE);
		instanceNameFieldEditor.setPropertyChangeListener(propertyChangeListener);
		
		activeCellEditor = new CheckboxCellEditor(basicGroup, SWT.CHECK);
		
		ttlFieldEditor = new StringFieldEditor("ttl", "Time to Live (sec)", optionsGroup);
		ttlFieldEditor.setEmptyStringAllowed(false);
		ttlFieldEditor.setEnabled(true, optionsGroup);
		ttlFieldEditor.setErrorMessage("You must provide a TTL parameter.");
		ttlFieldEditor.setPage(this);
		ttlFieldEditor.setTextLimit(Integer.toString(Integer.MAX_VALUE).length());
		ttlFieldEditor.setValidateStrategy(StringFieldEditor.VALIDATE_ON_KEY_STROKE);
		
		return composite;
		
	}
	
	@Override
	public boolean hasUnsavedChanges() {
		return unsavedChanges;
	}
	
	@Override
	public void performApply() {
		final PositionPacketNodePositionerXMLConfig formValues = getFormValues();
		this.plugin.setXMLConfig(formValues);
		this.config = formValues;
		this.unsavedChanges = false;
	}
	
	@Override
	public PositionPacketNodePositionerXMLConfig getFormValues() {
		final PositionPacketNodePositionerXMLConfig config = new PositionPacketNodePositionerXMLConfig();
		config.setActive(activeCellEditor.isActivated());
		config.setName(instanceNameFieldEditor.getStringValue());
		config.setTimeToLive(Integer.parseInt(ttlFieldEditor.getStringValue()));
		return config;
	}
	
	@Override
	public void setFormValues(final PositionPacketNodePositionerXMLConfig config) {
		listenForPropertyChanges = false;
		if (config.isActive()) {
			activeCellEditor.activate();
		} else {
			activeCellEditor.deactivate();
		}
		instanceNameFieldEditor.setStringValue(config.getName());
		ttlFieldEditor.setStringValue("" + config.getTimeToLive());
		// TODO es fehlen noch Felder
		
		this.config = config;
		listenForPropertyChanges = true;
	}
	
	@Override
	public Class<? extends Plugin> getPluginClass() {
		return PositionPacketNodePositionerPlugin.class;
	}
	
	@Override
	public Class<? extends PluginXMLConfig> getConfigClass() {
		return PositionPacketNodePositionerXMLConfig.class;
	}
	
}