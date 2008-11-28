package de.uniluebeck.itm.spyglass.gui.actions;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;

import de.uniluebeck.itm.spyglass.core.PacketRecorder;
import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;

public class RecordRecordAction extends Action {
	
	private static final Logger log = SpyglassLoggerFactory.getLogger(RecordRecordAction.class);
	
	private Spyglass spyglass;
	
	private boolean isRecording = false;
	
	private final ImageDescriptor imageDescriptorRecording = getImageDescriptor("record_record.png");
	
	private final ImageDescriptor imageDescriptorPausing = getImageDescriptor("record_pause.png");
	
	private Thread listenerThread;
	
	public RecordRecordAction(final Spyglass spyglass) {
		this.spyglass = spyglass;
	}
	
	@Override
	public void run() {
		
		if (spyglass.getPacketRecorder() == null) {
			MessageDialog.openError(null, "No recorder available",
					"The input type you selected does not support recording");
			isRecording = false;
		} else {
			isRecording = spyglass.getPacketRecorder().enableRecording(!isRecording);
		}
		setText(isRecording ? "Pause" : "Record");
		setToolTipText(isRecording ? "Pause" : "Record");
		setImageDescriptor(isRecording ? imageDescriptorPausing : imageDescriptorRecording);
		
		if (isRecording) {
			final Thread listenerThread = new Thread() {
				@Override
				public void run() {
					while (!isInterrupted()) {
						final PacketRecorder rec = spyglass.getPacketRecorder();
						if (rec != null) {
							isRecording = rec.isRecord();
							Display.getDefault().asyncExec(new Runnable() {
								public void run() {
									setText(isRecording ? "Pause" : "Record");
									setToolTipText(isRecording ? "Pause" : "Record");
									setImageDescriptor(isRecording ? imageDescriptorPausing
											: imageDescriptorRecording);
								}
							});
							
						}
						try {
							sleep(2000);
						} catch (final InterruptedException e) {
							// TODO Auto-generated catch block
							log.warn(e, e);
							interrupt();
						}
					}
				}
			};
			listenerThread.setDaemon(true);
			listenerThread.start();
		} else {
			if (listenerThread != null) {
				listenerThread.interrupt();
			}
		}
		
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
