package com.ecommerce.dasient.exceptions;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class WebServerNotFoundException extends RuntimeException {

    public WebServerNotFoundException(String webServerName) {
        this(webServerName, null);
    }

    public WebServerNotFoundException(String webServerName, Throwable cause) {
        super("Web server not found: " + webServerName, cause);
    }

}
