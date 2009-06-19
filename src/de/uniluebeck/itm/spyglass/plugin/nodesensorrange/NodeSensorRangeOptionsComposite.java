// --------------------------------------------------------------------------------
/**
 *
 */
package de.uniluebeck.itm.spyglass.plugin.nodesensorrange;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

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

import de.uniluebeck.itm.spyglass.gui.databinding.converter.ColorToRGBConverter;
import de.uniluebeck.itm.spyglass.gui.databinding.converter.RGBToColorConverter;
import de.uniluebeck.itm.spyglass.gui.databinding.validator.IntegerRangeValidator;
import de.uniluebeck.itm.spyglass.plugin.nodesensorrange.NodeSensorRangeXMLConfig.Config;
import de.uniluebeck.itm.spyglass.plugin.nodesensorrange.NodeSensorRangeXMLConfig.NodeSensorRange;
import de.uniluebeck.itm.spyglass.plugin.nodesensorrange.NodeSensorRangeXMLConfig.RANGE_TYPE;

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

	private Text defaultLineWidth;

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
			groupDefaultRange.setLayout(new GridLayout(4, false));
			groupDefaultRange.setText("Default range");

			{
				// elements of group "default range"
				{
					// first line
					label = new Label(groupDefaultRange, SWT.NONE);
					label.setText("Line width");

					data = new GridData();
					data.widthHint = 40;
					data.horizontalSpan = 3;

					defaultLineWidth = new Text(groupDefaultRange, SWT.BORDER);
					defaultLineWidth.setLayoutData(data);

				}
				{
					// second line
					label = new Label(groupDefaultRange, SWT.NONE);
					label.setText("Line color");

					data = new GridData();
					data.widthHint = 40;

					defaultRangeForegroundColor = new CLabel(groupDefaultRange, SWT.BORDER);
					defaultRangeForegroundColor.setLayoutData(data);

					data = new GridData();
					data.horizontalSpan = 2;

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
					label.setText("Background color");

					data = new GridData();
					data.widthHint = 40;

					defaultRangeBackgroundColor = new CLabel(groupDefaultRange, SWT.BORDER);
					defaultRangeBackgroundColor.setLayoutData(data);

					data = new GridData();
					data.horizontalSpan = 2;

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
					label.setText("Background alpha transparency");

					data = new GridData();
					data.widthHint = 40;

					defaultBackgroundAlphaTransparency = new Text(groupDefaultRange, SWT.BORDER);
					defaultBackgroundAlphaTransparency.setLayoutData(data);

					data = new GridData();
					data.horizontalSpan = 2;
					label = new Label(groupDefaultRange, SWT.NONE);
					label.setText("[0 (transparent) - 255 (opaque)]");
					label.setLayoutData(data);

				}
				{
					// fifth line
					label = new Label(groupDefaultRange, SWT.NONE);
					label.setText("Type");

					data = new GridData();
					data.widthHint = 100;
					data.horizontalSpan = 2;

					defaultRangeType = new Combo(groupDefaultRange, SWT.DROP_DOWN | SWT.READ_ONLY);
					defaultRangeType.setLayoutData(data);
					defaultRangeType.add(RANGE_TYPE.Circle.toString());
					defaultRangeType.add(RANGE_TYPE.Rectangle.toString());
					defaultRangeType.add(RANGE_TYPE.Cone.toString());

					data = new GridData();
					// data.horizontalSpan = 2;
					buttonOptions = new Button(groupDefaultRange, SWT.PUSH | SWT.CENTER);
					buttonOptions.setText("Options...");
					buttonOptions.setLayoutData(data);
					buttonOptions.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(final SelectionEvent e) {

							final String selectedRangeType = defaultRangeType.getText();

							final NodeSensorRange defaultRange = defaultConfigClone.getRange();
							// defaultConfigWrapper.getConfig().getRange();
							final NodeRangeDialog dialog = NodeRangeDialog.createDialog(getShell(), RANGE_TYPE.valueOf(selectedRangeType),
									defaultRange);

							if (Window.OK == dialog.open()) {
								// defaultConfigWrapper.getConfig().setRange(dialog.range);
								defaultConfigClone.setRange(dialog.range);
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

	private PropertyChangeListener dirtyListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(final PropertyChangeEvent evt) {
			page.markFormDirty();
		}
	};

	private Config defaultConfigClone;

	public void setDatabinding(final DataBindingContext dbc, final NodeSensorRangeXMLConfig.Config config, final NodeSensorRangePreferencePage page) {

		this.page = page;
		defaultConfigClone = config.clone();
		defaultConfigClone.addPropertyChangeListener(dirtyListener);

		IObservableValue obsModel;
		ISWTObservableValue obsWidget;
		UpdateValueStrategy usTargetToModel;
		UpdateValueStrategy usModelToTarget;
		final Realm realm = dbc.getValidationRealm();

		{
			obsWidget = SWTObservables.observeText(defaultLineWidth, SWT.Modify);
			obsModel = BeansObservables.observeValue(realm, defaultConfigClone, NodeSensorRangeXMLConfig.PROPERTYNAME_LINE_WIDTH);
			usTargetToModel = new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT);
			usTargetToModel.setAfterConvertValidator(new IntegerRangeValidator("Line Width", 1, Integer.MAX_VALUE));
			dbc.bindValue(obsWidget, obsModel, usTargetToModel, null);
		}
		{
			obsWidget = SWTObservables.observeSelection(defaultRangeType);
			obsModel = BeansObservables.observeValue(realm, defaultConfigClone, NodeSensorRangeXMLConfig.PROPERTYNAME_RANGE_TYPE);
			usTargetToModel = new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT).setConverter(new NodeSensorRangeTypeConverter(String.class,
					Enum.class));
			dbc.bindValue(obsWidget, obsModel, usTargetToModel, null);
		}
		{
			obsWidget = SWTObservables.observeBackground(defaultRangeForegroundColor);
			obsModel = BeansObservables.observeValue(realm, defaultConfigClone, NodeSensorRangeXMLConfig.PROPERTYNAME_COLOR_R_G_B);

			usTargetToModel = new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT);
			usTargetToModel.setConverter(new ColorToRGBConverter());
			usModelToTarget = new UpdateValueStrategy();
			usModelToTarget.setConverter(new RGBToColorConverter());
			dbc.bindValue(obsWidget, obsModel, usTargetToModel, usModelToTarget);
		}
		{
			obsWidget = SWTObservables.observeBackground(defaultRangeBackgroundColor);
			obsModel = BeansObservables.observeValue(realm, defaultConfigClone, NodeSensorRangeXMLConfig.PROPERTYNAME_BACKGROUND_R_G_B);
			usTargetToModel = new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT);
			usTargetToModel.setConverter(new ColorToRGBConverter());
			usModelToTarget = new UpdateValueStrategy();
			usModelToTarget.setConverter(new RGBToColorConverter());
			dbc.bindValue(obsWidget, obsModel, usTargetToModel, usModelToTarget);
		}
		{
			obsWidget = SWTObservables.observeText(defaultBackgroundAlphaTransparency, SWT.Modify);
			obsModel = BeansObservables.observeValue(realm, defaultConfigClone, NodeSensorRangeXMLConfig.PROPERTYNAME_BACKGROUND_ALPHA);
			usTargetToModel = new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT);
			usTargetToModel.setAfterConvertValidator(new IntegerRangeValidator("Background Alpha Transparency", 0, 255));
			dbc.bindValue(obsWidget, obsModel, usTargetToModel, null);
		}

		perNodeConfigurationComposite.setDataBinding(dbc, page);

	}

	@Override
	public void dispose() {
		defaultConfigClone.removePropertyChangeListener(dirtyListener);
		super.dispose();
	}

	// --------------------------------------------------------------------------------
	/**
	 * @return
	 */
	public Config getDefaultConfig() {
		return defaultConfigClone;
	}
}
