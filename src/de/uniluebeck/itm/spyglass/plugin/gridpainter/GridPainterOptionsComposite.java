package de.uniluebeck.itm.spyglass.plugin.gridpainter;

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

import de.uniluebeck.itm.spyglass.gui.converter.ArrayToColorConverter;
import de.uniluebeck.itm.spyglass.gui.converter.ColorToArrayConverter;

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
				// lower left point
				data = new GridData();
				data.widthHint = 100;
				
				label = new Label(group, SWT.NONE);
				label.setText("Lower Left Point X:");
				label.setLayoutData(data);
				
				data = new GridData();
				data.widthHint = 40;
				
				lowerLeftPointXText = new Text(group, SWT.BORDER);
				lowerLeftPointXText.setLayoutData(data);
				
				label = new Label(group, SWT.NONE);
				label.setText("m");
				
				data = new GridData();
				data.verticalSpan = 4;
				data.widthHint = 30;
				
				label = new Label(group, SWT.NONE);
				label.setText("");
				label.setLayoutData(data);
				
				data = new GridData();
				data.widthHint = 100;
				
				label = new Label(group, SWT.NONE);
				label.setText("Lower Left Point Y:");
				label.setLayoutData(data);
				
				data = new GridData();
				data.widthHint = 40;
				
				lowerLeftPointYText = new Text(group, SWT.BORDER);
				lowerLeftPointYText.setLayoutData(data);
				
				label = new Label(group, SWT.NONE);
				label.setText("m");
				
				label = new Label(group, SWT.NONE);
				label.setText("");
				
				// rows and columns
				label = new Label(group, SWT.NONE);
				label.setText("# Rows");
				
				data = new GridData();
				data.widthHint = 40;
				
				numRowsText = new Text(group, SWT.BORDER);
				numRowsText.setLayoutData(data);
				
				label = new Label(group, SWT.NONE);
				label.setText("");
				
				label = new Label(group, SWT.NONE);
				label.setText("# Columns");
				
				data = new GridData();
				data.widthHint = 40;
				
				numColsText = new Text(group, SWT.BORDER);
				numColsText.setLayoutData(data);
				
				label = new Label(group, SWT.NONE);
				label.setText("");
				
				lockNumberOfRowsNColsCheckbox = new Button(group, SWT.CHECK);
				lockNumberOfRowsNColsCheckbox.setText("lock");
				
				// row width and column height
				label = new Label(group, SWT.NONE);
				label.setText("Row Width");
				
				data = new GridData();
				data.widthHint = 40;
				
				gridElementWidthText = new Text(group, SWT.BORDER);
				gridElementWidthText.setLayoutData(data);
				
				label = new Label(group, SWT.NONE);
				label.setText("m");
				
				label = new Label(group, SWT.NONE);
				label.setText("Column Height");
				
				data = new GridData();
				data.widthHint = 40;
				
				gridElementHeightText = new Text(group, SWT.BORDER);
				gridElementHeightText.setLayoutData(data);
				
				label = new Label(group, SWT.NONE);
				label.setText("m");
				
				lockGridElementsSquareCheckbox = new Button(group, SWT.CHECK);
				lockGridElementsSquareCheckbox.setText("lock");
				
				label = new Label(group, SWT.NONE);
				label.setText("Line Width");
				
				data = new GridData();
				data.widthHint = 40;
				data.horizontalSpan = 2;
				
				lineWidth = new Text(group, SWT.BORDER);
				lineWidth.setLayoutData(data);
				
				label = new Label(group, SWT.NONE);
				label.setText("Line Color");
				
				data = new GridData();
				data.widthHint = 50;
				data.heightHint = 19;
				
				colorExample = new CLabel(group, SWT.BORDER);
				colorExample.setLayoutData(data);
				
				data = new GridData();
				data.horizontalSpan = 2;
				
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
						}
					}
				});
				
			}
			
		}
	}
	
	public void setDatabinding(final DataBindingContext dbc, final GridPainterXMLConfig config) {
		
		IObservableValue obsModel;
		ISWTObservableValue obsWidget;
		UpdateValueStrategy usTargetToModel;
		UpdateValueStrategy usModelToTarget;
		
		{
			obsWidget = SWTObservables.observeText(lowerLeftPointXText, SWT.Modify);
			obsModel = BeansObservables
					.observeValue(dbc.getValidationRealm(), config, "lowerLeftX");
			usTargetToModel = new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT);
			dbc.bindValue(obsWidget, obsModel, usTargetToModel, null);
		}
		{
			obsWidget = SWTObservables.observeText(lowerLeftPointYText, SWT.Modify);
			obsModel = BeansObservables
					.observeValue(dbc.getValidationRealm(), config, "lowerLeftY");
			usTargetToModel = new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT);
			dbc.bindValue(obsWidget, obsModel, usTargetToModel, null);
		}
		{
			obsWidget = SWTObservables.observeText(gridElementHeightText, SWT.Modify);
			obsModel = BeansObservables.observeValue(dbc.getValidationRealm(), config,
					"gridElementHeight");
			usTargetToModel = new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT);
			dbc.bindValue(obsWidget, obsModel, usTargetToModel, null);
		}
		{
			obsWidget = SWTObservables.observeText(gridElementWidthText, SWT.Modify);
			obsModel = BeansObservables.observeValue(dbc.getValidationRealm(), config,
					"gridElementWidth");
			usTargetToModel = new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT);
			dbc.bindValue(obsWidget, obsModel, usTargetToModel, null);
		}
		{
			obsWidget = SWTObservables.observeText(this.lineWidth, SWT.Modify);
			obsModel = BeansObservables.observeValue(dbc.getValidationRealm(), config, "lineWidth");
			usTargetToModel = new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT);
			dbc.bindValue(obsWidget, obsModel, usTargetToModel, null);
		}
		{
			obsWidget = SWTObservables.observeBackground(colorExample);
			obsModel = BeansObservables.observeValue(dbc.getValidationRealm(), config,
					"lineColorRGB");
			usTargetToModel = new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT);
			usTargetToModel.setConverter(new ColorToArrayConverter());
			usModelToTarget = new UpdateValueStrategy();
			usModelToTarget.setConverter(new ArrayToColorConverter(this.getDisplay()));
			dbc.bindValue(obsWidget, obsModel, usTargetToModel, usModelToTarget);
		}
		{
			obsWidget = SWTObservables.observeSelection(lockGridElementsSquareCheckbox);
			obsModel = BeansObservables.observeValue(dbc.getValidationRealm(), config,
					"lockGridElementsSquare");
			usTargetToModel = new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT);
			dbc.bindValue(obsWidget, obsModel, usTargetToModel, null);
		}
		{
			obsWidget = SWTObservables.observeSelection(lockNumberOfRowsNColsCheckbox);
			obsModel = BeansObservables.observeValue(dbc.getValidationRealm(), config,
					"lockNumberOfRowsNCols");
			usTargetToModel = new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT);
			dbc.bindValue(obsWidget, obsModel, usTargetToModel, null);
		}
		{
			obsWidget = SWTObservables.observeText(numColsText, SWT.Modify);
			obsModel = BeansObservables.observeValue(dbc.getValidationRealm(), config, "numCols");
			usTargetToModel = new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT);
			dbc.bindValue(obsWidget, obsModel, usTargetToModel, null);
		}
		{
			obsWidget = SWTObservables.observeText(numRowsText, SWT.Modify);
			obsModel = BeansObservables.observeValue(dbc.getValidationRealm(), config, "numRows");
			usTargetToModel = new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT);
			dbc.bindValue(obsWidget, obsModel, usTargetToModel, null);
		}
		
	}
}
