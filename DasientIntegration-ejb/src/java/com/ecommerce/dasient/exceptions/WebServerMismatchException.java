package com.ecommerce.dasient.exceptions;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class WebServerMismatchException extends RuntimeException {

    public WebServerMismatchException(String givenTo, String receivedFrom) {
        super(String.format("Received a response from '%s' for a clean" +
                " request that was given to '%s'", receivedFrom, givenTo));
    }

}
