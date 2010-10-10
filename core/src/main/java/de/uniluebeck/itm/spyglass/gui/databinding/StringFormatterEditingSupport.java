/*
 * --------------------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass. Copyright (C)
 * 2004-2007 by the SwarmNet (www.swarmnet.de) project SpyGlass is free
 * software; you can redistribute it and/or modify it under the terms of the BSD
 * License. Refer to spyglass-licence.txt file in the root of the SpyGlass
 * source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.gui.databinding;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.viewers.ColumnViewer;

import de.uniluebeck.itm.spyglass.gui.databinding.validator.StringFormatterValidator;

//--------------------------------------------------------------------------------
/**
 * Editing Support for String formatters
 * 
 * @author Sebastian Ebers
 * @author Oliver Kleine
 * @auther Dariush Forouher
 * 
 */
public class StringFormatterEditingSupport extends DatabindingTextEditingSupport {

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 * 
	 * @param viewer
	 *            a column viewer
	 * @param dbc
	 *            a data binding context
	 * @param elementName
	 *            the name of the model's parameter
	 */
	public StringFormatterEditingSupport(final ColumnViewer viewer, final DataBindingContext dbc, final String elementName) {
		super(viewer, dbc, elementName, null, null, new StringFormatterValidator());
	}

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 * 
	 * @param viewer
	 *            a column viewer
	 * @param dbc
	 *            a data binding context
	 */
	public StringFormatterEditingSupport(final ColumnViewer viewer, final DataBindingContext dbc) {
		super(viewer, dbc, null, null, new StringFormatterValidator());
	}

}