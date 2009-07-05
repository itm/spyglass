/*----------------------------------------------------------------------------------------
 * This file is part of the
 * WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de)
 * project SpyGlass is free software; you can redistribute it and/or modify it under the terms of
 * the BSD License. Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for
 * further details.
 * ---------------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.gui.databinding.converter;

import org.eclipse.core.databinding.conversion.Converter;

import de.uniluebeck.itm.spyglass.plugin.simpleglobalinformation.StatisticalInformationEvaluator.STATISTICAL_OPERATIONS;

// --------------------------------------------------------------------------------
/**
 * Converts from {@link STATISTICAL_OPERATIONS} to {@link String} and from {@link String} to
 * {@link STATISTICAL_OPERATIONS} respectively
 * 
 * @author Sebastian Ebers
 * 
 */
public class StatisticOperationConverter extends Converter {

	// --------------------------------------------------------------------------------
	/**
	 * Creates an instance of this class which converts one type into another
	 * 
	 * @param fromType
	 *            the class of the value which has to be converted
	 * @param toType
	 *            the class a provided value will be converted to
	 */
	public StatisticOperationConverter(final Class<?> fromType, final Class<?> toType) {
		super(fromType, toType);
	}

	// --------------------------------------------------------------------------------
	@Override
	public Object convert(final Object fromObject) {
		if (fromObject instanceof String) {
			final String key = (String) fromObject;
			final STATISTICAL_OPERATIONS[] values = STATISTICAL_OPERATIONS.values();
			for (final STATISTICAL_OPERATIONS statistical_operations : values) {
				if (statistical_operations.toString().equals(key)) {
					return statistical_operations;
				}
			}
			return null;
		} else if (fromObject instanceof Enum) {
			((Enum<?>) fromObject).toString();
		}
		return null;
	}

}
