/**
 * 
 */
package com.gpig.client;

/**
 * The exception thrown the execution of a post to the database fails, including
 *  when the the the response status is incorrect.
 * 
 * @author GPIGC
 */
public class FailedToWriteToDatastoreException extends Exception {

	private static final long serialVersionUID = -8810013928798104813L;
	
	public FailedToWriteToDatastoreException(String msg) {
		super(msg);
	}

}
