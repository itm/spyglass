package de.uniluebeck.itm.spyglass.gui.configuration;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
public abstract class PluginPreferencePage<PluginClass extends Plugin, ConfigClass extends PluginXMLConfig> extends PreferencePage {
	
	// --------------------------------------------------------------------------------
	/**
	 * Enumeration. Decides, if the surrounding PluginPreferencesWidget represents an Type or an
	 * Instance.
	 */
	protected enum PrefType {
		INSTANCE, TYPE
	}
	
	private Button applyButton;
	
	private final SelectionListener buttonSelectionListener = new SelectionAdapter() {
		@Override
		public void widgetSelected(final SelectionEvent e) {
			if (e.getSource() == deleteButton) {
				performDelete();
			} else if (e.getSource() == restoreButton) {
				performRestore();
			} else if (e.getSource() == applyButton) {
				performApply();
			} else if (e.getSource() == restoreDefaultsButton) {
				performRestoreDefaults();
			} else if (e.getSource() == saveAsDefaultButton) {
				performSaveAsDefault();
			} else if (e.getSource() == createInstanceButton) {
				performCreateInstance();
			}
		}
	};
	
	private Button createInstanceButton;
	
	/**
	 * 
	 */
	protected ConfigStore cs;
	
	private Button deleteButton;
	
	/**
	 * 
	 */
	protected PluginClass plugin;
	
	private Button restoreButton;
	
	private Button restoreDefaultsButton;
	
	private Button saveAsDefaultButton;
	
	/**
	 * 
	 */
	protected PrefType type;
	
	// --------------------------------------------------------------------------------
	/**
	 * @param cs
	 */
	public PluginPreferencePage(final ConfigStore cs) {
		super();
		noDefaultAndApplyButton();
		this.type = PrefType.TYPE;
		this.cs = cs;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param cs
	 * @param plugin
	 */
	public PluginPreferencePage(final ConfigStore cs, final PluginClass plugin) {
		super();
		noDefaultAndApplyButton();
		this.type = PrefType.INSTANCE;
		this.cs = cs;
		this.plugin = plugin;
	}
	
	@Override
	protected void contributeButtons(final Composite parent) {
		
		if (type == PrefType.INSTANCE) {
			
			deleteButton = createButton(parent, "Delete", buttonSelectionListener);
			restoreButton = createButton(parent, "Restore", buttonSelectionListener);
			applyButton = createButton(parent, "Apply", buttonSelectionListener);
			
		} else {
			
			restoreDefaultsButton = createButton(parent, "Restore Defaults", buttonSelectionListener);
			saveAsDefaultButton = createButton(parent, "Save as Default", buttonSelectionListener);
			createInstanceButton = createButton(parent, "Create Instance", buttonSelectionListener);
			
		}
		
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
	
	@Override
	public void finalize() throws Throwable {
		super.finalize();
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return
	 */
	public abstract ConfigClass getFormValues();
	
	// --------------------------------------------------------------------------------
	/**
	 * @return
	 */
	public abstract boolean hasUnsavedChanges();
	
	@Override
	public abstract void performApply();
	
	private void performCreateInstance() {
		// TODO
		MessageDialog.openInformation(getShell(), "TODO", "Create instance function not yet implemented.");
	}
	
	private void performDelete() {
		// TODO
		MessageDialog.openInformation(getShell(), "TODO", "Delete function not yet implemented.");
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected void performRestore() {
		setFormValues((ConfigClass) cs.readPluginInstanceConfig(plugin.getInstanceName()));
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected void performRestoreDefaults() {
		final Class<? extends Plugin> pluginClass = (Class<? extends Plugin>) this.getClass().getTypeParameters()[0].getClass();
		setFormValues((ConfigClass) cs.readPluginTypeDefaults(pluginClass));
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return
	 */
	protected void performSaveAsDefault() {
		cs.storePluginTypeDefaults(getFormValues());
	}
	
	// --------------------------------------------------------------------------------
	/**
	 */
	public abstract void setFormValues(ConfigClass config);
	
}
