package com.ecommerce.dasient.exceptions;

public class UnableToDetermineDasientUrlException extends Exception {

    public UnableToDetermineDasientUrlException() {
		super();
    }

	/**
     * Creates a new instance of <code>UnableToDetermineDasientUrlException</code> without detail message.
     */
    public UnableToDetermineDasientUrlException(String msg) {
        super(msg);
    }


    /**
     * Constructs an instance of <code>UnableToDetermineDasientUrlException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public UnableToDetermineDasientUrlException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
