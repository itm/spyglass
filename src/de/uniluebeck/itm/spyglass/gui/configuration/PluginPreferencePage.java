package de.uniluebeck.itm.spyglass.gui.configuration;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.AggregateValidationStatus;
import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.IListChangeListener;
import org.eclipse.core.databinding.observable.list.ListChangeEvent;
import org.eclipse.core.databinding.observable.list.ListDiffEntry;
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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

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
	
	private static Logger log = SpyglassLogger.get(PluginPreferencePage.class);
	
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
				performApply(); // the same method as Apply
			} else if (e.getSource() == buttons.createInstanceButton) {
				performCreateInstance();
			}
		}
	};
	
	/**
	 * This listener is called whenever someone modifies a field, which is observed by databinding
	 */
	private final IChangeListener formGotDirtyListener = new IChangeListener() {
		
		@Override
		public void handleChange(final ChangeEvent event) {
			
			markFormDirty();
			
		}
	};
	
	/**
	 * This flag indicates if the page contains unsaved changes (or, correcty, has been touched in
	 * some way).
	 * 
	 * This flag will automatically be set to true if a field connected to the <code>dbc</code> are
	 * modified. Subclasses, which don't use Databinding must set this flag to true themselves when
	 * appropriate.
	 */
	private boolean formIsDirty = false;
	
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
	 * reference to the dialog
	 */
	private final PluginPreferenceDialog dialog;
	
	/**
	 * Image that is displayed in the top of the window.
	 */
	private Image image;
	
	final PropertyChangeListener propertyChangeListener;
	
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
			
			if (!this.isValid()) {
				buttons.applyButton.setEnabled(false);
			}
			
		} else {
			
			buttons.restoreDefaultsButton = createButton(parent, "Restore Defaults",
					buttonSelectionListener);
			buttons.saveAsDefaultButton = createButton(parent, "Save as Default",
					buttonSelectionListener);
			buttons.createInstanceButton = createButton(parent, "Create Instance",
					buttonSelectionListener);
			
			if (!this.isValid()) {
				buttons.saveAsDefaultButton.setEnabled(false);
			}
			if (!this.isValid()) {
				buttons.createInstanceButton.setEnabled(false);
			}
		}
		
	}
	
	protected final Realm getRealm() {
		return SWTObservables.getRealm(getControl().getDisplay());
	}
	
	@Override
	protected Composite createContents(final Composite parent) {
		return createContentsInternal(parent);
	}
	
	@Override
	public void createControl(final Composite parent) {
		super.createControl(parent);
		resetDirtyFlag();
	}
	
	protected Composite createContentsInternal(final Composite parent) {
		final Composite composite = createComposite(parent);
		
		if (this.config == null) {
			// this means that the plugin type is abstract
			return composite;
		}
		
		dbc = new DataBindingContext(getRealm());
		
		// Add a Listener to each binding, so we get informed if the user modifies a field.
		dbc.getBindings().addListChangeListener(new IListChangeListener() {
			
			@Override
			public void handleListChange(final ListChangeEvent event) {
				for (final ListDiffEntry e : event.diff.getDifferences()) {
					final Binding b = (Binding) e.getElement();
					if (e.isAddition()) {
						b.getTarget().addChangeListener(formGotDirtyListener);
					} else {
						b.getTarget().removeChangeListener(formGotDirtyListener);
					}
				}
			}
			
		});
		
		addErrorBinding();
		
		basicGroup = new BasicGroupComposite(composite, SWT.NONE);
		basicGroup.disableUnwantedElements(basicOptions);
		basicGroup.setDatabinding(dbc, config, this.plugin, this.spyglass.getPluginManager(), this
				.isInstancePage());
		
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
				
				setValid(valStatus.isOK());
				
				if (valStatus.isOK()) {
					setErrorMessage(null);
					
					if (buttons.applyButton != null) {
						buttons.applyButton.setEnabled(true);
					}
					if (buttons.createInstanceButton != null) {
						buttons.createInstanceButton.setEnabled(true);
					}
					if (buttons.saveAsDefaultButton != null) {
						buttons.saveAsDefaultButton.setEnabled(true);
					}
				} else {
					setErrorMessage(valStatus.getMessage());
					if (buttons.applyButton != null) {
						buttons.applyButton.setEnabled(false);
					}
					if (buttons.createInstanceButton != null) {
						buttons.createInstanceButton.setEnabled(false);
					}
					if (buttons.saveAsDefaultButton != null) {
						buttons.saveAsDefaultButton.setEnabled(false);
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
		return formIsDirty;
	}
	
	private void resetDirtyFlag() {
		formIsDirty = false;
		
		if (this.config == null) {
			// this means that the plugin type is abstract
			return;
		}
		
		if (this.isInstancePage()) {
			buttons.applyButton.setEnabled(false);
			buttons.restoreButton.setEnabled(false);
		} else {
			buttons.restoreDefaultsButton.setEnabled(false);
			buttons.saveAsDefaultButton.setEnabled(false);
		}
	}
	
	/**
	 * Transfers the form data into the model.
	 */
	@Override
	public final void performApply() {
		log.info("Pressed button Apply");
		if (!this.isValid()) {
			MessageDialog.openError(this.getShell(), "Can not store changes",
					"Could not store your changes. There are still errors remaining in the form.");
		} else {
			this.storeToModel();
		}
		
	}
	
	/**
	 * Store the form data into the model
	 * 
	 * Subclasses overriding this method must call this method!
	 */
	protected void storeToModel() {
		log.debug("Storing form to model");
		this.dbc.updateModels();
		this.dbc.updateTargets();
		resetDirtyFlag();
	}
	
	/**
	 * ReStore the form data from the model
	 * 
	 * Subclasses overriding this method must call this method!
	 */
	protected void loadFromModel() {
		log.debug("Restoring form from model");
		
		this.dbc.updateTargets();
		
		// update the models (with the already existent values)
		// this is necessary to (re)validate the values in case of erroneous values already existent
		// in the configuration
		this.dbc.updateModels();
		resetDirtyFlag();
	}
	
	private final void performCreateInstance() {
		log.info("Pressed button create");
		
		// First save data.
		this.performApply();
		
		if (!this.isValid()) {
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
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
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
	
	public void setImage(final Image image) {
		this.image = image;
	}
	
	@Override
	public Image getImage() {
		return image;
	}
	
	public Composite createMS2Warning(final Composite parent) {
		
		final Composite composite = createContentsInternal(parent);
		
		final GridData groupData = new GridData(SWT.TOP, SWT.LEFT, true, true);
		groupData.horizontalAlignment = GridData.FILL;
		groupData.verticalAlignment = GridData.FILL;
		final Group group = new Group(composite, SWT.NONE);
		group.setLayoutData(groupData);
		group.setLayout(new GridLayout());
		group.setText("More information");
		
		final GridData labelData = new GridData();
		labelData.verticalAlignment = SWT.TOP;
		labelData.horizontalAlignment = SWT.LEFT;
		final Label label = new Label(group, SWT.NONE);
		label.setForeground(getShell().getDisplay().getSystemColor(SWT.COLOR_RED));
		label.setText("This plugin is to be implemented for Milestone 2 and is not yet working.");
		label.setLayoutData(labelData);
		
		return parent;
		
	}
	
	/**
	 * Calling this method marks the form dirty (and thus enables the "Apply" button)
	 */
	public void markFormDirty() {
		formIsDirty = true;
		
		if ((config == null) || !isValid()) {
			// this means that the plugin type is abstractor contains errors
			return;
		}
		
		if (isInstancePage()) {
			buttons.applyButton.setEnabled(true);
			buttons.restoreButton.setEnabled(true);
		} else {
			buttons.restoreDefaultsButton.setEnabled(true);
			buttons.saveAsDefaultButton.setEnabled(true);
		}
		
	}
	
}
