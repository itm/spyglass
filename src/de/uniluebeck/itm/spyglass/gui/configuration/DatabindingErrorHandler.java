// --------------------------------------------------------------------------------
/**
 *
 */
package de.uniluebeck.itm.spyglass.gui.configuration;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.ValidationStatusProvider;
import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.databinding.swt.ISWTObservable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolTip;
import org.eclipse.swt.widgets.Widget;

import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;

// --------------------------------------------------------------------------------
/**
 * Displays validation errors as balloon tooltips at widgets.
 *
 * @author Dariush Forouher
 *
 */
public class DatabindingErrorHandler implements IValueChangeListener, DisposeListener, ControlListener {

	private static Logger log = SpyglassLoggerFactory.getLogger(DatabindingErrorHandler.class);

	private final Map<Control, ToolTip> tipMap = new HashMap<Control, ToolTip>();
	private final Map<ToolTip, Point> tipPos = new HashMap<ToolTip, Point>();

	private final DataBindingContext dbc;

	private final Shell shell;

	// --------------------------------------------------------------------------------
	/**
	 *
	 * @param dbc
	 * @param shell
	 */
	public DatabindingErrorHandler(final DataBindingContext dbc, final Shell shell) {
		this.dbc = dbc;
		this.shell = shell;
	}

	// --------------------------------------------------------------------------------
	/* (non-Javadoc)
	 * @see org.eclipse.core.databinding.observable.value.IValueChangeListener#handleValueChange(org.eclipse.core.databinding.observable.value.ValueChangeEvent)
	 */
	@Override
	public void handleValueChange(final ValueChangeEvent event) {

		// I found no way to find the specific widget/Binding which caused
		// this change event. Alas, we have to iterate through all of them...
		for (final Object o : dbc.getValidationStatusProviders()) {
			final ValidationStatusProvider prov = (ValidationStatusProvider) o;

			final Status status = (Status) prov.getValidationStatus().getValue();

			for (final Object o2 : prov.getTargets()) {
				final IObservable observable = (IObservable) o2;

				//
				if (observable instanceof ISWTObservable) {
					final ISWTObservable observable2 = (ISWTObservable) observable;
					final Widget w = observable2.getWidget();

					if (w instanceof Control) {
						final Control c = (Control) w;

						updateToolTip(status, c);
					}
				} else {
					if (!status.isOK()) {
						log.warn(status.getMessage(), status.getException());
					}
				}
			}

		}

	}


	private void updateToolTip(final Status status, final Control c) {
		ToolTip tip = tipMap.get(c);

		if (status.isOK() && (tip != null)) {
			log.debug("Content of widget "+c+" became good: "+status, status.getException());
			destroyToolTip(c, tip);

		} else if (!status.isOK()) {
			log.debug("Content of widget "+c+" became bad: "+status, status.getException());

			if (tip == null) {
				tip = createNewToolTip(c);
			}

			tip.setMessage(status.getMessage());
			updatePosition(c, tip);
		}
	}

	private void destroyToolTip(final Control c, final ToolTip tip) {
		tip.dispose();
		tipMap.remove(c);
		if (!c.isDisposed()) {
			c.removeDisposeListener(this);
		}
	}

	private ToolTip createNewToolTip(final Control c) {
		ToolTip tip;
		tip = new ToolTip(shell, SWT.BALLOON | SWT.ICON_ERROR);
		tipMap.put(c, tip);
		c.addDisposeListener(this);
		c.addControlListener(this);

		// register control listener to all parent composites.
		// this way we get notified if we need to move the balloon.
		Composite parent = c.getParent();
		while(parent != null) {
			parent.addControlListener(this);
			parent = parent.getParent();
		}

		tip.setText("Bad input");
		tip.setAutoHide(false);

		return tip;
	}


	// --------------------------------------------------------------------------------
	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.DisposeListener#widgetDisposed(org.eclipse.swt.events.DisposeEvent)
	 */
	@Override
	public void widgetDisposed(final DisposeEvent e) {
		final ToolTip tip = tipMap.get(e.widget);
		if (tip != null) {
			destroyToolTip((Control)e.widget, tip);
		}
	}

	// --------------------------------------------------------------------------------
	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.ControlListener#controlMoved(org.eclipse.swt.events.ControlEvent)
	 */
	@Override
	public void controlMoved(final ControlEvent e) {
		for(final Control c: tipMap.keySet()) {
			final ToolTip tip = tipMap.get(c);
			updatePosition(c, tip);
		}

	}

	private void updatePosition(final Control c, final ToolTip tip) {
		final Point newPosition = Display.getCurrent().map(c, null, c.getSize().x, c.getSize().y);
		final Point oldPosition = tipPos.get(tip);
		if (!newPosition.equals(oldPosition)) {
			log.debug("Moved tooltop for control "+c);
			tip.setLocation(newPosition);
			tipPos.put(tip, newPosition);
		}
		tip.setVisible(true);
	}

	// --------------------------------------------------------------------------------
	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.ControlListener#controlResized(org.eclipse.swt.events.ControlEvent)
	 */
	@Override
	public void controlResized(final ControlEvent e) {
		// nothing to do
	}
}
