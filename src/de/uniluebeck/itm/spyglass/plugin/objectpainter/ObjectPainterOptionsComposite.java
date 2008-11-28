package de.uniluebeck.itm.spyglass.plugin.objectpainter;

import java.io.File;
import java.text.NumberFormat;
import java.text.ParseException;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.cloudgarden.resource.SWTResourceManager;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.gui.databinding.validator.FileReadableValidator;
import de.uniluebeck.itm.spyglass.gui.databinding.validator.IntegerRangeValidator;
import de.uniluebeck.itm.spyglass.xmlconfig.MetricsXMLConfig;

public class ObjectPainterOptionsComposite extends Composite {

	{
		// Register as a resource user - SWTResourceManager will
		// handle the obtaining and disposing of resources
		SWTResourceManager.registerResourceUser(this);
	}

	private Text imageFileText;
	private Label imageDimesionsLabel;
	private Text imageSizeWidthText;
	private Text imageSizeHeightText;
	private Label label1;
	private Label label2;
	private Label label4;
	private Button keepProportionsButton;
	private Label label3;

	private double imageAspectRatio;

	/**
	 * Is true while the algorith sets width/height (neccessary to avoid stack overflow ;)
	 */
	boolean aspectSettingInProgress = false;

	// --------------------------------------------------------------------------------
	/**
	 * @param parent
	 */
	public ObjectPainterOptionsComposite(final Composite parent) {
		super(parent, SWT.NONE);
		initGUI();
	}

	private void initGUI() {

		final GridLayout thisLayout = new GridLayout(1, true);

		this.setLayout(thisLayout);
		this.setSize(660, 191);

		final GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		this.setLayoutData(gridData);
		{
			final GridData groupData = new GridData();
			groupData.horizontalAlignment = GridData.FILL;
			groupData.verticalAlignment = GridData.FILL;
			groupData.grabExcessHorizontalSpace = true;
			groupData.grabExcessVerticalSpace = true;
			final Group group = new Group(this, SWT.NONE);
			group.setLayoutData(groupData);
			final GridLayout groupLayout = new GridLayout();
			group.setLayout(groupLayout);
			groupLayout.numColumns = 7;
			group.setText("Image options");

			{
				final GridData imageFileData = new GridData();
				imageFileData.widthHint = 100;
				final Label imageFileLabel = new Label(group, SWT.NONE);
				imageFileLabel.setLayoutData(imageFileData);
				imageFileLabel.setText("File path: ");

				final GridLayout imageFileCompositeLayout = new GridLayout();
				imageFileCompositeLayout.numColumns = 2;
				final Composite imageFileComposite = new Composite(group, SWT.NONE);
				imageFileComposite.setLayout(imageFileCompositeLayout);
				final GridData imageFileCompositeData = new GridData();
				imageFileCompositeData.horizontalSpan = 6;
				imageFileCompositeData.horizontalAlignment = GridData.FILL;
				imageFileCompositeData.grabExcessHorizontalSpace = true;
				imageFileComposite.setLayoutData(imageFileCompositeData);
				{
					final GridData imageFileTextData = new GridData();
					imageFileTextData.grabExcessHorizontalSpace = true;
					imageFileTextData.horizontalAlignment = GridData.FILL;
					imageFileText = new Text(imageFileComposite, SWT.BORDER);
					imageFileText.setLayoutData(imageFileTextData);
					imageFileText.addModifyListener(new ModifyListener() {
						public void modifyText(final ModifyEvent evt) {
							if (new File(imageFileText.getText()).canRead()) {
								final Image i = new Image(null, imageFileText.getText());
								imageAspectRatio = (i.getBounds().width) / ((double) i.getBounds().height);

								imageDimesionsLabel.setText(String.format("%d/%d px (ratio %.2f)", i.getBounds().height, i.getBounds().width,
										imageAspectRatio));

								i.dispose();
							} else {
								imageDimesionsLabel.setText("???/??? px");
							}
						}
					});

					final GridData imageFileButtonData = new GridData();
					imageFileButtonData.widthHint = 80;
					final Button imageFileButton = new Button(imageFileComposite, SWT.PUSH);
					imageFileButton.setText("Change...");
					imageFileButton.setLayoutData(imageFileButtonData);
					imageFileButton.addSelectionListener(new SelectionListener() {
						@Override
						public void widgetDefaultSelected(final SelectionEvent e) {
							widgetSelected(e);
						}

						@Override
						public void widgetSelected(final SelectionEvent e) {
							final FileDialog fileDialog = new FileDialog(getShell(), SWT.OPEN);
							fileDialog.setFilterExtensions(new String[] { "*.jpg;*.png;*.gif", "*" });
							final String file = fileDialog.open();
							if (file != null) {
								imageFileText.setText(file);
							}
						}
					});
				}

				final GridData lowerLeftLabelData = new GridData();
				final Label lowerLeftLabel = new Label(group, SWT.NONE);
				lowerLeftLabel.setLayoutData(lowerLeftLabelData);
				lowerLeftLabel.setText("Dimensions (W/H): ");
			}
			{
				final GridData lowerLeftXTextData = new GridData();
				lowerLeftXTextData.horizontalSpan = 6;
				lowerLeftXTextData.horizontalAlignment = GridData.FILL;
				imageDimesionsLabel = new Label(group, SWT.NONE);
				imageDimesionsLabel.setLayoutData(lowerLeftXTextData);
				imageDimesionsLabel.setText("???");
			}
			{
				// image size
				final GridData imageSizeLabelData = new GridData();
				imageSizeLabelData.horizontalSpan = 7;
				final Label imageSizeLabel = new Label(group, SWT.NONE);
				imageSizeLabel.setLayoutData(imageSizeLabelData);
				imageSizeLabel.setText("Scale Image to");
				imageSizeLabel.setFont(SWTResourceManager.getFont("Sans", 10, 1, false, false));
			}
			{
				label4 = new Label(group, SWT.NONE);
				label4.setText("Width / Height");
			}
			{
				final GridData imageSizeWidthTextData = new GridData();
				imageSizeWidthTextData.horizontalAlignment = GridData.FILL;
				imageSizeWidthTextData.heightHint = 17;
				imageSizeWidthTextData.grabExcessHorizontalSpace = true;
				imageSizeWidthText = new Text(group, SWT.BORDER);
				imageSizeWidthText.setLayoutData(imageSizeWidthTextData);
				imageSizeWidthText.addModifyListener(new ModifyListener() {
					public void modifyText(final ModifyEvent evt) {
						imageSizeWidthTextModifyText(evt);
					}
				});
			}
			{
				label2 = new Label(group, SWT.NONE);
				final GridData label2LData = new GridData();
				label2LData.widthHint = 7;
				label2LData.heightHint = 17;
				label2.setLayoutData(label2LData);
				label2.setText("/");
			}

			{

				final GridData imageSizeHeightTextData = new GridData();
				imageSizeHeightTextData.horizontalAlignment = GridData.FILL;
				imageSizeHeightTextData.heightHint = 17;
				imageSizeHeightTextData.grabExcessHorizontalSpace = true;
				imageSizeHeightText = new Text(group, SWT.BORDER);
				imageSizeHeightText.setLayoutData(imageSizeHeightTextData);
				imageSizeHeightText.addModifyListener(new ModifyListener() {
					public void modifyText(final ModifyEvent evt) {
						imageSizeHeightTextModifyText(evt);
					}
				});
			}
			{
				label3 = new Label(group, SWT.NONE);
				final GridData label3LData = new GridData();
				label3LData.horizontalSpan = 3;
				label3LData.widthHint = 57;
				label3LData.heightHint = 17;
				label3.setLayoutData(label3LData);
				label3.setText("m");

			}
			{
				label1 = new Label(group, SWT.NONE);
				label1.setText("");
			}
			{
				keepProportionsButton = new Button(group, SWT.CHECK | SWT.LEFT);
				keepProportionsButton.setText("Keep proportions");
			}
		}

	}

	public void setDatabinding(final DataBindingContext dbc, final ObjectPainterXMLConfig config, final Spyglass spyglass) {

		IObservableValue obsModel;
		ISWTObservableValue obsWidget;
		UpdateValueStrategy usTargetToModel;

		{
			obsWidget = SWTObservables.observeText(imageFileText, SWT.Modify);
			obsModel = BeansObservables.observeValue(dbc.getValidationRealm(), config, ObjectPainterXMLConfig.PROPERTYNAME_IMAGE_FILE_NAME);
			usTargetToModel = new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT).setAfterGetValidator(new FileReadableValidator());
			dbc.bindValue(obsWidget, obsModel, usTargetToModel, null);
		}

		{
			obsWidget = SWTObservables.observeText(imageSizeWidthText, SWT.Modify);
			obsModel = BeansObservables.observeValue(dbc.getValidationRealm(), config, ObjectPainterXMLConfig.PROPERTYNAME_IMAGE_SIZE_X);
			usTargetToModel = new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT).setAfterConvertValidator(new IntegerRangeValidator(0,
					Integer.MAX_VALUE));
			dbc.bindValue(obsWidget, obsModel, usTargetToModel, null);
		}
		{
			obsWidget = SWTObservables.observeText(imageSizeHeightText, SWT.Modify);
			obsModel = BeansObservables.observeValue(dbc.getValidationRealm(), config, ObjectPainterXMLConfig.PROPERTYNAME_IMAGE_SIZE_Y);
			usTargetToModel = new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT).setAfterConvertValidator(new IntegerRangeValidator(0,
					Integer.MAX_VALUE));
			dbc.bindValue(obsWidget, obsModel, usTargetToModel, null);
		}
		{
			obsWidget = SWTObservables.observeSelection(keepProportionsButton);
			obsModel = BeansObservables.observeValue(dbc.getValidationRealm(), config, ObjectPainterXMLConfig.PROPERTYNAME_KEEP_PROPORTIONS);
			usTargetToModel = new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT);
			dbc.bindValue(obsWidget, obsModel, usTargetToModel, null);
		}
		{
			final MetricsXMLConfig mConf = spyglass.getConfigStore().getSpyglassConfig().getGeneralSettings().getMetrics();
			dbc
					.bindValue(SWTObservables.observeText(this.label3), BeansObservables.observeValue(dbc.getValidationRealm(), mConf, "unit"), null,
							null);
		}

	}

	private void imageSizeHeightTextModifyText(final ModifyEvent evt) {
		if (aspectSettingInProgress || !this.keepProportionsButton.getSelection()) {
			return;
		}

		final String s = this.imageSizeHeightText.getText();
		aspectSettingInProgress = true;
		try {
			final int i = NumberFormat.getNumberInstance().parse(s).intValue();
			final int w = (int) (i / imageAspectRatio);
			this.imageSizeWidthText.setText(w + "");

		} catch (final ParseException e) {
			// this is no valid integer yet
		} finally {
			aspectSettingInProgress = false;
		}
		return;

	}

	private void imageSizeWidthTextModifyText(final ModifyEvent evt) {

		if (aspectSettingInProgress || !this.keepProportionsButton.getSelection()) {
			return;
		}

		final String s = this.imageSizeWidthText.getText();
		aspectSettingInProgress = true;

		try {
			final int i = NumberFormat.getNumberInstance().parse(s).intValue();
			final int h = (int) (i * imageAspectRatio);
			this.imageSizeHeightText.setText(h + "");

		} catch (final ParseException e) {
			// this is no valid integer yet
			return;
		} finally {
			aspectSettingInProgress = false;
		}
	}
}
