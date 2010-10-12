/*----------------------------------------------------------------------------------------
 * This file is part of the
 * WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de)
 * project SpyGlass is free software; you can redistribute it and/or modify it under the terms of
 * the BSD License. Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for
 * further details.
 * ---------------------------------------------------------------------------------------
 */

package de.uniluebeck.itm.spyglass.gui;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * Wizard for creating connection to Testbed
 */
public class TestbedWizard extends Wizard {
    private String snaaUrl;
    private String rsUrl;
    private String smUrl;

    public TestbedWizard() {
        super();
        setWindowTitle("Testbed Configuration");
        addPage(new FirstPage("Testbed URLs"));
        addPage(new SecondPage("Authorization"));
    }

    @Override
    public boolean performFinish() {
        return false;
    }

    public String getSnaaUrl() {
        return snaaUrl;
    }

    public void setSnaaUrl(String snaaUrl) {
        this.snaaUrl = snaaUrl;
    }

    public String getRsUrl() {
        return rsUrl;
    }

    public void setRsUrl(String rsUrl) {
        this.rsUrl = rsUrl;
    }

    public String getSmUrl() {
        return smUrl;
    }

    public void setSmUrl(String smUrl) {
        this.smUrl = smUrl;
    }

    public class FirstPage extends WizardPage {
        Text snaaText, rsText, smText;

        protected FirstPage(String pageName) {
            super(pageName);
            setMessage("Enter Service URLs");
        }

        @Override
        public void createControl(Composite composite) {
            final Composite area = new Composite(composite, SWT.NONE);
            final GridLayout gridLayout = new GridLayout();
            gridLayout.makeColumnsEqualWidth = true;
            gridLayout.numColumns = 2;
            area.setLayout(gridLayout);
            Label snaaLabel = new Label(area, SWT.NONE);
            snaaLabel.setText("SNAA Service");
            snaaText = new Text(area, SWT.BORDER);
            snaaText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            Label rsLabel = new Label(area, SWT.NONE);
            rsLabel.setText("Reservation Service");
            rsText = new Text(area, SWT.BORDER);
            rsText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            Label smLabel = new Label(area, SWT.NONE);
            smLabel.setText("SessionManagement Service");
            smText = new Text(area, SWT.BORDER);
            smText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            setControl(area);
            final DataBindingContext context = new DataBindingContext(SWTObservables.getRealm(composite.getDisplay()));
            IObservableValue snaaUrlObservable = PojoObservables.observeValue(SWTObservables.getRealm(composite.getDisplay()), this.getWizard(), "snaaUrl");            
            IObservableValue snaaTextObservable = SWTObservables.observeText(snaaText, SWT.Modify);
            context.bindValue(snaaTextObservable, snaaUrlObservable, new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE),
                    new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE));
            IObservableValue rsUrlObservable = PojoObservables.observeValue(SWTObservables.getRealm(composite.getDisplay()), this.getWizard(), "rsUrl");
            IObservableValue rsTextObservable = SWTObservables.observeText(rsText, SWT.Modify);
            context.bindValue(rsTextObservable, rsUrlObservable, new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE),
                    new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE));
            IObservableValue smUrlObservable = PojoObservables.observeValue(SWTObservables.getRealm(composite.getDisplay()), this.getWizard(), "smUrl");
            IObservableValue smTextObservable = SWTObservables.observeText(smText, SWT.Modify);
            context.bindValue(smTextObservable, smUrlObservable, new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE),
                    new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE));

        }
    }

    public class SecondPage extends WizardPage {
        protected SecondPage(String pageName) {
            super(pageName);
            setMessage("Authorize your Identity");
        }

        @Override
        public void createControl(Composite composite) {
            final Composite area = new Composite(composite, SWT.NONE);
            final GridLayout gridLayout = new GridLayout();
            gridLayout.makeColumnsEqualWidth = true;
            gridLayout.numColumns = 2;
            area.setLayout(gridLayout);
            setControl(area);
        }
    }
}
