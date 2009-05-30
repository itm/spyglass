// --------------------------------------------------------------------------------
/**
 *
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

import de.uniluebeck.itm.spyglass.plugin.nodesensorrange.NodeSensorRangeXMLConfig.CircleRange;
import de.uniluebeck.itm.spyglass.plugin.nodesensorrange.NodeSensorRangeXMLConfig.NodeSensorRange;

class CircleDialog extends NodeRangeDialog {

	private Text textRadius;

	public CircleDialog(final Shell parentShell, final NodeSensorRange range) {
		super(parentShell, range);
	}

	@Override
	protected Control createDialogArea(final Composite parent) {

		final Control control = super.createDialogArea(parent);

		setTitle("Circle Node Range");
		setMessage("Please select the appropriate options for the circle.");

		GridData data;

		final Composite composite = new Composite((Composite) control, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));

		data = new GridData();
		data.widthHint = 100;

		final Label label = new Label(composite, SWT.NONE);
		label.setText("Radius");
		label.setLayoutData(data);

		data = new GridData();
		data.widthHint = 40;

		textRadius = NodeSensorRangeOptionsComposite.createIntText(composite, data, new ModifyListener() {
			@Override
			public void modifyText(final ModifyEvent e) {
				final boolean empty = "".equals(textRadius.getText());
				((CircleRange) range).setCircleRadius(empty ? 0 : Integer.parseInt(textRadius.getText()));
			}
		});
		textRadius.setText(String.valueOf(((CircleRange) range).getCircleRadius()));

		return control;
	}
}