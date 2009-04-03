package de.uniluebeck.itm.spyglass.gui.configuration;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
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

import com.cloudgarden.resource.SWTResourceManager;

import de.uniluebeck.itm.spyglass.gui.databinding.validator.StringRegExValidator;
import de.uniluebeck.itm.spyglass.xmlconfig.GeneralSettingsXMLConfig;

public class GeneralPreferencesComposite extends org.eclipse.swt.widgets.Composite {

	{
		// Register as a resource user - SWTResourceManager will
		// handle the obtaining and disposing of resources
		SWTResourceManager.registerResourceUser(this);
	}

	private Group group1;
	private Label label1;
	private Label label11length;
	private Label label13;
	private Text unitTime;
	private Group group3;
	private Label label12;
	private Text scaleTime;
	private Text offsetY;
	private Label label10;
	private Label label9length;
	private Label label8;
	private Text offsetX;
	private Label label7;
	private Button linkButton;
	private Text scaleY;
	private Label label5;
	private Text scaleX;
	private Label label3;
	private Label label2;
	private Text unitLength;
	private Button showRuler;
	private Group group2;

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
		final GeneralPreferencesComposite inst = new GeneralPreferencesComposite(shell, SWT.NULL);
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

	public GeneralPreferencesComposite(final org.eclipse.swt.widgets.Composite parent, final int style) {
		super(parent, style);
		initGUI();
	}

	private void initGUI() {
		try {
			final GridLayout thisLayout = new GridLayout(1, true);
			thisLayout.makeColumnsEqualWidth = true;
			this.setLayout(thisLayout);
			this.setSize(330, 415);
			{
				group1 = new Group(this, SWT.NONE);
				final GridLayout group1Layout = new GridLayout();
				group1Layout.makeColumnsEqualWidth = true;
				group1.setLayout(group1Layout);
				final GridData group1LData = new GridData();
				group1LData.heightHint = 36;
				group1LData.horizontalAlignment = GridData.FILL;
				group1LData.widthHint = 504;
				group1LData.grabExcessHorizontalSpace = true;
				group1.setLayoutData(group1LData);
				group1.setText("Ruler");
				{
					showRuler = new Button(group1, SWT.CHECK | SWT.LEFT);
					final GridData showRulerLData = new GridData();
					showRulerLData.verticalAlignment = GridData.BEGINNING;
					showRulerLData.horizontalAlignment = GridData.BEGINNING;
					showRulerLData.grabExcessHorizontalSpace = true;
					showRuler.setLayoutData(showRulerLData);
					showRuler.setText("Show rulers");
				}
			}
			{
				group2 = new Group(this, SWT.NONE);
				final GridLayout group2Layout = new GridLayout();
				group2Layout.numColumns = 3;
				group2.setLayout(group2Layout);
				final GridData group2LData = new GridData();
				group2LData.heightHint = 220;
				group2LData.grabExcessHorizontalSpace = true;
				group2LData.horizontalAlignment = GridData.FILL;
				group2.setLayoutData(group2LData);
				group2.setText("Metric of space");
				{
					label1 = new Label(group2, SWT.NONE);
					final GridData label1LData = new GridData();
					label1LData.horizontalAlignment = GridData.FILL;
					final GridData label1LData1 = new GridData();
					label1LData1.verticalAlignment = GridData.BEGINNING;
					label1LData1.horizontalAlignment = GridData.BEGINNING;
					label1.setLayoutData(label1LData1);
					label1.setText("Unit of length: ");
				}
				{
					final GridData text1LData = new GridData();
					text1LData.widthHint = 65;
					text1LData.heightHint = 17;
					text1LData.verticalAlignment = GridData.BEGINNING;
					text1LData.horizontalAlignment = GridData.BEGINNING;
					unitLength = new Text(group2, SWT.BORDER);
					unitLength.setText("m");
					unitLength.setLayoutData(text1LData);
					unitLength.setTextLimit(5);
				}
				{
					label2 = new Label(group2, SWT.NONE);
					final GridData text2LData = new GridData();
					text2LData.horizontalSpan = 3;
					text2LData.horizontalAlignment = GridData.BEGINNING;
					text2LData.verticalAlignment = GridData.BEGINNING;
					text2LData.grabExcessHorizontalSpace = true;
					label2.setLayoutData(text2LData);
					label2.setText("Scale factor");
					label2.setFont(SWTResourceManager.getFont("Sans", 10, 1, false, false));
				}
				{
					label3 = new Label(group2, SWT.NONE);
					label3.setText("X axis: ");
					final GridData label3LData = new GridData();
					label3LData.verticalAlignment = GridData.BEGINNING;
					label3LData.horizontalAlignment = GridData.BEGINNING;
					label3LData.widthHint = 51;
					label3LData.heightHint = 17;
					label3.setLayoutData(label3LData);
				}
				{
					scaleX = new Text(group2, SWT.BORDER);
					final GridData scaleXLData = new GridData();
					scaleXLData.verticalAlignment = GridData.BEGINNING;
					scaleXLData.horizontalAlignment = GridData.BEGINNING;
					scaleXLData.widthHint = 65;
					scaleXLData.heightHint = 17;
					scaleX.setLayoutData(scaleXLData);
					scaleX.setText("1");
				}
				{
					linkButton = new Button(group2, SWT.PUSH | SWT.FLAT);
					final GridData linkButtonLData = new GridData();
					linkButtonLData.verticalAlignment = GridData.BEGINNING;
					linkButtonLData.horizontalAlignment = GridData.BEGINNING;
					linkButtonLData.verticalSpan = 2;
					linkButtonLData.widthHint = 22;
					linkButtonLData.heightHint = 56;
					linkButton.setLayoutData(linkButtonLData);
					linkButton.setImage(SWTResourceManager.getImage("de/uniluebeck/itm/spyglass/gui/configuration/chain_small_closed.png"));

					linkButton.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(final SelectionEvent evt) {
							/*
							 * if pressed, change image/text and change binding between both text
							 * fields (add binding / remove binding
							 */
							final boolean currentlyLocked = config.getMetrics().getLockAbs2metricFactor();
							config.getMetrics().setLockAbs2metricFactor(!currentlyLocked);

							updateScaleLink();

						}

					});
				}
				{
					label5 = new Label(group2, SWT.NONE);
					label5.setText("Y axis: ");
					final GridData label5LData = new GridData();
					label5LData.verticalAlignment = GridData.BEGINNING;
					label5LData.horizontalAlignment = GridData.BEGINNING;
					label5LData.widthHint = 48;
					label5LData.heightHint = 17;
					label5.setLayoutData(label5LData);
				}
				{
					scaleY = new Text(group2, SWT.BORDER);
					final GridData scaleYLData = new GridData();
					scaleYLData.verticalAlignment = GridData.BEGINNING;
					scaleYLData.horizontalAlignment = GridData.BEGINNING;
					scaleYLData.widthHint = 66;
					scaleYLData.heightHint = 14;
					scaleY.setLayoutData(scaleYLData);
					scaleY.setText("1");
				}
				{
					label7 = new Label(group2, SWT.NONE);
					label7.setText("Offset");
					final GridData label7LData = new GridData();
					label7LData.verticalAlignment = GridData.BEGINNING;
					label7LData.horizontalAlignment = GridData.BEGINNING;
					label7LData.widthHint = 56;
					label7LData.heightHint = 17;
					label7LData.horizontalSpan = 3;
					label7.setLayoutData(label7LData);
					label7.setFont(SWTResourceManager.getFont("Sans", 10, 1, false, false));
				}
				{
					label8 = new Label(group2, SWT.NONE);
					label8.setText("X axis: ");
					final GridData label8LData = new GridData();
					label8LData.verticalAlignment = GridData.BEGINNING;
					label8LData.horizontalAlignment = GridData.BEGINNING;
					label8LData.widthHint = 45;
					label8LData.heightHint = 17;
					label8.setLayoutData(label8LData);
				}
				{
					offsetX = new Text(group2, SWT.BORDER);
					final GridData offsetXLData = new GridData();
					offsetXLData.verticalAlignment = GridData.BEGINNING;
					offsetXLData.horizontalAlignment = GridData.BEGINNING;
					offsetXLData.heightHint = 17;
					offsetXLData.widthHint = 67;
					offsetX.setLayoutData(offsetXLData);
					offsetX.setText("0");
				}
				{
					label9length = new Label(group2, SWT.NONE);
					label9length.setText("m");
					final GridData label9lengthLData = new GridData();
					label9lengthLData.verticalAlignment = GridData.BEGINNING;
					label9lengthLData.horizontalAlignment = GridData.BEGINNING;
					label9lengthLData.widthHint = 51;
					label9lengthLData.heightHint = 17;
					label9length.setLayoutData(label9lengthLData);
				}
				{
					label10 = new Label(group2, SWT.NONE);
					label10.setText("Y axis: ");
					final GridData label10LData = new GridData();
					label10LData.verticalAlignment = GridData.BEGINNING;
					label10LData.horizontalAlignment = GridData.BEGINNING;
					label10LData.widthHint = 46;
					label10LData.heightHint = 17;
					label10.setLayoutData(label10LData);
				}
				{
					offsetY = new Text(group2, SWT.BORDER);
					final GridData offsetYLData = new GridData();
					offsetYLData.verticalAlignment = GridData.BEGINNING;
					offsetYLData.horizontalAlignment = GridData.BEGINNING;
					offsetYLData.widthHint = 68;
					offsetYLData.heightHint = 17;
					offsetY.setLayoutData(offsetYLData);
					offsetY.setText("0");
				}
				{
					label11length = new Label(group2, SWT.NONE);
					label11length.setText("m");
					final GridData label11lengthLData = new GridData();
					label11lengthLData.verticalAlignment = GridData.BEGINNING;
					label11lengthLData.horizontalAlignment = GridData.BEGINNING;
					label11lengthLData.widthHint = 50;
					label11lengthLData.heightHint = 17;
					label11length.setLayoutData(label11lengthLData);
				}
			}
			{
				group3 = new Group(this, SWT.NONE);
				final GridLayout group3Layout = new GridLayout();
				group3Layout.numColumns = 2;
				group3.setLayout(group3Layout);
				group3.setText("Metric of time");
				final GridData group3LData = new GridData();
				group3LData.horizontalAlignment = GridData.FILL;
				group3LData.heightHint = 80;
				group3LData.grabExcessHorizontalSpace = true;
				group3.setLayoutData(group3LData);
				{
					label12 = new Label(group3, SWT.NONE);
					final GridData label12LData = new GridData();
					label12.setLayoutData(label12LData);
					label12.setText("Unit of time: ");
				}
				{
					unitTime = new Text(group3, SWT.BORDER);
					final GridData text7LData = new GridData();
					text7LData.widthHint = 46;
					text7LData.heightHint = 17;
					text7LData.verticalAlignment = GridData.BEGINNING;
					text7LData.horizontalAlignment = GridData.BEGINNING;
					unitTime.setLayoutData(text7LData);
					unitTime.setText("s");
				}
				{
					label13 = new Label(group3, SWT.NONE);
					label13.setText("Scale:");
					final GridData label13LData = new GridData();
					label13.setLayoutData(label13LData);
				}
				{
					final GridData text6LData = new GridData();
					text6LData.widthHint = 46;
					text6LData.heightHint = 17;
					text6LData.verticalAlignment = GridData.BEGINNING;
					text6LData.horizontalAlignment = GridData.BEGINNING;
					scaleTime = new Text(group3, SWT.BORDER);
					scaleTime.setLayoutData(text6LData);
					scaleTime.setText("1");
				}
			}
			this.layout();
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	DataBindingContext dbc;
	GeneralSettingsXMLConfig config;
	Binding lockBinding;

	public void setDatabinding(final DataBindingContext dbc, final GeneralSettingsXMLConfig config) {

		this.config = config;
		this.dbc = dbc;

		// unit length

		final IObservableValue modelObservable2 = BeansObservables.observeValue(dbc.getValidationRealm(), config.getMetrics(), "unit");
		dbc.bindValue(SWTObservables.observeText(this.unitLength, SWT.Modify), modelObservable2, new UpdateValueStrategy(
				UpdateValueStrategy.POLICY_CONVERT)
				.setAfterConvertValidator(new StringRegExValidator("Unit of length", ".+", "Please enter a unit.")), new UpdateValueStrategy());

		// showRuler

		final IObservableValue observableVisible = BeansObservables.observeValue(dbc.getValidationRealm(), config, "showRuler");
		dbc.bindValue(SWTObservables.observeSelection(this.showRuler), observableVisible,

		new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT), null);

		// unit of time

		final IObservableValue modelObservable2b = BeansObservables.observeValue(dbc.getValidationRealm(), config, "timeUnit");
		dbc.bindValue(SWTObservables.observeText(this.unitTime, SWT.Modify), modelObservable2b, new UpdateValueStrategy(
				UpdateValueStrategy.POLICY_CONVERT).setAfterConvertValidator(new StringRegExValidator("Unit of time", ".+", "Please enter a unit.")),
				new UpdateValueStrategy());

		// offset X
		dbc.bindValue(SWTObservables.observeText(this.offsetX, SWT.Modify), BeansObservables.observeValue(dbc.getValidationRealm(), config
				.getMetrics(), "abs2metricOffsetX"), new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT), new UpdateValueStrategy());

		// offset Y
		dbc.bindValue(SWTObservables.observeText(this.offsetY, SWT.Modify), BeansObservables.observeValue(dbc.getValidationRealm(), config
				.getMetrics(), "abs2metricOffsetY"), new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT), new UpdateValueStrategy());

		// scale X
		dbc.bindValue(SWTObservables.observeText(this.scaleX, SWT.Modify), BeansObservables.observeValue(dbc.getValidationRealm(), config
				.getMetrics(), "abs2metricFactorX"), new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT), new UpdateValueStrategy());

		// scale Y
		dbc.bindValue(SWTObservables.observeText(this.scaleY, SWT.Modify), BeansObservables.observeValue(dbc.getValidationRealm(), config
				.getMetrics(), "abs2metricFactorY"), new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT), new UpdateValueStrategy());

		// scale Time
		dbc.bindValue(SWTObservables.observeText(this.scaleTime, SWT.Modify), BeansObservables.observeValue(dbc.getValidationRealm(), config,
				"timeScale"), new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT), new UpdateValueStrategy());

		dbc.bindValue(SWTObservables.observeText(this.unitLength, SWT.Modify), SWTObservables.observeText(this.label11length), null, null);
		dbc.bindValue(SWTObservables.observeText(this.unitLength, SWT.Modify), SWTObservables.observeText(this.label9length), null, null);

		updateScaleLink();

	}

	private void updateScaleLink() {
		final boolean locked = config.getMetrics().getLockAbs2metricFactor();

		if (locked) {
			scaleY.setText(scaleX.getText());

			if (lockBinding == null) {

				// bind the two fields together
				lockBinding = dbc.bindValue(SWTObservables.observeText(scaleX, SWT.Modify), SWTObservables.observeText(scaleY, SWT.Modify), null,
						null);
			}

			linkButton.setImage(SWTResourceManager.getImage("de/uniluebeck/itm/spyglass/gui/configuration/chain_small_closed.png"));

		} else {
			if (lockBinding != null) {
				// Kill the binding (it will be automatically removed from the dbc)
				lockBinding.dispose();
				lockBinding = null;
			}

			linkButton.setImage(SWTResourceManager.getImage("de/uniluebeck/itm/spyglass/gui/configuration/chain_small_open.png"));

		}
	}
}
