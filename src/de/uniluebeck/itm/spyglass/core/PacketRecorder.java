package de.uniluebeck.itm.spyglass.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;

import de.uniluebeck.itm.spyglass.packet.PacketFactory;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacket;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacketException;
import de.uniluebeck.itm.spyglass.util.SpyglassLogger;

// --------------------------------------------------------------------------------
/**
 * Instances of this class can record provided packages to files located in the local file system.
 * The recorded packages can be played back, too.
 * 
 * @author Sebastian Ebers
 * @see SpyglassPacket
 */
public class PacketRecorder {
	
	/** Object to log status and error messages within the PacketRecorder */
	static final Logger log = SpyglassLogger.getLogger(PacketRecorder.class);
	
	private boolean record = false;
	/**
	 * The queue where packets are dropped by the packet dispatcher and which is maintained
	 * concurrently
	 */
	private ConcurrentLinkedQueue<SpyglassPacket> packetQueue = null;
	
	/** The thread used to consume packets from the packet queue */
	private Thread packetConsumerThread = null;
	
	private String recordFileString = null;
	private FileInputStream playbackFileReader = null;
	
	private String recordDirectory = new File("./record/").getAbsoluteFile().toString();
	
	/**
	 * Constructor
	 */
	public PacketRecorder() {
		packetQueue = new ConcurrentLinkedQueue<SpyglassPacket>();
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Activates or deactivates the recording mode
	 * 
	 * @param record
	 *            if <code>true</code> the recording mode will be activated, if <code>false</code>
	 *            the recording mode will be deactivated
	 * @return <code>true</code> if the recording mode was activated, <code>false</code> otherwise
	 * 
	 */
	public boolean setRecord(final boolean record) {
		
		if (record) {
			
			// if no record file was selected, let the user select one
			if (recordFileString == null) {
				
				// if the user denies to select a file, the recording will be aborted
				if ((recordFileString = getRecordFilePath()) == null) {
					log
							.info("No file selected to be used to record the packages.\r\n The recording will be aborted!");
					return false;
				}
				
			}
			final File file = new File(recordFileString);
			boolean append = false;
			
			// check if the file already exists. If so, the user can decide to append the
			// information, to overwrite the file or to abort the recording
			if (file.exists() && (file.length() > 0)) {
				final int result = new MessageDialog(
						Display.getCurrent().getActiveShell(),
						"Append or Replace",
						null,
						"The file already exists. Shall the new information be appended or shall the file be replaced?",
						SWT.ICON_QUESTION, new String[] { "Append", "Replace", "Abort" }, 0).open();
				if (result == 2) {
					recordFileString = null;
					return false;
				}
				append = (result == 0);
			}
			
			startPacketConsumerThread(append);
			this.record = record;
			
		} else {
			
			this.record = record;
			packetConsumerThread.interrupt();
			packetConsumerThread = null;
		}
		return record;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 */
	private void startPacketConsumerThread(final boolean append) {
		
		packetConsumerThread = new Thread() {
			@Override
			public void run() {
				try {
					
					FileOutputStream recordFileWriter = new FileOutputStream(getRecordFileString(),
							append);
					final ConcurrentLinkedQueue<SpyglassPacket> queue = getPacketQueue();
					SpyglassPacket packet = null;
					while (!isInterrupted()) {
						byte[] data = null;
						synchronized (queue) {
							
							if (queue.isEmpty()) {
								try {
									queue.wait();
								} catch (final InterruptedException e) {
									log
											.info("The packet consumer thread was interrupted while waiting for a notification of the arrival of a new packet");
									interrupt();
								}
							}
							packet = queue.poll();
							
						}
						if (packet != null) {
							data = packet.serialize();
							recordFileWriter.write(data.length);
							recordFileWriter.write(data);
							//							
							// try {
							// final SpyglassPacket p = PacketFactory.createInstance(data);
							// } catch (final SpyglassPacketException e) {
							// // TODO Auto-generated catch block
							// log.error("", e);
							// }
							recordFileWriter.flush();
						}
					}
					queue.clear();
					recordFileWriter.close();
					recordFileWriter = null;
				} catch (final IOException e) {
					log.error("Error while recording a packet", e);
				}
			}
		};
		packetConsumerThread.start();
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the recordFileString
	 */
	String getRecordFileString() {
		return recordFileString;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the packetQueue
	 */
	ConcurrentLinkedQueue<SpyglassPacket> getPacketQueue() {
		return packetQueue;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param recordFileString
	 *            the recordFileString to set
	 */
	public void setRecordFile(final String recordFileString) {
		this.recordFileString = recordFileString;
	}
	
	// --------------------------------------------------------------------------
	/**
	 * Opens a dialog to select a file which will be used to store the recorded packets.
	 */
	private String getRecordFilePath() {
		final FileDialog fd = new FileDialog(Display.getCurrent().getActiveShell(), SWT.SAVE);
		fd.setFilterExtensions(new String[] { "*.rec" });
		fd.setFilterPath(recordDirectory);
		String path = fd.open();
		if (path != null) {
			if (!path.endsWith(".rec")) {
				path += ".rec";
			}
			return path;
		}
		return null;
	}
	
	/**
	 * Records a provided packet in recording mode, discards them in normal mode
	 * 
	 * @param packet
	 *            the packet to be recorded
	 */
	public void handlePacket(final SpyglassPacket packet) {
		
		if (!record) {
			return;
		}
		synchronized (packetQueue) {
			packetQueue.offer(packet);
			packetQueue.notify();
		}
		
	}
	
	/**
	 * @throws IOException
	 * @throws SpyglassPacketException
	 * 
	 */
	public void playBack() throws IOException, SpyglassPacketException {
		int next;
		byte[] packetData;
		while ((next = playbackFileReader.read()) != -1) {
			packetData = new byte[next];
			System.out.println(next);
			playbackFileReader.read(packetData);
			final SpyglassPacket packet = PacketFactory.createInstance(packetData);
		}
	}
	
	// --------------------------------------------------------------------------
	/**
	 * Opens a dialog to open a file which contains a previously recorder packet stream.
	 * 
	 * @exception FileNotFoundException
	 *                will be thrown if the user selects a file which cannot be found
	 */
	public void setPlayBackFile() throws FileNotFoundException {
		final FileDialog fd = new FileDialog(Display.getCurrent().getActiveShell(), SWT.OPEN);
		fd.setFilterExtensions(new String[] { "*.rec" });
		fd.setFilterPath(recordDirectory);
		final String path = fd.open();
		if (path != null) {
			playbackFileReader = new FileInputStream(path);
		}
		
	}
	
	public static void main(final String[] args) throws IOException, SpyglassPacketException {
		final PacketRecorder recorder = new PacketRecorder();
		recorder.playbackFileReader = new FileInputStream("record/record1.rec");
		recorder.playBack();
	}
}
