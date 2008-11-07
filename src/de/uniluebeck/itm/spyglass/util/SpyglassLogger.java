/*
 * ---------------------------------------------------------------------- This file is part of the
 * WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de)
 * project SpyGlass is free software; you can redistribute it and/or modify it under the terms of
 * the BSD License. Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for
 * further details. ------------------------------------------------------------------------
 */

package de.uniluebeck.itm.spyglass.util;

import java.util.Enumeration;
import java.util.ResourceBundle;

import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.LoggingEvent;
import org.eclipse.jface.dialogs.MessageDialog;

// --------------------------------------------------------------------------------
/**
 * Instances of this class extend the {@link Logger} from the log4j project.
 * 
 * @author Sebastian Ebers
 * 
 */
public class SpyglassLogger extends Logger {
	
	private Logger log;
	
	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 * 
	 * @param clazz
	 *            the class to be associated with the log messages
	 */
	public SpyglassLogger(final Class<?> clazz) {
		super(clazz.getName());
		log = Logger.getLogger(clazz);
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 * 
	 * @param clazz
	 *            the name of the class to be associated with the log messages
	 */
	protected SpyglassLogger(final String clazz) {
		super(clazz);
	}
	
	@Override
	public void addAppender(final Appender newAppender) {
		log.addAppender(newAppender);
	}
	
	@Override
	public void assertLog(final boolean assertion, final String msg) {
		log.assertLog(assertion, msg);
	}
	
	@Override
	public void callAppenders(final LoggingEvent event) {
		log.callAppenders(event);
	}
	
	@Override
	public void debug(final Object message, final Throwable t) {
		log.debug(message, t);
	}
	
	@Override
	public void debug(final Object message) {
		log.debug(message);
	}
	
	@Override
	public boolean equals(final Object obj) {
		return log.equals(obj);
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Log a message object with the <code>ERROR</code> level including the stack trace of the
	 * {@link Throwable} <code>t</code> passed as parameter. Additionally, the message is displayed
	 * in a dialog window to the user.
	 * 
	 * <p>
	 * See {@link #error(Object)} form for more detailed information.
	 * 
	 * @param message
	 *            the message object to log.
	 * @param t
	 *            the exception to log, including its stack trace.
	 */
	@Override
	public void error(final Object message, final Throwable t) {
		log.error(message, t);
		MessageDialog.openError(null, "An error occured", message.toString());
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Log a message object with the {@link Level#ERROR ERROR} Level. Additionally, the message is
	 * displayed in a dialog window to the user.
	 * 
	 * <p>
	 * This method first checks if this category is <code>ERROR</code> enabled by comparing the
	 * level of this category with {@link Level#ERROR ERROR} Level. If this category is
	 * <code>ERROR</code> enabled, then it converts the message object passed as parameter to a
	 * string by invoking the appropriate {@link org.apache.log4j.or.ObjectRenderer}. It proceeds to
	 * call all the registered appenders in this category and also higher in the hierarchy depending
	 * on the value of the additivity flag.
	 * 
	 * <p>
	 * <b>WARNING</b> Note that passing a {@link Throwable} to this method will print the name of
	 * the <code>Throwable</code> but no stack trace. To print a stack trace use the
	 * {@link #error(Object, Throwable)} form instead.
	 * 
	 * @param message
	 *            the message object to log
	 */
	@Override
	public void error(final Object message) {
		log.error(message);
		MessageDialog.openError(null, "An error occured", message.toString());
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Log a message object with the <code>FATAL</code> level including the stack trace of the
	 * {@link Throwable} <code>t</code> passed as parameter. Additionally, the message is displayed
	 * in a dialog window to the user.
	 * 
	 * <p>
	 * See {@link #fatal(Object)} for more detailed information.
	 * 
	 * @param message
	 *            the message object to log.
	 * @param t
	 *            the exception to log, including its stack trace.
	 */
	@Override
	public void fatal(final Object message, final Throwable t) {
		log.fatal(message, t);
		MessageDialog.openError(null, "Critical error", message.toString());
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Log a message object with the {@link Level#FATAL FATAL} Level. Additionally, the message is
	 * displayed in a dialog window to the user.
	 * 
	 * <p>
	 * This method first checks if this category is <code>FATAL</code> enabled by comparing the
	 * level of this category with {@link Level#FATAL FATAL} Level. If the category is
	 * <code>FATAL</code> enabled, then it converts the message object passed as parameter to a
	 * string by invoking the appropriate {@link org.apache.log4j.or.ObjectRenderer}. It proceeds to
	 * call all the registered appenders in this category and also higher in the hierarchy depending
	 * on the value of the additivity flag.
	 * 
	 * <p>
	 * <b>WARNING</b> Note that passing a {@link Throwable} to this method will print the name of
	 * the Throwable but no stack trace. To print a stack trace use the
	 * {@link #fatal(Object, Throwable)} form instead.
	 * 
	 * @param message
	 *            the message object to log
	 */
	@Override
	public void fatal(final Object message) {
		log.fatal(message);
		MessageDialog.openError(null, "Critical error", message.toString());
	}
	
	@Override
	public boolean getAdditivity() {
		return log.getAdditivity();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Enumeration getAllAppenders() {
		return log.getAllAppenders();
	}
	
	@Override
	public Appender getAppender(final String name) {
		return log.getAppender(name);
	}
	
	@Deprecated
	@Override
	public Priority getChainedPriority() {
		return log.getChainedPriority();
	}
	
	@Override
	public Level getEffectiveLevel() {
		return log.getEffectiveLevel();
	}
	
	@Deprecated
	@Override
	public LoggerRepository getHierarchy() {
		return log.getHierarchy();
	}
	
	@Override
	public LoggerRepository getLoggerRepository() {
		return log.getLoggerRepository();
	}
	
	@Override
	public ResourceBundle getResourceBundle() {
		return log.getResourceBundle();
	}
	
	@Override
	public int hashCode() {
		return log.hashCode();
	}
	
	@Override
	public void info(final Object message, final Throwable t) {
		log.info(message, t);
	}
	
	@Override
	public void info(final Object message) {
		log.info(message);
	}
	
	@Override
	public boolean isAttached(final Appender appender) {
		return log.isAttached(appender);
	}
	
	@Override
	public boolean isDebugEnabled() {
		return log.isDebugEnabled();
	}
	
	@Override
	public boolean isEnabledFor(final Priority level) {
		return log.isEnabledFor(level);
	}
	
	@Override
	public boolean isInfoEnabled() {
		return log.isInfoEnabled();
	}
	
	@Override
	public boolean isTraceEnabled() {
		return log.isTraceEnabled();
	}
	
	@Override
	public void l7dlog(final Priority priority, final String key, final Object[] params,
			final Throwable t) {
		log.l7dlog(priority, key, params, t);
	}
	
	@Override
	public void l7dlog(final Priority priority, final String key, final Throwable t) {
		log.l7dlog(priority, key, t);
	}
	
	@Override
	public void log(final Priority priority, final Object message, final Throwable t) {
		log.log(priority, message, t);
	}
	
	@Override
	public void log(final Priority priority, final Object message) {
		log.log(priority, message);
	}
	
	@Override
	public void log(final String callerFQCN, final Priority level, final Object message,
			final Throwable t) {
		log.log(callerFQCN, level, message, t);
	}
	
	@Override
	public void removeAllAppenders() {
		log.removeAllAppenders();
	}
	
	@Override
	public void removeAppender(final Appender appender) {
		log.removeAppender(appender);
	}
	
	@Override
	public void removeAppender(final String name) {
		log.removeAppender(name);
	}
	
	@Override
	public void setAdditivity(final boolean additive) {
		log.setAdditivity(additive);
	}
	
	@Override
	public void setLevel(final Level level) {
		log.setLevel(level);
	}
	
	@Deprecated
	@Override
	public void setPriority(final Priority priority) {
		log.setPriority(priority);
	}
	
	@Override
	public void setResourceBundle(final ResourceBundle bundle) {
		log.setResourceBundle(bundle);
	}
	
	@Override
	public String toString() {
		return log.toString();
	}
	
	@Override
	public void trace(final Object message, final Throwable t) {
		log.trace(message, t);
	}
	
	@Override
	public void trace(final Object message) {
		log.trace(message);
	}
	
	@Override
	public void warn(final Object message, final Throwable t) {
		log.warn(message, t);
	}
	
	@Override
	public void warn(final Object message) {
		log.warn(message);
	}
	
}
