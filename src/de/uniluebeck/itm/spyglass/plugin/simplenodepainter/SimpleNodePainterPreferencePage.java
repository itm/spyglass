package de.uniluebeck.itm.spyglass.plugin.simplenodepainter;

import org.apache.log4j.Category;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferenceDialog;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;
import de.uniluebeck.itm.spyglass.plugin.Plugin;
import de.uniluebeck.itm.spyglass.util.SpyglassLogger;

public class SimpleNodePainterPreferencePage extends PluginPreferencePage<SimpleNodePainterPlugin, SimpleNodePainterXMLConfig> {
	
	private static Category log = SpyglassLogger.get(SimpleNodePainterPreferencePage.class);
	
	private IntegerFieldEditor lineWidthFieldEditor;
	private StringFieldEditor defaultStringFormatterFieldEditor;
	private BooleanFieldEditor extInfFieldEditor;
	private ColorFieldEditor colorFieldEditor;
	
	private final String PREF_STORE_COLOR = "lineColor";
	private final String PREF_STORE_DEFAULT_EXT_INF = "defaultExtInf";
	
	public SimpleNodePainterPreferencePage(final PluginPreferenceDialog dialog, final Spyglass spyglass) {
		super(dialog, spyglass, BasicOptions.ALL);
	}
	
	public SimpleNodePainterPreferencePage(final PluginPreferenceDialog dialog, final Spyglass spyglass, final SimpleNodePainterPlugin plugin) {
		super(dialog, spyglass, plugin, BasicOptions.ALL);
	}
	
	@Override
	protected Composite createContents(final Composite parent) {
		
		final Composite composite = super.createContents(parent);
		
		final Group optionsGroup = createGroup(composite, "Display");
		
		lineWidthFieldEditor = new IntegerFieldEditor("lineWidth", "Line width (pixels)", optionsGroup);
		lineWidthFieldEditor.setEmptyStringAllowed(false);
		lineWidthFieldEditor.setEnabled(true, optionsGroup);
		lineWidthFieldEditor.setErrorMessage("You must provide a line width.");
		lineWidthFieldEditor.setPage(this);
		lineWidthFieldEditor.setValidRange(0, 100); // Knigge recomendation
		
		extInfFieldEditor = new BooleanFieldEditor(PREF_STORE_DEFAULT_EXT_INF, "Display extended information on new nodes", optionsGroup);
		extInfFieldEditor.setEnabled(true, optionsGroup);
		extInfFieldEditor.setPage(this);
		extInfFieldEditor.setPreferenceStore(getPreferenceStore());
		
		colorFieldEditor = new ColorFieldEditor(PREF_STORE_COLOR, "Line color", optionsGroup);
		colorFieldEditor.setEnabled(true, optionsGroup);
		colorFieldEditor.setPage(this);
		colorFieldEditor.setPreferenceStore(getPreferenceStore());
		
		defaultStringFormatterFieldEditor = new StringFieldEditor("defaultStringformat", "Default StringFormatter", optionsGroup);
		defaultStringFormatterFieldEditor.setEmptyStringAllowed(true);
		defaultStringFormatterFieldEditor.setEnabled(true, optionsGroup);
		// defaultStringFormatterFieldEditor.setErrorMessage("You must provide a line width.");
		defaultStringFormatterFieldEditor.setPage(this);
		// StringFormatter validation ?
		
		// TODO: table for semantic types -> string form
		
		return composite;
	}
	
	@Override
	public SimpleNodePainterXMLConfig getFormValues() {
		final SimpleNodePainterXMLConfig config = new SimpleNodePainterXMLConfig();
		super.fillInFormValues(config);
		
		// Store color
		this.colorFieldEditor.store();
		final String rgbColor = getPreferenceStore().getString(PREF_STORE_COLOR);
		final String[] rgbColorArray = rgbColor.split(",");
		final int[] rgbInt = new int[] { Integer.parseInt(rgbColorArray[0]), Integer.parseInt(rgbColorArray[1]), Integer.parseInt(rgbColorArray[2]) };
		config.setLineColorRGB(rgbInt);
		
		config.setLineWidth(this.lineWidthFieldEditor.getIntValue());
		// config.setDefaultStringFormatter(new
		// StringFormatter(this.defaultStringFormatterFieldEditor.getStringValue()));
		config.setExtendedDefaultValue(getPreferenceStore().getBoolean(PREF_STORE_DEFAULT_EXT_INF));
		
		// TODO Table
		
		return config;
	}
	
	@Override
	public void setFormValues(final SimpleNodePainterXMLConfig config) {
		super.setFormValues(config);
		
		listenForPropertyChanges = false;
		
		this.lineWidthFieldEditor.setStringValue(Integer.toString(config.getLineWidth()));
		// this.defaultStringFormatterFieldEditor.setStringValue(config.getDefaultStringFormatter().
		// getOrigExpression());
		
		getPreferenceStore().setValue(PREF_STORE_DEFAULT_EXT_INF, config.isExtendedDefaultValue());
		this.extInfFieldEditor.load();
		
		final int[] colorInt = config.getLineColorRGB();
		final String colorString = colorInt[0] + "," + colorInt[1] + "," + colorInt[2];
		getPreferenceStore().setValue(PREF_STORE_COLOR, colorString);
		this.colorFieldEditor.load();
		
		this.listenForPropertyChanges = true;
	}
	
	@Override
	public Class<? extends Plugin> getPluginClass() {
		return SimpleNodePainterPlugin.class;
	}
}