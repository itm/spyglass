// --------------------------------------------------------------------------------
/**
 * 
 */
package de.uniluebeck.itm.spyglass.plugin.springembedderpositioner;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

// --------------------------------------------------------------------------------
/**
 * @author olli
 * 
 */
public class SpringEmbedderPositionerOptionsComposite extends Composite {

	private Text timeout;
	private Text semType;
	private Text optimumSpringLength;
	private Text springStiffness;
	private Text repulsionFactor;
	private Text efficiencyFactor;

	public SpringEmbedderPositionerOptionsComposite(final Composite parent) {
		super(parent, SWT.NONE);
		initGui();
	}

	// --------------------------------------------------------------------------------
	/**
	 */
	private void initGui() {

		this.setLayout(new GridLayout());

		final GridData data1 = new GridData();
		data1.horizontalAlignment = GridData.FILL;
		data1.verticalAlignment = GridData.FILL;
		data1.grabExcessHorizontalSpace = true;
		// data1.grabExcessVerticalSpace = true;
		this.setLayoutData(data1);

		final GridData groupData = new GridData(SWT.TOP, SWT.LEFT, true, true);
		groupData.horizontalAlignment = GridData.FILL;
		groupData.verticalAlignment = GridData.FILL;
		groupData.verticalIndent = 10;

		// create the group "options"
		{
			final Group group1 = new Group(this, SWT.NONE);
			group1.setLayoutData(groupData);
			group1.setLayout(new GridLayout(3, false));
			group1.setText("Global options");

			// create fields of the group
			{
				final Label label1 = new Label(group1, SWT.NONE);
				label1.setText("Timeout: ");

				final GridData data2 = new GridData();
				data2.grabExcessHorizontalSpace = true;
				data2.horizontalAlignment = GridData.FILL;
				data2.horizontalSpan = 2;

				timeout = new Text(group1, SWT.BORDER);
				timeout.setLayoutData(data2);

				final GridData data3 = new GridData();
				data3.horizontalAlignment = GridData.FILL;
				data3.grabExcessHorizontalSpace = true;
				data3.horizontalSpan = 3;
				data3.verticalSpan = 5;
				data3.verticalAlignment = SWT.TOP;

				final Label label2 = new Label(group1, SWT.NONE);
				label2.setLayoutData(data3);
				label2.setText("(0 means no timeout.)");

				final GridData data4 = new GridData();
				data4.horizontalSpan = 2;

				final Label label3 = new Label(group1, SWT.NONE);
				label3.setText("Semantic type(s) for neighbourhood:");
				label3.setLayoutData(data4);

				final GridData data5 = new GridData();
				data5.grabExcessHorizontalSpace = true;
				data5.horizontalAlignment = SWT.FILL;

				semType = new Text(group1, SWT.BORDER);
				semType.setLayoutData(data5);

			}
		}

		// create the group "Algorithm specific options"
		{
			final Group group = new Group(this, SWT.NONE);
			group.setLayoutData(groupData);
			group.setLayout(new GridLayout(4, false));
			group.setText("Spring-Embedder algorithm options");

			// create fields of the group
			{
				// private Text optimumSpringLength;
				// private Text springStiffness;
				// private Text repulsionFactor;
				// private Text efficiencyFactor;

				final GridData data = new GridData();
				data.grabExcessHorizontalSpace = true;
				data.horizontalAlignment = GridData.FILL;

				final Label label1 = new Label(group, SWT.NONE);
				label1.setText("Optimum spring length:");

				optimumSpringLength = new Text(group, SWT.BORDER);
				optimumSpringLength.setLayoutData(data);

				final Label label2 = new Label(group, SWT.NONE);
				label2.setText("Spring's stiffness:");

				springStiffness = new Text(group, SWT.BORDER);
				springStiffness.setLayoutData(data);

				final Label label3 = new Label(group, SWT.NONE);
				label3.setText("Repulsion factor:");

				repulsionFactor = new Text(group, SWT.BORDER);
				repulsionFactor.setLayoutData(data);

				final Label label4 = new Label(group, SWT.NONE);
				label4.setText("Efficiency of forces:");

				efficiencyFactor = new Text(group, SWT.BORDER);
				efficiencyFactor.setLayoutData(data);

			}
		}
	}
}
