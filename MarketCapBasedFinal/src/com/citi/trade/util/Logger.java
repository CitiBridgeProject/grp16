package com.citi.trade.util;

public final class Logger {
	private static final Logger logger = new Logger();

	private Logger() {
	}

	public static Logger getLogger() {
		return logger;
	}

	public void info(String className, String Message) {
		System.out.println("INFO @ " + className + "::" + Message);
	}

	public void warning(String className, String Message) {
		System.out.println("WARN @ " + className + "::" + Message);
	}

	public void debug(String className, String Message) {
		System.out.println("DEBUG @ " + className + "::" + Message);
	}
}
