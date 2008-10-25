package de.uniluebeck.itm.spyglass.gui.databinding.converter;

import org.eclipse.core.databinding.conversion.Converter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/**
 * Converts an array of integers (representing RGB color values) into an swt-Color object.
 * 
 * @author Dariush Forouher
 * 
 */
public class ArrayToColorConverter extends Converter {
	
	private Display device;
	
	public ArrayToColorConverter(final Display device) {
		super(new int[3], new Color(null, 0, 0, 0));
		this.device = device;
	}
	
	@Override
	public Object convert(final Object fromObject) {
		final int[] color = (int[]) fromObject;
		return new Color(device, color[0], color[1], color[2]);
	}
	
}
