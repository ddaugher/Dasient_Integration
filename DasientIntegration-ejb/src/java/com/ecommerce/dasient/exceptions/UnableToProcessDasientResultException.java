package com.ecommerce.dasient.exceptions;

public class UnableToProcessDasientResultException extends Exception {

    public UnableToProcessDasientResultException() {
		super();
    }

	/**
     * Creates a new instance of <code>UnableToProcessDasientResultException</code> without detail message.
     */
    public UnableToProcessDasientResultException(String msg) {
        super(msg);
    }


    /**
     * Constructs an instance of <code>UnableToProcessDasientResultException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public UnableToProcessDasientResultException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
