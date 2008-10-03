package de.uniluebeck.itm.spyglass.packet;

/**
 * Exception, thrown if deserialize fails.
 * 
 * @author Nils Glombitza, ITM Uni Luebeck
 * 
 */
public class SpyglassPacketException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	public SpyglassPacketException() {
	}
	
	public SpyglassPacketException(final String msg) {
		super(msg);
	}
	
	public SpyglassPacketException(final String message, final Throwable cause) {
		super(message, cause);
	}
	
	public SpyglassPacketException(final Throwable cause) {
		super(cause);
	}
	
}
