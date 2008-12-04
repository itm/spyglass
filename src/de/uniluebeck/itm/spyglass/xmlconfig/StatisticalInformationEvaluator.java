/*
 * -------------------------------------------------------------------------------- This file is
 * part of the WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet
 * (www.swarmnet.de) project SpyGlass is free software; you can redistribute it and/or modify it
 * under the terms of the BSD License. Refer to spyglass-licence.txt file in the root of the
 * SpyGlass source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.xmlconfig;

import org.apache.log4j.Logger;
import org.simpleframework.xml.Element;

import de.uniluebeck.itm.spyglass.gui.configuration.PropertyBean;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacket;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacketException;
import de.uniluebeck.itm.spyglass.plugin.simpleglobalinformation.StatisticalOperation;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;
import de.uniluebeck.itm.spyglass.util.StringFormatter;

// --------------------------------------------------------------------------------
/**
 * Instances of this class evaluate {@link SpyglassPacket}s extracting numerical information as
 * input for statistical operations.<br>
 * Provided packets will be parsed for a numerical value using a {@link StringFormatter} configured
 * by a provided expression.
 * 
 * @author Sebastian Ebers
 * 
 */
public class StatisticalInformationEvaluator extends PropertyBean implements Comparable<StatisticalInformationEvaluator> {

	@Element(name = "semanticType")
	private int semanticType = -1;

	@Element(name = "description", required = false)
	private String description = "";

	@Element(name = "expression", required = false)
	private String expression = "";

	@Element(name = "operations")
	private STATISTICAL_OPERATIONS operation = STATISTICAL_OPERATIONS.SUM;

	private StringFormatter stringFormatter;

	// --------------------------------------------------------------------------------
	/**
	 * Enumeration of statistical operations which are offered to evaluate the numerical value(s)
	 * extracted from provided packets
	 */
	public enum STATISTICAL_OPERATIONS {
		/**
		 * Statistical operation: Calculate the sum the extracted values
		 */
		SUM,
		/**
		 * Statistical operation: Calculate the minimum of the extracted values
		 */
		MIN,
		/**
		 * Statistical operation: Calculate the maximum of the extracted values
		 */
		MAX,
		/**
		 * Statistical operation: Calculate the average of the extracted values
		 */
		AVG,
		/**
		 * Statistical operation: Calculate the median of the extracted values
		 */
		MEDIAN
	}

	private static final Logger log = SpyglassLoggerFactory.getLogger(StatisticalInformationEvaluator.class);

	private StatisticalOperation operationExecutor;

	/**
	 * Protected constructor to be used by the XML framework
	 */
	protected StatisticalInformationEvaluator() {
		// nothing to do here
	}

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 * 
	 * @param semanticType
	 *            the semantic type of packets which will be evaluated
	 */
	public StatisticalInformationEvaluator(final int semanticType) {
		this.semanticType = semanticType;
		description = "";
		expression = "";
		operation = STATISTICAL_OPERATIONS.SUM;
		operationExecutor = new StatisticalOperation(10, operation);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 * 
	 * @param description
	 *            the object's description
	 * @param expression
	 *            the expression which is used to obtain the numerical value from an elsewhere
	 *            provided packet
	 * @param operation
	 *            the statistical operation to perform with the value which is obtained from a
	 *            packet when using a {@link StringFormatter}
	 * @param semanticType
	 *            the semantic type of packets which will be evaluated
	 */
	public StatisticalInformationEvaluator(final String description, final String expression, final STATISTICAL_OPERATIONS operation,
			final int semanticType) {
		super();
		this.description = description;
		this.expression = expression;
		this.operation = operation;
		this.semanticType = semanticType;
	}

	// --------------------------------------------------------------------------------
	@Override
	public StatisticalInformationEvaluator clone() {
		return new StatisticalInformationEvaluator(description, expression, operation, semanticType);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns the semantic type of packets which will be evaluated
	 * 
	 * @return the semantic type of packets which will be evaluated
	 */
	public int getSemanticType() {
		return semanticType;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Sets the semantic type of packets which will be evaluated
	 * 
	 * @param semanticType
	 *            the semantic type of packets which will be evaluated
	 */
	public void setSemanticType(final int semanticType) {
		this.semanticType = semanticType;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns the object's description
	 * 
	 * @return the description the object's description
	 */
	public String getDescription() {
		return description;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Sets the object's description
	 * 
	 * @param description
	 *            the object's description
	 */
	public void setDescription(final String description) {
		final String oldValue = new String(this.description);
		this.description = description;
		firePropertyChange("description", oldValue, description);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns the expression which is used to obtain the numerical value from an elsewhere provided
	 * packet
	 * 
	 * @return the expression the expression which is used to obtain the numerical value from an
	 *         elsewhere provided packet
	 */
	public String getExpression() {
		return expression;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Sets the expression which is used to obtain the numerical value from an elsewhere provided
	 * packet.<br>
	 * The expression will be used along with a {@link StringFormatter}. The obtained value will be
	 * used as input for the statistical operation.
	 * 
	 * @param expression
	 *            the expression to set
	 */
	public void setExpression(final String expression) {
		StringFormatter newStringFormatter = null;
		try {
			newStringFormatter = new StringFormatter(expression);
		} catch (final IllegalArgumentException e) {
			log.error("The string " + expression + " is no valid format expression!", e);
			return;
		}
		this.stringFormatter = newStringFormatter;
		final String oldValue = new String(this.expression);
		this.expression = new String(expression);
		firePropertyChange("expression", oldValue, expression);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns the statistical operation to perform with the value which is obtained from a packet
	 * when using a {@link StringFormatter}
	 * 
	 * @return the statistical operation
	 */
	public STATISTICAL_OPERATIONS getOperation() {
		return operation;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Set the statistical operation to perform with the value which is obtained from a packet when
	 * using a {@link StringFormatter}
	 * 
	 * @param operation
	 *            the statistical operation
	 */
	public void setOperation(final STATISTICAL_OPERATIONS operation) {
		final STATISTICAL_OPERATIONS oldValue = this.operation;
		this.operation = operation;
		firePropertyChange("operation", oldValue, operation);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Compares the semantic type and description of this object with the specified object for
	 * order. Returns a negative integer, zero, or a positive integer as this object is less than,
	 * equal to, or greater than the specified object.
	 * 
	 * @param spe
	 *            the StatisticalInformationEvaluator to be compared.
	 * @return a negative integer, zero, or a positive integer as this object is less than, equal
	 *         to, or greater than the specified object.
	 */
	@Override
	public int compareTo(final StatisticalInformationEvaluator spe) {
		if (spe != null) {
			final int diff = this.semanticType - spe.semanticType;
			if (diff > 0) {
				return 1;
			} else if (diff < 0) {
				return -1;
			}
			return this.description.compareTo(spe.description);
		}
		return -1;
	}

	@Override
	public boolean equals(final Object other) {
		if (other instanceof StatisticalInformationEvaluator) {
			final StatisticalInformationEvaluator o = (StatisticalInformationEvaluator) other;
			return ((this.semanticType == o.semanticType) && ((this.description != null) && (o.description != null))
					&& this.description.equals(o.description) && ((this.expression != null) && (o.expression != null))
					&& this.expression.equals(o.expression) && ((this.operation != null) && (o.operation != null)) && this.operation
					.equals(o.operation));
		}
		return false;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns the concatenation of the description and statistic the value.<br>
	 * The provided packet is parsed using the predefined expression value to extract the
	 * information.
	 * 
	 * @param packet
	 *            a SypGlass packet
	 * @exception SpyglassPacketException
	 *                thrown if by parsing the packet using the predefined expression no valid value
	 *                can be extracted
	 * @return the concatenation of the description and statistic the value
	 */
	public String parse(final SpyglassPacket packet) throws SpyglassPacketException {
		try {
			final int value = Integer.valueOf(stringFormatter.parse(packet));
			final int computetValue = (int) operationExecutor.addValue(value);
			return description + " " + computetValue;
		} catch (final Exception e) {
			throw new SpyglassPacketException("A packet coult not be evaluated in StatisticalInformationEvaluator");
			// log.warn("A packet coult not be evaluated in StatisticalInformationEvaluator");
		}
		// return description + " ###";
	}

	@Override
	public String toString() {
		return "ST: " + semanticType + " " + description + " " + expression + " " + operation.toString();
	}
}
