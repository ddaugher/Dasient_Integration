package com.ecommerce.dasient.jsf.ruleeditor;

import com.ecommerce.dasient.model.Revision;
import com.ecommerce.dasient.model.RuleRevision;
import com.ecommerce.sbs.RuleSetLocal;
import java.util.ArrayList;
import java.util.List;

public class ListRulesBackingBean {

    private RuleSetLocal ruleSetBean;

    public void setRuleSetBean(RuleSetLocal ruleSetBean) {
        this.ruleSetBean = ruleSetBean;
    }

    private ListRulesParameters params;

    public void setParams(ListRulesParameters params) {
        this.params = params;
    }

    private ListRulesModelBean modelBean;

    public void setModelBean(ListRulesModelBean modelBean) {
        this.modelBean = modelBean;
    }

    public void loadRules() {
        if (params.isShowAll()) {
            modelBean.setLatestRevisionMappingId("listAllRules");
            modelBean.setSpecificRevisionMappingId("listAllRulesByRevision");
        }
        else {
            modelBean.setLatestRevisionMappingId("listRules");
            modelBean.setSpecificRevisionMappingId("listRulesByRevision");
        }

        Long revisionId = null;

        if (params.getRevisionId() == null) {
            Revision newestRevision = ruleSetBean.getLatestRevision();
            if (newestRevision != null)
                revisionId = newestRevision.getId();
        }
        else {
            revisionId = params.getRevisionId();
        }

        modelBean.setRevisionId(revisionId);

        List<ListRulesModelBean.RuleEntry> rules = new ArrayList<ListRulesModelBean.RuleEntry>();

        if (revisionId != null) {
            List<RuleRevision> ruleRevisions;

            if (params.isShowAll())
                ruleRevisions = ruleSetBean.getRulesByRevision(revisionId);
            else
                ruleRevisions = ruleSetBean.getAliveRulesByRevision(revisionId);

            for (RuleRevision ruleRevision : ruleRevisions) {
                ListRulesModelBean.RuleEntry entry = new ListRulesModelBean.RuleEntry();

                entry.setRuleId(ruleRevision.getRule().getId());
                entry.setRevisionId(ruleRevision.getRevision().getId());
                entry.setNoMatchesLast30days(0);
                entry.setDescription(ruleRevision.getState().getDescription());

                rules.add(entry);
            }
        }

        modelBean.setRules(rules);

        if (revisionId != null) {
            Revision preceedingRevision = ruleSetBean.getPrecedingRevision(revisionId);

            if (preceedingRevision != null)
                modelBean.setPreceedingRevision(preceedingRevision.getId());

            Revision succeedingRevision = ruleSetBean.getSucceedingRevision(revisionId);
            
            if (succeedingRevision != null)
                modelBean.setSucceedingRevision(succeedingRevision.getId());
        }
    }

}
