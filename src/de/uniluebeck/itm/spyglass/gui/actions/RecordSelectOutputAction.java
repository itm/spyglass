package de.uniluebeck.itm.spyglass.gui.actions;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;

import de.uniluebeck.itm.spyglass.core.PacketRecorder;
import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;

public class RecordSelectOutputAction extends Action {
	
	private static final Logger log = SpyglassLoggerFactory.getLogger(RecordSelectOutputAction.class);
	
	private Spyglass spyglass;
	
	private final ImageDescriptor imageDescriptor = getImageDescriptor("record_select_output.png");
	
	public RecordSelectOutputAction(final Spyglass spyglass) {
		this.spyglass = spyglass;
	}
	
	@Override
	public void run() {
		final PacketRecorder recorder = spyglass.getPacketRecorder();
		if (recorder == null) {
			MessageDialog.openError(null, "No recorder available",
					"The input type you selected does not support recording");
		} else {
			recorder.setRectordFile();
		}
		log.debug("Pressed button RECORD_SELECT_OUTPUT.");
	};
	
	@Override
	public String getToolTipText() {
		return "Select Output";
	}
	
	@Override
	public String getText() {
		return "Select Output";
	}
	
	@Override
	public ImageDescriptor getImageDescriptor() {
		return imageDescriptor;
	}
	
}
