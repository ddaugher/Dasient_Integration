package com.ecommerce.dasient.jsf.ruleeditor;

import com.ecommerce.sbs.RuleSetLocal;

public class RuleHistoryBackingBean {

    private RuleSetLocal ruleSetBean;

    public void setRuleSetBean(RuleSetLocal ruleSetBean) {
        this.ruleSetBean = ruleSetBean;
    }

    private RuleHistoryParameters params;

    public void setParams(RuleHistoryParameters params) {
        this.params = params;
    }

    private RuleHistoryModelBean modelBean;

    public void setModelBean(RuleHistoryModelBean modelBean) {
        this.modelBean = modelBean;
    }

    public void loadHistory() {
        modelBean.setRevisions(ruleSetBean.getRevisionsByRule(params.getRuleId()));
    }

}
