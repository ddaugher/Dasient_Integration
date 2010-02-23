package com.ecommerce.dasient.exceptions;

public class WebServerAuthenticationException extends Exception {

    public WebServerAuthenticationException(String msg) {
        super(msg);
    }

    public WebServerAuthenticationException(Throwable cause) {
        super(cause);
    }

    public WebServerAuthenticationException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
