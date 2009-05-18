// --------------------------------------------------------------------------------
/**
 * 
 */
package de.uniluebeck.itm.spyglass.gui.databinding.converter;

import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.swt.graphics.RGB;

// --------------------------------------------------------------------------------
/**
 * @author bimschas
 * 
 */
public class RGBToArrayConverter implements IConverter {

	@Override
	public Object convert(final Object fromObject) {
		final RGB rgb = (RGB) fromObject;
		return new int[] { rgb.red, rgb.green, rgb.blue };
	}

	@Override
	public Object getFromType() {
		return RGB.class;
	}

	@Override
	public Object getToType() {
		return int[].class;
	}

}
