package de.uniluebeck.itm.spyglass.plugin.positionpacketnodepositioner;

import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferenceDialog;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;
import de.uniluebeck.itm.spyglass.gui.validator.IntegerRangeValidator;
import de.uniluebeck.itm.spyglass.plugin.Plugin;

public class PositionPacketNodePositionerPreferencePage
		extends
		PluginPreferencePage<PositionPacketNodePositionerPlugin, PositionPacketNodePositionerXMLConfig> {
	
	private boolean somethingChanged = false;
	private Text fieldName;
	
	public PositionPacketNodePositionerPreferencePage(final PluginPreferenceDialog dialog,
			final Spyglass spyglass) {
		super(dialog, spyglass, BasicOptions.ALL_BUT_VISIBLE_AND_SEMANTIC_TYPES);
	}
	
	public PositionPacketNodePositionerPreferencePage(final PluginPreferenceDialog dialog,
			final Spyglass spyglass, final PositionPacketNodePositionerPlugin plugin) {
		super(dialog, spyglass, plugin, BasicOptions.ALL_BUT_VISIBLE_AND_SEMANTIC_TYPES);
	}
	
	// --------------------------------------------------------------------------------
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage#hasUnsavedChanges()
	 */
	@Override
	public boolean hasUnsavedChanges() {
		return super.hasUnsavedChanges() || somethingChanged;
	}
	
	@Override
	protected Composite createContents(final Composite parent) {
		
		final Composite composite = super.createContents(parent);
		
		createOptionsGroup(composite);
		
		addDatabinding();
		
		return composite;
		
	}
	
	private void createOptionsGroup(final Composite composite) {
		final Group optionsGroup = new Group(composite, SWT.SHADOW_ETCHED_IN);
		optionsGroup.setText("Options");
		optionsGroup.setLayout(new GridLayout(2, false));
		optionsGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		
		final Label label2 = new Label(optionsGroup, SWT.NONE);
		label2.setText("Timeout: ");
		
		final GridData textData = new GridData();
		textData.grabExcessHorizontalSpace = true;
		textData.horizontalAlignment = GridData.FILL;
		textData.heightHint = 17;
		fieldName = new Text(optionsGroup, SWT.BORDER);
		fieldName.setLayoutData(textData);
		fieldName.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent evt) {
				somethingChanged = true;
			}
		});
		
		final Label label3 = new Label(optionsGroup, SWT.NONE);
		label3.setText("(0 means no timeout.)");
		
	}
	
	private void addDatabinding() {
		final IObservableValue modelObservable = BeansObservables.observeValue(getRealm(),
				this.config, "timeout");
		
		dbc.bindValue(SWTObservables.observeText(fieldName, SWT.Modify), modelObservable,
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT)
						.setAfterConvertValidator(new IntegerRangeValidator(0, Integer.MAX_VALUE)),
				null);
	}
	
	@Override
	public Class<? extends Plugin> getPluginClass() {
		return PositionPacketNodePositionerPlugin.class;
	}
	
}