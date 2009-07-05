package de.uniluebeck.itm.spyglass.plugin.template;

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
import de.uniluebeck.itm.spyglass.plugin.Plugin;
import de.uniluebeck.itm.spyglass.plugin.simpleglobalinformation.SimpleGlobalInformationPlugin;

//--------------------------------------------------------------------------------
/**
 * Preference page for plugin TemplatePlugin.
 * 
 * @author Dariush Forouher
 * 
 */
public class TemplatePreferencePage extends PluginPreferencePage<TemplatePlugin, TemplateXMLConfig> {

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 * 
	 * @param dialog
	 *            the {@link PluginPreferenceDialog} window
	 * @param spyglass
	 *            the active {@link Spyglass} object
	 */
	public TemplatePreferencePage(final PluginPreferenceDialog dialog, final Spyglass spyglass) {
		super(dialog, spyglass, BasicOptions.ALL);
		/* Note: With the BasicOptions enum you can modify the basic group composite! */
	}

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 * 
	 * @param dialog
	 *            the {@link PluginPreferenceDialog} window
	 * @param spyglass
	 *            the active {@link Spyglass} object
	 * @param plugin
	 *            a {@link SimpleGlobalInformationPlugin} instance
	 */
	public TemplatePreferencePage(final PluginPreferenceDialog dialog, final Spyglass spyglass, final TemplatePlugin plugin) {
		super(dialog, spyglass, plugin, BasicOptions.ALL);
		/* Note: With the BasicOptions enum you can modify the basic group composite! */
	}

	// --------------------------------------------------------------------------------
	@Override
	public Class<? extends Plugin> getPluginClass() {
		return TemplatePlugin.class;
	}

	// --------------------------------------------------------------------------------
	@Override
	protected Composite createContents(final Composite parent) {

		// This composite belongs to YOU. Put in it whatever you want.
		// Note that it already contains a "Group"-Composite, called "Basic Options".
		final Composite composite = createContentsInternal(parent);

		// You probably want to add one (ore more) additional Group-Composites in this
		// composite put your fields in them.

		final Group optionsGroup = new Group(composite, SWT.SHADOW_ETCHED_IN);

		optionsGroup.setText("Options");
		optionsGroup.setLayout(new GridLayout(2, false));
		optionsGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		final Label label1 = new Label(optionsGroup, SWT.NONE);
		label1.setText("Some parameter: ");

		final GridData textData = new GridData();
		textData.grabExcessHorizontalSpace = true;
		textData.horizontalAlignment = GridData.FILL;
		final Text fieldName = new Text(optionsGroup, SWT.BORDER);
		fieldName.setLayoutData(textData);

		return composite;
	}

	// --------------------------------------------------------------------------------
	@Override
	protected void loadFromModel() {
		super.loadFromModel();

		// put code here to restore the form content to what the model contains
		// (if this class used databinding this could be omitted)

	}

	// --------------------------------------------------------------------------------
	@Override
	protected void storeToModel() {
		super.storeToModel();

		// put code here to store the form content into the model
		// (if this class used databinding this could be omitted)

	}

}