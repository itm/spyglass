// --------------------------------------------------------------------------------
/**
 *
 */
package de.uniluebeck.itm.spyglass.plugin.nodesensorrange;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import com.cloudgarden.resource.SWTResourceManager;

import de.uniluebeck.itm.spyglass.gui.configuration.PropertyBean;
import de.uniluebeck.itm.spyglass.gui.databinding.converter.ColorToRGBConverter;
import de.uniluebeck.itm.spyglass.gui.databinding.converter.RGBToColorConverter;
import de.uniluebeck.itm.spyglass.gui.databinding.validator.IntegerRangeValidator;
import de.uniluebeck.itm.spyglass.plugin.nodesensorrange.NodeSensorRangeXMLConfig.CircleRange;
import de.uniluebeck.itm.spyglass.plugin.nodesensorrange.NodeSensorRangeXMLConfig.ConeRange;
import de.uniluebeck.itm.spyglass.plugin.nodesensorrange.NodeSensorRangeXMLConfig.Config;
import de.uniluebeck.itm.spyglass.plugin.nodesensorrange.NodeSensorRangeXMLConfig.NodeSensorRange;
import de.uniluebeck.itm.spyglass.plugin.nodesensorrange.NodeSensorRangeXMLConfig.RANGE_TYPE;
import de.uniluebeck.itm.spyglass.plugin.nodesensorrange.NodeSensorRangeXMLConfig.RectangleRange;

// --------------------------------------------------------------------------------
/**
 * @author bimschas
 *
 */
public class NodeSensorRangeOptionsComposite extends Composite {

	{
		// Register as a resource user - SWTResourceManager will
		// handle the obtaining and disposing of resources
		SWTResourceManager.registerResourceUser(this);
	}

	public class ConfigWrapper extends PropertyBean implements PropertyChangeListener {

		public static final String PROPERTYNAME_CONFIG = "config";

		private Config config;

		public ConfigWrapper() {
			// nothing to do
		}

		public Config getConfig() {
			return config;
		}

		public void setConfig(final Config config) {

			// remove listener from old config instance
			if (this.config != null) {
				this.config.removePropertyChangeListener(NodeSensorRangeXMLConfig.PROPERTYNAME_RANGE_TYPE, this);
			}

			firePropertyChange(PROPERTYNAME_CONFIG, this.config, this.config = config);

			// add listener to new config instance
			this.config.addPropertyChangeListener(NodeSensorRangeXMLConfig.PROPERTYNAME_RANGE_TYPE, this);

		}

		@Override
		public void propertyChange(final PropertyChangeEvent e) {

			// if the property range type changes also change the range programmatically
			// this will have another change event as result
			if (NodeSensorRangeXMLConfig.PROPERTYNAME_RANGE_TYPE.equals(e.getPropertyName())) {

				final String rangeType = (String) e.getNewValue();

				final boolean isCircle = RANGE_TYPE.valueOf(rangeType) == RANGE_TYPE.Circle;
				final boolean isCone = RANGE_TYPE.valueOf(rangeType) == RANGE_TYPE.Cone;

				config.setRange(isCircle ? new CircleRange() : isCone ? new ConeRange() : new RectangleRange());

			} else {
				throw new RuntimeException("Unexpected case.");
			}

		}

	}

	private NodeSensorRangePreferencePage page;

	private Group groupDefaultRange;

	private Group groupPerNodeConfig;

	private CLabel defaultRangeForegroundColor;

	private Combo defaultRangeType;

	private Button buttonForegroundColor;

	private Button buttonOptions;

	private Button buttonBackgroundColor;

	private CLabel defaultRangeBackgroundColor;

	private Text defaultBackgroundAlphaTransparency;

	private ConfigWrapper defaultConfigWrapper;

	private DataBindingContext dbc;

	private Text defaultLineWidth;

	{
		// Register as a resource user - SWTResourceManager will
		// handle the obtaining and disposing of resources
		SWTResourceManager.registerResourceUser(this);
	}

	public NodeSensorRangeOptionsComposite(final Composite parent) {
		super(parent, SWT.NONE);
		initGUI();
	}

	static Text createIntText(final Composite composite, final GridData data, final ModifyListener modifyListener) {
		final Text text = new Text(composite, SWT.BORDER);
		text.setLayoutData(data);
		text.addListener(SWT.Verify, new Listener() {
			@Override
			public void handleEvent(final Event event) {
				final String boxText = text.getText();
				final String string = event.text;
				try {
					Integer.parseInt(boxText.concat(string));
				} catch (final NumberFormatException exc) {
					event.doit = false;
				}
			}
		});
		text.addModifyListener(modifyListener);
		return text;
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

			groupDefaultRange = new Group(this, SWT.NONE);
			groupDefaultRange.setLayoutData(data);
			groupDefaultRange.setLayout(new GridLayout(3, false));
			groupDefaultRange.setText("Default Range");

			{
				// elements of group "default range"
				{
					// first line
					label = new Label(groupDefaultRange, SWT.NONE);
					label.setText("Line Width");

					data = new GridData();
					data.widthHint = 40;
					data.horizontalSpan = 2;

					defaultLineWidth = new Text(groupDefaultRange, SWT.BORDER);
					defaultLineWidth.setLayoutData(data);

				}
				{
					// second line
					label = new Label(groupDefaultRange, SWT.NONE);
					label.setText("Line Color");

					data = new GridData();
					data.widthHint = 40;

					defaultRangeForegroundColor = new CLabel(groupDefaultRange, SWT.BORDER);
					defaultRangeForegroundColor.setLayoutData(data);

					data = new GridData();

					buttonForegroundColor = new Button(groupDefaultRange, SWT.PUSH | SWT.CENTER);
					buttonForegroundColor.setText("Change...");
					buttonForegroundColor.setLayoutData(data);
					buttonForegroundColor.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(final SelectionEvent evt) {
							final ColorDialog dlg = new ColorDialog(getShell());
							dlg.setRGB(defaultRangeForegroundColor.getBackground().getRGB());
							final RGB selectedColor = dlg.open();
							if (selectedColor != null) {
								defaultRangeForegroundColor.setBackground(new Color(getDisplay(), selectedColor));
								page.markFormDirty();
							}
						}
					});
				}
				{
					// third line
					label = new Label(groupDefaultRange, SWT.NONE);
					label.setText("Background Color");

					data = new GridData();
					data.widthHint = 40;

					defaultRangeBackgroundColor = new CLabel(groupDefaultRange, SWT.BORDER);
					defaultRangeBackgroundColor.setLayoutData(data);

					data = new GridData();

					buttonBackgroundColor = new Button(groupDefaultRange, SWT.PUSH | SWT.CENTER);
					buttonBackgroundColor.setText("Change...");
					buttonBackgroundColor.setLayoutData(data);
					buttonBackgroundColor.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(final SelectionEvent evt) {
							final ColorDialog dlg = new ColorDialog(getShell());
							dlg.setRGB(defaultRangeBackgroundColor.getBackground().getRGB());
							final RGB selectedColor = dlg.open();
							if (selectedColor != null) {
								defaultRangeBackgroundColor.setBackground(new Color(getDisplay(), selectedColor));
								page.markFormDirty();
							}
						}
					});
				}
				{
					// fourth line
					label = new Label(groupDefaultRange, SWT.NONE);
					label.setText("Background Alpha Transparency");

					data = new GridData();
					data.widthHint = 40;

					defaultBackgroundAlphaTransparency = new Text(groupDefaultRange, SWT.BORDER);
					defaultBackgroundAlphaTransparency.setLayoutData(data);

					label = new Label(groupDefaultRange, SWT.NONE);
					label.setText("[0 (transparent) - 255 (opaque)]");

				}
				{
					// fifth line
					label = new Label(groupDefaultRange, SWT.NONE);
					label.setText("Type");

					data = new GridData();
					data.widthHint = 100;

					defaultRangeType = new Combo(groupDefaultRange, SWT.DROP_DOWN | SWT.READ_ONLY);
					defaultRangeType.setLayoutData(data);
					defaultRangeType.add(RANGE_TYPE.Circle.toString());
					defaultRangeType.add(RANGE_TYPE.Rectangle.toString());
					defaultRangeType.add(RANGE_TYPE.Cone.toString());

					data = new GridData();

					buttonOptions = new Button(groupDefaultRange, SWT.PUSH | SWT.CENTER);
					buttonOptions.setText("Options...");
					buttonOptions.setLayoutData(data);
					buttonOptions.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(final SelectionEvent e) {

							final String selectedRangeType = defaultRangeType.getText();

							final NodeSensorRange defaultRange = defaultConfigWrapper.getConfig().getRange();
							final NodeRangeDialog dialog = NodeRangeDialog.createDialog(getShell(), RANGE_TYPE.valueOf(selectedRangeType), defaultRange);

							if (Window.OK == dialog.open()) {
								defaultConfigWrapper.getConfig().setRange(dialog.range);
							}

						}
					});
				}
				// end of group "default range"
			}

			data = new GridData(SWT.TOP, SWT.LEFT, true, true);
			data.horizontalAlignment = GridData.FILL;
			data.verticalAlignment = GridData.FILL;

			groupPerNodeConfig = new Group(this, SWT.NONE);
			groupPerNodeConfig.setLayoutData(data);
			groupPerNodeConfig.setLayout(new GridLayout(2, false));
			groupPerNodeConfig.setText("Per Node Configuration");

			perNodeConfigurationComposite.addPerNodeConfigurationTable(groupPerNodeConfig);

		}

	}

	public NodeSensorRangePerNodeConfigurationComposite getPerNodeConfigurationComposite() {
		return perNodeConfigurationComposite;
	}

	private NodeSensorRangePerNodeConfigurationComposite perNodeConfigurationComposite = new NodeSensorRangePerNodeConfigurationComposite();

	private Binding defaultRangeTypeBinding;

	private Binding defaultRangeForegroundColorBinding;

	private Binding defaultRangeBackgroundColorBinding;

	private Binding defaultBackgroundAlphaTransparencyBinding;

	private Binding defaultRangeBinding;

	private Binding defaultLineWidthBinding;


	public void setDatabinding(final DataBindingContext dbc, final NodeSensorRangeXMLConfig.Config defaultConfig, final NodeSensorRangePreferencePage page) {

		this.page = page;
		this.dbc = dbc;

		IObservableValue obsModel;
		ISWTObservableValue obsWidget;
		UpdateValueStrategy usTargetToModel;
		UpdateValueStrategy usModelToTarget;
		final Realm realm = dbc.getValidationRealm();

		{
			obsWidget = SWTObservables.observeText(defaultLineWidth, SWT.Modify);
			obsModel = BeansObservables.observeValue(realm, defaultConfig, NodeSensorRangeXMLConfig.PROPERTYNAME_LINE_WIDTH);
			usTargetToModel = new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT);
			usTargetToModel.setAfterConvertValidator(new IntegerRangeValidator("Line Width", 1, Integer.MAX_VALUE));
			defaultLineWidthBinding = dbc.bindValue(obsWidget, obsModel, usTargetToModel, null);
		}
		{
			obsWidget = SWTObservables.observeSelection(defaultRangeType);
			obsModel = BeansObservables.observeValue(realm, defaultConfig, NodeSensorRangeXMLConfig.PROPERTYNAME_RANGE_TYPE);
			usTargetToModel = new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT).setConverter(new NodeSensorRangeTypeConverter(String.class, Enum.class));
			defaultRangeTypeBinding = dbc.bindValue(obsWidget, obsModel, usTargetToModel, null);
		}
		{
			obsWidget = SWTObservables.observeBackground(defaultRangeForegroundColor);
			obsModel = BeansObservables.observeValue(realm, defaultConfig, NodeSensorRangeXMLConfig.PROPERTYNAME_COLOR_R_G_B);

			usTargetToModel = new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT);
			usTargetToModel.setConverter(new ColorToRGBConverter());
			usModelToTarget = new UpdateValueStrategy();
			usModelToTarget.setConverter(new RGBToColorConverter());
			defaultRangeForegroundColorBinding = dbc.bindValue(obsWidget, obsModel, usTargetToModel, usModelToTarget);
		}
		{
			obsWidget = SWTObservables.observeBackground(defaultRangeBackgroundColor);
			obsModel = BeansObservables.observeValue(realm, defaultConfig, NodeSensorRangeXMLConfig.PROPERTYNAME_BACKGROUND_R_G_B);
			usTargetToModel = new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT);
			usTargetToModel.setConverter(new ColorToRGBConverter());
			usModelToTarget = new UpdateValueStrategy();
			usModelToTarget.setConverter(new RGBToColorConverter());
			defaultRangeBackgroundColorBinding = dbc.bindValue(obsWidget, obsModel, usTargetToModel, usModelToTarget);
		}
		{
			obsWidget = SWTObservables.observeText(defaultBackgroundAlphaTransparency, SWT.Modify);
			obsModel = BeansObservables.observeValue(realm, defaultConfig, NodeSensorRangeXMLConfig.PROPERTYNAME_BACKGROUND_ALPHA);
			usTargetToModel = new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT);
			usTargetToModel.setAfterConvertValidator(new IntegerRangeValidator("Background Alpha Transparency", 0, 255));
			defaultBackgroundAlphaTransparencyBinding = dbc.bindValue(obsWidget, obsModel, usTargetToModel, null);
		}

		perNodeConfigurationComposite.setDataBinding(dbc, page);

	}
}
