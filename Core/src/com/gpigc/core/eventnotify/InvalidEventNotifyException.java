package com.gpigc.core.eventnotify;

/**
 * Exception representing a event notification that should not have been built
 * @author ajf518
 */
public class InvalidEventNotifyException extends Exception {
	
	public InvalidEventNotifyException() {}
	
	/**
	 * 
	 * @param arg0
	 */
	public InvalidEventNotifyException(String arg0) {
		super(arg0);
	}
	
	/**
	 * 
	 * @param t
	 */
	public InvalidEventNotifyException(Throwable t) {
		super(t);
	}
}
