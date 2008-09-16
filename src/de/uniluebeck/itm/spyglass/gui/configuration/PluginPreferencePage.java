package de.uniluebeck.itm.spyglass.gui.configuration;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.apache.log4j.Category;
import org.eclipse.core.databinding.AggregateValidationStatus;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.databinding.swt.SWTObservables;
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

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.plugin.Plugin;
import de.uniluebeck.itm.spyglass.util.SpyglassLogger;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

// --------------------------------------------------------------------------------

/**
 * @author Daniel Bimschas, Dariush Forouher
 * 
 * @param <T>
 */
public abstract class PluginPreferencePage<PluginClass extends Plugin, ConfigClass extends PluginXMLConfig>
		extends PreferencePage {
	
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
	 * Reference to the plugin instance. may be null if PrefType==TYPE.
	 */
	protected final PluginClass plugin;
	
	/**
	 * Temporal config. it contains the current settings on the preference page, before the the user
	 * pressed "Apply".
	 * 
	 * This field is final, since databinding listens to events from this object specifically.
	 */
	protected final ConfigClass config;
	
	/**
	 * is this page representing an plugin type or instance?
	 */
	protected PrefType type;
	
	/**
	 * reference to spyglass
	 */
	protected final Spyglass spyglass;
	
	/**
	 * databindingcontext. may be null before createContents() is called.
	 */
	protected DataBindingContext dbc = null;
	
	/**
	 * are there errors present in the form?
	 */
	private boolean databindingValidationIsOK;
	
	/**
	 * reference to the dialog
	 */
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
	
	private BasicGroupComposite basicGroup;
	
	// --------------------------------------------------------------------------------
	/**
	 * Create a preference page for editing the defaultsconfiguration of an plugin type.
	 * 
	 * @param cs
	 */
	@SuppressWarnings("unchecked")
	public PluginPreferencePage(final PluginPreferenceDialog dialog, final Spyglass spyglass,
			final BasicOptions basicOptions) {
		super();
		noDefaultAndApplyButton();
		this.type = PrefType.TYPE;
		this.dialog = dialog;
		this.spyglass = spyglass;
		this.basicOptions = basicOptions;
		this.plugin = null;
		
		// propertyChangeListener not needed since it's used for
		// instances updating their labels in preference tree
		this.propertyChangeListener = null;
		
		// This is fine
		config = (ConfigClass) spyglass.getConfigStore().readPluginTypeDefaults(
				this.getPluginClass());
		if (config == null) {
			// this page represents an abstract plugin type. so no config here
		}
		
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Create a preference page for editing the configuration of an plugin instance.
	 * 
	 * @param cs
	 * @param plugin
	 */
	@SuppressWarnings("unchecked")
	public PluginPreferencePage(final PluginPreferenceDialog dialog, final Spyglass spyglass,
			final PluginClass plugin, final BasicOptions basicOptions) {
		super();
		noDefaultAndApplyButton();
		this.type = PrefType.INSTANCE;
		this.dialog = dialog;
		this.spyglass = spyglass;
		this.plugin = plugin;
		this.basicOptions = basicOptions;
		
		this.config = (ConfigClass) plugin.getXMLConfig();
		
		this.propertyChangeListener = new PropertyChangeListener() {
			@Override
			public void propertyChange(final PropertyChangeEvent evt) {
				dialog.onPluginInstancePropertyChange();
			}
		};
		
		this.config.addPropertyChangeListener("active", propertyChangeListener);
		this.config.addPropertyChangeListener("visible", propertyChangeListener);
		this.config.addPropertyChangeListener("name", propertyChangeListener);
		
	}
	
	final PropertyChangeListener propertyChangeListener;
	
	@Override
	protected void contributeButtons(final Composite parent) {
		
		if (this.config == null) {
			// this means that the plugin type is abstract
			return;
		}
		
		if (type == PrefType.INSTANCE) {
			
			buttons.deleteButton = createButton(parent, "Delete Instance", buttonSelectionListener);
			buttons.restoreButton = createButton(parent, "Restore Values", buttonSelectionListener);
			buttons.applyButton = createButton(parent, "Apply", buttonSelectionListener);
			
		} else {
			
			buttons.restoreDefaultsButton = createButton(parent, "Restore Defaults",
					buttonSelectionListener);
			buttons.saveAsDefaultButton = createButton(parent, "Save as Default",
					buttonSelectionListener);
			buttons.createInstanceButton = createButton(parent, "Create Instance",
					buttonSelectionListener);
			
		}
		
	}
	
	protected final Realm getRealm() {
		return SWTObservables.getRealm(getControl().getDisplay());
	}
	
	@Override
	protected Composite createContents(final Composite parent) {
		final Composite composite = createComposite(parent);
		
		if (this.config == null) {
			// this means that the plugin type is abstract
			return composite;
		}
		
		dbc = new DataBindingContext(getRealm());
		
		addErrorBinding();
		
		basicGroup = new BasicGroupComposite(composite, SWT.NONE);
		basicGroup.disableUnwantedElements(basicOptions);
		basicGroup.setDatabinding(dbc, config);
		basicGroup.setDatabindingPluginName(dbc, config, this.plugin, this.spyglass
				.getPluginManager());
		
		return composite;
		
	}
	
	/**
	 * Adds the handler to the ValidationStatus provider of the DataBindingCotext. Whenever the
	 * validation status changes, the handler will update the errorString displayed to the user and
	 * set a flag variable.
	 * 
	 * the handler will also grey out the apply-Button, if there are errors present.
	 * 
	 */
	private void addErrorBinding() {
		final AggregateValidationStatus aggregateStatus = new AggregateValidationStatus(getRealm(),
				dbc.getValidationStatusProviders(), AggregateValidationStatus.MAX_SEVERITY);
		
		aggregateStatus.addValueChangeListener(new IValueChangeListener() {
			public void handleValueChange(final ValueChangeEvent event) {
				final Status valStatus = (Status) aggregateStatus.getValue();
				
				databindingValidationIsOK = valStatus.isOK();
				
				if (valStatus.isOK()) {
					setErrorMessage(null);
					
					if (buttons.applyButton != null) {
						buttons.applyButton.setEnabled(true);
					}
				} else {
					setErrorMessage(valStatus.getMessage());
					if (buttons.applyButton != null) {
						buttons.applyButton.setEnabled(false);
					}
				}
			}
		});
	}
	
	private Button createButton(final Composite parent, final String label,
			final SelectionListener selectionListener) {
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
	 * Does the form contain unsaved data? The return value of this method is only an indicator, IOW
	 * it may return false-positives.
	 * 
	 * Subclasses overriding this method should include the return value of super() in their answer.
	 * 
	 * @return true if this page contains unsaved data.
	 */
	public boolean hasUnsavedChanges() {
		if (basicGroup != null) {
			return basicGroup.isSomethingChanged();
		} else {
			return false;
		}
	}
	
	/**
	 * Transfers the formular data into the model.
	 */
	@Override
	public final void performApply() {
		log.info("Pressed button Apply");
		this.storeToModel();
		spyglass.getConfigStore().store();
	}
	
	private boolean containsErrors() {
		return !this.databindingValidationIsOK;
	}
	
	/**
	 * Store the form data into the model
	 * 
	 * Subclasses overriding this method must call this method!
	 */
	protected void storeToModel() {
		log.info("Storing form to model");
		if (containsErrors()) {
			MessageDialog.openError(this.getShell(), "Can not store changes",
					"Could not store your changes. There are still errors remaining in the form.");
		} else {
			this.dbc.updateModels();
			this.dbc.updateTargets();
			this.basicGroup.resetChanged();
		}
	}
	
	/**
	 * ReStore the form data from the model
	 * 
	 * Subclasses overriding this method must call this method!
	 */
	protected void loadFromModel() {
		log.info("Restoring form from model");
		
		this.dbc.updateTargets();
		this.basicGroup.resetChanged();
	}
	
	private final void performCreateInstance() {
		log.info("Pressed button create");
		
		// First save data.
		this.performApply();
		
		if (containsErrors()) {
			MessageDialog.openError(this.getShell(), "Can not store changes",
					"Could not store your changes. There are still errors remaining in the form.");
		} else {
			spyglass.getPluginManager().createNewPlugin(getPluginClass(), config);
		}
	}
	
	/**
	 * Delete the plugin
	 */
	private final void performDelete() {
		log.info("Pressed button Delete");
		
		final boolean ok = MessageDialog.openQuestion(getShell(), "Remove plugin instance",
				"Are you sure you want to remove the plugin instance?");
		if (ok) {
			final boolean ret = spyglass.getPluginManager().removePlugin(this.plugin);
			if (!ret) {
				MessageDialog.openError(this.getShell(), "Cannot delete plugin",
						"Could not delete the plugin.");
			} else {
				spyglass.getConfigStore().store();
			}
		}
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * 
	 * @return
	 */
	protected final void performRestore() {
		log.info("Pressed button restore");
		loadFromModel();
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return
	 */
	protected final void performRestoreDefaults() {
		log.info("Pressed button restoreDefaults");
		// TODO: vorher fragen?
		final Class<? extends Plugin> pluginClass = this.getPluginClass();
		final PluginXMLConfig defaults = spyglass.getConfigStore().readPluginTypeDefaults(
				pluginClass);
		this.config.overwriteWith(defaults);
		
		this.loadFromModel();
		
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return
	 */
	protected final void performSaveAsDefault() {
		log.info("Pressed button SaveAsDefault");
		
		storeToModel();
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
	
	protected Composite createComposite(final Composite parent) {
		final Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new GridLayout(1, true));
		final GridData gridData = new GridData(SWT.LEFT, SWT.TOP, true, true);
		gridData.horizontalSpan = 80;
		gridData.verticalSpan = 80;
		c.setLayoutData(gridData);
		return c;
	}
	
	public void removePropertyChangeListeners() {
		if (config != null) {
			config.removePropertyChangeListener("active", propertyChangeListener);
			config.removePropertyChangeListener("visible", propertyChangeListener);
			config.removePropertyChangeListener("name", propertyChangeListener);
		}
	}
	
	@Override
	public void dispose() {
		removePropertyChangeListeners();
	}
	
}
