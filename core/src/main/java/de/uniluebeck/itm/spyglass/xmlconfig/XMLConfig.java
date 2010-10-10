/*
 * --------------------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass.
 * Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de) project SpyGlass is free
 * software; you can redistribute it and/or modify it under the terms of the BSD License.
 * Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for further
 * details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.xmlconfig;

import java.lang.reflect.Method;

import org.simpleframework.xml.Root;

import de.uniluebeck.itm.spyglass.gui.configuration.PropertyBean;
import de.uniluebeck.itm.spyglass.plugin.Plugin;

// --------------------------------------------------------------------------------
/**
 * Instances of this class contain the configuration parameters of a {@link Plugin}
 */
@Root(strict = false)
public abstract class XMLConfig extends PropertyBean {

	/**
	 * Copy the data from <code>other</code> into this object.
	 * 
	 * @param other
	 *            another PluginXMLConfig object. <code>other</code> must be of exactly the same
	 *            type as the object this method is called on.
	 * @throws RuntimeException
	 *             if there is a problem during reflection. Such an exception can almost surely only
	 *             be caused by an implementation bug in the XMLConfig class.
	 * @throws ClassCastException
	 *             if the given object is of the wrong type.
	 */
	public final void overwriteWith(final XMLConfig other) {

		if (!this.getClass().equals(other.getClass())) {
			throw new ClassCastException("Cannot cast " + other.getClass() + " to " + this.getClass() + ".");
		}

		// From here on we assume that "this" and "other" both have the same type
		final Class<? extends XMLConfig> clazz = this.getClass();

		for (final Method setter : clazz.getMethods()) {

			// iterate through all setter methods
			if (setter.getName().startsWith("set") && (setter.getAnnotation(Transient.class) == null)) {
				try {

					final Method getter = findCorrespondingGetterMethod(clazz, setter);

					final Object newValue = getter.invoke(other);
					setter.invoke(this, newValue);

				} catch (final NoSuchMethodException e) {
					throw new RuntimeException("Could not find corresponding getter method to " + setter.getName() + " in class " + clazz.getName(),
							e);
				} catch (final Exception e) {
					throw new RuntimeException(
							String
									.format(
											"Something wicked happened during reflection. Maybe there is something wrong with the getter/setter method %s of class %s",
											setter.getName(), clazz.getName()), e);
				}

			}
		}
	}

	// --------------------------------------------------------------------------------
	/**
	 * Finds the corresponding getter-Method of the given setter.
	 * 
	 * @param clazz
	 * @param setter
	 * @return
	 * @throws NoSuchMethodException
	 */
	private Method findCorrespondingGetterMethod(final Class<? extends XMLConfig> clazz, final Method setter) throws NoSuchMethodException {

		final String propertyName = setter.getName().substring(3);

		boolean getGetter, isGetter;

		for (final Method getter : clazz.getMethods()) {

			getGetter = getter.getName().equalsIgnoreCase("get" + propertyName);
			isGetter = getter.getName().equalsIgnoreCase("is" + propertyName);

			if (getGetter || isGetter) {
				return getter;
			}

		}
		throw new NoSuchMethodException("Could not find a corresponding getter for " + setter.getName() + ".");
	}
}
