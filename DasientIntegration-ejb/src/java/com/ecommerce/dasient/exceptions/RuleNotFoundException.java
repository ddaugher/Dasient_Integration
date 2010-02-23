package com.ecommerce.dasient.exceptions;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class RuleNotFoundException extends RuntimeException {

    public RuleNotFoundException(long ruleId, long revisionId) {
        this(ruleId, revisionId, null);
    }


    public RuleNotFoundException(long ruleId, long revisionId, Throwable cause) {
        super(String.format("No rule %d found in revision %d", ruleId, revisionId), cause);
    }

}
