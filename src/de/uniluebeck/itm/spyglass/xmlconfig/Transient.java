package de.uniluebeck.itm.spyglass.xmlconfig;

/**
 * Applies to setter methods in XMLConfig objects. Methods with this annotation will not be copied
 * when calling XMLConfig.overwriteWith() or XMLConfig.clone().
 * 
 * @author Dariush Forouher
 * 
 */
@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface Transient {
	// nothing to do here
}
