package de.uniluebeck.itm.spyglass.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;

import de.uniluebeck.itm.spyglass.gateway.FileReaderGateway;
import de.uniluebeck.itm.spyglass.gateway.Gateway;
import de.uniluebeck.itm.spyglass.packet.IShellToSpyGlassPacketBroker;
import de.uniluebeck.itm.spyglass.packet.PacketFactory;
import de.uniluebeck.itm.spyglass.packet.PacketReader;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacket;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacketException;
import de.uniluebeck.itm.spyglass.util.SpyglassLogger;

// --------------------------------------------------------------------------------
/**
 * Instances of this class can record provided packages to files located in the local file system.
 * The recorded packages can be played back, too.<br>
 * Since it extends the {@link PacketReader} it can be used as ordinary {@link PacketReader} with
 * extended functionality within SpyGlass.
 * 
 * @author Sebastian Ebers
 * @see SpyglassPacket
 */
public class PacketRecorder extends IShellToSpyGlassPacketBroker {
	
	/** Object to log status and error messages within the PacketRecorder */
	static final Logger log = SpyglassLogger.getLogger(PacketRecorder.class);
	
	/**
	 * Indicates whether the incoming packages are currently recorded
	 */
	private boolean record = false;
	
	/**
	 * Indicates whether the packets to be provided should be read from a file or to be forwarded
	 * from iShell
	 */
	private boolean playback = false;
	
	/**
	 * The queue where packets are dropped by the packet dispatcher and which is maintained
	 * concurrently
	 */
	private ConcurrentLinkedQueue<SpyglassPacket> recordingQueue = null;
	
	/** The thread used to consume packets from the packet queue */
	private Thread packetConsumerThread = null;
	
	/** The path to the file the packages are recorder */
	private String recordFileString = null;
	
	/** The path to the directory where the record files are located */
	private String recordDirectory = new File("./record/").getAbsoluteFile().toString();
	
	/** The time stamp of the last packed read from the playback file */
	private long lastPlaybackPacketTimestamp = -1;
	
	private String previousRecordFile = null;
	
	/**
	 * Constructor
	 */
	public PacketRecorder() {
		recordingQueue = new ConcurrentLinkedQueue<SpyglassPacket>();
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
			
			// if no record file was selected, let the user select one and the user denies to
			// select a file, the recording will be aborted
			
			if (getRecordFilePath() == null) {
				log
						.info("No file selected to be used to record the packages.\r\n The recording will be aborted!");
				this.record = false;
			}

			else {
				
				// Check if the file already exists and if it differs from the previous chosen one.
				// If so, the user can decide to append the information, to overwrite the file or to
				// abort the recording
				final int result = checkAppend();
				
				// the user decided to abort selecting a file and nothing is to be done
				if (result == 2) {
					// in case a recording process is already running it will not be aborted
					return this.record;
				}
				
				startPacketConsumerThread((result == 0));
				this.record = true;
			}
			
		} else {
			
			this.record = false;
			packetConsumerThread.interrupt();
			recordingQueue.clear();
			packetConsumerThread = null;
		}
		
		return this.record;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Checks if the selected recording file already exists and if it is was already selected. If it
	 * was not selected but it already exists, a dialog window will show up to ask the user whether
	 * the content is to be appended to the file or not. Alternatively the user can abort selecting
	 * the file.
	 * 
	 * @return <tt>0</tt> if the content is to be appended, <tt>1</tt> if the files content is to be
	 *         replaced with the new one or <tt>2</tt> if the selection is to be aborted
	 */
	private int checkAppend() {
		int result = 0;
		// Check if the file already exists and if it differs from the previous chosen one.
		// If so, the user can decide to append the information, to overwrite the file or to
		// abort the recording
		final File file = new File(recordFileString);
		if (file.exists() && (file.length() > 0)
				&& ((previousRecordFile == null) || !recordFileString.equals(previousRecordFile))) {
			result = new MessageDialog(
					Display.getCurrent().getActiveShell(),
					"Append or Replace",
					null,
					"The file already exists. Shall the new information be appended or shall the file be replaced?",
					SWT.ICON_QUESTION, new String[] { "Append", "Replace", "Abort" }, 0).open();
		}
		return result;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Starts the thread used to consume the packets which have been pushed into the packet queue
	 * previously
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
											.info("The packet recorder's packet consumer thread was interrupted while waiting for a notification of the arrival of a new packet");
									interrupt();
								}
							}
							packet = queue.poll();
							
						}
						if (packet != null) {
							data = packet.serialize();
							recordFileWriter.write(data.length);
							recordFileWriter.write(data);
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
	 * @return the recordingQueue
	 */
	ConcurrentLinkedQueue<SpyglassPacket> getPacketQueue() {
		return recordingQueue;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param path
	 *            the recordFileString to set
	 */
	public void setRecordFile(final String path) {
		if (path != null) {
			if (recordFileString != null) {
				previousRecordFile = recordFileString;
			} else {
				previousRecordFile = path;
			}
			// if the recording is currently in process, stop it, replace the output file and
			// restart the recording again
			if (isRecord()) {
				setRecord(false);
				recordFileString = path;
				setRecord(true);
			} else {
				// otherwise just set the output file
				recordFileString = path;
			}
		}
	}
	
	// --------------------------------------------------------------------------
	/**
	 * Opens a dialog to select a file which will be used to store the recorded packets.
	 */
	private String getRecordFilePath() {
		if (recordFileString == null) {
			setRectordFile();
		}
		return recordFileString;
	}
	
	// --------------------------------------------------------------------------
	/**
	 * Opens a dialog to select a file which will be used to store the recorded packets.
	 */
	public void setRectordFile() {
		
		setRecordFile(selectRecodingFileByUser());
		
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Opens a message dialog for the user to select the recording file
	 * 
	 * @return the path to the file selected by the user
	 */
	private String selectRecodingFileByUser() {
		final FileDialog fd = new FileDialog(Display.getCurrent().getActiveShell(), SWT.SAVE);
		fd.setFilterExtensions(new String[] { "*.rec" });
		fd.setFilterPath(recordDirectory);
		String path = null;
		boolean conflictingFileSelected = false;
		do {
			path = fd.open();
			if (path != null) {
				if (!path.endsWith(".rec")) {
					path += ".rec";
				}
				
				conflictingFileSelected = isPlaybackFilePathEqual(path);
				if (isPlayback() && conflictingFileSelected) {
					
					MessageDialog
							.openError(null, "The file is already in use",
									"The chosen file is already used for playback. please choose a different one ");
					
				}

				// if a file conflict was detected but the playback mode is disabled, set the
				// playbackfile to null
				else if (!isPlayback() && isPlaybackFilePathEqual(path)) {
					((FileReaderGateway) getGateway()).setFile(null);
					conflictingFileSelected = false;
				}
			}
		} while (conflictingFileSelected);
		return path;
	}
	
	/**
	 * Returns whether a provided path equals the one of the playback file
	 * 
	 * @param path
	 *            the provided path
	 * @return <code>true</code> if the provided path equals the one of the playback file
	 */
	private boolean isPlaybackFilePathEqual(final String path) {
		File in = null;
		if ((getGateway() instanceof FileReaderGateway) && (path != null)) {
			in = ((FileReaderGateway) getGateway()).getFile();
			return ((in != null) && in.equals(new File(path)));
		}
		return false;
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
		synchronized (recordingQueue) {
			recordingQueue.offer(packet);
			recordingQueue.notify();
		}
		
	}
	
	@Override
	public void push(final SpyglassPacket packet) {
		// the packets could be pushed in the super classes queue but when the playback time lasts
		// to long, an out of memory exception might occur
		if (!playback) {
			handlePacket(packet);
			super.push(packet);
		}
	}
	
	// --------------------------------------------------------------------------------
	@Override
	public SpyglassPacket getNextPacket() throws SpyglassPacketException, InterruptedException {
		
		if (playback) {
			
			InputStream playbackFileReader = null;
			if ((getGateway() == null)
					|| ((playbackFileReader = getGateway().getInputStream()) == null)) {
				setPlayBackFile();
			}
			
			SpyglassPacket packet = null;
			try {
				
				int next;
				byte[] packetData;
				if ((next = playbackFileReader.read()) != -1) {
					packetData = new byte[next];
					System.out.println(next);
					playbackFileReader.read(packetData);
					packet = PacketFactory.createInstance(packetData);
				} else {
					return null;
				}
				
			} catch (final IOException e) {
				log.error("Error while reading a new packet...", e);
			}
			
			// Hold back the packet at least for delayMillies
			final long now = System.currentTimeMillis();
			final long diff = now - lastPlaybackPacketTimestamp;
			if (diff < delayMillies) {
				Thread.sleep(delayMillies - diff);
			}
			lastPlaybackPacketTimestamp = System.currentTimeMillis();
			
			// this is done to enable the user to cut the packet stream read from a file
			handlePacket(packet);
			return packet;
			
		} else {
			return super.getNextPacket();
		}
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Returns whether the currently provided files are read from a file or handed over from iShell
	 * 
	 * @return <code>true</code> if the currently provided files are read from a file or handed over
	 *         from iShell
	 */
	public boolean isPlayback() {
		return playback;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Enables or disables the playback mode
	 * 
	 * @param enable
	 *            <code>true</code> if the playback mode is to be enabled, <code>false</code>
	 *            otherwise
	 * @return <code>true</code> if the playback mode is enabled, <code>false</code> otherwise
	 */
	public boolean setPlayback(final boolean enable) {
		if (enable && (getPlaybackFile() == null)) {
			setPlayBackFile();
		} else {
			this.playback = false;
		}
		return this.playback;
	}
	
	/**
	 * Returns the file currently selected for playback
	 * 
	 * @return the file currently selected for playback
	 */
	public File getPlaybackFile() {
		if (getGateway() instanceof FileReaderGateway) {
			return ((FileReaderGateway) getGateway()).getFile();
		}
		return null;
	}
	
	/**
	 * Returns whether a provided path equals the one of the recording file
	 * 
	 * @param path
	 *            the provided path
	 * @return <code>true</code> if the provided path equals the one of the recording file
	 */
	private boolean isRecordingFilePathEqual(final String path) {
		if ((path != null) && (recordFileString != null)) {
			return (new File(path).equals(new File(recordFileString)));
		}
		return false;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Returns whether the incoming packets are currently recorded
	 * 
	 * @return <code>true</code> if the incoming packets are currently recorded
	 */
	public boolean isRecord() {
		return record;
	}
	
	// --------------------------------------------------------------------------
	/**
	 * Opens a dialog to open a file which contains a previously recorder packet stream.
	 * 
	 * @return <code>true</code> if a playback file was set successfully
	 */
	public boolean setPlayBackFile() {
		final String path = selectPlayBackFileByUser();
		
		if (path != null) {
			final Gateway gw = getGateway();
			if ((gw == null) || (!(gw instanceof FileReaderGateway))) {
				final FileReaderGateway frgw = new FileReaderGateway();
				frgw.setFile(new File(path));
				if (isPlayback()) {
					setPlayback(false);
					setGateway(frgw);
					setPlayback(true);
				}
			} else {
				((FileReaderGateway) gw).setFile(new File(path));
			}
			playback = getGateway().getInputStream() != null;
		} else {
			playback = false;
		}
		return playback;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Opens a message dialog for the user to select the playback file
	 * 
	 * @return the path to the file selected by the user
	 */
	private String selectPlayBackFileByUser() {
		final FileDialog fd = new FileDialog(Display.getCurrent().getActiveShell(), SWT.OPEN);
		fd.setFilterExtensions(new String[] { "*.rec" });
		fd.setFilterPath(recordDirectory);
		boolean isConflictingFileSelected = false;
		String path;
		do {
			path = fd.open();
			isConflictingFileSelected = isRecordingFilePathEqual(path);
			if (isRecord() && isConflictingFileSelected) {
				MessageDialog
						.openError(null, "The file is already in use",
								"The chosen file is already used for recording. Please choose a diferent one.");
				
			}
			// if a file conflict was detected but the recording mode is disabled, set the
			// recording file to null
			else if (isConflictingFileSelected && !isRecord()) {
				recordFileString = null;
				isConflictingFileSelected = false;
			}
		} while (isConflictingFileSelected);
		return path;
	}
	
	/**
	 * Sets the file to be used to playback the previously recorded packages
	 * 
	 * @param path
	 *            the path to the file
	 * @return <code>true</code> if a playback file was set successfully
	 * @deprecated
	 */
	@Deprecated
	private boolean setPlayBackFile(final String path) {
		if (path != null) {
			final Gateway gw = getGateway();
			if ((gw == null) || (!(gw instanceof FileReaderGateway))) {
				final FileReaderGateway frgw = new FileReaderGateway();
				frgw.setFile(new File(path));
				setGateway(frgw);
			} else {
				((FileReaderGateway) gw).setFile(new File(path));
			}
			playback = getGateway().getInputStream() != null;
		} else {
			playback = false;
		}
		return playback;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param args
	 * @throws IOException
	 * @throws SpyglassPacketException
	 * @throws InterruptedException
	 */
	public static void main(final String[] args) throws IOException, SpyglassPacketException,
			InterruptedException {
		final PacketRecorder recorder = new PacketRecorder();
		recorder.setDelayMillies(0);
		recorder.setPlayBackFile("record/record3.rec");
		SpyglassPacket packet = null;
		while ((packet = recorder.getNextPacket()) != null) {
			System.out.println(packet.getSenderId());
		}
	}
	
}
