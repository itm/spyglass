package de.uniluebeck.itm.spyglass.util;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.net.SocketAppender;

/**
 * Static wrapper class for accessing log4j
 * 
 * @author Sebastian Ebers
 */
public class SpyglassLogger {
	
	private static long timestamp = new Date().getTime();
	private static final String fileNameLog = "." + File.separator + "logs" + File.separator + timestamp + "_messages.log";
	private static PatternLayout patternLayout = new PatternLayout("%-9d{HH:mm:ss} [%t] %-8p %-20c{1} - %m%n");
	private static PatternLayout fileLoggerLayout = patternLayout;
	
	private static Logger standardLogger = Logger.getLogger(SpyglassLogger.class);
	private static Logger newline = Logger.getLogger(SpyglassLogger.class);
	private static Map<String, Logger> loggerMap = new HashMap<String, Logger>();
	private static Level loglevel = Level.INFO;
	public static boolean writeLogfile = false;
	public static boolean write2Socket = false;
	
	static {
		try {
			// newline.addAppender(new ConsoleAppender(new
			// PatternLayout("%n"),ConsoleAppender.SYSTEM_OUT));
			// newline.setLevel(loglevel);
			if (writeLogfile) {
				standardLogger.addAppender(new FileAppender(fileLoggerLayout, fileNameLog));
			}
			if (write2Socket) {
				standardLogger.addAppender(new SocketAppender("127.0.0.1", 4560));
			}
			standardLogger.setLevel(loglevel);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns an object to log messages which can be related to a certain class
	 * 
	 * @param clazz -
	 *            the class
	 * @return an object to log messages
	 * @see Logger
	 */
	public static Logger getLogger(final Class<?> clazz) {
		Logger logger = null;
		try {
			if ((logger = loggerMap.get(clazz.getName())) == null) {
				logger = Logger.getLogger(clazz);
				if (writeLogfile) {
					logger.addAppender(new FileAppender(fileLoggerLayout, fileNameLog));
				}
				if (write2Socket) {
					standardLogger.addAppender(new SocketAppender("127.0.0.1", 4560));
				}
				logger.setLevel(loglevel);
				loggerMap.put(clazz.getName(), logger);
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return logger;
	}
	
	public static Logger get(final Class<?> clazz) {
		return getLogger(clazz);
	}
	
	/**
	 * Log a message object with the {@link Level#DEBUG} level.
	 * 
	 * @param message -
	 *            the message object to log
	 * @param clazz -
	 *            the class which called this method
	 * @see Logger#debug(Object)
	 */
	public static void debug(final Object message, final Class<?> clazz) {
		getLogger(clazz).debug(message);
	}
	
	/**
	 * Log a message object with the {@link Level#DEBUG} level.
	 * 
	 * @param message -
	 *            the message object to log
	 * @see Logger#debug(Object)
	 */
	public static void debug(final Object message) {
		standardLogger.debug(message);
	}
	
	/**
	 * Log a message object with the {@link Level#INFO} level.
	 * 
	 * @param message -
	 *            the message object to log
	 * @param clazz -
	 *            the class which called this method
	 * @see Logger#info(Object)
	 */
	public static void info(final Object message, final Class<?> clazz) {
		getLogger(clazz).info(message);
	}
	
	/**
	 * Log a message object with the {@link Level#DEBUG} level.
	 * 
	 * @param message -
	 *            the message object to log
	 * @param clazz -
	 *            the class which called this method
	 * @see Logger#debug(Object)
	 */
	public static void info(final Object message) {
		standardLogger.info(message);
	}
	
	/**
	 * Log a message object with the {@link Level#WARN} level.
	 * 
	 * @param message -
	 *            the message object to log
	 * @param clazz -
	 *            the class which called this method
	 * @see Logger#warn(Object)
	 */
	public static void warn(final Object message, final Class<?> clazz) {
		getLogger(clazz).warn(message);
	}
	
	/**
	 * Log a message object with the {@link Level#WARN} level.
	 * 
	 * @param message -
	 *            the message object to log
	 * @see Logger#warn(Object)
	 */
	public static void warn(final Object message) {
		standardLogger.warn(message);
	}
	
	/**
	 * Log a message object with the {@link Level#ERROR} level including the
	 * stack trace of the Throwable t passed as parameter.
	 * 
	 * @param message -
	 *            the message object to log
	 * @param clazz -
	 *            the class which called this method
	 * @param t -
	 *            the exception to log, including its stack trace.
	 * @see Logger#error(Object, Throwable)
	 */
	public static void error(final Object message, final Class<?> clazz, final Throwable t) {
		getLogger(clazz).error(message, t);
	}
	
	/**
	 * Log a message object with the {@link Level#ERROR} level.
	 * 
	 * @param message -
	 *            the message object to log
	 * @param clazz -
	 *            the class which called this method
	 * @see Logger#error(Object)
	 */
	public static void error(final Object message, final Class<?> clazz) {
		getLogger(clazz).error(message);
	}
	
	/**
	 * Log a message object with the {@link Level#ERROR} level.
	 * 
	 * @param message -
	 *            the message object to log
	 * @see Logger#error(Object)
	 */
	public static void error(final Object message) {
		standardLogger.error(message);
	}
	
	/**
	 * Log a message object with the {@link Level#FATAL} level including the
	 * stack trace of the Throwable t passed as parameter.
	 * 
	 * @param message -
	 *            the message object to log
	 * @param clazz -
	 *            the class which called this method
	 * @param t -
	 *            the exception to log, including its stack trace.
	 * @see Logger#fatal(Object, Throwable)
	 */
	public static void fatal(final Object message, final Class<?> clazz, final Throwable t) {
		getLogger(clazz).fatal(message, t);
	}
	
	/**
	 * Log a message object with the {@link Level#FATAL} Level.
	 * 
	 * @param message -
	 *            the message object to log
	 * @param clazz -
	 *            the class which called this method
	 * @see Logger#fatal(Object)
	 */
	public static void fatal(final Object message, final Class<?> clazz) {
		getLogger(clazz).fatal(message);
	}
	
	/**
	 * Log a message object with the {@link Level#FATAL} Level.
	 * 
	 * @param message -
	 *            the message object to log
	 * @see Logger#fatal(Object)
	 */
	public static void fatal(final Object message) {
		standardLogger.fatal(message);
	}
	
	/**
	 * Log a message object with the {@link Level#TRACE} Level.
	 * 
	 * @param message -
	 *            the message object to log
	 * @param clazz -
	 *            the class which called this method
	 * @see Logger#trace(Object)
	 */
	public static void trace(final Object message, final Class<?> clazz) {
		getLogger(clazz).trace(message);
	}
	
	/**
	 * Log a message object with the {@link Level#TRACE} Level.
	 * 
	 * @param message -
	 *            the message object to log
	 * @see Logger#trace(Object)
	 */
	public static void trace(final Object message) {
		standardLogger.trace(message);
	}
	
	/**
	 * Returns the assigned {@link Level}, if any, for this Category.
	 * 
	 * @param clazz -
	 *            the class which called this method
	 * @return Level - the assigned Level, can be null.
	 */
	public static Level getLevel(final Class<?> clazz) {
		return getLogger(clazz).getLevel();
	}
	
	/**
	 * Returns the assigned {@link Level}, if any, for this Category.
	 * 
	 * @return Level - the assigned Level, can be null.
	 */
	public static Level getLevel() {
		return standardLogger.getLevel();
	}
	
	/**
	 * Adds a newline to the console output
	 */
	public static void newLine() {
		System.out.println("");
		// newline.debug("");
	}
}
