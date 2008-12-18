package de.uniluebeck.itm.spyglass.plugin.gridpainter;

import org.eclipse.core.databinding.Binding;
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

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.gui.databinding.converter.ArrayToColorConverter;
import de.uniluebeck.itm.spyglass.gui.databinding.converter.ColorToArrayConverter;
import de.uniluebeck.itm.spyglass.xmlconfig.MetricsXMLConfig;

public class GridPainterOptionsComposite extends Composite {

	{
		// Register as a resource user - SWTResourceManager will
		// handle the obtaining and disposing of resources
		SWTResourceManager.registerResourceUser(this);
	}

	private Text lowerLeftPointXText;

	private Text lowerLeftPointYText;

	private Text numRowsText;

	private Text numColsText;

	private Button lockGridElementsSquareCheckbox;

	private Text gridElementWidthText;

	private Text gridElementHeightText;

	private Button lockNumberOfRowsNColsCheckbox;

	private CLabel colorExample;

	private Button lineColor;

	private Text lineWidth;

	private Group group;

	GridPainterPreferencePage page;

	private Label lowerLeftPointXUnitLabel;

	private Label lowerLeftPointYUnitLabel;

	private Label gridElementWidthUnitLabel;

	private Label gridElementHeightUnitLabel;

	private Button buttonLockGridElementsSquare;

	private GridPainterXMLConfig config;

	private Binding lockBindingElementsSquare;

	private DataBindingContext dbc;

	private Binding lockBindingNumberOfRowsNCols;

	private Button buttonLockNumberOfRowsNCols;

	public GridPainterOptionsComposite(final Composite parent) {
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
			group.setLayout(new GridLayout(8, false));
			group.setText("Grid");

			{
				{
					// 1st row
					{
						// 1st column
						// lower left point
						data = new GridData();
						data.widthHint = 120;

						label = new Label(group, SWT.NONE);
						label.setText("Lower Left Point:");
						label.setLayoutData(data);
					}
					{
						// 2nd column
						data = new GridData();
						data.widthHint = 20;

						label = new Label(group, SWT.NONE);
						label.setText("x:");
						label.setLayoutData(data);
					}
					{
						// 3rd column
						data = new GridData();
						data.widthHint = 40;

						lowerLeftPointXText = new Text(group, SWT.BORDER);
						lowerLeftPointXText.setLayoutData(data);
					}
					{
						// 4th column
						lowerLeftPointXUnitLabel = new Label(group, SWT.NONE);
						lowerLeftPointXUnitLabel.setText("");
					}
					{
						// 5th column
						data = new GridData();
						data.widthHint = 30;

						label = new Label(group, SWT.NONE);
						label.setText("");
						label.setLayoutData(data);
					}
					{
						// 6th column
						data = new GridData();
						data.widthHint = 20;

						label = new Label(group, SWT.NONE);
						label.setText("y:");
						label.setLayoutData(data);
					}
					{
						// 7th column
						data = new GridData();
						data.widthHint = 40;

						lowerLeftPointYText = new Text(group, SWT.BORDER);
						lowerLeftPointYText.setLayoutData(data);
					}
					{
						// 8th column
						lowerLeftPointYUnitLabel = new Label(group, SWT.NONE);
						lowerLeftPointYUnitLabel.setText("");
						lowerLeftPointYUnitLabel.setLayoutData(new GridData());
					}
				}
				{
					// 2nd row
					{
						// 1st column
						// rows and columns
						data = new GridData();
						data.horizontalSpan = 2;

						label = new Label(group, SWT.NONE);
						label.setText("# Rows / Columns:");
						label.setLayoutData(data);
					}
					{
						// 3rd column
						data = new GridData();
						data.widthHint = 40;
						data.horizontalSpan = 2;

						numRowsText = new Text(group, SWT.BORDER);
						numRowsText.setLayoutData(data);
					}
					{
						// 4th to 6th column
						data = new GridData();
						data.widthHint = 50;
						data.heightHint = 20;
						data.horizontalSpan = 2;

						buttonLockNumberOfRowsNCols = new Button(group, SWT.PUSH | SWT.CENTER);
						buttonLockNumberOfRowsNCols.setLayoutData(data);
						buttonLockNumberOfRowsNCols.setImage(SWTResourceManager
								.getImage("de/uniluebeck/itm/spyglass/gui/configuration/chain_small_closed_hor.png"));

						buttonLockNumberOfRowsNCols.addSelectionListener(new SelectionAdapter() {
							@Override
							public void widgetSelected(final SelectionEvent evt) {

								/*
								 * if pressed, change image/text and change binding between both
								 * text fields (add binding / remove binding
								 */
								final boolean currentlyLocked = config.getLockNumberOfRowsNCols();
								config.setLockNumberOfRowsNCols(!currentlyLocked);
								updateLockNumberOfRowsNCols();

							}

						});
					}
					{
						// 7th to 8th column
						data = new GridData();
						data.widthHint = 40;
						data.horizontalSpan = 2;

						numColsText = new Text(group, SWT.BORDER);
						numColsText.setLayoutData(data);
					}
				}
				{
					// 3rd row
					{
						// 1st column
						// row width and column height
						data = new GridData();
						data.horizontalSpan = 2;

						label = new Label(group, SWT.NONE);
						label.setText("Row Width / Column Height:");
						label.setLayoutData(data);
					}
					{
						// 2nd column
						data = new GridData();
						data.widthHint = 40;

						gridElementWidthText = new Text(group, SWT.BORDER);
						gridElementWidthText.setLayoutData(data);
					}
					{
						// 3rd column
						gridElementWidthUnitLabel = new Label(group, SWT.NONE);
						gridElementWidthUnitLabel.setLayoutData(new GridData());
					}
					{
						// 4th to 6th column
						data = new GridData();
						data.widthHint = 50;
						data.heightHint = 20;
						data.horizontalSpan = 2;

						buttonLockGridElementsSquare = new Button(group, SWT.PUSH | SWT.CENTER);
						buttonLockGridElementsSquare.setLayoutData(data);
						buttonLockGridElementsSquare.setImage(SWTResourceManager
								.getImage("de/uniluebeck/itm/spyglass/gui/configuration/chain_small_closed_hor.png"));

						buttonLockGridElementsSquare.addSelectionListener(new SelectionAdapter() {
							@Override
							public void widgetSelected(final SelectionEvent evt) {

								/*
								 * if pressed, change image/text and change binding between both
								 * text fields (add binding / remove binding
								 */
								final boolean currentlyLocked = config.getLockGridElementsSquare();
								config.setLockGridElementsSquare(!currentlyLocked);
								updateGridElementsSquare();

							}

						});
					}
					{
						// 7th column
						data = new GridData();
						data.widthHint = 40;

						gridElementHeightText = new Text(group, SWT.BORDER);
						gridElementHeightText.setLayoutData(data);
					}
					{
						// 8th column
						gridElementHeightUnitLabel = new Label(group, SWT.NONE);
						gridElementHeightUnitLabel.setLayoutData(new GridData());
					}
				}
				{
					// 4th row
					{
						// 1st column
						data = new GridData();
						data.horizontalSpan = 2;

						label = new Label(group, SWT.NONE);
						label.setText("Line Width");
						label.setLayoutData(data);
					}
					{
						// 2nd to 8th column
						data = new GridData();
						data.widthHint = 40;
						data.horizontalSpan = 6;

						lineWidth = new Text(group, SWT.BORDER);
						lineWidth.setLayoutData(data);
					}
				}
				{
					// 5th row
					{
						// 1st column
						data = new GridData();
						data.horizontalSpan = 2;

						label = new Label(group, SWT.NONE);
						label.setText("Line Color");
						label.setLayoutData(data);
					}
					{
						// 2nd column
						data = new GridData();
						data.widthHint = 50;
						data.heightHint = 19;

						colorExample = new CLabel(group, SWT.BORDER);
						colorExample.setLayoutData(data);
					}
					{
						// 3rd to 8th column
						data = new GridData();
						data.horizontalSpan = 5;

						lineColor = new Button(group, SWT.PUSH | SWT.CENTER);
						lineColor.setText("Change color");
						lineColor.setLayoutData(data);
						lineColor.addSelectionListener(new SelectionAdapter() {
							@Override
							public void widgetSelected(final SelectionEvent evt) {
								final ColorDialog dlg = new ColorDialog(getShell());
								dlg.setRGB(colorExample.getBackground().getRGB());
								final RGB color = dlg.open();
								if (color != null) {
									colorExample.setBackground(new Color(getDisplay(), color));
									page.markFormDirty();
								}
							}
						});
					}

				}

			}

		}
	}

	private void updateLockNumberOfRowsNCols() {

		final boolean locked = config.getLockNumberOfRowsNCols();

		if (locked) {

			numColsText.setText(numRowsText.getText());

			if (lockBindingNumberOfRowsNCols == null) {

				// bind the two fields together
				lockBindingNumberOfRowsNCols = dbc.bindValue(SWTObservables.observeText(numRowsText, SWT.Modify), SWTObservables.observeText(
						numColsText, SWT.Modify), null, null);
			}

			buttonLockNumberOfRowsNCols.setImage(SWTResourceManager
					.getImage("de/uniluebeck/itm/spyglass/gui/configuration/chain_small_closed_hor.png"));

		} else {
			if (lockBindingNumberOfRowsNCols != null) {
				// Kill the binding (it will be automatically removed from the dbc)
				lockBindingNumberOfRowsNCols.dispose();
				lockBindingNumberOfRowsNCols = null;
			}

			buttonLockNumberOfRowsNCols
					.setImage(SWTResourceManager.getImage("de/uniluebeck/itm/spyglass/gui/configuration/chain_small_open_hor.png"));

		}
	}

	private void updateGridElementsSquare() {

		final boolean locked = config.getLockGridElementsSquare();

		if (locked) {
			gridElementHeightText.setText(gridElementWidthText.getText());

			if (lockBindingElementsSquare == null) {

				// bind the two fields together
				lockBindingElementsSquare = dbc.bindValue(SWTObservables.observeText(gridElementWidthText, SWT.Modify), SWTObservables.observeText(
						gridElementHeightText, SWT.Modify), null, null);
			}

			buttonLockGridElementsSquare.setImage(SWTResourceManager
					.getImage("de/uniluebeck/itm/spyglass/gui/configuration/chain_small_closed_hor.png"));

		} else {
			if (lockBindingElementsSquare != null) {
				// Kill the binding (it will be automatically removed from the dbc)
				lockBindingElementsSquare.dispose();
				lockBindingElementsSquare = null;
			}

			buttonLockGridElementsSquare.setImage(SWTResourceManager
					.getImage("de/uniluebeck/itm/spyglass/gui/configuration/chain_small_open_hor.png"));

		}
	}

	public void setDatabinding(final DataBindingContext dbc, final GridPainterXMLConfig config, final GridPainterPreferencePage page,
			final Spyglass spyglass) {

		this.page = page;
		this.config = config;
		this.dbc = dbc;

		IObservableValue obsModel;
		ISWTObservableValue obsWidget;
		UpdateValueStrategy usTargetToModel;
		UpdateValueStrategy usModelToTarget;

		{
			obsWidget = SWTObservables.observeText(lowerLeftPointXText, SWT.Modify);
			obsModel = BeansObservables.observeValue(dbc.getValidationRealm(), config, GridPainterXMLConfig.PROPERTYNAME_GRID_LOWER_LEFT_POINT_X);
			usTargetToModel = new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT);
			dbc.bindValue(obsWidget, obsModel, usTargetToModel, null);
		}
		{
			obsWidget = SWTObservables.observeText(lowerLeftPointYText, SWT.Modify);
			obsModel = BeansObservables.observeValue(dbc.getValidationRealm(), config, GridPainterXMLConfig.PROPERTYNAME_GRID_LOWER_LEFT_POINT_Y);
			usTargetToModel = new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT);
			dbc.bindValue(obsWidget, obsModel, usTargetToModel, null);
		}
		{
			obsWidget = SWTObservables.observeText(gridElementHeightText, SWT.Modify);
			obsModel = BeansObservables.observeValue(dbc.getValidationRealm(), config, GridPainterXMLConfig.PROPERTYNAME_GRID_ELEMENT_HEIGHT);
			usTargetToModel = new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT);
			dbc.bindValue(obsWidget, obsModel, usTargetToModel, null);
		}
		{
			obsWidget = SWTObservables.observeText(gridElementWidthText, SWT.Modify);
			obsModel = BeansObservables.observeValue(dbc.getValidationRealm(), config, GridPainterXMLConfig.PROPERTYNAME_GRID_ELEMENT_WIDTH);
			usTargetToModel = new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT);
			dbc.bindValue(obsWidget, obsModel, usTargetToModel, null);
		}
		{
			obsWidget = SWTObservables.observeText(this.lineWidth, SWT.Modify);
			obsModel = BeansObservables.observeValue(dbc.getValidationRealm(), config, GridPainterXMLConfig.PROPERTYNAME_LINE_WIDTH);
			usTargetToModel = new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT);
			dbc.bindValue(obsWidget, obsModel, usTargetToModel, null);
		}
		{
			obsWidget = SWTObservables.observeBackground(colorExample);
			obsModel = BeansObservables.observeValue(dbc.getValidationRealm(), config, GridPainterXMLConfig.PROPERTYNAME_LINE_COLOR_R_G_B);
			usTargetToModel = new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT);
			usTargetToModel.setConverter(new ColorToArrayConverter());
			usModelToTarget = new UpdateValueStrategy();
			usModelToTarget.setConverter(new ArrayToColorConverter(this.getDisplay()));
			dbc.bindValue(obsWidget, obsModel, usTargetToModel, usModelToTarget);
		}
		{
			obsWidget = SWTObservables.observeText(numColsText, SWT.Modify);
			obsModel = BeansObservables.observeValue(dbc.getValidationRealm(), config, GridPainterXMLConfig.PROPERTYNAME_NUM_COLS);
			usTargetToModel = new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT);
			dbc.bindValue(obsWidget, obsModel, usTargetToModel, null);
		}
		{
			obsWidget = SWTObservables.observeText(numRowsText, SWT.Modify);
			obsModel = BeansObservables.observeValue(dbc.getValidationRealm(), config, GridPainterXMLConfig.PROPERTYNAME_NUM_ROWS);
			usTargetToModel = new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT);
			dbc.bindValue(obsWidget, obsModel, usTargetToModel, null);
		}

		final MetricsXMLConfig metrics = spyglass.getConfigStore().getSpyglassConfig().getGeneralSettings().getMetrics();
		{
			obsModel = BeansObservables.observeValue(dbc.getValidationRealm(), metrics, MetricsXMLConfig.PROPERTYNAME_UNIT);
			dbc.bindValue(SWTObservables.observeText(lowerLeftPointXUnitLabel), obsModel, null, null);
			dbc.bindValue(SWTObservables.observeText(lowerLeftPointYUnitLabel), obsModel, null, null);
			dbc.bindValue(SWTObservables.observeText(gridElementWidthUnitLabel), obsModel, null, null);
			dbc.bindValue(SWTObservables.observeText(gridElementHeightUnitLabel), obsModel, null, null);
		}

		updateGridElementsSquare();
		updateLockNumberOfRowsNCols();

	}
}
