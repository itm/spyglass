/*----------------------------------------------------------------------------------------
 * This file is part of the
 * WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de)
 * project SpyGlass is free software; you can redistribute it and/or modify it under the terms of
 * the BSD License. Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for
 * further details.
 * ---------------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.io;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.simpleframework.xml.Element;

import de.uniluebeck.itm.spyglass.SpyglassEnvironment;
import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.gateway.FileReaderGateway;
import de.uniluebeck.itm.spyglass.gateway.Gateway;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacket;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacketException;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;
import de.uniluebeck.itm.spyglass.util.Tools;

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
public class SpyglassPacketRecorder extends SpyGlassPacketQueue implements PacketRecorder {

	// ----------------------------------------------------------------
	/** Object to log status and error messages within the PacketRecorder */
	private static final Logger log = SpyglassLoggerFactory.getLogger(PacketRecorder.class);

	// --------------------------------------------------------------------------------
	/** Object to secure packet reading and manipulations at the input gateway */
	private final Object gatewayMutex = new Object();

	// ----------------------------------------------------------------
	/** Indicates whether the recorder should skip waiting for the arrival of a new packet */
	private AtomicBoolean skipWaiting = new AtomicBoolean(false);

	// ----------------------------------------------------------------
	/** Indicates whether the recorder is shut down or not */
	private volatile boolean recorderShutDown = false;

	// ----------------------------------------------------------------
	/** Indicates whether the incoming packages are currently recorded */
	private boolean record = false;

	// ----------------------------------------------------------------
	/** The path to the directory where the record files are located */
	private final String recordDirectory = new File(SpyglassEnvironment.getDefalutRecordDirectory()).getAbsoluteFile().toString();

	// ----------------------------------------------------------------
	/** Object used to coordinate the time between delivering two packets */
	private DelayModule delayModule = null;

	// ----------------------------------------------------------------
	/** Object encapsulating playback functionality */
	private PlaybackModule playbackModule = null;

	// ----------------------------------------------------------------
	/** Object encapsulating recording functionality */
	private RecordingModule recModule = null;

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	public SpyglassPacketRecorder() {
		setSourceType(SOURCE_TYPE.ORTHER);
		playbackModule = new PlaybackModule();
	}

	// --------------------------------------------------------------------------------
	@Override
	public void init(final Spyglass spyglass) {

		super.init(spyglass);
		delayModule = new DelayModule(gatewayMutex, spyglass.getConfigStore().getSpyglassConfig());
		playbackModule.init(spyglass);
		recModule = new RecordingModule();
	}

	// --------------------------------------------------------------------------------
	@Override
	public void push(final SpyglassPacket packet) {
		if (!recorderShutDown) {
			// the packets could be pushed in the super classes queue but when the readFromFile time
			// lasts to long, an out of memory exception might occur
			if (!getSourceType().equals(SOURCE_TYPE.FILE)) {
				recModule.handlePacket(packet);
				super.push(packet);
			}
		} else {
			log.warn("The packet will not be pushed into the packet queue since this instance was shut down!");
		}
	}

	// --------------------------------------------------------------------------------
	@Override
	public SpyglassPacket getNextPacket() throws SpyglassPacketException, InterruptedException {

		SpyglassPacket packet = null;

		if (!recorderShutDown) {
			// this do while is needed, to guarantee that valid packets are returned
			do {
				// if a file was chosen as input source ...
				if (getSourceType().equals(SOURCE_TYPE.FILE)) {
					recModule.handlePacket(packet = playbackModule.getNextPacket());
				} else {
					packet = super.getNextPacket();
				}
			} while ((packet == null) && !skipWaiting.getAndSet(false));
		} else {
			log.warn("No packet will be fetched from the queue since this instance was shut down!");
		}

		return packet;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns whether the currently provided files are read from a file or handed over from iShell
	 * 
	 * @return <code>true</code> if the currently provided files are read from a file or handed over
	 *         from iShell
	 */
	public boolean isReadFromFile() {
		return getSourceType().equals(PacketReader.SOURCE_TYPE.FILE);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Enables or disables the readFromFile mode
	 * 
	 * @param enable
	 *            <code>true</code> if the readFromFile mode is to be enabled, <code>false</code>
	 *            otherwise
	 * @return <code>true</code> if the readFromFile mode is enabled, <code>false</code> otherwise
	 */
	public boolean setReadFromFile(final boolean enable) {
		this.setSourceType(enable ? SOURCE_TYPE.FILE : SOURCE_TYPE.ORTHER);
		return getSourceType().equals(SOURCE_TYPE.FILE);
	}

	// --------------------------------------------------------------------------------
	@Override
	public void setSourceType(final SOURCE_TYPE sourceType) {

		if (sourceType.equals(SOURCE_TYPE.FILE) && (getPlayBackFile() == null)) {
			setPlayBackFile(playbackModule.selectPlayBackFileByUser());
		}
		super.setSourceType(sourceType);
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
	 * Sets the file to be used to play back the previously recorded packages.
	 * 
	 * @param path
	 *            the path to the file
	 * @return <code>true</code> if a readFromFile file was set successfully
	 */
	public boolean setPlayBackFile(final String path) {
		return playbackModule.setPlayBackFile(path);
	}

	// --------------------------------------------------------------------------------
	@Override
	public File getPlayBackFile() {
		return playbackModule.getPlayBackFile();
	}

	// --------------------------------------------------------------------------------
	@Override
	public void reset() throws IOException {
		log.info("Reset requested");
		skipWaiting.set(true);

		super.reset();
		synchronized (gatewayMutex) {
			if (isReadFromFile() && !isRecord()) {
				MessageDialog.openInformation(null, "Reset Playbak", "The playback will be started from the beginning of the file.");
			}

			else if (isReadFromFile() && isRecord()) {
				if (MessageDialog.openQuestion(null, "Reset Recorder", "The playback will be started from the beginning of the file.\r\n"
						+ "Do you want to disable recording?")) {
					setRecording(false);
				}
			}

			else if (isRecord() && MessageDialog.openQuestion(null, "Reset Recorder", "Do you want to disable recording?")) {
				setRecording(false);
			}

			recModule.reset();

			delayModule.reset();

			gatewayMutex.notifyAll();
			playbackModule.reset();
		}

		log.info("Reset done");
	}

	// --------------------------------------------------------------------------------
	/**
	 * Sets the file which is used for recording the input
	 * 
	 * @param path
	 *            the path to the file which is used for recording the input
	 */
	public void setRecordFile(final String path) {
		recModule.setRecordFile(path);
	}

	// --------------------------------------------------------------------------------
	@Override
	public File getRecordFile() {
		return recModule.getRecordFile();
	}

	// --------------------------------------------------------------------------------
	/**
	 * Activates or deactivates the recording mode
	 * 
	 * @param enable
	 *            if <code>true</code> the recording mode will be activated, if <code>false</code>
	 *            the recording mode will be deactivated
	 * @return <code>true</code> if the recording mode was activated, <code>false</code> otherwise
	 * 
	 */
	public boolean setRecording(final boolean enable) {
		return recModule.setRecording(enable);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Shuts the packet recorder down.<br>
	 * All currently cached packets will be disposed and the thread consuming the packets will be
	 * stopped. Additionally, all registered listeners will be removed.
	 * 
	 * @throws IOException
	 *             thrown if the resetting of the input fails
	 */
	@Override
	public void shutdown() throws IOException {
		super.shutdown();
		recorderShutDown = true;
		recModule.shutdown();
	}

	// --------------------------------------------------------------------------------
	// --------------------------------------------------------------------------------
	// --------------------------------------------------------------------------------
	/**
	 * Module used for recording {@link SpyglassPacket}s
	 * 
	 * @author Sebastian Ebers
	 */
	private class RecordingModule {

		/**
		 * Constructor
		 */
		public RecordingModule() {
			// nothing to do
		}

		// ----------------------------------------------------------------
		/**
		 * The queue where packets are dropped by the packet dispatcher and which is maintained
		 * concurrently
		 */
		private ConcurrentLinkedQueue<SpyglassPacket> recordingQueue = new ConcurrentLinkedQueue<SpyglassPacket>();

		// ----------------------------------------------------------------
		/** The thread used to consume packets from the packet queue */
		private Thread packetConsumerThread = null;

		// ----------------------------------------------------------------
		/** The path to the file the packages are recorder */
		private String recordFileString = null;

		// ----------------------------------------------------------------
		/**
		 * The string to the file which was last selected by the user for recording (this will be
		 * needed to check whether the user has to be asked to append content)
		 */
		private String lastSelectedRecordFilePath = null;

		// --------------------------------------------------------------------------------
		/**
		 * Returns the queue where the packages which are to be recorded are temporarily stored
		 * 
		 * @return the queue where the packages which are to be recorded are temporarily stored
		 */
		private ConcurrentLinkedQueue<SpyglassPacket> getPacketQueue() {
			return recordingQueue;
		}

		// --------------------------------------------------------------------------------
		/**
		 * Sets the file which is used for recording the input
		 * 
		 * @param path
		 *            the path to the file which is used for recording the input
		 */
		@SuppressWarnings("synthetic-access")
		public void setRecordFile(final String path) {

			if (path == null) {
				recordFileString = null;
			}

			else {

				if (isReadFromFile() && playbackModule.equalsPlaybackFilePath(path)) {

					MessageDialog.openError(null, "The file is already in use",
							"Sorry, the chosen file is already in use for playback. Please choose a different one ");

				}

				// if a file conflict was detected but the readFromFile mode is disabled, set
				// the playback file to null
				else if (!isReadFromFile() && playbackModule.equalsPlaybackFilePath(path)) {
					playbackModule.setPlayBackFile(null);
				}

				// if the recording is currently in process, stop it, replace the output file and
				// restart the recording again
				if (isRecord()) {
					setRecording(false);
					recordFileString = path;
					setRecording(true);
				} else {
					// otherwise just set the output file
					recordFileString = path;
				}
			}
		}

		// --------------------------------------------------------------------------------
		/**
		 * Returns the file used to record the packages
		 * 
		 * @return the file used to record the packages
		 */
		public File getRecordFile() {
			return recordFileString != null ? new File(recordFileString) : null;
		}

		// --------------------------------------------------------------------------------
		/**
		 * Activates or deactivates the recording mode
		 * 
		 * @param enable
		 *            if <code>true</code> the recording mode will be activated, if
		 *            <code>false</code> the recording mode will be deactivated
		 * @return <code>true</code> if the recording mode was activated, <code>false</code>
		 *         otherwise
		 * 
		 */
		@SuppressWarnings("synthetic-access")
		public boolean setRecording(final boolean enable) {

			if (enable) {

				// if no record file was selected, let the user select one and the user denies to
				// select a file, the recording will be aborted

				if (getRecordFilePath() == null) {
					log.info("No file selected to be used to record the packages.\r\n The recording will be aborted!");
					record = false;
				}

				if (recordFileString != null) {

					final File file = new File(recordFileString);

					if (Tools.isWritable(file, true)) {

						// Check if the file already exists and if it differs from the previous
						// chosen one.
						// If so, the user can decide to append the information, to overwrite the
						// file or to abort the recording
						final int result = checkAppend(file);

						// the user decided to abort selecting a file and nothing is to be done
						if (result == 2) {
							// in case a recording process is already running it will not be aborted
							return record;
						}

						startPacketConsumerThread((result == 0));
						record = true;
					}
				}

			}

			// if recording is to be disabled, stop the thread and clean up
			else {

				record = false;
				if ((packetConsumerThread != null) && !packetConsumerThread.isInterrupted()) {
					packetConsumerThread.interrupt();
				}
				recordingQueue.clear();
				packetConsumerThread = null;
			}

			return record;
		}

		// --------------------------------------------------------------------------------
		/**
		 * Checks if the selected recording file already exists. If so, a dialog window will show up
		 * to ask the user whether the content is to be appended to the file or not. Alternatively,
		 * the user can abort selecting the file at all.
		 * 
		 * @param file
		 *            the file to be checked
		 * @return <ul>
		 *         <li><tt>0</tt> if the content is to be appended</li>
		 *         <li><tt>1</tt> if the files content is to be replaced with the new one</li>
		 *         <li><tt>2</tt> if the selection is to be aborted</li>
		 *         </ul>
		 */
		private int checkAppend(final File file) {
			int result = 0;

			// Check if the file already exists and if it differs from the previous chosen one.
			// If so, the user can decide to append the information, to overwrite the file or to
			// abort the recording
			if (file.exists() && (file.length() > 0)) {
				result = new MessageDialog(Display.getCurrent().getActiveShell(), "Append or Replace", null,
						"The file already exists. Shall the new information be appended or shall the file be replaced?", SWT.ICON_QUESTION,
						new String[] { "Append", "Replace", "Abort" }, 0).open();
				lastSelectedRecordFilePath = recordFileString;
			}
			return result;
		}

		// --------------------------------------------------------------------------------
		/**
		 * Checks if the selected recording file already exists and if it is was already selected.
		 * If it was not selected but it already exists, a dialog window will show up to ask the
		 * user whether the content is to be appended to the file or not. Alternatively, the user
		 * can abort selecting the file at all.
		 * 
		 * @param file
		 *            the file to be checked
		 * @return <ul>
		 *         <li><tt>0</tt> if the content is to be appended</li>
		 *         <li><tt>1</tt> if the files content is to be replaced with the new one</li>
		 *         <li><tt>2</tt> if the selection is to be aborted</li>
		 *         </ul>
		 */
		private int checkAppendToPrevFile(final File file) {
			int result = 0;

			// Check if the file already exists and if it differs from the previous chosen one.
			// If so, the user can decide to append the information, to overwrite the file or to
			// abort the recording
			if (file.exists() && (file.length() > 0)
					&& ((lastSelectedRecordFilePath == null) || !recordFileString.equals(lastSelectedRecordFilePath))) {
				result = new MessageDialog(Display.getCurrent().getActiveShell(), "Append or Replace", null,
						"The file already exists. Shall the new information be appended or shall the file be replaced?", SWT.ICON_QUESTION,
						new String[] { "Append", "Replace", "Abort" }, 0).open();
				lastSelectedRecordFilePath = recordFileString;
			}
			return result;
		}

		// --------------------------------------------------------------------------------
		/**
		 * Starts the thread which consumes the packets which have been pushed into the recording
		 * queue previously
		 */
		private void startPacketConsumerThread(final boolean append) {

			packetConsumerThread = new Thread() {
				@SuppressWarnings("synthetic-access")
				@Override
				public void run() {
					try {

						FileOutputStream recordFileWriter = new FileOutputStream(getRecordFileString(), append);
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
												.debug("The packet recorder's packet consumer thread was interrupted while waiting for a notification of the arrival of a new packet");
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
		 * Returns the path to the file which is used for recording the input
		 * 
		 * @return the path to the file which is used for recording the input
		 */
		private String getRecordFileString() {
			return recordFileString;
		}

		// --------------------------------------------------------------------------
		/**
		 * Returns the file where the recoded data has to be saved.<br>
		 * If no file was specified, yet, a dialog will be opened to let the user choose one.
		 */
		private String getRecordFilePath() {
			if (recordFileString == null) {
				setRecordFile(selectRecodingFileByUser());
			}
			return recordFileString;
		}

		// --------------------------------------------------------------------------------
		/**
		 * Opens a message dialog for the user to select the recording file
		 * 
		 * @return the path to the file selected by the user
		 */
		@SuppressWarnings("synthetic-access")
		private String selectRecodingFileByUser() {
			final FileDialog fd = new FileDialog(Display.getCurrent().getActiveShell(), SWT.SAVE);
			fd.setFilterExtensions(new String[] { "*.rec" });
			fd.setFilterPath(recordDirectory);
			String path = null;
			boolean conflictingFileSelected = false;
			do {
				path = fd.open();
				conflictingFileSelected = false;
				if (path != null) {
					if (!path.endsWith(".rec")) {
						path += ".rec";
					}

					conflictingFileSelected = playbackModule.equalsPlaybackFilePath(path);
					if (isReadFromFile() && conflictingFileSelected) {

						MessageDialog.openError(null, "The file is already in use",
								"Sorry, the chosen file is already in use for playback. Please choose a different one ");

					}

					// if a file conflict was detected but the readFromFile mode is disabled, set
					// the
					// playback file to null
					else if (!isReadFromFile() && playbackModule.equalsPlaybackFilePath(path)) {
						playbackModule.setPlayBackFile(null);
						conflictingFileSelected = false;
					}
				}
			} while (conflictingFileSelected);
			return path;
		}

		// --------------------------------------------------------------------------------
		/**
		 * Records a provided packet in recording mode, discards them in normal mode
		 * 
		 * @param packet
		 *            the packet to be recorded
		 */
		@SuppressWarnings("synthetic-access")
		void handlePacket(final SpyglassPacket packet) {

			if (packet != null) {

				if (!record) {
					return;
				}
				synchronized (recordingQueue) {
					recordingQueue.offer(packet);
					recordingQueue.notify();
				}
			}
		}

		// --------------------------------------------------------------------------------
		/**
		 * Returns whether a provided path equals the one of the recording file
		 * 
		 * @param path
		 *            a path e.g. to the playback file
		 * @return <code>true</code> if the provided path equals the one of the recording file
		 */
		boolean equalsRecordingFilePath(final String path) {
			if ((path != null) && (recordFileString != null)) {
				return (new File(path).equals(new File(recordFileString)));
			}
			return false;
		}

		// --------------------------------------------------------------------------------
		/**
		 * Resets the recording module
		 */
		public void reset() {
			recordFileString = null;
			lastSelectedRecordFilePath = null;
			getPacketQueue().clear();
		}

		// --------------------------------------------------------------------------------
		/**
		 * Shuts the recording module down
		 * 
		 * @throws IOException
		 */
		public void shutdown() throws IOException {
			if ((packetConsumerThread != null) && !packetConsumerThread.isInterrupted()) {
				packetConsumerThread.interrupt();
			}
		}

	}

	// --------------------------------------------------------------------------------
	// --------------------------------------------------------------------------------
	// --------------------------------------------------------------------------------
	/**
	 * Module encapsulating play back functionality
	 * 
	 * @author Sebastian Ebers
	 */
	private class PlaybackModule {

		@Element(name = "packetReader", required = false)
		private ComplexPacketReader complexPacketReader = new ComplexPacketReader();

		// --------------------------------------------------------------------------------
		/**
		 * Constructor
		 */
		public PlaybackModule() {
			// nothing to do
		}

		// --------------------------------------------------------------------------------
		/**
		 * Initiates the module
		 * 
		 * @param spyglass
		 *            the spyglass instance
		 */
		public void init(final Spyglass spyglass) {
			complexPacketReader.init(spyglass);
			complexPacketReader.setDelayMillies(getDelayMillies());

			addPropertyChangeListener(new PropertyChangeListener() {
				// --------------------------------------------------------------------------------
				@SuppressWarnings("synthetic-access")
				@Override
				public void propertyChange(final PropertyChangeEvent evt) {
					if (evt.getPropertyName().equals("delayMillies")) {
						complexPacketReader.setDelayMillies((Integer) evt.getNewValue());
					}
				}
			});

		}

		// --------------------------------------------------------------------------------
		/**
		 * Opens a message dialog for the user to select the readFromFile file
		 * 
		 * @return the path to the file selected by the user
		 */
		@SuppressWarnings("synthetic-access")
		String selectPlayBackFileByUser() {
			final FileDialog fd = new FileDialog(Display.getCurrent().getActiveShell(), SWT.OPEN);
			fd.setFilterExtensions(new String[] { "*.rec" });
			fd.setFilterPath(recordDirectory);
			boolean isConflictingFileSelected = false;
			String path;
			do {
				isConflictingFileSelected = false;
				path = fd.open();
				isConflictingFileSelected = recModule.equalsRecordingFilePath(path);
				if (isRecord() && isConflictingFileSelected) {
					MessageDialog.openError(null, "The file is already in use",
							"Sorry, the chosen file is already in use for recording. Please choose a diferent one.");

				}
				// if a file conflict was detected but the recording mode is disabled, set the
				// recording file to null
				else if (isConflictingFileSelected && !isRecord()) {
					setRecordFile(null);
					isConflictingFileSelected = false;
				}
			} while (isConflictingFileSelected);
			return path;
		}

		// --------------------------------------------------------------------------
		/**
		 * Returns the file to be used to read the previously recorded packages
		 * 
		 * @return the file to be used to read the previously recorded packages
		 */
		public File getPlayBackFile() {
			if (complexPacketReader.getGateway() instanceof FileReaderGateway) {
				return ((FileReaderGateway) complexPacketReader.getGateway()).getFile();
			}
			return null;
		}

		// --------------------------------------------------------------------------
		/**
		 * Sets the file to be used to play back the previously recorded packages.
		 * 
		 * @param path
		 *            the path to the file
		 * @return <code>true</code> if a readFromFile file was set successfully
		 */
		@SuppressWarnings("synthetic-access")
		public boolean setPlayBackFile(final String path) {

			if (path != null) {

				boolean isConflictingFileSelected = recModule.equalsRecordingFilePath(path);
				if (isRecord() && isConflictingFileSelected) {
					Display.getDefault().syncExec(new Runnable() {
						@Override
						public void run() {
							MessageDialog.openError(null, "The file is already in use",
									"Sorry, the chosen file is already in use for recording.\r\nPlayback will not be started.");
						}
					});

				}
				// if a file conflict was detected but the recording mode is disabled, set the
				// recording file to null
				else if (isConflictingFileSelected && !isRecord()) {
					setRecordFile(null);
					isConflictingFileSelected = false;
				}

				if (!isConflictingFileSelected) {

					// get a mutex lock which will prevent the input stream to be accessed
					// externally
					synchronized (gatewayMutex) {
						// check whether the current Gateway is capable of processing a file
						Gateway gw = complexPacketReader.getGateway();
						if ((gw == null) || (!(gw instanceof FileReaderGateway))) {

							// if not, create a usable gateway and
							final FileReaderGateway frgw = new FileReaderGateway();
							frgw.setFile(new File(path));
							complexPacketReader.setGateway(frgw);
							gw = frgw;
						}

						((FileReaderGateway) gw).setFile(new File(path));

						setReadFromFile(complexPacketReader.getGateway().getInputStream() != null);
					}
				}
			} else {
				((FileReaderGateway) complexPacketReader.getGateway()).setFile(null);
				setReadFromFile(false);
			}
			return isReadFromFile();
		}

		// --------------------------------------------------------------------------
		/**
		 * Returns a new packet, once it arrives. It will never return null, but block until it has
		 * something to return.
		 * 
		 * @exception SpyglassPacketException
		 *                if the packet to return is invalid
		 * @exception InterruptedException
		 *                if the method was interrupted while waiting on a packet.
		 * @return a new SpyGlass packet
		 * 
		 */
		public SpyglassPacket getNextPacket() throws SpyglassPacketException, InterruptedException {
			return complexPacketReader.getNextPacket();
		}

		// --------------------------------------------------------------------------------
		/**
		 * Returns whether a provided path equals the one of the playback file
		 * 
		 * @param path
		 *            a path e.g. to the recording file
		 * @return <code>true</code> if the provided path equals the one of the playback file
		 */
		public boolean equalsPlaybackFilePath(final String path) {
			File in = null;
			if ((complexPacketReader.getGateway() instanceof FileReaderGateway) && (path != null)) {
				in = ((FileReaderGateway) complexPacketReader.getGateway()).getFile();
				return ((in != null) && in.equals(new File(path)));
			}
			return false;
		}

		// --------------------------------------------------------------------------------
		/**
		 * Resets the playback module
		 * 
		 * @throws IOException
		 */
		void reset() throws IOException {
			complexPacketReader.reset();
		}

	}

}
