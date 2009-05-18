// --------------------------------------------------------------------------------
/**
 * 
 */
package de.uniluebeck.itm.spyglass.gui.databinding.converter;

import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

// --------------------------------------------------------------------------------
/**
 * @author bimschas
 * 
 */
public class ColorToRGBConverter implements IConverter {

	@Override
	public Object convert(final Object arg0) {
		return ((Color) arg0).getRGB();
	}

	@Override
	public Object getFromType() {
		return Color.class;
	}

	@Override
	public Object getToType() {
		return RGB.class;
	}

}
