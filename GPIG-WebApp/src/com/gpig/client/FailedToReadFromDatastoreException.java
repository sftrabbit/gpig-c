package com.gpig.client;

/**
 * The Exception thrown when the execution of a query on the database fails.
 * 
 * @author GPIGC
 */
public class FailedToReadFromDatastoreException extends Exception {

	private static final long serialVersionUID = 1231492092061405159L;

	public FailedToReadFromDatastoreException(String msg) {
		super(msg);
	}

}
