package de.uniluebeck.itm.spyglass.gui.configuration;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.jfree.util.Log;

import de.uniluebeck.itm.spyglass.core.ConfigStore;
import de.uniluebeck.itm.spyglass.core.Spyglass;
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
	
	private final Spyglass spyglass;
	
	private final PluginPreferenceDialog dialog;
	
	// --------------------------------------------------------------------------------
	/**
	 * @param cs
	 */
	public PluginPreferencePage(final PluginPreferenceDialog dialog, final Spyglass spyglass) {
		super();
		noDefaultAndApplyButton();
		this.type = PrefType.TYPE;
		this.dialog = dialog;
		this.cs = spyglass.getConfigStore();
		this.spyglass = spyglass;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param cs
	 * @param plugin
	 */
	public PluginPreferencePage(final PluginPreferenceDialog dialog, final Spyglass spyglass, final PluginClass plugin) {
		super();
		noDefaultAndApplyButton();
		this.type = PrefType.INSTANCE;
		this.dialog = dialog;
		this.cs = spyglass.getConfigStore();
		this.spyglass = spyglass;
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
		spyglass.getPluginManager().createNewPlugin(getPluginClass(), getFormValues());
	}
	
	private void performDelete() {
		final boolean ok = MessageDialog.openQuestion(getShell(), "Remove plugin instance", "Are you sure you want to remove the plugin instance?");
		if (ok) {
			spyglass.getPluginManager().removePlugin(this.plugin);
		}
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
		try {
			
			final Class<? extends Plugin> pluginClass = (Class<? extends Plugin>) this.getClass().getMethod("getPluginClass").invoke(this);
			setFormValues((ConfigClass) cs.readPluginTypeDefaults(pluginClass));
			
		} catch (final Exception e) {
			Log.error("", e);
		}
		
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
	 * Checks if this is an instance page or a type page.
	 * 
	 * @return <code>true</code> if this is a preference page for a plugin instance,
	 *         <code>false</code> if this is a preference page for an instantiable plugin type.
	 */
	public boolean isInstancePage() {
		return type == PrefType.INSTANCE;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Returns the <code>Plugin</code> instance associated with this page.
	 * 
	 * @return the associated <code>Plugin</code> instance or <code>null</code> if this is a type
	 *         page (i.e. not an instance page, also see
	 *         {@link PluginPreferencePage#isInstancePage()})
	 */
	public Plugin getPlugin() {
		return plugin;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Returns the class-Object of the plugin this preference page is associated with. Needed for
	 * runtime-reflection.
	 * 
	 * @return the class-Object of the plugin this preference page is associated with
	 */
	public abstract Class<? extends Plugin> getPluginClass();
	
	// --------------------------------------------------------------------------------
	/**
	 * Returns the class-Object of the plugins' PluginXMLConfig this preference page is associated
	 * with. Needed for runtime reflection.
	 * 
	 * @return the class-Object of the plugins' PluginXMLConfig this preference page is associated
	 *         with
	 */
	public abstract Class<? extends PluginXMLConfig> getConfigClass();
	
	// --------------------------------------------------------------------------------
	/**
	 */
	public abstract void setFormValues(ConfigClass config);
	
	protected Composite createComposite(final Composite parent) {
		final Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new GridLayout());
		final GridData gridData = new GridData(SWT.LEFT, SWT.TOP, true, true);
		gridData.horizontalSpan = 50;
		gridData.verticalSpan = 50;
		c.setLayoutData(gridData);
		return c;
	}
	
	protected Group createGroup(final Composite parent, final String groupText) {
		final Group g = new Group(parent, SWT.SHADOW_ETCHED_IN);
		g.setText(groupText);
		g.setLayout(new GridLayout());
		g.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false));
		return g;
	}
}
