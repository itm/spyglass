package de.uniluebeck.itm.spyglass.plugin.gridpainter;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class GridPainterOptionsComposite extends Composite {
	
	private Text lowerLeftPointXText;
	
	public GridPainterOptionsComposite(final Composite parent) {
		super(parent, SWT.NONE);
		initGUI();
	}
	
	private boolean somethingChanged;
	
	private ModifyListener modifyListener = new ModifyListener() {
		@Override
		public void modifyText(final ModifyEvent e) {
			somethingChanged = true;
		}
	};
	
	private Text lowerLeftPointYText;
	
	private Text numRowsText;
	
	private Text numColsText;
	
	private Button lockGridElementsSquareCheckbox;
	
	private Text gridElementWidthText;
	
	private Text gridElementHeightText;
	
	private Button lockNumberOfRowsNColsCheckbox;
	
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
				lowerLeftPointXText.addModifyListener(modifyListener);
				
				label = new Label(group, SWT.NONE);
				label.setText("m");
				
				data = new GridData();
				data.verticalSpan = 3;
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
				lowerLeftPointYText.addModifyListener(modifyListener);
				
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
				numRowsText.addModifyListener(modifyListener);
				
				label = new Label(group, SWT.NONE);
				label.setText("");
				
				label = new Label(group, SWT.NONE);
				label.setText("# Columns");
				
				data = new GridData();
				data.widthHint = 40;
				
				numColsText = new Text(group, SWT.BORDER);
				numColsText.setLayoutData(data);
				numColsText.addModifyListener(modifyListener);
				
				label = new Label(group, SWT.NONE);
				label.setText("");
				
				lockNumberOfRowsNColsCheckbox = new Button(group, SWT.CHECK);
				lockNumberOfRowsNColsCheckbox.setText("lock");
				lockNumberOfRowsNColsCheckbox.addSelectionListener(new SelectionListener() {
					@Override
					public void widgetDefaultSelected(final SelectionEvent e) {
						widgetSelected(e);
					}
					
					@Override
					public void widgetSelected(final SelectionEvent e) {
						somethingChanged = true;
					}
				});
				
				// row width and column height
				label = new Label(group, SWT.NONE);
				label.setText("Row Width");
				
				data = new GridData();
				data.widthHint = 40;
				
				gridElementWidthText = new Text(group, SWT.BORDER);
				gridElementWidthText.setLayoutData(data);
				gridElementWidthText.addModifyListener(modifyListener);
				
				label = new Label(group, SWT.NONE);
				label.setText("m");
				
				label = new Label(group, SWT.NONE);
				label.setText("Column Height");
				
				data = new GridData();
				data.widthHint = 40;
				
				gridElementHeightText = new Text(group, SWT.BORDER);
				gridElementHeightText.setLayoutData(data);
				gridElementHeightText.addModifyListener(modifyListener);
				
				label = new Label(group, SWT.NONE);
				label.setText("m");
				
				lockGridElementsSquareCheckbox = new Button(group, SWT.CHECK);
				lockGridElementsSquareCheckbox.setText("lock");
				lockGridElementsSquareCheckbox.addSelectionListener(new SelectionListener() {
					@Override
					public void widgetDefaultSelected(final SelectionEvent e) {
						widgetSelected(e);
					}
					
					@Override
					public void widgetSelected(final SelectionEvent e) {
						somethingChanged = true;
					}
				});
				
				label = new Label(group, SWT.NONE);
				label.setText("TODO: lineColor & lineWidth");
				
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
		// config.getLineColorRGB();
		// config.getLineWidth();
		
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
