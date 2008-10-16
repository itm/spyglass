package de.uniluebeck.itm.spyglass.gui.configuration;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.AggregateValidationStatus;
import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.util.SpyglassLogger;
import de.uniluebeck.itm.spyglass.xmlconfig.GeneralSettingsXMLConfig;

public class GeneralPreferencePage extends PreferencePage {
	
	private static Logger log = SpyglassLogger.get(GeneralPreferencePage.class);
	
	/**
	 * databinding context. may be null before createContents() is called.
	 */
	protected DataBindingContext dbc = null;
	
	/**
	 * reference to the gen setting config
	 */
	private final GeneralSettingsXMLConfig config;
	
	/**
	 * This flag indicates if the page contains unsaved changes (or, correcty, has been touched in
	 * some way).
	 * 
	 * This flag will automatically be set to true if a field connected to the <code>dbc</code> are
	 * modified.
	 */
	protected boolean formIsDirty = false;
	
	public GeneralPreferencePage(final Spyglass spyglass) {
		config = spyglass.getConfigStore().getSpyglassConfig().getGeneralSettings();
		
	}
	
	@Override
	public void createControl(final Composite parent) {
		super.createControl(parent);
		getDefaultsButton().setEnabled(false);
		getApplyButton().setEnabled(false);
	}
	
	@Override
	protected Control createContents(final Composite parent) {
		final GeneralPreferencesComposite comp = new GeneralPreferencesComposite(parent, SWT.NONE);
		
		dbc = new DataBindingContext(getRealm());
		
		addErrorBinding();
		
		comp.setDatabinding(dbc, config);
		this.dbc.updateTargets();
		
		// Add a Listener to each binding, so we get informed if someone modifies something.
		for (final Object o : dbc.getBindings()) {
			final Binding b = (Binding) o;
			b.getTarget().addChangeListener(formGotDirtyListener);
		}
		
		return comp;
	}
	
	private void addErrorBinding() {
		final AggregateValidationStatus aggregateStatus = new AggregateValidationStatus(getRealm(),
				dbc.getValidationStatusProviders(), AggregateValidationStatus.MAX_SEVERITY);
		
		aggregateStatus.addValueChangeListener(new IValueChangeListener() {
			public void handleValueChange(final ValueChangeEvent event) {
				final Status valStatus = (Status) aggregateStatus.getValue();
				
				setValid(valStatus.isOK());
				
				if (valStatus.isOK()) {
					setErrorMessage(null);
					
				} else {
					setErrorMessage(valStatus.getMessage());
				}
				
			}
		});
		
	}
	
	private final Realm getRealm() {
		return SWTObservables.getRealm(getControl().getDisplay());
	}
	
	/**
	 * Transfers the form data into the model.
	 */
	@Override
	public void performApply() {
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
	private void storeToModel() {
		log.debug("Storing form to model");
		this.dbc.updateModels();
		this.dbc.updateTargets();
		resetDirtyFlag();
	}
	
	@Override
	protected void performDefaults() {
		log.info("Pressed button restore");
		loadFromModel();
		
	}
	
	private void resetDirtyFlag() {
		formIsDirty = false;
		getDefaultsButton().setEnabled(false);
		getApplyButton().setEnabled(false);
	}
	
	/**
	 * ReStore the form data from the model
	 * 
	 * Subclasses overriding this method must call this method!
	 */
	private void loadFromModel() {
		log.debug("Restoring form from model");
		
		this.dbc.updateTargets();
		
		// update the models (with the already existent values)
		// this is necessary to (re)validate the values in case of erroneous values already existent
		// in the configuration
		this.dbc.updateModels();
		
		resetDirtyFlag();
	}
	
	/**
	 * This listener is called whenever someone modifies a field, which is observed by databinding
	 */
	private final IChangeListener formGotDirtyListener = new IChangeListener() {
		
		@Override
		public void handleChange(final ChangeEvent event) {
			formIsDirty = true;
			
			if (isValid()) {
				getDefaultsButton().setEnabled(true);
				getApplyButton().setEnabled(true);
			}
		}
	};
	
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
	
}
