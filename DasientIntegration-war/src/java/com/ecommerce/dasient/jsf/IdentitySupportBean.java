package com.ecommerce.dasient.jsf;

import org.jasig.cas.client.util.AssertionHolder;
import org.jasig.cas.client.validation.Assertion;

public class IdentitySupportBean {

    public boolean isLoggedIn() {
        return AssertionHolder.getAssertion() != null;
    }

    public String getUsername() {
        Assertion assertion = AssertionHolder.getAssertion();
        if (assertion != null)
            return assertion.getPrincipal().getName();
        else
            return null;
    }

}
