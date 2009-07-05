package de.uniluebeck.itm.spyglass.plugin.simplenodepainter;

import java.util.HashMap;

import org.eclipse.core.databinding.observable.set.ISetChangeListener;
import org.eclipse.core.databinding.observable.set.SetChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferenceDialog;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;
import de.uniluebeck.itm.spyglass.plugin.Plugin;

public class SimpleNodePainterPreferencePage extends PluginPreferencePage<SimpleNodePainterPlugin, SimpleNodePainterXMLConfig> {

	/**
	 * Reference to the map backing {@link tableData}. This map is a copy of the one from the
	 * XMLConfig. Changes in the table reflect on this map. the entries of this map are propagated
	 * tho the original map in the XMLConfig only when storeToModel() is called.
	 */
	private HashMap<Integer, String> tempStringFormatterTable = new HashMap<Integer, String>();

	private OptionsComposite optionsComposite;

	private final ISetChangeListener setChangeListener;

	public SimpleNodePainterPreferencePage(final PluginPreferenceDialog dialog, final Spyglass spyglass) {
		super(dialog, spyglass, BasicOptions.ALL);
		setChangeListener = new ISetChangeListener() {
			// --------------------------------------------------------------------------------
			@Override
			public void handleSetChange(final SetChangeEvent event) {
				markFormDirty();
			}
		};
	}

	public SimpleNodePainterPreferencePage(final PluginPreferenceDialog dialog, final Spyglass spyglass, final SimpleNodePainterPlugin plugin) {
		super(dialog, spyglass, plugin, BasicOptions.ALL);
		setChangeListener = new ISetChangeListener() {
			// --------------------------------------------------------------------------------
			@Override
			public void handleSetChange(final SetChangeEvent event) {
				markFormDirty();
			}
		};
	}

	@Override
	public Class<? extends Plugin> getPluginClass() {
		return SimpleNodePainterPlugin.class;
	}

	// --------------------------------------------------------------------------------
	@Override
	protected void loadFromModel() {
		super.loadFromModel();
		tempStringFormatterTable = config.getStringFormatters();
		optionsComposite.stringFormatter.connectTableWithData(dbc, tempStringFormatterTable, setChangeListener);
	}

	// --------------------------------------------------------------------------------
	@Override
	public void storeToModel() {
		super.storeToModel();
		config.setStringFormatters(this.tempStringFormatterTable);
	}

	// --------------------------------------------------------------------------------
	@Override
	protected Composite createContents(final Composite parent) {

		final Composite composite = super.createContents(parent);

		optionsComposite = new OptionsComposite(composite, SWT.NONE);

		optionsComposite.setDatabinding(dbc, config, this);

		tempStringFormatterTable = config.getStringFormatters();
		optionsComposite.stringFormatter.connectTableWithData(dbc, tempStringFormatterTable, setChangeListener);

		// necessary to prevent the change listener to react on the initialization
		return composite;
	}

	// --------------------------------------------------------------------------------
	@Override
	public void dispose() {
		// the object will be null if the preference page was just added to the tree and not
		// selected by the user
		if (optionsComposite != null) {
			optionsComposite.dispose();
		}

		super.dispose();
	}

}