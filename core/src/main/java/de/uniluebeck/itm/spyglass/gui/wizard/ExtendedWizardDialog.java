/*
 * ----------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the
 * SwarmNet (www.swarmnet.de) project SpyGlass is free software;
 * you can redistribute it and/or modify it under the terms of the BSD License.
 * Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for further details.
 * ------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.gui.wizard;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * Extension of standard swt-wizarddialog
 *
 * @author jens kluttig
 */
public class ExtendedWizardDialog extends WizardDialog {
    public ExtendedWizardDialog(Shell parentShell, IWizard newWizard) {
        super(parentShell, newWizard);
    }

    @Override
    protected void nextPressed() {
        IExtendedWizardPage currentPage = null;
        if (this.getCurrentPage() instanceof IExtendedWizardPage) {
            currentPage = (IExtendedWizardPage) this.getCurrentPage();
            currentPage.nextPressed();
        }
        super.nextPressed();
    }
}
