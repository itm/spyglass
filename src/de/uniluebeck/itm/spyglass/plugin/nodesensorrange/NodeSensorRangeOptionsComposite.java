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
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyEvent;
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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
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

				final boolean isCircle = NodeSensorRangeXMLConfig.PROPERTYVALUE_RANGE_TYPE_CIRCLE.equals(rangeType);
				final boolean isCone = NodeSensorRangeXMLConfig.PROPERTYVALUE_RANGE_TYPE_CONE.equals(rangeType);

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

	private static Text createIntText(final Composite composite, final GridData data, final ModifyListener modifyListener) {
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

	private abstract class NodeRangeDialog extends TitleAreaDialog {

		protected NodeRangeDialog(final Shell parentShell, final NodeSensorRange range) {
			super(parentShell);
			this.range = range;
		}

		public NodeSensorRangeXMLConfig.NodeSensorRange range;

	}

	private class ConeDialog extends NodeRangeDialog {

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

				radius = createIntText(composite, data, new ModifyListener() {
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

				orientation = createIntText(composite, data, new ModifyListener() {
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

				viewAngle = createIntText(composite, data, new ModifyListener() {
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

	private class CircleDialog extends NodeRangeDialog {

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

			textRadius = createIntText(composite, data, new ModifyListener() {
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

	private class RectangleDialog extends NodeRangeDialog {

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

				width = createIntText(composite, data, new ModifyListener() {
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

				height = createIntText(composite, data, new ModifyListener() {
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

				orientation = createIntText(composite, data, new ModifyListener() {
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
					defaultRangeType.add(NodeSensorRangeXMLConfig.PROPERTYVALUE_RANGE_TYPE_CIRCLE);
					defaultRangeType.add(NodeSensorRangeXMLConfig.PROPERTYVALUE_RANGE_TYPE_RECTANGLE);
					defaultRangeType.add(NodeSensorRangeXMLConfig.PROPERTYVALUE_RANGE_TYPE_CONE);

					data = new GridData();

					buttonOptions = new Button(groupDefaultRange, SWT.PUSH | SWT.CENTER);
					buttonOptions.setText("Options...");
					buttonOptions.setLayoutData(data);
					buttonOptions.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(final SelectionEvent e) {

							final String selectedRangeType = defaultRangeType.getText();

							final NodeRangeDialog dialog;
							final NodeSensorRangeXMLConfig.NodeSensorRange dialogConfig;

							final boolean isCircle = NodeSensorRangeXMLConfig.PROPERTYVALUE_RANGE_TYPE_CIRCLE.equals(selectedRangeType);
							final boolean isCone = NodeSensorRangeXMLConfig.PROPERTYVALUE_RANGE_TYPE_CONE.equals(selectedRangeType);
							final NodeSensorRange defaultRange = defaultConfigWrapper.getConfig().getRange();

							if (isCircle) {
								dialogConfig = defaultRange instanceof CircleRange ? defaultRange.clone() : new CircleRange();
								dialog = new CircleDialog(getShell(), dialogConfig);
							} else if (isCone) {
								dialogConfig = defaultRange instanceof ConeRange ? defaultRange.clone() : new ConeRange();
								dialog = new ConeDialog(getShell(), dialogConfig);
							} else {
								dialogConfig = defaultRange instanceof RectangleRange ? defaultRange.clone() : new RectangleRange();
								dialog = new RectangleDialog(getShell(), dialogConfig);
							}

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

	private PropertyChangeListener defaultConfigPropertyChangeListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(final PropertyChangeEvent evt) {

			// only property that can change is the default config
			// the new config is a new instance of class Config

			// unregister old data binding if it was set from old Config instance
			if (evt.getOldValue() != null) {

				dbc.removeBinding(defaultBackgroundAlphaTransparencyBinding);
				dbc.removeBinding(defaultRangeBackgroundColorBinding);
				dbc.removeBinding(defaultRangeBinding);
				dbc.removeBinding(defaultRangeForegroundColorBinding);
				dbc.removeBinding(defaultRangeTypeBinding);
				dbc.removeBinding(defaultLineWidthBinding);

			}

			IObservableValue obsModel;
			ISWTObservableValue obsWidget;
			UpdateValueStrategy usTargetToModel;
			UpdateValueStrategy usModelToTarget;
			final Realm realm = dbc.getValidationRealm();
			final Config config = (Config) evt.getNewValue();

			{
				obsWidget = SWTObservables.observeText(defaultLineWidth, SWT.Modify);
				obsModel = BeansObservables.observeValue(realm, config, NodeSensorRangeXMLConfig.PROPERTYNAME_LINE_WIDTH);
				usTargetToModel = new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT);
				usTargetToModel.setAfterConvertValidator(new IntegerRangeValidator("Line Width", 1, Integer.MAX_VALUE));
				defaultLineWidthBinding = dbc.bindValue(obsWidget, obsModel, usTargetToModel, null);
			}
			{
				obsWidget = SWTObservables.observeSelection(defaultRangeType);
				obsModel = BeansObservables.observeValue(realm, config, NodeSensorRangeXMLConfig.PROPERTYNAME_RANGE_TYPE);
				usTargetToModel = new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT);
				defaultRangeTypeBinding = dbc.bindValue(obsWidget, obsModel, usTargetToModel, null);
			}
			{
				obsWidget = SWTObservables.observeBackground(defaultRangeForegroundColor);
				obsModel = BeansObservables.observeValue(realm, config, NodeSensorRangeXMLConfig.PROPERTYNAME_COLOR_R_G_B);

				usTargetToModel = new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT);
				usTargetToModel.setConverter(new ColorToRGBConverter());
				usModelToTarget = new UpdateValueStrategy();
				usModelToTarget.setConverter(new RGBToColorConverter());
				defaultRangeForegroundColorBinding = dbc.bindValue(obsWidget, obsModel, usTargetToModel, usModelToTarget);
			}
			{
				obsWidget = SWTObservables.observeBackground(defaultRangeBackgroundColor);
				obsModel = BeansObservables.observeValue(realm, config, NodeSensorRangeXMLConfig.PROPERTYNAME_BACKGROUND_R_G_B);
				usTargetToModel = new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT);
				usTargetToModel.setConverter(new ColorToRGBConverter());
				usModelToTarget = new UpdateValueStrategy();
				usModelToTarget.setConverter(new RGBToColorConverter());
				defaultRangeBackgroundColorBinding = dbc.bindValue(obsWidget, obsModel, usTargetToModel, usModelToTarget);
			}
			{
				obsWidget = SWTObservables.observeText(defaultBackgroundAlphaTransparency, SWT.Modify);
				obsModel = BeansObservables.observeValue(realm, config, NodeSensorRangeXMLConfig.PROPERTYNAME_BACKGROUND_ALPHA);
				usTargetToModel = new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT);
				usTargetToModel.setAfterConvertValidator(new IntegerRangeValidator("Background Alpha Transparency", 0, 255));
				defaultBackgroundAlphaTransparencyBinding = dbc.bindValue(obsWidget, obsModel, usTargetToModel, null);
			}

		}
	};

	public void setDatabinding(final DataBindingContext dbc, final NodeSensorRangeXMLConfig config, final NodeSensorRangePreferencePage page) {

		this.page = page;
		this.dbc = dbc;

		// The options composite contains a clone of the original config and works on it, so that
		// the clone can later be used as the plug-in config after the user pressed apply.
		this.defaultConfigWrapper = new ConfigWrapper();
		this.defaultConfigWrapper.setConfig(config.getDefaultConfig().clone());

		defaultConfigWrapper.addPropertyChangeListener(ConfigWrapper.PROPERTYNAME_CONFIG, defaultConfigPropertyChangeListener);

		final IObservableValue obsValue;
		final IObservableValue obsModel;
		final Realm realm = dbc.getValidationRealm();

		{
			obsValue = BeansObservables.observeValue(realm, defaultConfigWrapper, ConfigWrapper.PROPERTYNAME_CONFIG);
			obsModel = BeansObservables.observeValue(realm, config, NodeSensorRangeXMLConfig.PROPERTYNAME_DEFAULT_CONFIG);
			dbc.bindValue(obsValue, obsModel, null, null);
		}

		perNodeConfigurationComposite.setDataBinding(dbc, config);

	}
}
