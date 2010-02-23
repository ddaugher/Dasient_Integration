package com.ecommerce.dasient.exceptions;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class DomainNotFoundException extends RuntimeException {

    public DomainNotFoundException(String name) {
        this(name, null);
    }

    public DomainNotFoundException(String name, Throwable cause) {
        super("Domain not found: " + name, cause);
    }

}
