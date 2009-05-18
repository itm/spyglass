// --------------------------------------------------------------------------------
/**
 * 
 */
package de.uniluebeck.itm.spyglass.gui.databinding.converter;

import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

// --------------------------------------------------------------------------------
/**
 * @author bimschas
 * 
 */
public class RGBToColorConverter implements IConverter {

	@Override
	public Object convert(final Object fromObject) {
		// will produce swt ressource leak
		return new Color(Display.getDefault(), (RGB) fromObject);
	}

	@Override
	public Object getFromType() {
		return RGB.class;
	}

	@Override
	public Object getToType() {
		return Color.class;
	}

}
