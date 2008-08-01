package de.uniluebeck.itm.spyglass.plugin.vectorsequencepainter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferenceDialog;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;
import de.uniluebeck.itm.spyglass.plugin.Plugin;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

public class VectorSequencePainterPreferencePage extends PluginPreferencePage<VectorSequencePainterPlugin, VectorSequencePainterXMLConfig> {
	
	public VectorSequencePainterPreferencePage(final PluginPreferenceDialog dialog, final Spyglass spyglass) {
		super(dialog, spyglass);
	}
	
	public VectorSequencePainterPreferencePage(final PluginPreferenceDialog dialog, final Spyglass spyglass, final VectorSequencePainterPlugin plugin) {
		super(dialog, spyglass, plugin);
	}
	
	@Override
	protected Control createContents(final Composite parent) {
		final Label label = new Label(parent, SWT.NONE);
		label.setText("VectorSequencePainterPlugin Preference Page");
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
	public VectorSequencePainterXMLConfig getFormValues() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void setFormValues(final VectorSequencePainterXMLConfig config) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public Class<? extends PluginXMLConfig> getConfigClass() {
		return VectorSequencePainterXMLConfig.class;
	}
	
	@Override
	public Class<? extends Plugin> getPluginClass() {
		return VectorSequencePainterPlugin.class;
	}
	
}