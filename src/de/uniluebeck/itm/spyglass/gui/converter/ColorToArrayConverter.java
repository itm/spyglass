package de.uniluebeck.itm.spyglass.gui.converter;

import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

/**
 * Converts an swt-Color object into an Array representing the RGB values.
 * 
 * @author Dariush Forouher
 * 
 */
public class ColorToArrayConverter implements IConverter {
	
	@Override
	public Object convert(final Object fromObject) {
		final Color color = (Color) fromObject;
		final RGB rgb = color.getRGB();
		return new int[] { rgb.red, rgb.green, rgb.blue };
	}
	
	@Override
	public Object getFromType() {
		return new Color(null, 0, 0, 0);
	}
	
	@Override
	public Object getToType() {
		return new int[3];
	}
	
}
