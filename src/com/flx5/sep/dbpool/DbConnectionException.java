package com.flx5.sep.dbpool;

public class DbConnectionException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8068565124941813991L;

	public DbConnectionException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public DbConnectionException(String msg) {
		super(msg);
	}

	public DbConnectionException(Throwable cause) {
		super(cause);
	}
	
	
}
