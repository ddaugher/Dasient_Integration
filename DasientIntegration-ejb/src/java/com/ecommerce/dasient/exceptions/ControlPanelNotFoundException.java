package com.ecommerce.dasient.exceptions;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class ControlPanelNotFoundException extends RuntimeException {

    public ControlPanelNotFoundException(String controlPanelName) {
        this(controlPanelName, null);
    }

    public ControlPanelNotFoundException(String controlPanelName, Throwable cause) {
        super("Control panel not found: " + controlPanelName, cause);
    }

}
