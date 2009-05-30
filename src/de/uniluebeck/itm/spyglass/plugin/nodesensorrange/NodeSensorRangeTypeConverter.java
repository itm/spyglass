package de.uniluebeck.itm.spyglass.plugin.nodesensorrange;

import org.eclipse.core.databinding.conversion.Converter;

import de.uniluebeck.itm.spyglass.plugin.nodesensorrange.NodeSensorRangeXMLConfig.RANGE_TYPE;


// --------------------------------------------------------------------------------
/**
 * Converts from {@link RANGE_TYPE} to {@link String} and from {@link String} to
 * {@link RANGE_TYPE} respectively
 *
 * @author Sebastian Ebers
 *
 */
public class NodeSensorRangeTypeConverter extends Converter {

	/**
	 * Creates an instance of this class which converts one type into another
	 *
	 * @param fromType
	 *            the class of the value which has to be converted
	 * @param toType
	 *            the class a provided value will be converted to
	 */
	public NodeSensorRangeTypeConverter(final Class<?> fromType, final Class<?> toType) {
		super(fromType, toType);
	}

	@Override
	public Object convert(final Object fromObject) {
		if (fromObject instanceof String) {
			final String key = (String) fromObject;
			final RANGE_TYPE[] values = RANGE_TYPE.values();
			for (final RANGE_TYPE statistical_operations : values) {
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
