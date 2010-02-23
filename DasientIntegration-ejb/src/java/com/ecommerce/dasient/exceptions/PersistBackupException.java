package com.ecommerce.dasient.exceptions;

public class PersistBackupException extends Exception {

    public PersistBackupException(String msg) {
        super(msg);
    }

    public PersistBackupException(Throwable cause) {
        super(cause);
    }

    public PersistBackupException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
