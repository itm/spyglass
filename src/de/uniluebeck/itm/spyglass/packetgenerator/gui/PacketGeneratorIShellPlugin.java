package de.uniluebeck.itm.spyglass.packetgenerator.gui;

import ishell.device.MessagePacket;
import ishell.plugins.Plugin;
import ishell.util.IconTheme;

import java.io.File;

import org.apache.log4j.Logger;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.load.Persister;

import de.uniluebeck.itm.spyglass.packetgenerator.PacketGenerator;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;

/**
 * This is an iShell Plugin which wraps the Spyglass PacketGenerator.
 * 
 * Currently it has no GUI and reads the configfile from a fixed position (see below). Otherwise it
 * behaves exactly like if it was run from the command line.
 * 
 * @author dariush
 * 
 */
public class PacketGeneratorIShellPlugin extends Plugin {
	private static Logger log = SpyglassLoggerFactory.getLogger(PacketGeneratorIShellPlugin.class);
	
	/**
	 * The packet generator
	 */
	PacketGenerator generator;
	
	/**
	 * reference to the thread.
	 */
	Thread generatorThread;
	
	/**
	 * SWT object
	 */
	IShellTab gui;
	
	/**
	 * This thread runs in the background and creates packets and sends them over the socket to
	 * ishell.
	 * 
	 * 
	 * @author dariush
	 * 
	 */
	private class GeneratorThread extends Thread {
		
		public GeneratorThread() {
			this.setName("PacketGeneratorThread");
		}
		
		@Override
		public void run() {
			
			try {
				generator.run();
			} catch (final Exception e) {
				log.error("", e);
			}
			
		}
	}
	
	// --------------------------------------------------------------------------
	// ------
	/*
	 * (non-Javadoc)
	 * 
	 * @see ishell.plugins.Plugin#init()
	 */
	@Override
	public int[] init() {
		
		final CTabItem tabItem = getTabItem(getName());
		tabItem.setImage(IconTheme.lookup("system-search"));
		
		final Composite container = this.getTabContainer(true);
		container.setLayout(new FillLayout());
		container.addControlListener(new ControlListener() {
			// TODO: anything to listen??
			@Override
			public void controlMoved(final ControlEvent e) {
				// Nothing to do
			}
			
			@Override
			public void controlResized(final ControlEvent e) {
				//
			}
		});
		
		final PluginGeneratorGUIActions actions = new PluginGeneratorGUIActions(this);
		addToolBarAction(actions.PLAY_PLAY_PAUSE);
		
		gui = new IShellTab(container, this);
		
		startGenerator();
		
		return new int[] {};
	}
	
	void startGenerator() {
		final File configFile = new File(gui.getConfigPath());
		
		log.debug("Reading config from file: " + configFile);
		
		if (!configFile.isFile()) {
			throw new RuntimeException("Can't find config file '" + configFile + "'");
		}
		
		final Serializer serializer = new Persister();
		try {
			generator = serializer.read(PacketGenerator.class, configFile);
		} catch (final Exception e) {
			// TODO Auto-generated catch block
			log.error("", e);
		}
		
		if (generator == null) {
			throw new RuntimeException("Could not load configuration.");
		}
		
		generatorThread = new GeneratorThread();
		generatorThread.start();
	}
	
	// --------------------------------------------------------------------------
	// ------
	/*
	 * (non-Javadoc)
	 * 
	 * @see ishell.plugins.Plugin#shutdown()
	 */
	@Override
	public void shutdown() {
		stopGenerator();
		log.info("Stopped the packet generator.");
	}
	
	void stopGenerator() {
		this.generator.shutdown();
	}
	
	// --------------------------------------------------------------------------
	// ------
	/*
	 * (non-Javadoc)
	 * 
	 * @see ishell.plugins.Plugin#getName()
	 */
	@Override
	public String getName() {
		return "SpyGlassPacketGenerator";
	}
	
	// --------------------------------------------------------------------------
	// ------
	/*
	 * (non-Javadoc)
	 * 
	 * @see ishell.plugins.Plugin#getDescription()
	 */
	@Override
	public String getDescription() {
		return getName() + " is a generator for spyglass packets.";
	}
	
	// --------------------------------------------------------------------------
	// ------
	/*
	 * (non-Javadoc)
	 * 
	 * @see ishell.device.iSenseDeviceListenerAdapter#receivePacket(ishell.device .MessagePacket)
	 */
	@Override
	public void receivePacket(final MessagePacket arg0) {
		
	}
	
}
