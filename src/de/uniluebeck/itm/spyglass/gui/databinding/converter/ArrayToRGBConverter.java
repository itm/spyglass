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
public class ArrayToRGBConverter implements IConverter {

	@Override
	public Object convert(final Object arg0) {
		final int[] rgb = (int[]) arg0;
		return new RGB(rgb[0], rgb[1], rgb[2]);
	}

	@Override
	public Object getFromType() {
		return int[].class;
	}

	@Override
	public Object getToType() {
		return RGB.class;
	}

}
