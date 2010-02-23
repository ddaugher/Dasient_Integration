package com.ecommerce.dasient.jsf.ruleeditor;

import com.ecommerce.sbs.RuleSetLocal;

public class ReadRevisionBackingBean {

    private RuleSetLocal ruleSetBean;

    public void setRuleSetBean(RuleSetLocal ruleSetBean) {
        this.ruleSetBean = ruleSetBean;
    }

    private ReadRevisionParameters params;

    public void setParams(ReadRevisionParameters params) {
        this.params = params;
    }

    private ReadRevisionModelBean modelBean;

    public void setModelBean(ReadRevisionModelBean modelBean) {
        this.modelBean = modelBean;
    }

    public void loadRevision() {
        modelBean.setRevision(ruleSetBean.getRevision(params.getRevisionId()));
    }

}
