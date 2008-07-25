package de.uniluebeck.itm.spyglass.gui.configuration;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import de.uniluebeck.itm.spyglass.core.ConfigStore;
import de.uniluebeck.itm.spyglass.plugin.Plugin;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

// --------------------------------------------------------------------------------
/**
 * @author Daniel Bimschas
 * 
 * @param <T>
 */
public abstract class PluginPreferencePage<T extends Plugin> extends PreferencePage {
	
	// --------------------------------------------------------------------------------
	/**
	 * Enumeration. Decides, if the surrounding PluginPreferencesWidget represents an Type or an
	 * Instance.
	 */
	protected enum PrefType {
		INSTANCE, TYPE
	}
	
	/**
	 * 
	 */
	protected ConfigStore cs;
	
	/**
	 * 
	 */
	protected T plugin;
	
	/**
	 * 
	 */
	protected PrefType type;
	
	/**
	 * @param parent
	 * @param plugin
	 * @param cs
	 * @param type
	 */
	public PluginPreferencePage(final ConfigStore cs) {
		super();
		this.type = PrefType.TYPE;
		this.cs = cs;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param cs
	 * @param plugin
	 */
	public PluginPreferencePage(final ConfigStore cs, final T plugin) {
		this.type = PrefType.INSTANCE;
		this.cs = cs;
		this.plugin = plugin;
	}
	
	@Override
	public void finalize() throws Throwable {
		super.finalize();
	}
	
	private Button testButton;
	
	@Override
	protected void contributeButtons(final Composite parent) {
		testButton = createButton(parent, "Test", new SelectionAdapter() {
		});
	}
	
	private Button createButton(final Composite parent, final String label, final SelectionListener selectionListener) {
		((GridLayout) parent.getLayout()).numColumns++;
		final Button button = new Button(parent, SWT.PUSH);
		button.setText(label);
		button.setFont(JFaceResources.getDialogFont());
		button.addSelectionListener(selectionListener);
		setButtonLayoutData(button);
		return button;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return
	 */
	public abstract PluginXMLConfig getCurrentPluginConfig();
	
	// --------------------------------------------------------------------------------
	/**
	 * @return
	 */
	public abstract boolean hasUnsavedChanges();
	
	@Override
	public abstract void performApply();
	
	// --------------------------------------------------------------------------------
	/**
	 * @return
	 */
	public abstract boolean performRestore();
	
	// --------------------------------------------------------------------------------
	/**
	 * @return
	 */
	public abstract boolean performRestoreDefaults();
	
	// --------------------------------------------------------------------------------
	/**
	 * @return
	 */
	public abstract boolean performSaveAsDefault();
	
}
