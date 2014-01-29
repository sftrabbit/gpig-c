/**
 * 
 */
package com.gpig.client;

/**
 * @author Tom Davies
 */
public class FailedToWriteToDatastoreException extends Exception {

	private static final long serialVersionUID = -8810013928798104813L;
	
	public FailedToWriteToDatastoreException(String msg) {
		super(msg);
	}

}
