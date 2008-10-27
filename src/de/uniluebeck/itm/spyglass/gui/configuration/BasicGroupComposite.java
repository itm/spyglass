package de.uniluebeck.itm.spyglass.gui.configuration;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage.BasicOptions;
import de.uniluebeck.itm.spyglass.gui.databinding.converter.IntListToStringConverter;
import de.uniluebeck.itm.spyglass.gui.databinding.converter.StringToIntListConverter;
import de.uniluebeck.itm.spyglass.gui.databinding.validator.IntegerRangeValidator;
import de.uniluebeck.itm.spyglass.gui.databinding.validator.PluginNameValidator;
import de.uniluebeck.itm.spyglass.gui.databinding.validator.StringToIntListValidator;
import de.uniluebeck.itm.spyglass.plugin.Plugin;
import de.uniluebeck.itm.spyglass.plugin.PluginManager;
import de.uniluebeck.itm.spyglass.util.SpyglassLogger;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

public class BasicGroupComposite extends org.eclipse.swt.widgets.Composite {
	
	private static final Logger log = SpyglassLogger.getLogger(BasicGroupComposite.class);
	
	private Label label1;
	private Button isActive;
	private Button isVisible;
	private Button allTypes;
	private Text semanticTypes;
	private Label label2;
	private Group group1;
	private Text pluginName;
	
	/**
	 * Auto-generated main method to display this org.eclipse.swt.widgets.Composite inside a new
	 * Shell.
	 */
	public static void main(final String[] args) {
		showGUI();
	}
	
	/**
	 * Auto-generated method to display this org.eclipse.swt.widgets.Composite inside a new Shell.
	 */
	public static void showGUI() {
		final Display display = Display.getDefault();
		final Shell shell = new Shell(display);
		final BasicGroupComposite inst = new BasicGroupComposite(shell, SWT.NULL);
		final Point size = inst.getSize();
		shell.setLayout(new FillLayout());
		shell.layout();
		if ((size.x == 0) && (size.y == 0)) {
			inst.pack();
			shell.pack();
		} else {
			final Rectangle shellBounds = shell.computeTrim(0, 0, size.x, size.y);
			shell.setSize(shellBounds.width, shellBounds.height);
		}
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
	public void disableUnwantedElements(final BasicOptions basicOptions) {
		switch (basicOptions) {
			case ALL:
				semanticTypes.setEnabled(true);
				allTypes.setEnabled(true);
				isVisible.setEnabled(true);
				break;
			case ALL_BUT_VISIBLE:
				semanticTypes.setEnabled(true);
				allTypes.setEnabled(true);
				isVisible.setEnabled(false);
				break;
			case ALL_BUT_VISIBLE_AND_SEMANTIC_TYPES:
				semanticTypes.setEnabled(false);
				allTypes.setEnabled(false);
				isVisible.setEnabled(false);
				break;
			case ALL_BUT_SEMANTIC_TYPES:
				semanticTypes.setEnabled(false);
				allTypes.setEnabled(false);
				isVisible.setEnabled(true);
				break;
		}
	}
	
	public void setDatabinding(final DataBindingContext dbc, final PluginXMLConfig config,
			final Plugin owner, final PluginManager manager, final boolean isInstancePage) {
		
		// plugin name
		{
			final IObservableValue modelObservable = BeansObservables.observeValue(dbc
					.getValidationRealm(), config, "name");
			final ISWTObservableValue fieldObservableText = SWTObservables.observeText(pluginName,
					SWT.Modify);
			
			if (isInstancePage) {
				dbc.bindValue(fieldObservableText, modelObservable, new UpdateValueStrategy(
						UpdateValueStrategy.POLICY_CONVERT)
						.setAfterConvertValidator(new PluginNameValidator(manager, owner)), null);
				
			} else {
				dbc.bindValue(fieldObservableText, modelObservable, new UpdateValueStrategy(
						UpdateValueStrategy.POLICY_CONVERT), null);
				
			}
		}
		
		// semantic types
		{
			final IObservableValue modelObservable2 = BeansObservables.observeValue(dbc
					.getValidationRealm(), config, "semanticTypes");
			final UpdateValueStrategy strToModel = new UpdateValueStrategy(
					UpdateValueStrategy.POLICY_CONVERT);
			final UpdateValueStrategy strFromModel = new UpdateValueStrategy();
			strFromModel.setConverter(new IntListToStringConverter());
			strToModel.setConverter(new StringToIntListConverter());
			strToModel.setAfterConvertValidator(new IntegerRangeValidator(-1, 255));
			strToModel.setAfterGetValidator(new StringToIntListValidator());
			dbc.bindValue(SWTObservables.observeText(this.semanticTypes, SWT.Modify),
					modelObservable2, strToModel, strFromModel);
		}
		
		// all semTypes
		{
			final IObservableValue observableAllSemTypes = BeansObservables.observeValue(dbc
					.getValidationRealm(), config, "allSemanticTypes");
			final IObservableValue observableAllSemTypesCheckbox = SWTObservables
					.observeSelection(this.allTypes);
			dbc.bindValue(observableAllSemTypesCheckbox, observableAllSemTypes,
					new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT), null);
		}
		
		// is visible
		{
			final IObservableValue observableVisible = BeansObservables.observeValue(dbc
					.getValidationRealm(), config, "visible");
			dbc.bindValue(SWTObservables.observeSelection(this.isVisible), observableVisible,
					new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT), null);
		}
		
		// is active
		{
			final IObservableValue observableActive = BeansObservables.observeValue(dbc
					.getValidationRealm(), config, "active");
			
			final IObservableValue observableActiveButton = SWTObservables
					.observeSelection(this.isActive);
			dbc.bindValue(observableActiveButton, observableActive, new UpdateValueStrategy(
					UpdateValueStrategy.POLICY_CONVERT), null);
		}
		
		// disable the visibility field if plug-in is inactive
		// XXX: disabled since this produces strange errors.
		{
			// dbc.bindValue(SWTObservables.observeEnabled(this.isVisible), SWTObservables
			// .observeSelection(this.isActive), new UpdateValueStrategy(
			// UpdateValueStrategy.POLICY_NEVER), null);
		}
	}
	
	public BasicGroupComposite(final org.eclipse.swt.widgets.Composite parent, final int style) {
		super(parent, style);
		initGUI();
	}
	
	private void initGUI() {
		try {
			final FillLayout thisLayout = new FillLayout(org.eclipse.swt.SWT.HORIZONTAL);
			this.setLayout(thisLayout);
			final GridData gridData = new GridData();
			gridData.horizontalAlignment = GridData.FILL;
			gridData.grabExcessHorizontalSpace = true;
			this.setLayoutData(gridData);
			this.setSize(505, 115);
			{
				group1 = new Group(this, SWT.NONE);
				final GridLayout group1Layout = new GridLayout();
				group1Layout.makeColumnsEqualWidth = false;
				group1Layout.numColumns = 3;
				group1.setLayout(group1Layout);
				group1.setText("Basics");
				{
					label1 = new Label(group1, SWT.NONE);
					label1.setText("Plugin name:");
					final GridData label1LData = new GridData();
					label1LData.widthHint = 98;
					label1LData.heightHint = 17;
					label1LData.horizontalAlignment = GridData.END;
					label1.setLayoutData(label1LData);
				}
				{
					pluginName = new Text(group1, SWT.BORDER);
					final GridData pluginNameLData = new GridData();
					pluginNameLData.heightHint = 17;
					pluginNameLData.horizontalSpan = 2;
					pluginNameLData.grabExcessHorizontalSpace = true;
					pluginNameLData.verticalAlignment = GridData.BEGINNING;
					pluginNameLData.horizontalAlignment = GridData.FILL;
					pluginName.setLayoutData(pluginNameLData);
					
					final GridData lineWidthLData = new GridData();
					lineWidthLData.verticalSpan = 2;
				}
				{
					label2 = new Label(group1, SWT.NONE);
					label2.setText("Semantic types: ");
					final GridData label2LData = new GridData();
					label2LData.widthHint = 114;
					label2LData.heightHint = 17;
					label2.setLayoutData(label2LData);
				}
				{
					semanticTypes = new Text(group1, SWT.BORDER);
					final GridData semanticTypesLData = new GridData();
					semanticTypesLData.heightHint = 17;
					semanticTypesLData.grabExcessHorizontalSpace = true;
					semanticTypesLData.horizontalAlignment = GridData.FILL;
					semanticTypes.setLayoutData(semanticTypesLData);
					semanticTypes.addModifyListener(new ModifyListener() {
						
						@Override
						public void modifyText(final ModifyEvent e) {
							if (semanticTypes.getText().equals("-1")) {
								semanticTypes.setEnabled(false);
								allTypes.setSelection(true);
							} else {
								semanticTypes.setEnabled(true);
								allTypes.setSelection(false);
							}
							
						}
					});
					
				}
				{
					allTypes = new Button(group1, SWT.LEFT | SWT.CHECK);
					allTypes.setText("All Types");
					final GridData allTypesLData = new GridData();
					allTypesLData.widthHint = 73;
					allTypesLData.heightHint = 28;
					allTypes.setLayoutData(allTypesLData);
					allTypes.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(final SelectionEvent evt) {
							if (allTypes.getSelection()) {
								semanticTypes.setText("-1");
							} else {
								semanticTypes.setText("1");
							}
						}
					});
					
				}
				{
					isActive = new Button(group1, SWT.CHECK | SWT.LEFT);
					isActive.setText("Active");
					isActive.setBounds(7, 81, 65, 22);
					
				}
				{
					isVisible = new Button(group1, SWT.CHECK | SWT.LEFT);
					isVisible.setText("Visible");
					isVisible.setBounds(173, 81, 68, 22);
				}
			}
			this.layout();
		} catch (final Exception e) {
			log.error("Problem while building the basicGroup composite.", e);
		}
	}
	
}
