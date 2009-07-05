/*
 * --------------------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass. Copyright (C)
 * 2004-2007 by the SwarmNet (www.swarmnet.de) project SpyGlass is free
 * software; you can redistribute it and/or modify it under the terms of the BSD
 * License. Refer to spyglass-licence.txt file in the root of the SpyGlass
 * source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.nodesensorrange;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import de.uniluebeck.itm.spyglass.plugin.nodesensorrange.NodeSensorRangeXMLConfig.ConeRange;
import de.uniluebeck.itm.spyglass.plugin.nodesensorrange.NodeSensorRangeXMLConfig.NodeSensorRange;

class ConeDialog extends NodeRangeDialog {

	private Text viewAngle;

	private Text orientation;

	private Text radius;

	// --------------------------------------------------------------------------------
	/**
	 * @param parentShell
	 * @param range
	 */
	protected ConeDialog(final Shell parentShell, final NodeSensorRange range) {
		super(parentShell, range);
	}

	@Override
	protected Control createDialogArea(final Composite parent) {
		final Control control = super.createDialogArea(parent);

		setTitle("Cone Node Range");
		setMessage("Please select the appropriate options for the cone.");

		GridData data;

		final Composite composite = new Composite((Composite) control, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));

		{
			data = new GridData();
			data.widthHint = 100;

			final Label label = new Label(composite, SWT.NONE);
			label.setText("Length");
			label.setLayoutData(data);

			data = new GridData();
			data.widthHint = 40;

			radius = NodeSensorRangeOptionsComposite.createIntText(composite, data, new ModifyListener() {
				@Override
				public void modifyText(final ModifyEvent e) {
					final boolean empty = "".equals(radius.getText());
					((ConeRange) range).setConeRadius(empty ? 0 : Integer.parseInt(radius.getText()));
				}
			});
			radius.setText(String.valueOf(((ConeRange) range).getConeRadius()));
		}

		{
			data = new GridData();

			final Label label = new Label(composite, SWT.NONE);
			label.setText("Orientation");
			label.setLayoutData(data);

			data = new GridData();
			data.widthHint = 40;

			orientation = NodeSensorRangeOptionsComposite.createIntText(composite, data, new ModifyListener() {
				@Override
				public void modifyText(final ModifyEvent e) {
					final boolean empty = "".equals(orientation.getText());
					((ConeRange) range).setConeOrientation(empty ? 0 : Integer.parseInt(orientation.getText()));
				}
			});
			orientation.setText(String.valueOf(((ConeRange) range).getConeOrientation()));
		}

		{
			data = new GridData();

			final Label label = new Label(composite, SWT.NONE);
			label.setText("View Angle");
			label.setLayoutData(data);

			data = new GridData();
			data.widthHint = 40;

			viewAngle = NodeSensorRangeOptionsComposite.createIntText(composite, data, new ModifyListener() {
				@Override
				public void modifyText(final ModifyEvent e) {
					final boolean empty = "".equals(viewAngle.getText());
					((ConeRange) range).setConeViewAngle(empty ? 0 : Integer.parseInt(viewAngle.getText()) % 360);
				}
			});
			viewAngle.setText(String.valueOf(((ConeRange) range).getConeViewAngle()));
		}

		return control;
	}
}