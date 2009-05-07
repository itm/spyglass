package de.uniluebeck.itm.spyglass.gui.actions;

import java.io.File;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;

import de.uniluebeck.itm.spyglass.SpyglassEnvironment;
import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.io.PacketRecorder;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;

public class RecordSelectOutputAction extends Action {

	private static final Logger log = SpyglassLoggerFactory.getLogger(RecordSelectOutputAction.class);

	private Spyglass spyglass;

	private String defaultDir = SpyglassEnvironment.getDefalutRecordDirectory();

	private final ImageDescriptor imageDescriptor = getImageDescriptor("record_select_output.png");

	public RecordSelectOutputAction(final Spyglass spyglass) {
		this.spyglass = spyglass;
	}

	@Override
	public void run() {
		final PacketRecorder recorder = spyglass.getPacketRecorder();
		if (recorder == null) {
			MessageDialog.openError(null, "No recorder available", "The input type you selected does not support recording");
		} else {
			final FileDialog fd = new FileDialog(Display.getCurrent().getActiveShell(), SWT.SAVE);
			fd.setFilterExtensions(new String[] { "*.rec" });
			fd.setFilterPath(new File(defaultDir).getAbsoluteFile().toString());
			final String path = fd.open();
			if (path != null) {
				recorder.setRecordFile(path);
			}
		}
		log.debug("Pressed button RECORD_SELECT_OUTPUT.");
	};

	@Override
	public String getToolTipText() {
		return "Select output file";
	}

	@Override
	public String getText() {
		return "Select output file";
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return imageDescriptor;
	}

}
