package com.ecommerce.dasient.jsf.ruleeditor;

import com.ecommerce.dasient.model.RuleAction;
import com.ecommerce.sbs.RuleSetLocal;
import java.util.List;

public class RuleActionSupportBean {

    private RuleSetLocal ruleSetBean;

    public void setRuleSetBean(RuleSetLocal ruleSetBean) {
        this.ruleSetBean = ruleSetBean;
    }

    private List<RuleAction> actions;

    public List<RuleAction> getActions() {
        if (actions == null) {
            actions = ruleSetBean.getRuleActions();
        }
        return actions;
    }

}
