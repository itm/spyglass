package de.unisiegen.zess.spyglass.plugin.dataanalyzer;

import de.uniluebeck.itm.spyglass.gui.actions.RecordRecordAction;
import java.io.File;
import com.google.common.collect.Lists;
import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.io.SpyglassPacketRecorder;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacket;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacketException;
import de.uniluebeck.itm.spyglass.util.StringTuple;
import de.uniluebeck.itm.tr.util.StringUtils;
import eu.wisebed.api.common.Message;
import eu.wisebed.api.controller.Controller;
import eu.wisebed.api.controller.RequestStatus;
import eu.wisebed.api.sm.SecretReservationKey;
import eu.wisebed.api.sm.SessionManagement;
import eu.wisebed.testbed.api.wsn.WSNServiceHelper;
import java.util.logging.Level;
import org.eclipse.jface.dialogs.MessageDialog;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.Thread;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.ws.Endpoint;
import java.io.IOException;
import java.util.List;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.Date;
import java.util.Timer;
import java.util.Vector;
import java.text.SimpleDateFormat;
import java.util.TimerTask;

import de.unisiegen.zess.spyglass.plugin.dataanalyzer.DataAnalyzerPlugin;
import de.unisiegen.zess.spyglass.plugin.dataanalyzer.Injectable;

import static com.google.common.collect.Lists.newArrayList;

public class ZessPacketRecorder extends SpyglassPacketRecorder implements Injectable {

    private static final Logger log = LoggerFactory.getLogger(ZessPacketRecorder.class);
    private Spyglass spg;
    private DataAnalyzerPlugin dataPlugin;
	
	@Element(required = true)
    private String logFile;
    private Timer timer = null;
	private BufferedReader reader = null;
	private Vector<String> newLines = new Vector<String>();
	private SimpleDateFormat timeParser = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.S'Z'");;
	private long startTimeDiff;

	public ZessPacketRecorder() {
		log.debug("Construct ZessPacketRecorder without fileName");
	}
    public ZessPacketRecorder(String fileName) {
		log.debug("Construct ZessPacketRecorder using file " + fileName);
    	logFile = fileName;
    }


    @Override
    public void init(final Spyglass spyglass) {
		log.debug("Init ZessPacketRecorder...");
        super.init(spyglass);
        spg = spyglass;

        dataPlugin = new DataAnalyzerPlugin();
        dataPlugin.init(this);

		timer = new Timer("ZESS Packet Recorder Timer");
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				ReadFromFile();
			}
		}, 1000, 1000);
    }

    @Override
	public void shutdown() throws IOException {
		log.debug("Shutting down ZessPacketRecorder...");

		if (dataPlugin != null) {
			dataPlugin.shutdown();
			dataPlugin = null;
			System.gc();
		}

		if (timer != null) {
			timer.cancel();		
		}

		if (reader != null) {
			reader.close();
		}

		super.shutdown();
	}

    public void InjectPackage(SpyglassPacket pkg) {
        add(pkg);
    }

	public void ReadFromFile() {
		if (reader == null) {
			try {
				reader = new BufferedReader(new FileReader(logFile));
				do {
					String line = reader.readLine();
					log.debug("Read line: " + line);
					
					if (IsValidLine(line)) {
						String[] parts = line.split("\\|");
						startTimeDiff = (new Date()).getTime() - timeParser.parse(parts[0].trim()).getTime();
					
						log.debug("Start reading from file. Starting time difference is " + startTimeDiff);
						newLines.add(line);
						return;
					}
				} while (reader.ready());
			}
			catch (Exception e) {
				timer.cancel();
				log.debug("Log file is invalid. Not reading it.");
				e.printStackTrace();
				return;
			}		
			timer.cancel();
			log.debug("No start time found in log file. Stopped reading it.");
			return;
		}

		Date currentTime = new Date();
		do {
			String line;
			if (!newLines.isEmpty()) {
				line = newLines.remove(0);
			}
			else {
				try {
					line = reader.readLine();
					log.debug("Read line: " + line);
				}
				catch (Exception e) {
					timer.cancel();
					log.debug("Log file is invalid. Stopped reading it.");
					return;
				}
			}

			if (IsValidLine(line)) {
				String[] parts = line.split("\\|");
				try {
					Date ltime = timeParser.parse(parts[0].trim());
					long diff = currentTime.getTime() - startTimeDiff;
					if (diff >= ltime.getTime()) {
						log.debug("Read from file. time is " + ltime.toString());
											
						dataPlugin.processPacket(ToByteArray(parts[3].trim()));
					}
					else {
						newLines.add(0, line);
						return;
					}
				}
				catch (Exception e) {return;}
			}
			else {
				timer.cancel();
				log.debug("Log file contains invalid line. Stopped reading it.");
				return;
			}
		} while (true);			
	}

	private boolean IsValidLine(String line) {
		if (line == null) {return false;}
		int cnt = 0;
		for (int i = 0; i < line.length(); ++i) {
			if (line.charAt(i) == '|') {
				cnt++;
			}
		}
		if (cnt >= 3) {return true;}
		return false;
	}

	private byte[] ToByteArray(String line) {
		String parts[] = line.split(" ");
		if (parts.length <= 1) {return null;}
		byte b[] = new byte[parts.length-1];
		for (int i = 1; i < parts.length; ++i) {
			b[i-1] = Integer.decode(parts[i]).byteValue();
		}
		return b;
	}
}
