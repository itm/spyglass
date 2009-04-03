// --------------------------------------------------------------------------------
/**
 * 
 */
package de.uniluebeck.itm.spyglass.plugin.springembedderpositioner;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.uniluebeck.itm.spyglass.gui.databinding.converter.IntListToStringConverter;
import de.uniluebeck.itm.spyglass.gui.databinding.converter.StringToIntListConverter;
import de.uniluebeck.itm.spyglass.gui.databinding.validator.DoubleRangeValidator;
import de.uniluebeck.itm.spyglass.gui.databinding.validator.IntegerRangeValidator;
import de.uniluebeck.itm.spyglass.gui.databinding.validator.StringToIntListValidator;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

// --------------------------------------------------------------------------------
/**
 * @author Oliver Kleine
 * 
 */
public class SpringEmbedderPositionerOptionsComposite extends Composite {

	private Text timeout;
	private Text semType;
	private Text optimumSpringLength;
	private Text springStiffness;
	private Text repulsionFactor;
	private Text efficiencyFactor;

	private DataBindingContext dbc;
	private SpringEmbedderPositionerXMLConfig config;

	public SpringEmbedderPositionerOptionsComposite(final Composite parent, final DataBindingContext dbc,
			final SpringEmbedderPositionerXMLConfig config) {
		super(parent, SWT.NONE);
		initGui();
		this.dbc = dbc;
		this.config = config;

		IObservableValue obsModel;
		ISWTObservableValue obsWidget;

		{
			obsWidget = SWTObservables.observeText(timeout, SWT.Modify);
			obsModel = BeansObservables.observeValue(dbc.getValidationRealm(), config, PluginXMLConfig.PROPERTYNAME_TIMEOUT);
			dbc.bindValue(obsWidget, obsModel, new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT)
					.setAfterConvertValidator(new IntegerRangeValidator("Timeout", 0, Integer.MAX_VALUE)), null);
		}

		{
			obsWidget = SWTObservables.observeText(optimumSpringLength, SWT.Modify);
			obsModel = BeansObservables.observeValue(dbc.getValidationRealm(), config,
					SpringEmbedderPositionerXMLConfig.PROPERTYNAME_OPTIMUM_SPRING_LENGTH);
			dbc.bindValue(obsWidget, obsModel, new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT)
					.setAfterConvertValidator(new IntegerRangeValidator("Optimum spring length", 0, Integer.MAX_VALUE)), null);

		}

		{
			obsWidget = SWTObservables.observeText(springStiffness, SWT.Modify);
			obsModel = BeansObservables.observeValue(dbc.getValidationRealm(), config,
					SpringEmbedderPositionerXMLConfig.PROPERTYNAME_SPRING_STIFFNESS);
			dbc.bindValue(obsWidget, obsModel, new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT)
					.setAfterConvertValidator(new DoubleRangeValidator("Spring's stiffness", 0, 1)), null);
		}

		{
			obsWidget = SWTObservables.observeText(repulsionFactor, SWT.Modify);
			obsModel = BeansObservables.observeValue(dbc.getValidationRealm(), config,
					SpringEmbedderPositionerXMLConfig.PROPERTYNAME_REPULSION_FACTOR);
			dbc.bindValue(obsWidget, obsModel, new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT)
					.setAfterConvertValidator(new IntegerRangeValidator("Repulsion factor", 0, Integer.MAX_VALUE)), null);
		}

		{
			obsWidget = SWTObservables.observeText(efficiencyFactor, SWT.Modify);
			obsModel = BeansObservables.observeValue(dbc.getValidationRealm(), config,
					SpringEmbedderPositionerXMLConfig.PROPERTYNAME_EFFICIENCY_FACTOR);
			dbc.bindValue(obsWidget, obsModel, new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT)
					.setAfterConvertValidator(new DoubleRangeValidator("Efficiency of forces", 0, 1)), null);
		}

		{
			obsWidget = SWTObservables.observeText(semType, SWT.Modify);
			obsModel = BeansObservables.observeValue(dbc.getValidationRealm(), config,
					SpringEmbedderPositionerXMLConfig.PROPERTYNAME_EDGE_SEMANTIC_TYPES);

			final UpdateValueStrategy strToModel = new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT);
			strToModel.setConverter(new StringToIntListConverter());
			strToModel.setAfterConvertValidator(new IntegerRangeValidator("Semantic type(s) for neighbourhood", -1, 255));
			strToModel.setAfterGetValidator(new StringToIntListValidator("Semantic type(s) for neighbourhood"));

			final UpdateValueStrategy strFromModel = new UpdateValueStrategy();
			strFromModel.setConverter(new IntListToStringConverter());

			dbc.bindValue(obsWidget, obsModel, strToModel, strFromModel);
		}

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

				// TODO: Make semtypes for neighbourhood editable
				semType.setEnabled(false);
				final GridData data6 = new GridData();
				data6.horizontalSpan = 3;
				data6.grabExcessHorizontalSpace = true;
				data6.horizontalAlignment = SWT.RIGHT;

				final Label label4 = new Label(group1, SWT.NONE);
				label4.setText("Does currently work with semantictype 9 only");
				label4.setLayoutData(data6);

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
