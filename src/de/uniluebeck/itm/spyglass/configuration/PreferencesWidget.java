package de.uniluebeck.itm.spyglass.configuration;
import org.eclipse.swt.widgets.Widget;

public abstract class PreferencesWidget extends Widget {

	
	public PreferencesWidget(Widget arg0, int arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	private Widget parent;

	public void finalize() throws Throwable {
		super.finalize();
	}

	/**
	 * 
	 * @param parent
	 */
	public void PreferencesWidget(Widget parent){

	}

	public boolean hasUnsavedChanges(){
		return false;
	}

	public void performApply(){

	}

	public boolean performRestore(){
		return false;
	}

	public boolean performRestoreDefaults(){
		return false;
	}

	public boolean performSaveAsDefault(){
		return false;
	}

}