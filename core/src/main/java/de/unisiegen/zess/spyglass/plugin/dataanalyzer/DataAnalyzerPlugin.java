package de.unisiegen.zess.spyglass.plugin.dataanalyzer;

import java.util.Timer;
import java.util.TimerTask;

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

/**
 */
public class DataAnalyzerPlugin /*extends Plugin implements GlobalInformation*/ {
	public native int ProcessData(byte[] packet);
	public native byte[] AnalyzeData();

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
			//System.loadLibrary("ClusterInfo.dll");			
			/*dllok = true;

			String j = "Felditentifikation_Lib.dll";
			int len  = j.length();
			byte[] data = new byte[2 + len];
			data[0] = 0;
			for (int i = 1; i <= len; i++) {
				data[i] = (byte)j.charAt(i - 1);
			}
			
			int r = ProcessData(data);

			String k = "Cluster-vollständig.xml";
			int len2  = k.length();
			byte[] data2 = new byte[2 + len2];
			data2[0] = 1;
			for (int i = 1; i <= len2; i++)
			{
				data2[i] = (byte)k.charAt(i - 1);
			}
			
			int r2 = ProcessData(data2);*/

			// set IP of Handheld			
			/*byte[] data3 = new byte[5];
			data3[0] = 9;
			data3[1] = 127;
			data3[2] = 0;
			data3[3] = 0;
			data3[4] = 1;
			int r3 = ProcessData(data3);*/
		}
		catch(Exception e) {
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

		if (dllok) {
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
		String[] result = (new String(buf)).split(",");
		for (int i = 0; i < result.length; ++i) {
			int ptype = Integer.parseInt(result[i]);
	
			switch (ptype) {
				case -1: { // Grid event
					if (i + 6 > result.length) {
						throw new Exception("Error parsing data from Movedetect DLL!");
					}
					byte pkg[] = new byte[SpyglassGridPackage.PACKET_SIZE];
					pkg[0] = 0;
					pkg[1] = SpyglassGridPackage.PACKET_SIZE;
					pkg[2] = 2;
					pkg[3] = 7;
					pkg[4] = SpyglassGridPackage.PACKET_TYPE;
					
					rows = Integer.parseInt(result[i+1]);		// cols
					cols = Integer.parseInt(result[i+2]);		// rows
					gwidth = Integer.parseInt(result[i+3]);		// grid element width
					gheight = Integer.parseInt(result[i+4]);	// grid element height

					SerializeUInt16(cols, pkg, 25);
					SerializeUInt16(rows, pkg, 23);
					SerializeUInt16(gwidth*cols, pkg, 21);
					SerializeUInt16(gheight*rows, pkg, 19);
					
					SpyglassGridPackage gp = new SpyglassGridPackage(pkg);
					if (pkgReader != null) {
						pkgReader.InjectPackage(gp);
					}

					pkg = new byte[SpyglassNorthPackage.PACKET_SIZE];
					pkg[0] = 0;
					pkg[1] = SpyglassNorthPackage.PACKET_SIZE;
					pkg[2] = 2;
					pkg[3] = 7;
					pkg[4] = SpyglassNorthPackage.PACKET_TYPE;
					SerializeUInt16(Integer.parseInt(result[i+5]), pkg, 19);	// north direction
					SpyglassNorthPackage nop = new SpyglassNorthPackage(pkg);
					if (pkgReader != null) {
						pkgReader.InjectPackage(nop);
					}
					i += 5;
				}
				break;
				case -2: { // New node event
					if (i + 9 > result.length) {
						throw new Exception("Error parsing data from Movedetect DLL!");
					}
					byte pkg[] = new byte[SpyglassNodePackage.PACKET_SIZE];
					pkg[0] = 0;
					pkg[1] = SpyglassNodePackage.PACKET_SIZE;
					pkg[2] = 2;
					pkg[3] = 7;
					pkg[4] = SpyglassNodePackage.PACKET_TYPE;
					
					int gnum = Integer.parseInt(result[i+2])-1;
					int col = gnum % cols;
					int row = gnum / cols;
					int px = Integer.parseInt(result[i+3]);
					int py = Integer.parseInt(result[i+4]);

					SerializeUInt16(Integer.parseInt(result[i+1],16), pkg, 5);	// ID
					SerializeUInt16(px+col*gwidth, pkg, 13);			// Pos x
					SerializeUInt16(py+row*gheight, pkg, 15);			// Pos y
					SerializeUInt8(Integer.parseInt(result[i+5]), pkg, 19);		// PIR Type
					SerializeUInt16(Integer.parseInt(result[i+5]), pkg, 20);	// Orientation
					SerializeUInt8(Integer.parseInt(result[i+6]), pkg, 22);		// Has Magnetsensor
					SerializeUInt8(Integer.parseInt(result[i+7]), pkg, 23);		// Has Geophon


					SpyglassNodePackage np = new SpyglassNodePackage(pkg);
					if (pkgReader != null) {
						pkgReader.InjectPackage(np);
					}
					i += 8;
				}
				break;
				case -33: {	// List of active grid elements
					if (i + 4 > result.length) {
						throw new Exception("Error parsing data from Movedetect DLL!");
					}
					
					byte pkg[] = new byte[SpyglassGridActivityPackage.PACKET_SIZE];
					pkg[0] = 0;
					pkg[1] = SpyglassGridActivityPackage.PACKET_SIZE;
					pkg[2] = 2;
					pkg[3] = 7;
					pkg[4] = SpyglassGridActivityPackage.PACKET_TYPE;

					int pos = i + 3;
					int gnum = Integer.parseInt(result[pos])-1;
					while (pos < result.length && gnum >= 1) {
						
						int col = gnum % cols;
						int row = gnum / cols;

						SerializeUInt16(row, pkg, 19);
						SerializeUInt16(col, pkg, 21);

						SpyglassGridActivityPackage ap = new SpyglassGridActivityPackage(pkg);
						if (pkgReader != null) {
							pkgReader.InjectPackage(ap);
						}
						pos++;
						gnum = Integer.parseInt(result[pos])-1;
					}
					i = pos - 1;
				}
				break;
				case -22: {	// classification: 0 = Person, 1 = Person + Waffe, 2 = Auto
					if (i + 2 > result.length) {
						throw new Exception("Error parsing data from Movedetect DLL!");
					}

					byte pkg[] = new byte[SpyglassClassificationPackage.PACKET_SIZE];
					pkg[0] = 0;
					pkg[1] = SpyglassClassificationPackage.PACKET_SIZE;
					pkg[2] = 2;
					pkg[3] = 7;
					pkg[4] = SpyglassClassificationPackage.PACKET_TYPE;
			
					SerializeUInt8(Integer.parseInt(result[i+1]), pkg, 19); // type
					
					SpyglassGridPackage cp = new SpyglassGridPackage(pkg);
					if (pkgReader != null) {
						pkgReader.InjectPackage(cp);
					}
					i += 1;
				}
				break;
				case -11: {	// IDs of sensornode which should activate their magnetic sensors
					if (i + 2 > result.length) {
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
					throw new Exception("Error parsing data from Movedetect DLL!");
			}
		}
	}

	private void SerializeUInt16(int value, byte[] msg, int pos) {
		if (msg == null || msg.length - 2 < pos) return;
		msg[pos] = (byte) (value & 0xFF);
		msg[pos+1] = (byte)  (value>>8 & 0xFF);
	}

	private void SerializeUInt8(int value, byte[] msg, int pos) {
		if (msg == null || msg.length - 1 < pos) return;
		msg[pos] = (byte) (value & 0xFF);
	}
}


