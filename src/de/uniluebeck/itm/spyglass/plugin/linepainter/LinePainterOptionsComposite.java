package de.uniluebeck.itm.spyglass.plugin.linepainter;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.cloudgarden.resource.SWTResourceManager;

import de.uniluebeck.itm.spyglass.gui.databinding.StringFormatter;
import de.uniluebeck.itm.spyglass.gui.databinding.converter.ArrayToColorConverter;
import de.uniluebeck.itm.spyglass.gui.databinding.converter.ColorToArrayConverter;
import de.uniluebeck.itm.spyglass.gui.databinding.validator.IntegerRangeValidator;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

public class LinePainterOptionsComposite extends Composite {

	private Group group;
	private Text lineWidth;
	private CLabel lineColor;
	private Button buttonLineColor;
	StringFormatter stringFormatter = new StringFormatter();
	private LinePainterPreferencePage page;
	private Text ttl;

	{
		// Register as a resource user - SWTResourceManager will
		// handle the obtaining and disposing of resources
		SWTResourceManager.registerResourceUser(this);
	}

	public LinePainterOptionsComposite(final Composite parent) {
		super(parent, SWT.NONE);
		initGUI();
	}

	private void initGUI() {

		GridData data;
		Label label;

		setLayout(new GridLayout());
		data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		setLayoutData(data);

		{
			data = new GridData(SWT.TOP, SWT.LEFT, true, true);
			data.horizontalAlignment = GridData.FILL;
			data.verticalAlignment = GridData.FILL;

			group = new Group(this, SWT.NONE);
			group.setLayoutData(data);
			group.setLayout(new GridLayout(3, false));
			group.setText("Display");

			{
				// ------------------------------------
				// line width
				// ------------------------------------

				label = new Label(group, SWT.NONE);
				label.setText("Line width");

				data = new GridData();
				data.widthHint = 40;
				data.horizontalSpan = 2;

				lineWidth = new Text(group, SWT.BORDER);
				lineWidth.setLayoutData(data);

				// ------------------------------------
				// line color
				// ------------------------------------

				label = new Label(group, SWT.NONE);
				label.setText("Line color");

				data = new GridData();
				data.widthHint = 50;
				data.heightHint = 19;

				lineColor = new CLabel(group, SWT.BORDER);
				lineColor.setLayoutData(data);

				// ------------------------------------
				// line color
				// ------------------------------------

				data = new GridData();

				buttonLineColor = new Button(group, SWT.PUSH | SWT.CENTER);
				buttonLineColor.setText("Change color");
				buttonLineColor.setLayoutData(data);
				buttonLineColor.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(final SelectionEvent evt) {
						final ColorDialog dlg = new ColorDialog(getShell());
						dlg.setRGB(lineColor.getBackground().getRGB());
						final RGB color = dlg.open();
						if (color != null) {
							lineColor.setBackground(new Color(getDisplay(), color));
							page.markFormDirty();
						}
					}
				});

				// ------------------------------------
				// time to live
				// ------------------------------------

				label = new Label(group, SWT.NONE);
				label.setText("Time to live");

				data = new GridData();
				data.widthHint = 40;
				data.horizontalSpan = 2;

				ttl = new Text(group, SWT.BORDER);
				ttl.setLayoutData(data);

			}

			stringFormatter.addStringFormatterFields(group, 3);

		}

	}

	public void setDatabinding(final DataBindingContext dbc, final LinePainterXMLConfig config, final LinePainterPreferencePage page) {

		this.page = page;

		IObservableValue obsModel;
		ISWTObservableValue obsWidget;
		UpdateValueStrategy usTargetToModel;
		UpdateValueStrategy usModelToTarget;

		{
			obsWidget = SWTObservables.observeText(lineWidth, SWT.Modify);
			obsModel = BeansObservables.observeValue(dbc.getValidationRealm(), config, LinePainterXMLConfig.PROPERTYNAME_LINE_WIDTH);
			usTargetToModel = new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT);
			dbc.bindValue(obsWidget, obsModel, usTargetToModel, null);
		}
		{
			obsWidget = SWTObservables.observeBackground(lineColor);
			obsModel = BeansObservables.observeValue(dbc.getValidationRealm(), config, LinePainterXMLConfig.PROPERTYNAME_LINE_COLOR_R_G_B);
			usTargetToModel = new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT);
			usTargetToModel.setConverter(new ColorToArrayConverter());
			usModelToTarget = new UpdateValueStrategy();
			usModelToTarget.setConverter(new ArrayToColorConverter(this.getDisplay()));
			dbc.bindValue(obsWidget, obsModel, usTargetToModel, usModelToTarget);
		}
		{
			obsWidget = SWTObservables.observeText(ttl, SWT.Modify);
			obsModel = BeansObservables.observeValue(dbc.getValidationRealm(), config, PluginXMLConfig.PROPERTYNAME_TIMEOUT);
			usTargetToModel = new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT);
			usTargetToModel.setAfterConvertValidator(new IntegerRangeValidator("Time To Live", 0, Integer.MAX_VALUE));
			dbc.bindValue(obsWidget, obsModel, usTargetToModel, null);
		}

		stringFormatter.setDataBinding(dbc, config);

	}

}
