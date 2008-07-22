package de.uniluebeck.itm.spyglass.gui.configuration;

import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.swt.widgets.Shell;

public class PluginPreferencesDialog {
	
	private final PreferenceManager preferenceManager;
	private final PreferenceDialog preferenceDialog;
	
	public PluginPreferencesDialog(final Shell parentShell) {
		
		preferenceManager = new PreferenceManager();
		preferenceDialog = new PreferenceDialog(parentShell, preferenceManager);
		
		addPreferenceNodes();
		
	}
	
	private void addPreferenceNodes() {
		
		final PreferenceNode general = new PreferenceNode("General", "General", null, GeneralPreferencePage.class.getCanonicalName());
		final PreferenceNode plugins = new PreferenceNode("Plugins", "Plugins", null, PluginsPreferencePage.class.getCanonicalName());
		
		preferenceManager.addToRoot(general);
		preferenceManager.addToRoot(plugins);
		
	}
	
	@Override
	public void finalize() throws Throwable {
		// TODO
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return see {@link org.eclipse.jface.window.Window#open()}
	 */
	public int open() {
		return preferenceDialog.open();
	}
	
}