package de.uniluebeck.itm.spyglass.gui.databinding;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.viewers.ColumnViewer;

import de.uniluebeck.itm.spyglass.gui.databinding.validator.StringFormatterValidator;

/**
 * Editing Support for String formatters
 *
 * @author Sebastian Ebers
 * @author Oliver Kleine
 * @auther Dariush Forouher
 *
 */
public class StringFormatterEditingSupport extends DatabindingTextEditingSupport {

	public StringFormatterEditingSupport(final ColumnViewer viewer, final DataBindingContext dbc, final String elementName) {
		super(viewer, dbc, elementName, null, null, new StringFormatterValidator() );
	}

	public StringFormatterEditingSupport(final ColumnViewer viewer, final DataBindingContext dbc) {
		super(viewer, dbc, null, null, new StringFormatterValidator() );
	}

}