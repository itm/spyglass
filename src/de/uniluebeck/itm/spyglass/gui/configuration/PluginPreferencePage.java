package de.uniluebeck.itm.spyglass.gui.configuration;

import java.util.TreeSet;

import org.apache.log4j.Category;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
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
import de.uniluebeck.itm.spyglass.util.SpyglassLogger;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

// --------------------------------------------------------------------------------
/**
 * @author Daniel Bimschas
 * 
 * @param <T>
 */
public abstract class PluginPreferencePage<PluginClass extends Plugin, ConfigClass extends PluginXMLConfig> extends PreferencePage {
	
	private static Category log = SpyglassLogger.get(PluginPreferencePage.class);
	
	// --------------------------------------------------------------------------------
	/**
	 * Enumeration. Decides, if the surrounding PluginPreferencesWidget represents an Type or an
	 * Instance.
	 */
	protected enum PrefType {
		INSTANCE, TYPE
	}
	
	protected enum BasicOptions {
		/**
		 * Show all Fields in the the optionsGroup Basic
		 */
		ALL,

		/**
		 * Show all Fields except "isVisible" in the the optionsGroup Basic
		 */
		ALL_BUT_VISIBLE,

		/**
		 * Show all Fields except "isVisible" and fields for handling semantic types in the the
		 * optionsGroup Basic
		 */
		ALL_BUT_VISIBLE_AND_SEMANTIC_TYPES,

		/**
		 * Show all Fields except fields for handling semantic types in the the optionsGroup Basic
		 */
		ALL_BUT_SEMANTIC_TYPES
		
	}
	
	BasicOptions basicOptions = BasicOptions.ALL;
	
	private final SelectionListener buttonSelectionListener = new SelectionAdapter() {
		@Override
		public void widgetSelected(final SelectionEvent e) {
			if (e.getSource() == buttons.deleteButton) {
				performDelete();
			} else if (e.getSource() == buttons.restoreButton) {
				performRestore();
			} else if (e.getSource() == buttons.applyButton) {
				performApply();
			} else if (e.getSource() == buttons.restoreDefaultsButton) {
				performRestoreDefaults();
			} else if (e.getSource() == buttons.saveAsDefaultButton) {
				performSaveAsDefault();
			} else if (e.getSource() == buttons.createInstanceButton) {
				performCreateInstance();
			}
		}
	};
	
	/**
	 * 
	 */
	protected ConfigStore cs;
	
	private final String PREF_STORE_NAME = "instanceName";
	private final String PREF_STORE_SEMANTIC_TYPES = "semanticTypes";
	private final String PREF_STORE_ALL_SEMANTIC_TYPES = "allSemanticTypes";
	private final String PREF_STORE_VISIBLE = "isVisible";
	private final String PREF_STORE_ACTIVE = "isActive";
	
	/**
	 * Reference to the plugin instance. may be null if PrefType==TYPE.
	 */
	protected PluginClass plugin;
	
	/**
	 * The config object used by this preference page. guarantied to be defined.
	 */
	protected ConfigClass config;
	
	/**
	 * 
	 */
	protected PrefType type;
	
	private final Spyglass spyglass;
	
	private final PluginPreferenceDialog dialog;
	
	private class Buttons {
		
		private Button restoreButton;
		private Button restoreDefaultsButton;
		private Button saveAsDefaultButton;
		private Button deleteButton;
		private Button createInstanceButton;
		private Button applyButton;
		
	}
	
	private Buttons buttons = new Buttons();
	
	private class Fields {
		private StringFieldEditor instanceName;
		
		private Composite semanticTypesContainer;
		private StringFieldEditor semanticTypes;
		private BooleanFieldEditor isActive;
		private BooleanFieldEditor isVisible;
		private BooleanFieldEditor allSemanticTypes;
	}
	
	private Fields fields = new Fields();
	
	// --------------------------------------------------------------------------------
	/**
	 * @param cs
	 */
	public PluginPreferencePage(final PluginPreferenceDialog dialog, final Spyglass spyglass, final BasicOptions basicOptions) {
		super();
		noDefaultAndApplyButton();
		this.type = PrefType.TYPE;
		this.dialog = dialog;
		this.cs = spyglass.getConfigStore();
		this.spyglass = spyglass;
		this.basicOptions = basicOptions;
		
		this.setPreferenceStore(new PreferenceStore());
		
		// This is fine
		this.config = (ConfigClass) cs.readPluginTypeDefaults(this.getPluginClass());
		
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param cs
	 * @param plugin
	 */
	public PluginPreferencePage(final PluginPreferenceDialog dialog, final Spyglass spyglass, final PluginClass plugin,
			final BasicOptions basicOptions) {
		super();
		noDefaultAndApplyButton();
		this.type = PrefType.INSTANCE;
		this.dialog = dialog;
		this.cs = spyglass.getConfigStore();
		this.spyglass = spyglass;
		this.plugin = plugin;
		this.basicOptions = basicOptions;
		
		this.setPreferenceStore(new PreferenceStore());
		
		// This is fine
		this.config = (ConfigClass) plugin.getXMLConfig();
		
	}
	
	@Override
	protected void contributeButtons(final Composite parent) {
		
		if (type == PrefType.INSTANCE) {
			
			buttons.deleteButton = createButton(parent, "Delete", buttonSelectionListener);
			buttons.restoreButton = createButton(parent, "Restore", buttonSelectionListener);
			buttons.applyButton = createButton(parent, "Apply", buttonSelectionListener);
			
		} else {
			
			buttons.restoreDefaultsButton = createButton(parent, "Restore Defaults", buttonSelectionListener);
			buttons.saveAsDefaultButton = createButton(parent, "Save as Default", buttonSelectionListener);
			buttons.createInstanceButton = createButton(parent, "Create Instance", buttonSelectionListener);
			
		}
		
	}
	
	@Override
	protected Composite createContents(final Composite parent) {
		
		final Composite composite = createComposite(parent);
		
		final Group basicGroup = createGroup(composite, "Basic");
		
		final Composite c1 = new Composite(basicGroup, SWT.NONE);
		fields.instanceName = new StringFieldEditor(PREF_STORE_NAME, "Instance name", c1);
		fields.instanceName.setEmptyStringAllowed(false);
		fields.instanceName.setErrorMessage("You must provide a unique instance name.");
		fields.instanceName.setPage(this);
		fields.instanceName.setTextLimit(100);
		fields.instanceName.setValidateStrategy(StringFieldEditor.VALIDATE_ON_KEY_STROKE);
		fields.instanceName.setPropertyChangeListener(propertyChangeListener);
		
		final Composite c2 = new Composite(basicGroup, SWT.NONE);
		c2.setLayout(new GridLayout(2, false));
		
		fields.semanticTypesContainer = new Composite(c2, SWT.NONE);
		final Composite c2a = fields.semanticTypesContainer;
		fields.semanticTypes = new StringFieldEditor(PREF_STORE_SEMANTIC_TYPES, "Semantic types", c2a);
		fields.semanticTypes.setEmptyStringAllowed(false);
		fields.semanticTypes.setErrorMessage("You must provide a list of semantic types (integers)");
		fields.semanticTypes.setPage(this);
		fields.semanticTypes.setTextLimit(100);
		fields.semanticTypes.setValidateStrategy(StringFieldEditor.VALIDATE_ON_KEY_STROKE); // TODO
		fields.semanticTypes.setPropertyChangeListener(propertyChangeListener);
		
		final Composite c2b = new Composite(c2, SWT.NONE);
		fields.allSemanticTypes = new BooleanFieldEditor(PREF_STORE_ALL_SEMANTIC_TYPES, "All types", c2b);
		fields.allSemanticTypes.setPreferenceStore(getPreferenceStore());
		fields.allSemanticTypes.load();
		fields.allSemanticTypes.setPropertyChangeListener(new IPropertyChangeListener() {
			
			@Override
			public void propertyChange(final PropertyChangeEvent event) {
				if (fields.allSemanticTypes.getBooleanValue() == true) {
					fields.semanticTypes.setStringValue("0-255");
					fields.semanticTypes.setEnabled(false, c2a);
				} else {
					fields.semanticTypes.setEnabled(true, c2a);
				}
				
			}
		});
		
		final Composite c3 = new Composite(basicGroup, SWT.NONE);
		c3.setLayout(new GridLayout(2, false));
		
		final Composite c3a = new Composite(c3, SWT.NONE);
		fields.isVisible = new BooleanFieldEditor(PREF_STORE_VISIBLE, "Visible", c3a);
		fields.isVisible.setPreferenceStore(getPreferenceStore());
		fields.isVisible.load();
		
		final Composite c3b = new Composite(c3, SWT.NONE);
		fields.isActive = new BooleanFieldEditor(PREF_STORE_ACTIVE, "Active", c3b);
		fields.isActive.setPreferenceStore(getPreferenceStore());
		fields.isActive.load();
		
		switch (this.basicOptions) {
			case ALL:
				fields.semanticTypes.setEnabled(true, c2a);
				fields.allSemanticTypes.setEnabled(true, c2b);
				fields.isVisible.setEnabled(true, c3a);
				break;
			case ALL_BUT_VISIBLE:
				fields.semanticTypes.setEnabled(true, c2a);
				fields.allSemanticTypes.setEnabled(true, c2b);
				fields.isVisible.setEnabled(false, c3a);
				break;
			case ALL_BUT_VISIBLE_AND_SEMANTIC_TYPES:
				fields.semanticTypes.setEnabled(false, c2a);
				fields.allSemanticTypes.setEnabled(false, c2b);
				fields.isVisible.setEnabled(false, c3a);
				break;
			case ALL_BUT_SEMANTIC_TYPES:
				fields.semanticTypes.setEnabled(false, c2a);
				fields.allSemanticTypes.setEnabled(false, c2b);
				fields.isVisible.setEnabled(true, c3a);
				break;
		}
		
		return composite;
		
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
	public abstract ConfigClass getFormValues();
	
	protected void fillInFormValues(final ConfigClass config) {
		
		// First the values have to be pushed from the Fields into the preference store.
		this.fields.allSemanticTypes.store();
		this.fields.isActive.store();
		this.fields.isVisible.store();
		
		// Now read them out from the preference store
		config.setActive(getPreferenceStore().getBoolean(PREF_STORE_ACTIVE));
		config.setName(this.fields.instanceName.getStringValue());
		config.setSemanticTypes(PluginPreferencePage.intString2Array(this.fields.semanticTypes.getStringValue()));
		config.setVisible(getPreferenceStore().getBoolean(PREF_STORE_VISIBLE));
		// if (getPreferenceStore().getBoolean(PREF_STORE_ALL_SEMANTIC_TYPES)) {
		// config.setSemanticTypes(PluginXMLConfig.ALL_SEMANTIC_TYPES); // TODO
		// }
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return
	 */
	public final boolean hasUnsavedChanges() {
		return unsavedChanges;
	}
	
	protected boolean unsavedChanges = false;
	
	protected boolean listenForPropertyChanges = true;
	
	final IPropertyChangeListener propertyChangeListener = new IPropertyChangeListener() {
		@Override
		public void propertyChange(final PropertyChangeEvent event) {
			
			if (listenForPropertyChanges && !unsavedChanges) {
				log.debug("Property change received.");
				unsavedChanges = !getXMLConfig().equals(getFormValues());
			}
		}
	};
	
	@Override
	public final void performApply() {
		final ConfigClass formValues = getFormValues();
		this.plugin.setXMLConfig(formValues);
		this.config = formValues;
		this.unsavedChanges = false;
	}
	
	private final void performCreateInstance() {
		spyglass.getPluginManager().createNewPlugin(getPluginClass(), getFormValues());
	}
	
	private final void performDelete() {
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
	protected final void performRestore() {
		setFormValues((ConfigClass) cs.readPluginInstanceConfig(plugin.getInstanceName()));
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected final void performRestoreDefaults() {
		try {
			
			final Class<? extends Plugin> pluginClass = (Class<? extends Plugin>) this.getClass().getMethod("getPluginClass").invoke(this);
			setFormValues((ConfigClass) cs.readPluginTypeDefaults(pluginClass));
			
		} catch (final Exception e) {
			Log.error("", e);
		}
		
	}
	
	protected final ConfigClass getXMLConfig() {
		return config;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return
	 */
	protected final void performSaveAsDefault() {
		cs.storePluginTypeDefaults(getFormValues());
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Checks if this is an instance page or a type page.
	 * 
	 * @return <code>true</code> if this is a preference page for a plugin instance,
	 *         <code>false</code> if this is a preference page for an instantiable plugin type.
	 */
	public final boolean isInstancePage() {
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
	public final Plugin getPlugin() {
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
	public final Class<? extends PluginXMLConfig> getConfigClass() {
		return this.config.getClass();
	}
	
	// --------------------------------------------------------------------------------
	/**
	 */
	public void setFormValues(final ConfigClass config) {
		listenForPropertyChanges = false;
		
		getPreferenceStore().setValue(PREF_STORE_ACTIVE, config.isActive());
		getPreferenceStore().setValue(PREF_STORE_VISIBLE, config.isVisible());
		getPreferenceStore().setValue(PREF_STORE_NAME, config.getName());
		
		if (config.isAllSemanticTypes()) {
			getPreferenceStore().setValue(PREF_STORE_ALL_SEMANTIC_TYPES, true);
			this.fields.semanticTypes.setStringValue("0-255");
			this.fields.semanticTypes.setEnabled(false, fields.semanticTypesContainer);
		} else {
			final String semTypes = intArray2String(config.getSemanticTypes());
			this.fields.semanticTypes.setStringValue(semTypes);
			
		}
		
		this.fields.instanceName.setStringValue(config.getName());
		this.fields.allSemanticTypes.load();
		this.fields.isActive.load();
		this.fields.isVisible.load();
		
		this.config = config;
		
		listenForPropertyChanges = true;
	}
	
	/**
	 * Converts an array to String representing the array.
	 * 
	 * Note, that this method expects the array to be sorted!
	 */
	public static String intArray2String(final int[] a) {
		
		if (a.length == 0) {
			return "";
		} else if (a.length == 1) {
			return "" + a[0];
		}
		
		String s = "" + a[0];
		
		int iMinus = a[0];
		
		boolean list = false;
		// start at 2nd entry
		for (int i = 1; i < a.length; i++) {
			
			final int next = a[i];
			
			final boolean last = a.length == i + 1;
			
			if (iMinus == next - 1) {
				list = true;
			} else if (list) {
				s += "-" + iMinus + "," + next;
				list = false;
			} else {
				s += "," + next;
				list = false;
			}
			
			if (list && last) {
				s += "-" + next;
			}
			
			iMinus = next;
		}
		
		return s;
	}
	
	final static int MAXIMUM_SEMTYPE = 255;
	
	public static int[] intString2Array(final String s) {
		
		final String[] parts = s.split(",");
		final TreeSet<Integer> set = new TreeSet<Integer>();
		
		for (final String p : parts) {
			if (p.matches("\\d+")) {
				final int i = Integer.parseInt(p);
				if (i <= MAXIMUM_SEMTYPE) {
					set.add(i);
				}
			} else if (p.matches("\\d+-\\d+")) {
				final String[] q = p.split("-");
				final int start = Integer.parseInt(q[0]);
				final int stop = Integer.parseInt(q[1]);
				for (int j = start; j <= stop; j++) {
					if (j <= MAXIMUM_SEMTYPE) {
						set.add(j);
					}
				}
			}
		}
		
		final int[] list = new int[set.size()];
		int c = 0;
		for (final Integer integer : set) {
			list[c++] = integer;
		}
		return list;
		
	}
	
	protected Composite createComposite(final Composite parent) {
		final Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new GridLayout(1, false));
		final GridData gridData = new GridData(SWT.LEFT, SWT.TOP, true, true);
		gridData.horizontalSpan = 50;
		gridData.verticalSpan = 50;
		c.setLayoutData(gridData);
		return c;
	}
	
	protected final Group createGroup(final Composite parent, final String groupText) {
		final Group g = new Group(parent, SWT.SHADOW_ETCHED_IN);
		g.setText(groupText);
		g.setLayout(new GridLayout(1, false));
		g.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false));
		return g;
	}
}
