package de.uniluebeck.itm.spyglass.plugin.imagepainter;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.cloudgarden.resource.SWTResourceManager;

public class ImagePainterOptionsComposite extends Composite {

	{
		// Register as a resource user - SWTResourceManager will
		// handle the obtaining and disposing of resources
		SWTResourceManager.registerResourceUser(this);
	}

	private Text imageFileText;
	private Text lowerLeftXText;
	private Text lowerLeftYText;
	private Text imageSizeWidthText;
	private Text imageSizeHeightText;
	private Button keepProportionsButton;

	// --------------------------------------------------------------------------------
	/**
	 * @param parent
	 */
	public ImagePainterOptionsComposite(final Composite parent) {
		super(parent, SWT.NONE);
		initGUI();
	}

	private void initGUI() {

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
			group.setLayout(new GridLayout(7, false));
			group.setText("More information");

			{
				final GridData imageFileData = new GridData();
				imageFileData.widthHint = 100;
				final Label imageFileLabel = new Label(group, SWT.NONE);
				imageFileLabel.setLayoutData(imageFileData);
				imageFileLabel.setText("Image File");

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
					imageFileTextData.widthHint = 300;
					imageFileText = new Text(imageFileComposite, SWT.BORDER);
					imageFileText.setLayoutData(imageFileTextData);

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
				lowerLeftLabelData.widthHint = 100;
				final Label lowerLeftLabel = new Label(group, SWT.NONE);
				lowerLeftLabel.setLayoutData(lowerLeftLabelData);
				lowerLeftLabel.setText("Lower Left");

				Label label = new Label(group, SWT.NONE);
				label.setText("x:");

				final GridData lowerLeftXTextData = new GridData();
				lowerLeftXTextData.widthHint = 40;
				lowerLeftXText = new Text(group, SWT.BORDER);
				lowerLeftXText.setLayoutData(lowerLeftXTextData);

				label = new Label(group, SWT.NONE);
				label.setText("m (TODO!)");

				label = new Label(group, SWT.NONE);
				label.setText("y:");

				final GridData lowerLeftYTextData = new GridData();
				lowerLeftYTextData.widthHint = 40;
				lowerLeftYText = new Text(group, SWT.BORDER);
				lowerLeftYText.setLayoutData(lowerLeftYTextData);

				label = new Label(group, SWT.NONE);
				label.setText("m (TODO!)");

				// image size
				final GridData imageSizeLabelData = new GridData();
				imageSizeLabelData.widthHint = 100;
				final Label imageSizeLabel = new Label(group, SWT.NONE);
				imageSizeLabel.setLayoutData(imageSizeLabelData);
				imageSizeLabel.setText("Image Size");

				label = new Label(group, SWT.NONE);
				label.setText("Width:");

				final GridData imageSizeWidthTextData = new GridData();
				imageSizeWidthTextData.widthHint = 40;
				imageSizeWidthText = new Text(group, SWT.BORDER);
				imageSizeWidthText.setLayoutData(imageSizeWidthTextData);

				label = new Label(group, SWT.NONE);
				label.setText("m (TODO!)");

				label = new Label(group, SWT.NONE);
				label.setText("Height:");

				final GridData imageSizeHeightTextData = new GridData();
				imageSizeHeightTextData.widthHint = 40;
				imageSizeHeightText = new Text(group, SWT.BORDER);
				imageSizeHeightText.setLayoutData(imageSizeHeightTextData);

				label = new Label(group, SWT.NONE);
				label.setText("m (TODO!)");

				// keep proportions
				label = new Label(group, SWT.NONE);
				label.setText("");

				final GridData keepProportionsButtonData = new GridData();
				keepProportionsButtonData.horizontalSpan = 6;
				keepProportionsButton = new Button(group, SWT.CHECK);
				keepProportionsButton.setText("Keep proportions");
				keepProportionsButton.setLayoutData(keepProportionsButtonData);
			}
		}
	}

	public void setDatabinding(final DataBindingContext dbc, final ImagePainterXMLConfig config) {

		IObservableValue obsModel;
		ISWTObservableValue obsWidget;
		UpdateValueStrategy usTargetToModel;

		{
			obsWidget = SWTObservables.observeText(imageFileText, SWT.Modify);
			obsModel = BeansObservables.observeValue(dbc.getValidationRealm(), config, ImagePainterXMLConfig.PROPERTYNAME_IMAGE_FILE_NAME);
			usTargetToModel = new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT);
			dbc.bindValue(obsWidget, obsModel, usTargetToModel, null);
		}
		{
			obsWidget = SWTObservables.observeText(lowerLeftXText, SWT.Modify);
			obsModel = BeansObservables.observeValue(dbc.getValidationRealm(), config, ImagePainterXMLConfig.PROPERTYNAME_LOWER_LEFT_X);
			usTargetToModel = new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT);
			dbc.bindValue(obsWidget, obsModel, usTargetToModel, null);
		}
		{
			obsWidget = SWTObservables.observeText(lowerLeftYText, SWT.Modify);
			obsModel = BeansObservables.observeValue(dbc.getValidationRealm(), config, ImagePainterXMLConfig.PROPERTYNAME_LOWER_LEFT_Y);
			usTargetToModel = new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT);
			dbc.bindValue(obsWidget, obsModel, usTargetToModel, null);
		}
		{
			obsWidget = SWTObservables.observeText(imageSizeWidthText, SWT.Modify);
			obsModel = BeansObservables.observeValue(dbc.getValidationRealm(), config, ImagePainterXMLConfig.PROPERTYNAME_IMAGE_SIZE_X);
			usTargetToModel = new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT);
			dbc.bindValue(obsWidget, obsModel, usTargetToModel, null);
		}
		{
			obsWidget = SWTObservables.observeText(imageSizeHeightText, SWT.Modify);
			obsModel = BeansObservables.observeValue(dbc.getValidationRealm(), config, ImagePainterXMLConfig.PROPERTYNAME_IMAGE_SIZE_Y);
			usTargetToModel = new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT);
			dbc.bindValue(obsWidget, obsModel, usTargetToModel, null);
		}
		{
			obsWidget = SWTObservables.observeSelection(keepProportionsButton);
			obsModel = BeansObservables.observeValue(dbc.getValidationRealm(), config, ImagePainterXMLConfig.PROPERTYNAME_KEEP_PROPORTIONS);
			usTargetToModel = new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT);
			dbc.bindValue(obsWidget, obsModel, usTargetToModel, null);
		}

	}
}
