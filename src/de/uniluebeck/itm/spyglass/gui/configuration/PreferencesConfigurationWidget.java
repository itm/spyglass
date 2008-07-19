package de.uniluebeck.itm.spyglass.gui.configuration;
import org.eclipse.swt.widgets.Widget;

import de.uniluebeck.itm.spyglass.plugin.Plugin;

public abstract class PreferencesConfigurationWidget<T extends Plugin> extends Widget {

	public PreferencesConfigurationWidget(Widget arg0, int arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	public void finalize() throws Throwable {
		super.finalize();
	}

	public void commitChanges(){

	}

	public boolean isChanged(){
		return false;
	}

	public void revertChanges(){

	}

}