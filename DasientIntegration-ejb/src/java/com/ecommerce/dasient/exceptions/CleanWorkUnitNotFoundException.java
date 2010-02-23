package com.ecommerce.dasient.exceptions;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class CleanWorkUnitNotFoundException extends RuntimeException {

    public CleanWorkUnitNotFoundException(long workUnitId) {
        this(workUnitId, null);
    }

    public CleanWorkUnitNotFoundException(long workUnitId, Throwable cause) {
        super("Clean work unit not found: " + String.valueOf(workUnitId), cause);
    }

}
