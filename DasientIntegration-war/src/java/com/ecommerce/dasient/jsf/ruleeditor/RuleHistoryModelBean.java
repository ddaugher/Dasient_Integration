package com.ecommerce.dasient.jsf.ruleeditor;

import com.ecommerce.dasient.model.Revision;
import java.util.List;

public class RuleHistoryModelBean {

    private long ruleId;

    public long getRuleId() {
        return ruleId;
    }

    public void setRuleId(long ruleId) {
        this.ruleId = ruleId;
    }

    private List<Revision> revisions;

    public List<Revision> getRevisions() {
        return revisions;
    }

    public void setRevisions(List<Revision> revisions) {
        this.revisions = revisions;
    }

}
