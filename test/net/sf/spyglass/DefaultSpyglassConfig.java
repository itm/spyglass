package net.sf.spyglass;

import java.io.File;

import net.sf.spyglass.core.SpyglassConfiguration;
import net.sf.spyglass.drawing.Canvas2D;
import net.sf.spyglass.drawing.SpyglassCanvas;
import net.sf.spyglass.drawing.primitive.Rectangle;
import net.sf.spyglass.gateway.FileReaderGateway;
import net.sf.spyglass.packet.PacketReader;
import net.sf.spyglass.packet.SimplePacketReader;
import net.sf.spyglass.plugin.*;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.load.Persister;

public class DefaultSpyglassConfig
{
	public static void main( String[] args ) throws Exception
	{
		SpyglassConfiguration config = new SpyglassConfiguration();
		
		/*
		 * Create objects
		 */

		Rectangle defaultNodePainterDrawingObject = new Rectangle();
		defaultNodePainterDrawingObject.setColorR( 200 );
		defaultNodePainterDrawingObject.setColorG( 0 );
		defaultNodePainterDrawingObject.setColorB( 0 );
		defaultNodePainterDrawingObject.setWidth( 20 );
		defaultNodePainterDrawingObject.setHeight( 20 );

		Plugin p = new NodePainterPlugin();
		p.setDefaultDrawingObject( defaultNodePainterDrawingObject );
		
		PluginManager pm = new PluginManager();
		pm.addPlugin( p );
		
		FileReaderGateway gateway = new FileReaderGateway();
		gateway.setFile( new File( "source.packet.txt" ) );
		
		SpyglassCanvas canvas = new Canvas2D();
		
		PacketReader packetReader = new SimplePacketReader();
		packetReader.setGateway( gateway );
		
		
		/*
		 * Injection
		 */

		
		config.setPluginManager( pm );
		config.setCanvas( canvas );
		config.setPacketReader( packetReader );
		
		/*
		 * Serialize configuration
		 */
		
		Serializer serializer = new Persister();
		File file = new File( "test/net/sf/spyglass/config.xml" );
		serializer.write( config, file );
	}
}
