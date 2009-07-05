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

import de.uniluebeck.itm.spyglass.plugin.nodesensorrange.NodeSensorRangeXMLConfig.NodeSensorRange;
import de.uniluebeck.itm.spyglass.plugin.nodesensorrange.NodeSensorRangeXMLConfig.RectangleRange;

class RectangleDialog extends NodeRangeDialog {

	private Text height;

	private Text width;

	private Text orientation;

	public RectangleDialog(final Shell parentShell, final NodeSensorRange range) {
		super(parentShell, range);
	}

	@Override
	protected Control createDialogArea(final Composite parent) {

		final Control control = super.createDialogArea(parent);

		setTitle("Rectangle Node Range");
		setMessage("Please select the appropriate options for the rectangle.");

		GridData data;

		final Composite composite = new Composite((Composite) control, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));

		{
			data = new GridData();
			data.widthHint = 100;

			final Label label = new Label(composite, SWT.NONE);
			label.setText("Width");
			label.setLayoutData(data);

			data = new GridData();
			data.widthHint = 40;

			width = NodeSensorRangeOptionsComposite.createIntText(composite, data, new ModifyListener() {
				@Override
				public void modifyText(final ModifyEvent e) {
					final boolean empty = "".equals(width.getText());
					((RectangleRange) range).setRectangleWidth(empty ? 0 : Integer.parseInt(width.getText()));
				}
			});
			width.setText(String.valueOf(((RectangleRange) range).getRectangleWidth()));
		}
		{
			data = new GridData();
			data.widthHint = 100;

			final Label label = new Label(composite, SWT.NONE);
			label.setText("Height");
			label.setLayoutData(data);

			data = new GridData();
			data.widthHint = 40;

			height = NodeSensorRangeOptionsComposite.createIntText(composite, data, new ModifyListener() {
				@Override
				public void modifyText(final ModifyEvent e) {
					final boolean empty = "".equals(height.getText());
					((RectangleRange) range).setRectangleHeight(empty ? 0 : Integer.parseInt(height.getText()));
				}
			});
			height.setText(String.valueOf(((RectangleRange) range).getRectangleHeight()));
		}
		{
			data = new GridData();
			data.widthHint = 100;

			final Label label = new Label(composite, SWT.NONE);
			label.setText("Orientation");
			label.setLayoutData(data);

			data = new GridData();
			data.widthHint = 40;

			orientation = NodeSensorRangeOptionsComposite.createIntText(composite, data, new ModifyListener() {
				@Override
				public void modifyText(final ModifyEvent e) {
					final boolean empty = "".equals(orientation.getText());
					((RectangleRange) range).setRectangleOrientation(empty ? 0 : Integer.parseInt(orientation.getText()) % 360);
				}
			});
			orientation.setText(String.valueOf(((RectangleRange) range).getRectangleOrientation()));
		}
		return control;
	}

}