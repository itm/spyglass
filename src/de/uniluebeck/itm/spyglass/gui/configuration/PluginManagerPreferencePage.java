package de.uniluebeck.itm.spyglass.gui.configuration;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

public class PluginManagerPreferencePage extends PreferencePage {
	
	@Override
	protected Control createContents(final Composite arg0) {
		final Label label = new Label(arg0, SWT.NONE);
		label.setText("Plugin Manager Preference Page");
		return label;
	}
	
}
