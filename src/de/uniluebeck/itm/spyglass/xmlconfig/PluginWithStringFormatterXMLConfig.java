package de.uniluebeck.itm.spyglass.xmlconfig;

import java.util.HashMap;
import java.util.Map;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementMap;

import de.uniluebeck.itm.spyglass.util.StringFormatter;

public abstract class PluginWithStringFormatterXMLConfig extends PluginXMLConfig {

	public static final String PROPERTYNAME_DEFAULT_STRING_FORMATTER = "defaultStringFormatter";

	public static final String PROPERTYNAME_STRING_FORMATTERS = "stringFormatters";

	@ElementMap(entry = "stringFormatter", key = "nodeID", attribute = true, valueType = String.class, required = false)
	private HashMap<Integer, String> stringFormatters = new HashMap<Integer, String>();

	@Element(required = false)
	private String defaultStringFormatter = null;

	// --------------------------------------------------------------------------------
	/**
	 * @return the stringFormatters
	 */
	@SuppressWarnings("unchecked")
	public HashMap<Integer, String> getStringFormatters() {
		return (HashMap<Integer, String>) stringFormatters.clone();
	}

	/**
	 * Returns a string formatting object in respect to a syntax type or <code>null</code> if no
	 * matching object was created previously.
	 * 
	 * @return a string formatting object in respect to a syntax type or <code>null</code> if no
	 *         matching object was created previously.
	 */
	public StringFormatter getStringFormatter(final int semanticType) {
		if (stringFormatters.containsKey(semanticType)) {
			return new StringFormatter(stringFormatters.get(semanticType));
		}
		return null;
	}

	// --------------------------------------------------------------------------------
	/**
	 * @param stringFormatters
	 *            the stringFormatters to set
	 */
	@SuppressWarnings("unchecked")
	public void setStringFormatters(final HashMap<Integer, String> stringFormatters) {
		final Map<Integer, String> oldValue = this.stringFormatters;
		this.stringFormatters = (HashMap<Integer, String>) stringFormatters.clone();
		firePropertyChange(PROPERTYNAME_STRING_FORMATTERS, oldValue, stringFormatters);
	}

	// --------------------------------------------------------------------------------
	/**
	 * @return the defaultStringFormatter
	 */
	public String getDefaultStringFormatter() {
		return defaultStringFormatter;
	}

	// --------------------------------------------------------------------------------
	/**
	 * @param defaultStringFormatter
	 *            the defaultStringFormatter to set
	 */
	public void setDefaultStringFormatter(final String defaultStringFormatter) {
		final String oldValue = this.defaultStringFormatter;
		this.defaultStringFormatter = defaultStringFormatter;
		firePropertyChange(PROPERTYNAME_DEFAULT_STRING_FORMATTER, oldValue, defaultStringFormatter);
	}

	protected boolean equals(final PluginWithStringFormatterXMLConfig o) {
		return (defaultStringFormatter != null) && defaultStringFormatter.equals(o.defaultStringFormatter)
				&& stringFormatters.equals(o.stringFormatters);
	}

	@Override
	protected void finalize() throws Throwable {
		stringFormatters.clear();
		super.finalize();
	}

}
