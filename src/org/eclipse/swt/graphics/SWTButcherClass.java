// --------------------------------------------------------------------------------
/**
 * 
 */
package org.eclipse.swt.graphics;


// --------------------------------------------------------------------------------
/**
 * @author Dariush Forouher
 *
 */
public class SWTButcherClass {
	
	/**
	 * Enable Tracking & Debugging on a Display object after it has been created.
	 * 
	 * Note: This bypasses every SWT API, so expect problems!
	 * I've only tested this with SWT 3.4.1 for GTK.
	 * 
	 * There is absolutely no support!
	 */
	public static void enableTracking(final Device d) {
		
		/* This is a hack of the worst kind. I'm not responsible
		 * if this wracks your computer. */
		Device.DEBUG = true;
		d.debug = true;
		d.errors = new Error [128];
		d.objects = new Object [128];
		//d.trackingLock = new Object();
		d.tracking = true;
	}
}
