package de.unisiegen.zess.spyglass.plugin.dataanalyzer;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferenceDialog;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;
import de.uniluebeck.itm.spyglass.gui.view.GlobalInformationWidget;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacket;
import de.uniluebeck.itm.spyglass.plugin.GlobalInformation;
import de.uniluebeck.itm.spyglass.plugin.Plugin;
import de.uniluebeck.itm.spyglass.plugin.PluginManager;
import de.uniluebeck.itm.spyglass.plugin.positionpacketnodepositioner.PositionPacketNodePositionerPreferencePage;
import de.uniluebeck.itm.spyglass.plugin.positionpacketnodepositioner.PositionPacketNodePositionerXMLConfig;
import de.uniluebeck.itm.spyglass.plugin.simplenodepainter.SimpleNodePainterPlugin;
import de.uniluebeck.itm.spyglass.plugin.simplenodepainter.SimpleNodePainterPreferencePage;
import de.uniluebeck.itm.spyglass.plugin.simplenodepainter.SimpleNodePainterXMLConfig;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;
import de.uniluebeck.itm.spyglass.io.wisebed.WisebedPacketReader;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;

/**
 */
public class DataAnalyzerPlugin /*extends Plugin implements GlobalInformation*/ {
	public native int ProcessData(byte[] packet);
	public native byte[] AnalyzeData();

	private static final Logger log = SpyglassLoggerFactory.getLogger(DataAnalyzerPlugin.class);

	private int cols = 0;
	private int rows = 0;
	private int gwidth = 0;
	private int gheight = 0;
	private Timer timer = null;
	private boolean dllok = false;
	private WisebedPacketReader pkgReader = null;

	//private DataAnalyzerXMLConfig xmlConfig;
	
	/**
	 * Constructor
	 */
	public DataAnalyzerPlugin() {
		//super(true);
		//xmlConfig = new DataAnalyzerXMLConfig();
		try {
			log.debug("Loading library ...");
			System.load("c:\\upload\\clusterinfo.dll");
			System.out.println("ZESS: Library loaded");
			dllok = true;

			String j = "c:\\upload\\Felditentifikation_Lib.dll";
			int len  = j.length();
			byte[] data = new byte[2 + len];
			data[0] = 0;
			for (int i = 1; i <= len; i++) {
				data[i] = (byte)j.charAt(i - 1);
			}
			
			int r = ProcessData(data);

			String k = "c:\\upload\\Cluster-map.xml";
			int len2  = k.length();
			byte[] data2 = new byte[2 + len2];
			data2[0] = 1;
			for (int i = 1; i <= len2; i++)
			{
				data2[i] = (byte)k.charAt(i - 1);
			}
			
			int r2 = ProcessData(data2);

			// set IP of Handheld			
			/*byte[] data3 = new byte[5];
			data3[0] = 9;
			data3[1] = 127;
			data3[2] = 0;
			data3[3] = 0;
			data3[4] = 1;
			int r3 = ProcessData(data3);*/
		}
		catch(Error e) {
			System.out.println("ZESS: Error loading library on " + System.getProperty("java.library.path"));
			e.printStackTrace();
		}
	}

	public static String getHumanReadableName() {
		return "Data Analyzer";
	}

	/*@Override
	public String toString() {
		return DataAnalyzerPlugin.getHumanReadableName() + "." + this.getInstanceName();
	}*/

	//@Override
	public void init(WisebedPacketReader reader) { /*final PluginManager manager) throws Exception {
		super.init(manager);*/
		pkgReader = reader;
	
		log.debug("Init ...");
		if (dllok) {
			log.debug("Starting Timer ...");
			timer = new Timer("Data Analysis Timer");
			timer.schedule(new TimerTask() {
			    @Override
			    public void run() {
				try {
					Parse(AnalyzeData());
				}
				catch(Exception e) {
					e.printStackTrace();
				}
		
			    }
			}, 1000, 750);
		}
	}

	/*@Override
	public PluginPreferencePage<DataAnalyzerPlugin, DataAnalyzerXMLConfig> createPreferencePage(final PluginPreferenceDialog dialog,
			final Spyglass spyglass) {
		return new DataAnalyzerPreferencePage(dialog, spyglass, this);
	}

	public static PluginPreferencePage<DataAnalyzerPlugin, DataAnalyzerXMLConfig> createTypePreferencePage(
			final PluginPreferenceDialog dialog, final Spyglass spyglass) {
		return new DataAnalyzerPreferencePage(dialog, spyglass);
	}
	
	@Override
	public PluginXMLConfig getXMLConfig() {
		return xmlConfig;
	}*/

	//@Override
	public void processPacket(SpyglassPacket packet) {
		if (dllok) {
			ProcessData(packet.getPayload());
		}
	}

	/*@Override
	protected void resetPlugin() {		
	}

	@Override
	public void setWidget(GlobalInformationWidget widget) {
		widget.setShow(false);
	}*/

	private void Parse(final byte[] buf) throws Exception {
		if (buf == null) {
			return;
		}
		
		log.debug("Parse: " + new String(buf));
	
		String[] result = (new String(buf)).split(",");
		for (int i = 0; i < result.length; ++i) {
			int ptype = Integer.parseInt(result[i]);

			switch (ptype) {
				case -1: { // Grid event
					if (i + 6 > result.length) {
						log.debug("Error parsing data from Movedetect DLL!");
						throw new Exception("Error parsing data from Movedetect DLL!");
					}
					byte pkg[] = new byte[SpyglassGridPackage.PACKET_SIZE+2];
					pkg[0] = 0;
					pkg[1] = SpyglassGridPackage.PACKET_SIZE;
					pkg[2] = 2;
					pkg[3] = 7;
					pkg[4] = SpyglassGridPackage.PACKET_TYPE;
					
					cols = Integer.parseInt(result[i+1]);		// cols
					rows = Integer.parseInt(result[i+2]);		// rows
					gwidth = Integer.parseInt(result[i+3]);		// grid element width
					gheight = Integer.parseInt(result[i+4]);	// grid element height

					SerializeUInt16(cols, pkg, 25);
					SerializeUInt16(rows, pkg, 23);
					SerializeUInt16(gwidth*cols, pkg, 21);
					SerializeUInt16(gheight*rows, pkg, 19);
					
					SpyglassGridPackage gp = new SpyglassGridPackage(pkg);
					if (pkgReader != null) {
						pkgReader.InjectPackage(gp);
						log.debug("New package: " + gp.toString());
					}

					pkg = new byte[SpyglassNorthPackage.PACKET_SIZE+2];
					pkg[0] = 0;
					pkg[1] = SpyglassNorthPackage.PACKET_SIZE;
					pkg[2] = 2;
					pkg[3] = 7;
					pkg[4] = SpyglassNorthPackage.PACKET_TYPE;
					SerializeUInt16(Integer.parseInt(result[i+5]), pkg, 19);	// north direction
					SpyglassNorthPackage nop = new SpyglassNorthPackage(pkg);
					if (pkgReader != null) {
						pkgReader.InjectPackage(nop);
						log.debug("New package: " + nop.toString());
					}
					i += 5;
				}
				break;
				case -2: 
					{ // New node event
					if (i + 9 > result.length) {
						throw new Exception("Error parsing data from Movedetect DLL!");
					}

					byte pkg[] = new byte[SpyglassNodePackage.PACKET_SIZE+2];
					int type = Integer.parseInt(result[i+5]);
					boolean hasGeo = Integer.parseInt(result[i+8]) != 0;

					pkg[0] = 0;
					pkg[1] = SpyglassNodePackage.PACKET_SIZE;
					pkg[2] = 2;
					pkg[3] = 7;
					pkg[4] = SpyglassNodePackage.PACKET_TYPE(type, hasGeo);

					int gnum = Integer.parseInt(result[i+2])-1;
					int col = gnum % cols;
					int row = gnum / cols;
					int px = Integer.parseInt(result[i+3]);
					int py = Integer.parseInt(result[i+4]);

					SerializeUInt16(Integer.parseInt(result[i+1],16), pkg, 5);	// ID
					SerializeUInt16(px+col*gwidth, pkg, 13);			// Pos x
					SerializeUInt16(py+row*gheight, pkg, 15);			// Pos y
					SerializeUInt16(Integer.parseInt(result[i+6]), pkg, 19);	// Orientation

					SpyglassNodePackage np = new SpyglassNodePackage(pkg);
					if (pkgReader != null) {
						pkgReader.InjectPackage(np);
						log.debug("New package: " + np.toString());
					}

					i += 8;
				}
				break;
				case -33: {	// List of active grid elements
					if (i + 3 > result.length) {
						log.debug("Error parsing data from Movedetect DLL!");
						throw new Exception("Error parsing data from Movedetect DLL!");
					}
					
					byte pkg[] = new byte[SpyglassGridActivityPackage.PACKET_SIZE+2];
					pkg[0] = 0;
					pkg[1] = SpyglassGridActivityPackage.PACKET_SIZE;
					pkg[2] = 2;
					pkg[3] = 7;
					pkg[4] = SpyglassGridActivityPackage.PACKET_TYPE;

					int pos = i + 3;
		
					while (pos < result.length) {
						int gnum = Integer.parseInt(result[pos])-1;
						if (gnum < 0) {
							break;
						}

						int col = gnum % cols;
						int row = gnum / cols;

						SerializeUInt16(col*gwidth+gwidth/2, pkg, 13);			// Pos x
						SerializeUInt16(row*gheight+gheight/2, pkg, 15);			// Pos y

						SpyglassGridActivityPackage ap = new SpyglassGridActivityPackage(pkg);
						if (pkgReader != null) {
							pkgReader.InjectPackage(ap);
							log.debug("New package: " + ap.toString());
						}
						pos++;
					}
					i = pos - 1;
				}
				break;
				case -22: {	// classification: 0 = Person, 1 = Person + Waffe, 2 = Auto
					if (i + 2 > result.length) {
						log.debug("Error parsing data from Movedetect DLL!");
						throw new Exception("Error parsing data from Movedetect DLL!");
					}

					int type = Integer.parseInt(result[i+1]);

					byte pkg[] = new byte[SpyglassClassificationPackage.PACKET_SIZE+2];
					pkg[0] = 0;
					pkg[1] = SpyglassClassificationPackage.PACKET_SIZE;
					pkg[2] = 2;
					pkg[3] = 7;
					pkg[4] = SpyglassClassificationPackage.PACKET_TYPE(type);
			
					SerializeUInt16(0, pkg, 13);			// Pos x
					SerializeUInt16(0, pkg, 15);			// Pos y

					
					SpyglassClassificationPackage cp = new SpyglassClassificationPackage(pkg);
					if (pkgReader != null) {
						pkgReader.InjectPackage(cp);
						log.debug("New package: " + cp.toString());
					}
					i += 1;
				}
				break;
				case -11: {	// IDs of sensornode which should activate their magnetic sensors
					if (i + 2 > result.length) {
						log.debug("Error parsing data from Movedetect DLL!");
						throw new Exception("Error parsing data from Movedetect DLL!");
					}
					
					while (i + 1 < result.length) {
						int ID = Integer.parseInt(result[i+1],16);
						// Activate sensor node with id ID as int or with id result[i+1] as string (hex)
						i++;
					}
				}
				break;
				default: // parse error
					log.debug("Error parsing data from Movedetect DLL!");
					throw new Exception("Error parsing data from Movedetect DLL!");
			}
		}
	}

	private void SerializeUInt16(int value, byte[] msg, int pos) {
		if (msg == null || msg.length - 2 < pos) return;
		msg[pos+1] = (byte) (value & 0xFF);
		msg[pos] = (byte)  (value>>8 & 0xFF);
	}

	private void SerializeUInt8(int value, byte[] msg, int pos) {
		if (msg == null || msg.length - 1 < pos) return;
		msg[pos] = (byte) (value & 0xFF);
	}
}


