package de.uniluebeck.itm.spyglass.plugin;

import org.eclipse.swt.widgets.Widget;

public interface GlobalInformation {

	/**
	 * 
	 * @param widget
	 */
	public void addWidget(Widget widget);

	public Widget getWidget();

}