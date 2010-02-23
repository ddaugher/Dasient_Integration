package com.ecommerce.dasient.exceptions;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class RevisionNotFoundException extends RuntimeException {

    public RevisionNotFoundException(long revisionId) {
        this(revisionId, null);
    }

    public RevisionNotFoundException(long revisionId, Throwable cause) {
        super("Revision not found: " + String.valueOf(revisionId), cause);
    }

}
