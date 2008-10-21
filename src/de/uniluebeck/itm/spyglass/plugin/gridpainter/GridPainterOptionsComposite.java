package de.uniluebeck.itm.spyglass.plugin.gridpainter;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
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

import de.uniluebeck.itm.spyglass.gui.converter.ArrayToColorConverter;
import de.uniluebeck.itm.spyglass.gui.converter.ColorToArrayConverter;

public class GridPainterOptionsComposite extends Composite {
	
	private Text lowerLeftPointXText;
	
	public GridPainterOptionsComposite(final Composite parent) {
		super(parent, SWT.NONE);
		initGUI();
	}
	
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
	
	private void initGUI() {
		
		GridData data;
		Label label;
		
		this.setLayout(new GridLayout());
		final GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		this.setLayoutData(gridData);
		
		{
			final GridData groupData = new GridData(SWT.TOP, SWT.LEFT, true, true);
			groupData.horizontalAlignment = GridData.FILL;
			groupData.verticalAlignment = GridData.FILL;
			final Group group = new Group(this, SWT.NONE);
			group.setLayoutData(groupData);
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
		
		IObservableValue observable;
		
		observable = BeansObservables.observeValue(dbc.getValidationRealm(), config,
				"gridElementHeight");
		dbc.bindValue(SWTObservables.observeText(gridElementHeightText, SWT.Modify), observable,
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT), null);
		
		observable = BeansObservables.observeValue(dbc.getValidationRealm(), config,
				"gridElementWidth");
		dbc.bindValue(SWTObservables.observeText(gridElementWidthText, SWT.Modify), observable,
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT), null);
		
		// config.getGridLowerLeftPoint();
		
		observable = BeansObservables.observeValue(dbc.getValidationRealm(), config, "lineWidth");
		dbc.bindValue(SWTObservables.observeText(this.lineWidth, SWT.Modify), observable,
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT), null);
		
		observable = BeansObservables
				.observeValue(dbc.getValidationRealm(), config, "lineColorRGB");
		dbc.bindValue(SWTObservables.observeBackground(colorExample), observable,
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT)
						.setConverter(new ColorToArrayConverter()), new UpdateValueStrategy()
						.setConverter(new ArrayToColorConverter(this.getDisplay())));
		
		observable = BeansObservables.observeValue(dbc.getValidationRealm(), config,
				"lockGridElementsSquare");
		dbc.bindValue(SWTObservables.observeSelection(lockGridElementsSquareCheckbox), observable,
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT), null);
		
		observable = BeansObservables.observeValue(dbc.getValidationRealm(), config,
				"lockNumberOfRowsNCols");
		dbc.bindValue(SWTObservables.observeSelection(lockNumberOfRowsNColsCheckbox), observable,
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT), null);
		
		observable = BeansObservables.observeValue(dbc.getValidationRealm(), config, "numCols");
		dbc.bindValue(SWTObservables.observeText(numColsText, SWT.Modify), observable,
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT), null);
		
		observable = BeansObservables.observeValue(dbc.getValidationRealm(), config, "numRows");
		dbc.bindValue(SWTObservables.observeText(numRowsText, SWT.Modify), observable,
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT), null);
		
	}
}
