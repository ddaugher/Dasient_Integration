package com.ecommerce.dasient.jsf.ruleeditor;

import com.ecommerce.sbs.RuleSetLocal;

public class ListAuthorsBackingBean {

    private RuleSetLocal ruleSetBean;

    public void setRuleSetBean(RuleSetLocal ruleSetBean) {
        this.ruleSetBean = ruleSetBean;
    }

    private ListAuthorsModelBean modelBean;

    public void setModelBean(ListAuthorsModelBean modelBean) {
        this.modelBean = modelBean;
    }

    public void loadAuthors() {
        modelBean.setAuthors(ruleSetBean.getRevisionAuthors());
    }

}
