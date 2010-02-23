package com.ecommerce.dasient.jsf.ruleeditor;

import com.ecommerce.sbs.RuleSetLocal;

public class ReadAuthorBackingBean {

    private RuleSetLocal ruleSetBean;

    public void setRuleSetBean(RuleSetLocal ruleSetBean) {
        this.ruleSetBean = ruleSetBean;
    }

    private ReadAuthorParameters params;

    public void setParams(ReadAuthorParameters params) {
        this.params = params;
    }

    private ReadAuthorModelBean modelBean;

    public void setModelBean(ReadAuthorModelBean modelBean) {
        this.modelBean = modelBean;
    }

    public void loadAuthor() {
        modelBean.setAuthor(params.getAuthor());
        modelBean.setRevisions(ruleSetBean.getRevisionsByAuthor(params.getAuthor()));
    }

}
