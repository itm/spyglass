package de.uniluebeck.itm.spyglass.gui.databinding.converter;

import org.eclipse.core.databinding.conversion.Converter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

/**
 * Converts an swt-Color object into an Array representing the RGB values.
 * 
 * @author Dariush Forouher
 * 
 */
public class ColorToArrayConverter extends Converter {
	
	public ColorToArrayConverter() {
		super(new Color(null, 0, 0, 0), new int[3]);
	}
	
	@Override
	public Object convert(final Object fromObject) {
		final Color color = (Color) fromObject;
		final RGB rgb = color.getRGB();
		return new int[] { rgb.red, rgb.green, rgb.blue };
	}
	
}
