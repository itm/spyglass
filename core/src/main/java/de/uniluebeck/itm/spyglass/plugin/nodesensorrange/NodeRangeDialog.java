/*
 * --------------------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass. Copyright (C)
 * 2004-2007 by the SwarmNet (www.swarmnet.de) project SpyGlass is free
 * software; you can redistribute it and/or modify it under the terms of the BSD
 * License. Refer to spyglass-licence.txt file in the root of the SpyGlass
 * source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.nodesensorrange;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.widgets.Shell;

import de.uniluebeck.itm.spyglass.plugin.nodesensorrange.NodeSensorRangeXMLConfig.CircleRange;
import de.uniluebeck.itm.spyglass.plugin.nodesensorrange.NodeSensorRangeXMLConfig.ConeRange;
import de.uniluebeck.itm.spyglass.plugin.nodesensorrange.NodeSensorRangeXMLConfig.NodeSensorRange;
import de.uniluebeck.itm.spyglass.plugin.nodesensorrange.NodeSensorRangeXMLConfig.RANGE_TYPE;
import de.uniluebeck.itm.spyglass.plugin.nodesensorrange.NodeSensorRangeXMLConfig.RectangleRange;

abstract class NodeRangeDialog extends TitleAreaDialog {

	protected NodeRangeDialog(final Shell parentShell, final NodeSensorRange range) {
		super(parentShell);
		this.range = range;
	}

	public static NodeRangeDialog createDialog(final Shell parentShell, final RANGE_TYPE range_type, final NodeSensorRange defaultRange) {

		final NodeSensorRange dialogConfig;

		switch (range_type) {
			case Circle:
				dialogConfig = defaultRange instanceof CircleRange ? defaultRange.clone() : new CircleRange();
				return new CircleDialog(parentShell, dialogConfig);
			case Cone:
				dialogConfig = defaultRange instanceof ConeRange ? defaultRange.clone() : new ConeRange();
				return new ConeDialog(parentShell, dialogConfig);
			case Rectangle:
				dialogConfig = defaultRange instanceof RectangleRange ? defaultRange.clone() : new RectangleRange();
				return new RectangleDialog(parentShell, dialogConfig);
			default:
				throw new RuntimeException("Unknown type!");
		}

	}

	public NodeSensorRangeXMLConfig.NodeSensorRange range;

}