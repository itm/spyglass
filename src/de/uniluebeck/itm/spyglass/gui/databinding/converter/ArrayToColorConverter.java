package de.uniluebeck.itm.spyglass.gui.databinding.converter;

import org.eclipse.core.databinding.conversion.Converter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/**
 * Converts an array of integers (representing RGB color values) into an swt-Color object.
 * 
 * Note: This class leaks ressources of type Color. But as a framework to dispose them would be
 * pretty complex and ugly, and as this databinding stuff is only deployed in the probably seldom
 * used PrefDialog, I hope we can live with this leak.
 * 
 * @author Dariush Forouher
 * 
 */
public class ArrayToColorConverter extends Converter {

	private Display device;

	public ArrayToColorConverter(final Display device) {
		super(new int[3], null);
		this.device = device;
	}

	@Override
	public Object convert(final Object fromObject) {
		final int[] color = (int[]) fromObject;
		return new Color(device, color[0], color[1], color[2]);
	}

}
