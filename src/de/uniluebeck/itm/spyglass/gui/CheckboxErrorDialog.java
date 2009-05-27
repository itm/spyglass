/* 
 * ----------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the
 * SwarmNet (www.swarmnet.de) project SpyGlass is free software;
 * you can redistribute it and/or modify it under the terms of the BSD License.
 * Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for further details.
 * ------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.gui;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

// --------------------------------------------------------------------------------
/**
 * Displays a customized {@link ErrorDialog}.<br>
 * Additionally to the ordinary functionality, a check box button is available to select if the same
 * message has to be displayed the next time it occurs.
 * 
 * @author Sebastian Ebers
 * 
 */
public class CheckboxErrorDialog extends ErrorDialog {

	private boolean dontShowAgain = false;

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 * 
	 * @param parentShell
	 *            the parent shell
	 * @param dialogTitle
	 *            the dialog's title
	 * @param message
	 *            the message to be displayed
	 * @param status
	 *            the status object
	 */
	private CheckboxErrorDialog(final Shell parentShell, final String dialogTitle, final String message, final IStatus status) {
		super(parentShell, dialogTitle, message, status, IStatus.OK | IStatus.INFO | IStatus.WARNING | IStatus.ERROR);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Sets whether the user wants the same message to be suppressed next time.<br>
	 * Note that this has to be handled outside of this dialog.
	 * 
	 * @param dontShowAgain
	 *            indicates whether the message has to be suppressed the next time it would be shown
	 *            according to the user's selection
	 */
	protected void setDontShowAgain(final boolean dontShowAgain) {
		this.dontShowAgain = dontShowAgain;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns whether the user wants the same message to be suppressed next time.<br>
	 * Note that this has to be handled outside of this dialog.
	 * 
	 * @return <code>true</code> if the message has to be suppressed the next time
	 */
	public boolean isDontShowAgain() {
		return dontShowAgain;
	}

	// --------------------------------------------------------------------------------
	@Override
	protected void createButtonsForButtonBar(final Composite parent) {
		final Button button = new Button(parent, SWT.CHECK);
		button.setText("Don't show this message again");
		button.setFont(JFaceResources.getDialogFont());
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				setDontShowAgain(button.getSelection());
			}
		});
		setButtonLayoutData(button);
		new Label(parent, SWT.NONE);

		// create OK and Details buttons
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createDetailsButton(parent);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Opens an error dialog to display the given error. Use this method if the error object being
	 * displayed does not contain child items, or if you wish to display all such items without
	 * filtering.
	 * 
	 * @param parent
	 *            the parent shell of the dialog, or <code>null</code> if none
	 * @param dialogTitle
	 *            the title to use for this dialog, or <code>null</code> to indicate that the
	 *            default title should be used
	 * @param message
	 *            the message to show in this dialog, or <code>null</code> to indicate that the
	 *            error's message should be shown as the primary message
	 * @return @return {@link CheckboxErrorDialog#isDontShowAgain()}
	 */
	public static boolean openError(final Shell parent, final String dialogTitle, final String message) {
		final CheckboxErrorDialog d = new CheckboxErrorDialog(parent, dialogTitle, message, new Status(IStatus.ERROR, "unspecified ID", 0, "", null));
		d.open();
		return d.isDontShowAgain();
	}

	// --------------------------------------------------------------------------------
	/**
	 * Opens an error dialog to display the given error. Use this method if the error object being
	 * displayed does not contain child items, or if you wish to display all such items without
	 * filtering.
	 * 
	 * @param parent
	 *            the parent shell of the dialog, or <code>null</code> if none
	 * @param dialogTitle
	 *            the title to use for this dialog, or <code>null</code> to indicate that the
	 *            default title should be used
	 * @param message
	 *            the message to show in this dialog, or <code>null</code> to indicate that the
	 *            error's message should be shown as the primary message
	 * @param t
	 *            on object of the type {@link Throwable}
	 * @return {@link CheckboxErrorDialog#isDontShowAgain()}
	 */
	public static boolean openError(final Shell parent, final String dialogTitle, final String message, final Throwable t) {

		if (t == null) {
			return openError(parent, dialogTitle, message);
		}

		final String lineSeparator = java.security.AccessController.doPrivileged(new sun.security.action.GetPropertyAction("line.separator"));

		// the tab character '\t' is not supported in this context by all operating systems (e.g.
		// Microsoft Windows)
		final String[] stacks = getStackTrace(t).replace("\t", "        ").split(lineSeparator);

		final Status[] childStatus = new Status[stacks.length];

		for (int i = 0; i < stacks.length; i++) {
			childStatus[i] = new Status(IStatus.ERROR, "not_used", 0, stacks[i], null);
		}
		final MultiStatus m = new MultiStatus("not_used", 0, childStatus, t.getMessage(), t);

		final CheckboxErrorDialog d = new CheckboxErrorDialog(parent, dialogTitle, message, m);
		d.open();
		return d.isDontShowAgain();
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns a {@link Throwable}'s stack trace as a string
	 * 
	 * @param t
	 *            the {@link Throwable} which stack trace has to be returned
	 * @return the stack trace of the given {@link Throwable}
	 * 
	 */
	public static String getStackTrace(final Throwable t) {
		final StringWriter stringWriter = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(stringWriter);
		t.printStackTrace(printWriter);
		return stringWriter.toString();
	}

}
