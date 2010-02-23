package com.ecommerce.dasient.jsf.ruleeditor;

import com.ecommerce.dasient.model.Revision;
import com.ecommerce.dasient.model.RevisionType;
import com.ecommerce.dasient.model.RuleAction;
import com.ecommerce.dasient.model.RuleRevision;
import com.ecommerce.sbs.RuleSetLocal;
import java.util.Date;
import java.util.List;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.NoSuchEJBException;

public class RuleSetBusinessBean implements RuleSetLocal {

    @EJB(mappedName = "DasientIntegration/RuleSetBean/local")
    private RuleSetLocal ruleSetBean;

    @PreDestroy
    private void preDestroy() {
        try {
            ruleSetBean.release();
        }
        catch (NoSuchEJBException exc) {
            // thrown if the EJB was released already
        }
    }

    public void release() {
        ruleSetBean.release();
    }

    public RuleRevision updateRule(long ruleId, long revisionId, String revisionAuthor, String revisionComment, String ruleDescription, String ruleAction, List<String> ruleTerms) {
        return ruleSetBean.updateRule(ruleId, revisionId, revisionAuthor, revisionComment, ruleDescription, ruleAction, ruleTerms);
    }

    public RuleRevision resurrectRule(long ruleId, long revisionId, String revisionAuthor, String revisionComment) {
        return ruleSetBean.resurrectRule(ruleId, revisionId, revisionAuthor, revisionComment);
    }

    public Revision getSucceedingRevision(long revisionId) {
        return ruleSetBean.getSucceedingRevision(revisionId);
    }

    public List<RuleRevision> getRulesByRevision(long revisionId) {
        return ruleSetBean.getRulesByRevision(revisionId);
    }

    public List<RuleRevision> getAliveRulesByRevision(long revisionId) {
        return ruleSetBean.getAliveRulesByRevision(revisionId);
    }

    public RuleRevision getRuleRevision(long ruleId, long revisionId) {
        return ruleSetBean.getRuleRevision(ruleId, revisionId);
    }

    public RuleRevision getRuleLatestRevision(long ruleId) {
        return ruleSetBean.getRuleLatestRevision(ruleId);
    }

    public List<RuleAction> getRuleActions() {
        return ruleSetBean.getRuleActions();
    }

    public List<Revision> getRevisionsByRule(long ruleId) {
        return ruleSetBean.getRevisionsByRule(ruleId);
    }

    public List<Revision> getRevisionsByAuthor(String author) {
        return ruleSetBean.getRevisionsByAuthor(author);
    }

    public List<Revision> getRevisionsByAge(Date fromTime) {
        return ruleSetBean.getRevisionsByAge(fromTime);
    }

    public List<RevisionType> getRevisionTypes() {
        return ruleSetBean.getRevisionTypes();
    }

    public List<String> getRevisionAuthors() {
        return ruleSetBean.getRevisionAuthors();
    }

    public Revision getRevision(long revisionId) {
        return ruleSetBean.getRevision(revisionId);
    }

    public Revision getPrecedingRevision(long revisionId) {
        return ruleSetBean.getPrecedingRevision(revisionId);
    }

    public Revision getLatestRevision() {
        return ruleSetBean.getLatestRevision();
    }

    public RuleRevision deleteRule(long ruleId, long revisionId, String revisionAuthor, String revisionComment) {
        return ruleSetBean.deleteRule(ruleId, revisionId, revisionAuthor, revisionComment);
    }

    public RuleRevision createRule(String revisionAuthor, String revisionComment, String ruleDescription, String ruleAction, List<String> ruleTerms) {
        return ruleSetBean.createRule(revisionAuthor, revisionComment, ruleDescription, ruleAction, ruleTerms);
    }

}
