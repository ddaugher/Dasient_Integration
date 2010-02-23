package com.ecommerce.dasient.exceptions;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class ScanHistoryNotFoundException extends RuntimeException {

    public ScanHistoryNotFoundException(long historyId) {
        this(historyId, null);
    }


    public ScanHistoryNotFoundException(long historyId, Throwable cause) {
        super("Scan history not found: " + historyId, cause);
    }

}
