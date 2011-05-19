/*
 * --------------------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass. Copyright (C)
 * 2004-2007 by the SwarmNet (www.swarmnet.de) project SpyGlass is free
 * software; you can redistribute it and/or modify it under the terms of the BSD
 * License. Refer to spyglass-licence.txt file in the root of the SpyGlass
 * source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.positionstaticnodepositioner;

import de.uniluebeck.itm.spyglass.SpyglassEnvironment;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferenceDialog;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;
import de.uniluebeck.itm.spyglass.gui.databinding.validator.IntegerRangeValidator;
import de.uniluebeck.itm.spyglass.plugin.Plugin;
import java.io.File;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

public class PositionStaticNodePositionerPreferencePage
        extends PluginPreferencePage<PositionStaticNodePositionerPlugin, PositionStaticNodePositionerXMLConfig> {

    private Text fieldName;
    private Text topologyFileText;
    private Text lowerLeftXText;
    private Text lowerLeftYText;
    private Text topologySizeWidthText;
    private Text topologySizeHeightText;
    private Button keepProportionsButton;
    private Label heightUnitLabel;
    private Label widthUnitLabel;
    private Label yUnitLabel;
    private Label xUnitLabel;

    public PositionStaticNodePositionerPreferencePage(final PluginPreferenceDialog dialog, final Spyglass spyglass) {
        super(dialog, spyglass, BasicOptions.ALL_BUT_VISIBLE_AND_SEMANTIC_TYPES);
    }

    public PositionStaticNodePositionerPreferencePage(final PluginPreferenceDialog dialog, final Spyglass spyglass,
            final PositionStaticNodePositionerPlugin plugin) {
        super(dialog, spyglass, plugin, BasicOptions.ALL_BUT_VISIBLE_AND_SEMANTIC_TYPES);
    }

    @Override
    protected Composite createContents(final Composite parent) {

        final Composite composite = super.createContents(parent);

        createOptionsGroup(composite);

        addDatabinding();

        return composite;

    }
     private ModifyListener topologyFileModifyListener = new ModifyListener() {

		@Override
		public void modifyText(final ModifyEvent e) {

			final String newFileName = topologyFileText.getText();

			try {

				final File f = new File(newFileName);

				if (f.exists()) {

					Image img = null;

					try {

						img = new Image(null, newFileName);
						//imgRatio = (float) img.getImageData().width / (float) img.getImageData().height;
						//updateLock();
						img.dispose();

					} catch (final SWTException swtException) {
						if (img != null) {
							img.dispose();
						}
						//imgRatio = 1f;
					}

				}

			} catch (final NullPointerException npe) {
				// do nothing, text field should not deliver null
			}
		}
	};

    private void createOptionsGroup(final Composite composite) {




        final GridData topologyFileTextData = new GridData();
        topologyFileTextData.widthHint = 300;
        topologyFileText = new Text(composite, SWT.BORDER);
        topologyFileText.setLayoutData(topologyFileTextData);
        
        
       
        
        topologyFileText.addModifyListener(topologyFileModifyListener);

        final GridData topologyFileButtonData = new GridData();
        topologyFileButtonData.widthHint = 80;
        Button topologyFileButton = new Button(composite, SWT.PUSH);
        topologyFileButton.setText("Change...");
        topologyFileButton.setLayoutData(topologyFileButtonData);
        topologyFileButton.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetDefaultSelected(final SelectionEvent e) {
                widgetSelected(e);
            }

            @Override
            public void widgetSelected(final SelectionEvent e) {
                final FileDialog fileDialog = new FileDialog(getShell(), SWT.OPEN);
                fileDialog.setFilterExtensions(new String[]{"*.xml", "*"});
                fileDialog.setFilterPath(SpyglassEnvironment.getConfigFileWorkingDirectory());
                final String file = fileDialog.open();
                if (file != null) {
                    topologyFileText.setText(file);
                    config.setTopologyFileName(file);
                    
                    
                }
            }
        });


        final Group optionsGroup = new Group(composite, SWT.SHADOW_ETCHED_IN);

        optionsGroup.setText(
                "Options");
        optionsGroup.setLayout(
                new GridLayout(3, false));
        optionsGroup.setLayoutData(
                new GridData(SWT.FILL, SWT.TOP, true, false));

        final Label label2 = new Label(optionsGroup, SWT.NONE);

        label2.setText(
                "Timeout: ");

        final GridData textData = new GridData();
        // textData.grabExcessHorizontalSpace = true;
        // textData.horizontalAlignment = GridData.FILL;
        textData.widthHint = 80;
        textData.heightHint = 17;
        fieldName = new Text(optionsGroup, SWT.BORDER);

        fieldName.setLayoutData(textData);
        final Label label2a = new Label(optionsGroup, SWT.NONE);

        label2a.setText(
                "seconds");

        final GridData data = new GridData();
        data.horizontalSpan = 3;
        final Label label3 = new Label(optionsGroup, SWT.NONE);

        label3.setText(
                "(0 means no timeout.)");
        label3.setLayoutData(data);
    }

    private void addDatabinding() {
        final IObservableValue modelObservable = BeansObservables.observeValue(getRealm(), this.config, "timeout");

        dbc.bindValue(SWTObservables.observeText(fieldName, SWT.Modify), modelObservable, new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT).setAfterConvertValidator(new IntegerRangeValidator("Timeout", 0, Integer.MAX_VALUE)), null);
    }

    @Override
    public Class<? extends Plugin> getPluginClass() {
        return PositionStaticNodePositionerPlugin.class;
    }
}