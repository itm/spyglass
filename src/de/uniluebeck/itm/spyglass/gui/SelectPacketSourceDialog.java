/*----------------------------------------------------------------------------------------
 * This file is part of the
 * WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de)
 * project SpyGlass is free software; you can redistribute it and/or modify it under the terms of
 * the BSD License. Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for
 * further details.
 * ---------------------------------------------------------------------------------------
 */

package de.uniluebeck.itm.spyglass.gui;

import java.beans.PropertyChangeListener;
import java.io.File;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import de.uniluebeck.itm.spyglass.core.PacketRecorder;
import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.gateway.FileReaderGateway;
import de.uniluebeck.itm.spyglass.gateway.Gateway;
import de.uniluebeck.itm.spyglass.gui.configuration.PropertyBean;
import de.uniluebeck.itm.spyglass.gui.databinding.converter.BooleanInversionConverter;
import de.uniluebeck.itm.spyglass.packet.IShellToSpyGlassPacketBroker;
import de.uniluebeck.itm.spyglass.packet.PacketReader;

// --------------------------------------------------------------------------------
/**
 * Instances of this class are dialogs which offer the user the opportunity to select the input
 * source.<br>
 * The user can actually choose between a file which has to be specified or SpyGlass which has no
 * additional parameters.
 * 
 * @author Sebastian Ebers
 * 
 */
public class SelectPacketSourceDialog extends TitleAreaDialog {

	private Button buttoniShell, buttonFile, buttonOpenFileDialog;
	private Text textPath2File;
	private Spyglass spyglass;
	private MyValues myvalues;

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 * 
	 * @param parentShell
	 *            the parent shell
	 * @param spyglass
	 *            the current {@link Spyglass} instance
	 */
	@SuppressWarnings("synthetic-access")
	public SelectPacketSourceDialog(final Shell parentShell, final Spyglass spyglass) {
		super(parentShell);
		this.spyglass = spyglass;
		myvalues = new MyValues();
	}

	// --------------------------------------------------------------------------------
	@Override
	public void create() {
		super.create();
		setTitle("Packet Source");
		setMessage("Pleas select the source of the packets which have to be evaluated.");
	}

	// --------------------------------------------------------------------------------
	@Override
	protected Control createDialogArea(final Composite parent) {
		final Composite area = new Composite(parent, SWT.NONE);
		final FillLayout thisLayout = new FillLayout(org.eclipse.swt.SWT.HORIZONTAL);
		area.setLayout(thisLayout);
		final GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessVerticalSpace = true;
		area.setLayoutData(gridData);

		final Group group1 = new Group(area, SWT.NONE);
		final GridLayout group1Layout = new GridLayout();
		group1Layout.makeColumnsEqualWidth = false;
		group1Layout.numColumns = 3;
		group1.setLayout(group1Layout);
		group1.setText("Sources");

		buttoniShell = new Button(group1, SWT.RADIO);
		buttoniShell.setText("iShell");

		new Label(group1, SWT.NONE);
		new Label(group1, SWT.NONE);

		buttonFile = new Button(group1, SWT.RADIO);
		buttonFile.setText("File");

		final Composite wrapper = new Composite(group1, SWT.BORDER);
		wrapper.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
		textPath2File = new Text(wrapper, SWT.BORDER);
		textPath2File.setEditable(false);
		final GC gc = new GC(textPath2File);
		final FontMetrics fm = gc.getFontMetrics();
		final int width = 60 * fm.getAverageCharWidth();
		final int height = fm.getHeight();
		gc.dispose();
		textPath2File.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false));
		textPath2File.setSize(textPath2File.computeSize(width, height));

		buttonOpenFileDialog = new Button(group1, SWT.NONE);
		buttonOpenFileDialog.setText("...");
		buttonOpenFileDialog.addListener(SWT.Selection, new Listener() {
			@SuppressWarnings("synthetic-access")
			public void handleEvent(final Event event) {
				final FileDialog fd = new FileDialog(Display.getCurrent().getActiveShell(), SWT.SAVE);
				fd.setFilterExtensions(new String[] { "*.rec" });
				fd.setFilterPath(new File("./record/").getAbsoluteFile().toString());
				final String path = fd.open();
				if (path != null) {
					textPath2File.setText(path);
				}
			}
		});

		// set up data-binding
		{

			myvalues.setUseIShell(!(spyglass.getPacketReader().isReadFromFile()));
			final DataBindingContext dbc = new DataBindingContext(SWTObservables.getRealm(getParentShell().getDisplay()));

			// binding of select button to use a file and text file with its path
			dbc.bindValue(SWTObservables.observeSelection(buttonFile), SWTObservables.observeEnabled(textPath2File), null, new UpdateValueStrategy(
					UpdateValueStrategy.POLICY_NEVER));

			// binding of select button to use a file and open a file selection dialog
			dbc.bindValue(SWTObservables.observeSelection(buttonFile), SWTObservables.observeEnabled(buttonOpenFileDialog), null,
					new UpdateValueStrategy(UpdateValueStrategy.POLICY_NEVER));

			// binding of select button to use a file and the select button to use iShell
			dbc.bindValue(SWTObservables.observeSelection(buttonFile), SWTObservables.observeSelection(buttoniShell), new UpdateValueStrategy(
					UpdateValueStrategy.POLICY_CONVERT).setConverter(new BooleanInversionConverter()), new UpdateValueStrategy(
					UpdateValueStrategy.POLICY_CONVERT).setConverter(new BooleanInversionConverter()));

			// bind the button to select iShell to the corresponding property
			final IObservableValue modelObservableIsIshell = BeansObservables.observeValue(dbc.getValidationRealm(), myvalues, "useIShell");
			dbc.bindValue(SWTObservables.observeSelection(buttoniShell), modelObservableIsIshell, null, null);

			// binding of the text field which contains the path to the file to the corresponding
			// string value
			final IObservableValue modelObservable1 = BeansObservables.observeValue(dbc.getValidationRealm(), myvalues, "path2File");
			dbc.bindValue(SWTObservables.observeText(textPath2File, SWT.Modify), modelObservable1, null, null);
		}
		initializeValues();
		group1.pack();
		area.pack();
		return area;
	}

	// --------------------------------------------------------------------------------
	@Override
	protected void okPressed() {
		if (!myvalues.useIShell) {
			if ((myvalues.path2File == null) || myvalues.path2File.equals("")) {
				MessageDialog.openError(getParentShell(), "Invalid file path", "The path to the file is invalid:\r\nThe new File was not set!");
			} else {
				if (setFile(myvalues.path2File)) {
					super.okPressed();
				} else {
					super.cancelPressed();
				}
			}
		} else if (myvalues.useIShell) {
			setIShell();
			super.okPressed();
		}
	}

	// --------------------------------------------------------------------------------
	/**
	 * Initializes the values of the labels etc. using the currently defined ones of the application
	 */
	private void initializeValues() {
		myvalues.setUseIShell(!(spyglass.getPacketReader().isReadFromFile()));
		if (myvalues.useIShell) {
			buttonOpenFileDialog.setEnabled(false);
			textPath2File.setEnabled(false);
		}

		buttoniShell.setSelection(myvalues.useIShell);
		buttonFile.setSelection(!myvalues.useIShell);
		final Gateway gw = spyglass.getPacketReader().getGateway();
		if ((gw != null) && (gw instanceof FileReaderGateway)) {
			if (((FileReaderGateway) gw).getFile() != null) {
				textPath2File.setText(((FileReaderGateway) gw).getFile().getPath());
			}
		}
	}

	// --------------------------------------------------------------------------------
	/**
	 * Set iShell as input source
	 */
	private void setIShell() {
		PacketReader packetReader = spyglass.getPacketReader();

		if ((packetReader == null) || !(packetReader instanceof IShellToSpyGlassPacketBroker)) {
			packetReader = new PacketRecorder();
			spyglass.setPacketReader(packetReader);
		}
		if (packetReader instanceof PacketRecorder) {
			((PacketRecorder) packetReader).setReadFromFile(false);
		}
	}

	// --------------------------------------------------------------------------------
	/**
	 * Set a file as input source
	 * 
	 * @param path
	 *            the path to the file
	 * @return <code>true</code> if the file was set successfully
	 */
	private boolean setFile(final String path) {
		PacketReader packetReader = spyglass.getPacketReader();
		boolean success = true;

		// if no packet reader is defined, create a packet recorder and make it the current packet
		// reader
		if (packetReader == null) {
			packetReader = new PacketRecorder();
			spyglass.setPacketReader(packetReader);
		}

		if (path != null) {

			// if the packet reader is actually a packet recorder, use its method to set the
			// playback file since the file will be checked for conflicts etc.
			if (packetReader instanceof PacketRecorder) {
				success = success && ((PacketRecorder) packetReader).setPlayBackFile(path);
			}

			// otherwise check if the reader's gateway is capable of processing files
			else {
				final Gateway gw = packetReader.getGateway();
				// if not, create a new FileReaderGateway and set its file
				if ((gw == null) || (!(gw instanceof FileReaderGateway))) {
					final FileReaderGateway frgw = new FileReaderGateway();
					frgw.setFile(new File(path));
					success = success && (frgw.getInputStream() != null);
				} else {
					// or just set its file
					((FileReaderGateway) gw).setFile(new File(path));
					success = success && (gw.getInputStream() != null);
				}
			}
		}
		// return whether the file was set successfully and the packet reaeder's input stream is
		// available
		return success && (packetReader.getGateway().getInputStream() != null);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Instances of this class are used as containers for two values which are used in the data
	 * binding context.<br>
	 * Values in a model have to be inside of a class which offers methods to add
	 * {@link PropertyChangeListener}s. This is why the values have to be located here since the
	 * class {@link SelectPacketSourceDialog} does not offer these methods.
	 * 
	 * @author Sebastian Ebers
	 * 
	 */
	private class MyValues extends PropertyBean {
		String path2File;
		boolean useIShell;

		// --------------------------------------------------------------------------------
		/**
		 * @return the path2File
		 */
		public String getPath2File() {
			return path2File;
		}

		// --------------------------------------------------------------------------------
		/**
		 * @param path2File
		 *            the path2File to set
		 */
		public void setPath2File(final String path2File) {
			final String oldValue = this.path2File;
			this.path2File = path2File;
			firePropertyChange("path2File", oldValue, path2File);
		}

		// --------------------------------------------------------------------------------
		/**
		 * @return the useIShell
		 */
		public boolean isUseIShell() {
			return useIShell;
		}

		// --------------------------------------------------------------------------------
		/**
		 * @param useIShell
		 *            the useIShell to set
		 */
		public void setUseIShell(final boolean useIShell) {
			final boolean oldValue = this.useIShell;
			this.useIShell = useIShell;
			firePropertyChange("useIShell", oldValue, useIShell);
		}

	}

}
