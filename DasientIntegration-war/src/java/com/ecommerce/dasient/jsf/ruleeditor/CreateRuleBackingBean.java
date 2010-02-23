package com.ecommerce.dasient.jsf.ruleeditor;

import com.ecommerce.sbs.RuleSetLocal;

public class CreateRuleBackingBean {

    private RuleSetLocal ruleSetBean;

    public void setRuleSetBean(RuleSetLocal ruleSetBean) {
        this.ruleSetBean = ruleSetBean;
    }

    private CreateRuleModelBean modelBean;

    public void setModelBean(CreateRuleModelBean modelBean) {
        this.modelBean = modelBean;
    }

}
