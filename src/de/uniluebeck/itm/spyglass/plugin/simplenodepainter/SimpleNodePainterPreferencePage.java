package de.uniluebeck.itm.spyglass.plugin.simplenodepainter;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferenceDialog;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;

public class SimpleNodePainterPreferencePage extends PluginPreferencePage<SimpleNodePainterPlugin, SimpleNodePainterXMLConfig> {
	
	public SimpleNodePainterPreferencePage(final PluginPreferenceDialog dialog, final Spyglass spyglass) {
		super(dialog, spyglass, SimpleNodePainterPlugin.class, BasicOptions.ALL);
	}
	
	public SimpleNodePainterPreferencePage(final PluginPreferenceDialog dialog, final Spyglass spyglass, final SimpleNodePainterPlugin plugin) {
		super(dialog, spyglass, plugin, BasicOptions.ALL);
	}
	
	@Override
	protected Composite createContents(final Composite parent) {
		
		final Composite composite = super.createContents(parent);
		
		final Group optionsGroup = createGroup(composite, "Display");
		
		final IntegerFieldEditor lineWidthFieldEditor = new IntegerFieldEditor("lineWidth", "Line width (pixels)", optionsGroup);
		lineWidthFieldEditor.setEmptyStringAllowed(false);
		lineWidthFieldEditor.setEnabled(true, optionsGroup);
		lineWidthFieldEditor.setErrorMessage("You must provide a line width.");
		lineWidthFieldEditor.setPage(this);
		lineWidthFieldEditor.setTextLimit(Integer.toString(Integer.MAX_VALUE).length());
		// lineWidthFieldEditor.setValidateStrategy(StringFieldEditor.VALIDATE_ON_KEY_STROKE);
		lineWidthFieldEditor.setPreferenceStore(this.prefStore);
		
		final BooleanFieldEditor extInfFieldEditor = new BooleanFieldEditor("width", "Display extended information", optionsGroup);
		extInfFieldEditor.setEnabled(true, optionsGroup);
		extInfFieldEditor.setPage(this);
		extInfFieldEditor.setPreferenceStore(this.prefStore);
		
		final ColorFieldEditor colorFieldEditor = new ColorFieldEditor("color", "Line color", optionsGroup);
		colorFieldEditor.setEnabled(true, optionsGroup);
		colorFieldEditor.setPage(this);
		colorFieldEditor.setPreferenceStore(this.prefStore);
		
		// TODO: table for semantic types
		
		return composite;
	}
	
	@Override
	public SimpleNodePainterXMLConfig getFormValues() {
		// TODO implement
		return tmpConfig;
	}
	
	private SimpleNodePainterXMLConfig tmpConfig;
	
	@Override
	public void setFormValues(final SimpleNodePainterXMLConfig config) {
		this.tmpConfig = config;
	}
	
}