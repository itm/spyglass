package de.uniluebeck.itm.spyglass.gui.actions;

import org.apache.log4j.Logger;
import org.eclipse.jface.resource.ImageDescriptor;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.util.SpyglassLogger;

public class RecordRecordAction extends Action {
	
	private static final Logger log = SpyglassLogger.get(RecordRecordAction.class);
	
	private Spyglass spyglass;
	
	private boolean isRecording = false;
	
	private final ImageDescriptor imageDescriptorRecording = getImageDescriptor("record_record.png");
	
	private final ImageDescriptor imageDescriptorPausing = getImageDescriptor("record_pause.png");
	
	public RecordRecordAction(final Spyglass spyglass) {
		this.spyglass = spyglass;
	}
	
	@Override
	public void run() {
		
		isRecording = spyglass.getPacketRecorder().setRecord(!isRecording);
		
		setText(isRecording ? "Pause" : "Record");
		setToolTipText(isRecording ? "Pause" : "Record");
		setImageDescriptor(isRecording ? imageDescriptorPausing : imageDescriptorRecording);
		
		log.debug("Pressed button RECORD_RECORD.");
		
	};
	
	@Override
	public String getToolTipText() {
		return isRecording ? "Pause" : "Record";
	}
	
	@Override
	public String getText() {
		return isRecording ? "Pause" : "Record";
	}
	
	@Override
	public ImageDescriptor getImageDescriptor() {
		return isRecording ? imageDescriptorPausing : imageDescriptorRecording;
	}
}
