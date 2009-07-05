/*
 * --------------------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass. Copyright (C)
 * 2004-2007 by the SwarmNet (www.swarmnet.de) project SpyGlass is free
 * software; you can redistribute it and/or modify it under the terms of the BSD
 * License. Refer to spyglass-licence.txt file in the root of the SpyGlass
 * source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.gui.configuration;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.AggregateValidationStatus;
import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.ValidationStatusProvider;
import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.list.IListChangeListener;
import org.eclipse.core.databinding.observable.list.ListChangeEvent;
import org.eclipse.core.databinding.observable.list.ListDiffEntry;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.core.runtime.IStatus;
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
public class DatabindingErrorHandler implements IValueChangeListener, DisposeListener, ControlListener, IListChangeListener {

	private static Logger log = SpyglassLoggerFactory.getLogger(DatabindingErrorHandler.class);

	private final Map<Control, ToolTip> tipMap = new HashMap<Control, ToolTip>();
	private final Map<ToolTip, Point> tipPos = new HashMap<ToolTip, Point>();
	private final Map<ToolTip, Integer> tipStatus = new HashMap<ToolTip, Integer>();

	private final DataBindingContext dbc;

	private final Shell shell;

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 * 
	 * @param dbc
	 *            the data binding context
	 * @param aggregateStatus
	 *            class to be used to aggregate status values from a data binding context into a
	 *            single status value
	 * @param shell
	 *            the shell to be used
	 */
	public DatabindingErrorHandler(final DataBindingContext dbc, final AggregateValidationStatus aggregateStatus, final Shell shell) {
		this.dbc = dbc;
		this.shell = shell;

		// theoretically we don' have to remove these listeners as they will be automatically purged
		// when observables are disposed.
		aggregateStatus.addValueChangeListener(this);
		dbc.getBindings().addListChangeListener(this);

	}

	// --------------------------------------------------------------------------------
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.databinding.observable.value.IValueChangeListener#handleValueChange(org.
	 * eclipse.core.databinding.observable.value.ValueChangeEvent)
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

					assert w instanceof Control;
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

	// --------------------------------------------------------------------------------
	private void updateToolTip(final Status status, final Control c) {
		ToolTip tip = tipMap.get(c);

		// destroy the tip if the severity changed.
		if ((tip != null) && (tipStatus.get(tip) != status.getSeverity())) {
			log.debug("Content of widget " + c + " changed: " + status, status.getException());
			destroyToolTip(c, tip);
			tip = null;
		}

		// only display errors. others are too complicated at the moment
		if (status.getSeverity() == IStatus.ERROR) {
			log.debug("Content of widget " + c + " became bad: " + status, status.getException());

			if (tip == null) {
				tip = createNewToolTip(c, status.getSeverity());
			}
			tip.setVisible(false);
			tip.setMessage(status.getMessage());
			tip.setVisible(true);
			updatePosition(c, tip);
		}
	}

	// --------------------------------------------------------------------------------
	private void destroyToolTip(final Control c, final ToolTip tip) {
		tip.setVisible(false);
		tip.dispose();
		tipMap.remove(c);
		tipStatus.remove(tip);
		if (!c.isDisposed()) {
			c.removeDisposeListener(this);
		}
	}

	// --------------------------------------------------------------------------------
	private ToolTip createNewToolTip(final Control c, final int severity) {
		ToolTip tip;
		int flags = SWT.BALLOON;
		if (IStatus.ERROR == severity) {
			flags |= SWT.ICON_ERROR;
		}
		if (IStatus.WARNING == severity) {
			flags |= SWT.ICON_WARNING;
		}
		if (IStatus.INFO == severity) {
			flags |= SWT.ICON_INFORMATION;
		}
		tip = new ToolTip(shell, flags);
		tipMap.put(c, tip);
		tipStatus.put(tip, severity);
		c.addDisposeListener(this);
		c.addControlListener(this);

		// register control listener to all parent composites.
		// this way we get notified if we need to move the balloon.
		Composite parent = c.getParent();
		while (parent != null) {
			parent.addControlListener(this);
			parent = parent.getParent();
		}

		if (IStatus.ERROR == severity) {
			tip.setText("Error");
		}
		if (IStatus.WARNING == severity) {
			tip.setText("Warning");
		}
		if (IStatus.INFO == severity) {
			tip.setText("Info");
		}

		tip.setAutoHide(false);

		return tip;
	}

	// --------------------------------------------------------------------------------
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.swt.events.DisposeListener#widgetDisposed(org.eclipse.swt.events.DisposeEvent)
	 */
	@Override
	public void widgetDisposed(final DisposeEvent e) {
		final ToolTip tip = tipMap.get(e.widget);
		if (tip != null) {
			destroyToolTip((Control) e.widget, tip);
		}
	}

	// --------------------------------------------------------------------------------
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.swt.events.ControlListener#controlMoved(org.eclipse.swt.events.ControlEvent)
	 */
	@Override
	public void controlMoved(final ControlEvent e) {
		for (final Control c : tipMap.keySet()) {
			final ToolTip tip = tipMap.get(c);
			updatePosition(c, tip);
		}

	}

	// --------------------------------------------------------------------------------
	private void updatePosition(final Control c, final ToolTip tip) {
		final Point newPosition = Display.getCurrent().map(c, null, c.getSize().x, c.getSize().y);
		final Point oldPosition = tipPos.get(tip);
		if (!newPosition.equals(oldPosition)) {
			log.debug("Moved tooltop for control " + c);
			tip.setVisible(false);
			tip.setLocation(newPosition);
			tip.setAutoHide(false);
			tip.setVisible(true);
			tipPos.put(tip, newPosition);
		}
	}

	// --------------------------------------------------------------------------------
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.swt.events.ControlListener#controlResized(org.eclipse.swt.events.ControlEvent)
	 */
	@Override
	public void controlResized(final ControlEvent e) {
		// nothing to do
	}

	// --------------------------------------------------------------------------------
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.databinding.observable.list.IListChangeListener#handleListChange(org.eclipse
	 * .core.databinding.observable.list.ListChangeEvent)
	 */
	@Override
	public void handleListChange(final ListChangeEvent event) {

		// if a binding gets disposed while it contains a validation error, we have to notice this
		// to remove the information bubble
		for (final ListDiffEntry e : event.diff.getDifferences()) {
			if (!e.isAddition()) {
				final Binding b = (Binding) e.getElement();
				if (b.getTarget() instanceof ISWTObservable) {
					final ISWTObservable obs = (ISWTObservable) b.getTarget();
					assert obs.getWidget() instanceof Control;
					final Control c = (Control) obs.getWidget();
					if (tipMap.containsKey(c)) {
						destroyToolTip(c, tipMap.get(c));
					}
				}
			}
		}

	}
}
