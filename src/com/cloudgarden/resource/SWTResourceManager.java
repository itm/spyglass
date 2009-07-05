package com.cloudgarden.resource;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;

/**
 * Class to manage SWT resources (Font, Color, Image and Cursor) There are no restrictions on the
 * use of this code.
 * 
 * You may change this code and your changes will not be overwritten, but if you change the version
 * number below then this class will be completely overwritten by Jigloo.
 * #SWTResourceManager:version4.0.0#
 */
public class SWTResourceManager {

	@SuppressWarnings("unchecked")
	private static HashMap resources = new HashMap();
	@SuppressWarnings("unchecked")
	private static Vector users = new Vector();
	private static SWTResourceManager instance = new SWTResourceManager();

	private static DisposeListener disposeListener = new DisposeListener() {
		@SuppressWarnings("synthetic-access")
		public void widgetDisposed(final DisposeEvent e) {
			users.remove(e.getSource());
			if (users.size() == 0) {
				dispose();
			}
		}
	};

	/**
	 * This method should be called by *all* Widgets which use resources provided by this
	 * SWTResourceManager. When widgets are disposed, they are removed from the "users" Vector, and
	 * when no more registered Widgets are left, all resources are disposed.
	 * <P>
	 * If this method is not called for all Widgets then it should not be called at all, and the
	 * "dispose" method should be explicitly called after all resources are no longer being used.
	 */
	@SuppressWarnings("unchecked")
	public static void registerResourceUser(final Widget widget) {
		if (users.contains(widget)) {
			return;
		}
		users.add(widget);
		widget.addDisposeListener(disposeListener);
	}

	@SuppressWarnings("unchecked")
	public static void dispose() {
		final Iterator it = resources.keySet().iterator();
		while (it.hasNext()) {
			final Object resource = resources.get(it.next());
			if (resource instanceof Font) {
				((Font) resource).dispose();
			} else if (resource instanceof Color) {
				((Color) resource).dispose();
			} else if (resource instanceof Image) {
				((Image) resource).dispose();
			} else if (resource instanceof Cursor) {
				((Cursor) resource).dispose();
			}
		}
		resources.clear();
	}

	public static Font getFont(final String name, final int size, final int style) {
		return getFont(name, size, style, false, false);
	}

	@SuppressWarnings("unchecked")
	public static Font getFont(final String name, final int size, final int style, final boolean strikeout, final boolean underline) {
		final String fontName = name + "|" + size + "|" + style + "|" + strikeout + "|" + underline;
		if (resources.containsKey(fontName)) {
			return (Font) resources.get(fontName);
		}
		final FontData fd = new FontData(name, size, style);
		if (strikeout || underline) {
			try {
				final Class lfCls = Class.forName("org.eclipse.swt.internal.win32.LOGFONT");
				final Object lf = FontData.class.getField("data").get(fd);
				if ((lf != null) && (lfCls != null)) {
					if (strikeout) {
						lfCls.getField("lfStrikeOut").set(lf, new Byte((byte) 1));
					}
					if (underline) {
						lfCls.getField("lfUnderline").set(lf, new Byte((byte) 1));
					}
				}
			} catch (final Throwable e) {
				System.err.println("Unable to set underline or strikeout" + " (probably on a non-Windows platform). " + e);
			}
		}
		final Font font = new Font(Display.getDefault(), fd);
		resources.put(fontName, font);
		return font;
	}

	public static Image getImage(final String url, final Control widget) {
		final Image img = getImage(url);
		if ((img != null) && (widget != null)) {
			img.setBackground(widget.getBackground());
		}
		return img;
	}

	@SuppressWarnings("unchecked")
	public static Image getImage(String url) {
		try {
			url = url.replace('\\', '/');
			if (url.startsWith("/")) {
				url = url.substring(1);
			}
			if (resources.containsKey(url)) {
				return (Image) resources.get(url);
			}
			final Image img = new Image(Display.getDefault(), instance.getClass().getClassLoader().getResourceAsStream(url));
			if (img != null) {
				resources.put(url, img);
			}
			return img;
		} catch (final Exception e) {
			System.err.println("SWTResourceManager.getImage: Error getting image " + url + ", " + e);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public static Color getColor(final int red, final int green, final int blue) {
		final String name = "COLOR:" + red + "," + green + "," + blue;
		if (resources.containsKey(name)) {
			return (Color) resources.get(name);
		}
		final Color color = new Color(Display.getDefault(), red, green, blue);
		resources.put(name, color);
		return color;
	}

	@SuppressWarnings("unchecked")
	public static Cursor getCursor(final int type) {
		final String name = "CURSOR:" + type;
		if (resources.containsKey(name)) {
			return (Cursor) resources.get(name);
		}
		final Cursor cursor = new Cursor(Display.getDefault(), type);
		resources.put(name, cursor);
		return cursor;
	}

}
