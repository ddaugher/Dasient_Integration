package com.ecommerce.dasient.jsf.ruleeditor;

import com.ecommerce.dasient.model.RuleRevision;
import com.ecommerce.dasient.model.RuleTerm;
import com.ecommerce.sbs.RuleSetLocal;
import java.util.List;

public class DeleteRuleBackingBean {

    private RuleSetLocal ruleSetBean;

    public void setRuleSetBean(RuleSetLocal ruleSetBean) {
        this.ruleSetBean = ruleSetBean;
    }

    private DeleteRuleParameters params;

    public void setParams(DeleteRuleParameters params) {
        this.params = params;
    }

    private DeleteRuleModelBean modelBean;

    public void setModelBean(DeleteRuleModelBean modelBean) {
        this.modelBean = modelBean;
    }

    public void loadRule() {
        RuleRevision ruleRevision = ruleSetBean.getRuleLatestRevision(params.getRuleId());

        modelBean.setRuleId(ruleRevision.getRule().getId());
        modelBean.setRevisionId(ruleRevision.getRevision().getId());
        modelBean.setAction(ruleRevision.getState().getAction().getId());
        modelBean.setDescription(ruleRevision.getState().getDescription());

        List<RuleTerm> terms = ruleRevision.getState().getTerms();

        if (terms.size() >= 1)
            modelBean.setPattern1(terms.get(0).getTerm());
        if (terms.size() >= 2)
            modelBean.setPattern2(terms.get(1).getTerm());
        if (terms.size() >= 3)
            modelBean.setPattern3(terms.get(2).getTerm());
        if (terms.size() >= 4)
            modelBean.setPattern4(terms.get(3).getTerm());
        if (terms.size() >= 5)
            modelBean.setPattern5(terms.get(4).getTerm());
        if (terms.size() >= 6)
            modelBean.setPattern6(terms.get(5).getTerm());
    }

}
