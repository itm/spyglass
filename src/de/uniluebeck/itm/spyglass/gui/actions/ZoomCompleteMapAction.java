package de.uniluebeck.itm.spyglass.gui.actions;

import ishell.util.Logging;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Category;
import org.eclipse.jface.resource.ImageDescriptor;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.plugin.Drawable;
import de.uniluebeck.itm.spyglass.plugin.Plugin;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;

public class ZoomCompleteMapAction extends Action {
	
	private static final Category log = Logging.get(ZoomCompleteMapAction.class);
	
	private final ImageDescriptor imageDescriptor = getImageDescriptor("zoom_complete_map.png");
	
	private final Spyglass spyglass;
	
	public ZoomCompleteMapAction(final Spyglass spyglass) {
		this.spyglass = spyglass;
	}
	
	@Override
	public void run() {
		log.debug("Pressed button ZOOM_COMPLETE_MAP.");
		final List<Plugin> list = spyglass.getPluginManager().getVisibleActivePlugins();
		
		final List<DrawingObject> dobs = new ArrayList<DrawingObject>();
		
		for (final Plugin plugin : list) {
			if (plugin instanceof Drawable) {
				final Drawable plugin2 = (Drawable) plugin;
				
				dobs.addAll(plugin2.getAutoZoomDrawingObjects());
			}
		}
		
		AbsoluteRectangle maxRect = null;
		
		for (final DrawingObject drawingObject : dobs) {
			final AbsoluteRectangle nextRect = drawingObject.getBoundingBox();
			if (nextRect == null) {
				continue;
			}
			
			if (maxRect == null) {
				maxRect = nextRect;
			} else {
				maxRect = maxRect.union(nextRect);
			}
		}
		if (maxRect != null) {
			spyglass.getDrawingArea().autoZoom(maxRect);
		}
	};
	
	@Override
	public String getText() {
		return "Zoom Complete Map";
	}
	
	@Override
	public String getToolTipText() {
		return "Zoom Complete Map";
	}
	
	@Override
	public ImageDescriptor getImageDescriptor() {
		return imageDescriptor;
	}
	
}
