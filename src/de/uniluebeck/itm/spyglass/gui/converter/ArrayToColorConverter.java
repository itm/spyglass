package de.uniluebeck.itm.spyglass.gui.converter;

import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/**
 * Converts an array of integers (representing RGB color values) into an swt-Color object.
 * 
 * @author Dariush Forouher
 * 
 */
public class ArrayToColorConverter implements IConverter {
	
	private Display device;
	
	public ArrayToColorConverter(final Display device) {
		super();
		this.device = device;
	}
	
	@Override
	public Object convert(final Object fromObject) {
		final int[] color = (int[]) fromObject;
		return new Color(device, color[0], color[1], color[2]);
	}
	
	@Override
	public Object getFromType() {
		return new int[3];
	}
	
	@Override
	public Object getToType() {
		return new Color(null, 0, 0, 0);
	}
	
}
