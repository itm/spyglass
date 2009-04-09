// --------------------------------------------------------------------------------
/**
 *
 */
package de.uniluebeck.itm.spyglass.gui.configuration;

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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;

// --------------------------------------------------------------------------------
/**
 * This is common superclass to all preference pages in spyglass which use databinding.
 *
 *
 * @author Dariush Forouher
 *
 */
public abstract class AbstractDatabindingPreferencePage extends PreferencePage {

	private static Logger log = SpyglassLoggerFactory.getLogger(AbstractDatabindingPreferencePage.class);

	/**
	 * databinding context. may be null before createContents() is called.
	 */
	protected DataBindingContext dbc = null;

	/**
	 * This flag indicates if the page contains unsaved changes (or, correcty, has been touched in
	 * some way).
	 *
	 * This flag will automatically be set to true if a field connected to the <code>dbc</code> are
	 * modified.
	 */
	protected boolean formIsDirty = false;

	/**
	 * This listener is called whenever someone modifies a field, which is observed by databinding
	 */
	private final IChangeListener formGotDirtyListener = new IChangeListener() {

		@Override
		public void handleChange(final ChangeEvent event) {
			markFormDirty();
		}
	};

	protected final Realm getRealm() {
		return SWTObservables.getRealm(getControl().getDisplay());
	}

	// --------------------------------------------------------------------------------
	/**
	 */
	public AbstractDatabindingPreferencePage() {
		super();
	}

	// --------------------------------------------------------------------------------
	/**
	 * @param title
	 */
	public AbstractDatabindingPreferencePage(final String title) {
		super(title);
	}

	// --------------------------------------------------------------------------------
	/**
	 * @param title
	 * @param image
	 */
	public AbstractDatabindingPreferencePage(final String title, final ImageDescriptor image) {
		super(title, image);
	}

	/**
	 * Add error handling to the databindingcontext.
	 *
	 */
	protected AggregateValidationStatus addErrorBinding() {

		final AggregateValidationStatus aggregateStatus = new AggregateValidationStatus(getRealm(),
				dbc.getValidationStatusProviders(), AggregateValidationStatus.MAX_SEVERITY);

		aggregateStatus.addValueChangeListener(new DatabindingErrorHandler(dbc,getShell()));

		aggregateStatus.addValueChangeListener(new IValueChangeListener() {
			public void handleValueChange(final ValueChangeEvent event) {
				final Status valStatus = (Status) aggregateStatus.getValue();

				setValid(valStatus.isOK());

				if (valStatus.isOK()) {
					setErrorMessage(null);

				} else {
					setErrorMessage("This page contains errors.");
				}

			}
		});

		return aggregateStatus;
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
	public final boolean hasUnsavedChanges() {
		return formIsDirty;
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
		dbc.updateModels();
		checkForErrors();
		dbc.updateTargets();
		checkForErrors();
		resetDirtyFlag();
	}

	/**
	 * Checks for errors in the dbc and displays an error message for each if there are so
	 *
	 * (these errors are likely bugs in the application, but we have to display them anyway.)
	 */
	private final void checkForErrors() {
		final IStatus status = AggregateValidationStatus.getStatusMerged(dbc.getValidationStatusProviders());
		if (!status.isOK()) {
			for (final IStatus s : status.getChildren()) {
				if (!s.isOK()) {
					log.error(s.getMessage(), s.getException());
				}
			}
		}
	}

	protected abstract void resetDirtyFlag();

	/**
	 * ReStore the form data from the model
	 *
	 * Subclasses overriding this method must call this method!
	 */
	protected void loadFromModel() {
		log.debug("Restoring form from model");

		dbc.updateTargets();
		checkForErrors();

		// update the models (with the already existent values)
		// this is necessary to (re)validate the values in case of erroneous values already existent
		// in the configuration
		dbc.updateModels();
		checkForErrors();
		resetDirtyFlag();
	}

	/**
	 * Calling this method marks the form dirty (and thus enables the "Apply" button)
	 */
	public void markFormDirty() {
		formIsDirty = true;
	}

	protected Composite createContentsInternal(final Composite parent) {
		final Composite composite = createComposite(parent);

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

		return composite;
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

}