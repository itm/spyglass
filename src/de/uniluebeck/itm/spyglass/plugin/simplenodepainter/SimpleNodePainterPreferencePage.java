package de.uniluebeck.itm.spyglass.plugin.simplenodepainter;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferenceDialog;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;
import de.uniluebeck.itm.spyglass.plugin.Plugin;

public class SimpleNodePainterPreferencePage extends
		PluginPreferencePage<SimpleNodePainterPlugin, SimpleNodePainterXMLConfig> {
	
	/**
	 * Reference to the map backing {@link tableData}. This map is a copy of the one from the
	 * XMLConfig. Changes in the table reflect on this map. the entries of this map are propagated
	 * tho the original map in the XMLConfig only when storeToModel() is called.
	 */
	private HashMap<Integer, String> tempStringFormatterTable = new HashMap<Integer, String>();
	
	private OptionsComposite optionsComposite;
	
	public SimpleNodePainterPreferencePage(final PluginPreferenceDialog dialog,
			final Spyglass spyglass) {
		super(dialog, spyglass, BasicOptions.ALL);
	}
	
	public SimpleNodePainterPreferencePage(final PluginPreferenceDialog dialog,
			final Spyglass spyglass, final SimpleNodePainterPlugin plugin) {
		super(dialog, spyglass, plugin, BasicOptions.ALL);
	}
	
	@Override
	public Class<? extends Plugin> getPluginClass() {
		return SimpleNodePainterPlugin.class;
	}
	
	// --------------------------------------------------------------------------------
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage#loadFromModel()
	 */
	@Override
	protected void loadFromModel() {
		super.loadFromModel();
		
		// TODO: find a sane way to apply an UpdateSetStrategy to the Table, so we don't have
		// to do this by hand
		tempStringFormatterTable = config.getStringFormatters();
		optionsComposite.connectTableWithData(dbc, tempStringFormatterTable);
		
		optionsComposite.resetSomethingChanged();
		
	}
	
	// --------------------------------------------------------------------------------
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage#storeToModel()
	 */
	@Override
	public void storeToModel() {
		super.storeToModel();
		
		// TODO: find a sane way to apply an UpdateSetStrategy to the Table, so we don't have
		// to do this by hand
		config.setStringFormatters(this.tempStringFormatterTable);
		plugin.refreshConfigurationParameters();
		optionsComposite.resetSomethingChanged();
	}
	
	@Override
	protected Composite createContents(final Composite parent) {
		
		final Composite composite = super.createContents(parent);
		
		optionsComposite = new OptionsComposite(composite, SWT.NONE);
		
		optionsComposite.setDatabinding(dbc, config);
		
		tempStringFormatterTable = config.getStringFormatters();
		optionsComposite.connectTableWithData(dbc, tempStringFormatterTable);
		
		return composite;
	}
	
	// --------------------------------------------------------------------------------
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage#hasUnsavedChanges()
	 */
	@Override
	public boolean hasUnsavedChanges() {
		// TODO Auto-generated method stub
		return super.hasUnsavedChanges() || optionsComposite.isSomethingChanged();
	}
	
}