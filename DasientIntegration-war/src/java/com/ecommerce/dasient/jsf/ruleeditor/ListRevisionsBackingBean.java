package com.ecommerce.dasient.jsf.ruleeditor;

import com.ecommerce.dasient.model.Revision;
import com.ecommerce.sbs.RuleSetLocal;
import java.util.Date;
import java.util.List;

public class ListRevisionsBackingBean {

    private RuleSetLocal ruleSetBean;

    public void setRuleSetBean(RuleSetLocal ruleSetBean) {
        this.ruleSetBean = ruleSetBean;
    }

    private ListRevisionsModelBean modelBean;

    public void setModelBean(ListRevisionsModelBean modelBean) {
        this.modelBean = modelBean;
    }

    private static final long ONE_MONTH_IN_MILLISECONDS = 30 * 24 * 60 * 60 * 1000;

    public void loadHistory() {
        Date fromTime = new Date();

        fromTime.setTime(fromTime.getTime() - ONE_MONTH_IN_MILLISECONDS);

        List<Revision> revisions = ruleSetBean.getRevisionsByAge(fromTime);

        modelBean.setRevisions(revisions);
    }

}
