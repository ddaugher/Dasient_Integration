package com.ecommerce.dasient.exceptions;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * A base class for all web service exceptions.
 *
 * Main purpose is to add the field "trace" to the web service response, containing the stack trace.
 */
public class DasientWebServiceException extends Exception {

    /**
     * Initializes the exception.
     *
     * @param cause the cause of this exception
     */
    public DasientWebServiceException(Throwable cause) {
        super(cause);
    }

    /**
     * Initializes the exception.
     *
     * @param msg a message describing what went wrong
     */
    public DasientWebServiceException(String msg) {
        super(msg);
    }

    /**
     * Initializes the exception.
     *
     * @param cause the cause of this exception
     * @param msg a message describing what went wrong
     */
    public DasientWebServiceException(String msg, Throwable cause) {
        super(msg, cause);
    }

    /**
     * Returns a string representation of the stack trace.
     *
     * This exists to make the stack trace appear in the web service response.
     *
     * @return the stack trace as a string
     */
    public String getTrace() {
        StringWriter buffer = new StringWriter();
        printStackTrace(new PrintWriter(buffer));
        return buffer.toString();
    }
}
