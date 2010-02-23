package com.ecommerce.sbs;

import com.ecommerce.dasient.model.Revision;
import com.ecommerce.dasient.model.RevisionType;
import com.ecommerce.dasient.model.RevisionTypeId;
import com.ecommerce.dasient.model.Rule;
import com.ecommerce.dasient.model.RuleAction;
import com.ecommerce.dasient.model.RuleRevision;
import com.ecommerce.dasient.model.RuleState;
import com.ecommerce.dasient.model.RuleTerm;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJBException;
import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import org.apache.log4j.Logger;
import org.jboss.ejb3.annotation.LocalBinding;

@Stateful
@LocalBinding(jndiBinding = "DasientIntegration/RuleSetBean/local")
@TransactionManagement(TransactionManagementType.CONTAINER)
public class RuleSetBean implements RuleSetLocal {

    private static final Logger logger = Logger.getLogger(RuleSetBean.class);

    /**
     * A reference to the persistence context.
     */
    @PersistenceContext(unitName = "DasientPU", type = PersistenceContextType.EXTENDED)
    private EntityManager dasientEM;

    @PostConstruct
    private void postConstruct() {
        logger.debug("postConstruct");
    }

    @PreDestroy
    private void preDestroy() {
        logger.debug("preDestroy");
    }

    @Remove
    public void release() {
        logger.debug("release");
    }

    @SuppressWarnings("unchecked")
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public List<RuleAction> getRuleActions() {
        return dasientEM.createNamedQuery("getRuleActions").getResultList();
    }

    @SuppressWarnings("unchecked")
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public List<RevisionType> getRevisionTypes() {
        return dasientEM.createNamedQuery("getRevisionTypes").getResultList();
    }

    @SuppressWarnings("unchecked")
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public Revision getLatestRevision() {
        Long revisionId = (Long) dasientEM.createNamedQuery("getLatestRevision").getSingleResult();

        if (revisionId != null)
            return dasientEM.find(Revision.class, revisionId);
        else
            return null;
    }

    @SuppressWarnings("unchecked")
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public Revision getRevision(long revisionId) {
        List<Revision> revisions = dasientEM.createNamedQuery("getRevision")
                .setParameter("revisionId", revisionId).getResultList();

        if (!revisions.isEmpty())
            return revisions.get(0);
        else
            return null;
    }

    @SuppressWarnings("unchecked")
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public Revision getPrecedingRevision(long revisionId) {
        List<Revision> revisions = dasientEM.createNamedQuery("getPrecedingRevisions")
                .setParameter("revisionId", revisionId).setMaxResults(1).getResultList();

        if (!revisions.isEmpty())
            return revisions.get(0);
        else
            return null;
    }

    @SuppressWarnings("unchecked")
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public Revision getSucceedingRevision(long revisionId) {
        List<Revision> revisions = dasientEM.createNamedQuery("getSucceedingRevisions")
                .setParameter("revisionId", revisionId).setMaxResults(1).getResultList();

        if (!revisions.isEmpty())
            return revisions.get(0);
        else
            return null;
    }

    @SuppressWarnings("unchecked")
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public List<Revision> getRevisionsByAge(Date fromTime) {
        return dasientEM.createNamedQuery("getRevisionsByAge")
                .setParameter("fromTime", fromTime).getResultList();
    }

    @SuppressWarnings("unchecked")
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public List<Revision> getRevisionsByRule(long ruleId) {
        return dasientEM.createNamedQuery("getRevisionsByRule")
                .setParameter("ruleId", ruleId).getResultList();
    }

    @SuppressWarnings("unchecked")
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public List<Revision> getRevisionsByAuthor(String author) {
        return dasientEM.createNamedQuery("getRevisionsByAuthor")
                .setParameter("author", author).getResultList();
    }

    @SuppressWarnings("unchecked")
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public List<String> getRevisionAuthors() {
        return dasientEM.createNamedQuery("getRevisionAuthors").getResultList();
    }

    @SuppressWarnings("unchecked")
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public RuleRevision getRuleLatestRevision(long ruleId) {
        List<RuleRevision> revisions = dasientEM.createNamedQuery("getRuleLatestRevision")
                .setParameter("ruleId", ruleId).getResultList();

        if (!revisions.isEmpty())
            return revisions.get(0);
        else
            return null;
    }

    @SuppressWarnings("unchecked")
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public RuleRevision getRuleRevision(long ruleId, long revisionId) {
        List<RuleRevision> revisions = dasientEM.createNamedQuery("getRuleRevision")
                .setParameter("ruleId", ruleId).setParameter("revisionId", revisionId).getResultList();

        if (!revisions.isEmpty())
            return revisions.get(0);
        else
            return null;
    }

    @SuppressWarnings("unchecked")
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public List<RuleRevision> getRulesByRevision(long revisionId) {
        return dasientEM.createNamedQuery("getRulesByRevision")
                .setParameter("revisionId", revisionId).getResultList();
    }

    @SuppressWarnings("unchecked")
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public List<RuleRevision> getAliveRulesByRevision(long revisionId) {
        return dasientEM.createNamedQuery("getAliveRulesByRevision")
                .setParameter("revisionId", revisionId).getResultList();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public RuleRevision createRule(String revisionAuthor, String revisionComment, String ruleDescription, String ruleAction, List<String> ruleTerms) {
        RuleAction action = dasientEM.find(RuleAction.class, ruleAction);
        if (action == null)
            throw new EJBException("RuleAction not found: " + ruleAction);

        RevisionType revisionType = dasientEM.find(RevisionType.class, RevisionTypeId.CREATE);
        if (revisionType == null)
            throw new EJBException("RevisionType not found: " + RevisionTypeId.CREATE);

        Rule newRule = new Rule();
        newRule.setRevisions(new ArrayList<RuleRevision>());

        dasientEM.persist(newRule);

        Revision newRevision = new Revision();
        newRevision.setRevisionAuthor(revisionAuthor);
        newRevision.setRevisionComment(revisionComment);
        newRevision.setRevisionTime(new Date());

        dasientEM.persist(newRevision);

        RuleState newState = new RuleState();
        newState.setAction(action);
        newState.setDescription(ruleDescription);
        newState.setTerms(new ArrayList<RuleTerm>());
        newState.setRuleRevisions(new ArrayList<RuleRevision>());

        dasientEM.persist(newState);

        for (String strTerm : ruleTerms) {
            RuleTerm newTerm = new RuleTerm();
            newTerm.setTerm(strTerm);

            newTerm.setRuleState(newState);
            newState.getTerms().add(newTerm);

            dasientEM.persist(newTerm);
        }

        RuleRevision newRuleRevision = new RuleRevision();
        newRuleRevision.setType(revisionType);
        newRuleRevision.setDeleted(false);

        newRuleRevision.setRule(newRule);
        newRule.getRevisions().add(newRuleRevision);

        newRuleRevision.setRevision(newRevision);
        newRevision.setRuleRevision(newRuleRevision);

        newState.getRuleRevisions().add(newRuleRevision);
        newRuleRevision.setState(newState);

        dasientEM.persist(newRuleRevision);

        return newRuleRevision;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public RuleRevision updateRule(long ruleId, long revisionId, String revisionAuthor, String revisionComment, String ruleDescription, String ruleAction, List<String> ruleTerms) {
        RuleAction action = dasientEM.find(RuleAction.class, ruleAction);
        if (action == null)
            throw new EJBException("RuleAction not found: " + action);

        RevisionType revisionType = dasientEM.find(RevisionType.class, RevisionTypeId.UPDATE);
        if (revisionType == null)
            throw new EJBException("RevisionType not found: " + RevisionTypeId.UPDATE);

        RuleRevision ruleRevision = (RuleRevision) dasientEM.createNamedQuery("getRuleRevision")
                .setParameter("ruleId", ruleId).setParameter("revisionId", revisionId).getSingleResult();

        if (ruleRevision.isDeleted())
            throw new EJBException(String.format("Revision %d of rule %d is currently deleted, refusing to update", revisionId, ruleId));

        Rule rule = ruleRevision.getRule();

        Revision newRevision = new Revision();
        newRevision.setRevisionAuthor(revisionAuthor);
        newRevision.setRevisionComment(revisionComment);
        newRevision.setRevisionTime(new Date());

        dasientEM.persist(newRevision);

        RuleState newState = new RuleState();
        newState.setAction(action);
        newState.setDescription(ruleDescription);
        newState.setTerms(new ArrayList<RuleTerm>());
        newState.setRuleRevisions(new ArrayList<RuleRevision>());

        dasientEM.persist(newState);

        for (String strTerm : ruleTerms) {
            RuleTerm newTerm = new RuleTerm();
            newTerm.setTerm(strTerm);

            newTerm.setRuleState(newState);
            newState.getTerms().add(newTerm);

            dasientEM.persist(newTerm);
        }

        RuleRevision newRuleRevision = new RuleRevision();
        newRuleRevision.setType(revisionType);
        newRuleRevision.setDeleted(false);

        newRuleRevision.setRule(rule);
        rule.getRevisions().add(newRuleRevision);

        newRuleRevision.setRevision(newRevision);
        newRevision.setRuleRevision(newRuleRevision);

        newRuleRevision.setPrevRevision(ruleRevision);
        ruleRevision.setNextRevision(newRuleRevision);

        newState.getRuleRevisions().add(newRuleRevision);
        newRuleRevision.setState(newState);

        dasientEM.persist(newRuleRevision);

        return newRuleRevision;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public RuleRevision deleteRule(long ruleId, long revisionId, String revisionAuthor, String revisionComment) {
        RevisionType revisionType = dasientEM.find(RevisionType.class, RevisionTypeId.DELETE);
        if (revisionType == null)
            throw new EJBException("RevisionType not found: " + RevisionTypeId.DELETE);

        RuleRevision ruleRevision = (RuleRevision) dasientEM.createNamedQuery("getRuleRevision")
                .setParameter("ruleId", ruleId).setParameter("revisionId", revisionId).getSingleResult();

        if (ruleRevision.isDeleted())
            throw new EJBException(String.format("Revision %d of rule %d is already deleted, refusing to delete", revisionId, ruleId));

        Rule rule = ruleRevision.getRule();

        Revision newRevision = new Revision();
        newRevision.setRevisionAuthor(revisionAuthor);
        newRevision.setRevisionComment(revisionComment);
        newRevision.setRevisionTime(new Date());

        dasientEM.persist(newRevision);

        RuleState state = ruleRevision.getState();

        RuleRevision newRuleRevision = new RuleRevision();
        newRuleRevision.setType(revisionType);
        newRuleRevision.setDeleted(true);

        newRuleRevision.setRule(rule);
        rule.getRevisions().add(newRuleRevision);

        newRuleRevision.setRevision(newRevision);
        newRevision.setRuleRevision(newRuleRevision);

        newRuleRevision.setPrevRevision(ruleRevision);
        ruleRevision.setNextRevision(newRuleRevision);

        state.getRuleRevisions().add(newRuleRevision);
        newRuleRevision.setState(state);

        dasientEM.persist(newRuleRevision);

        return newRuleRevision;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public RuleRevision resurrectRule(long ruleId, long revisionId, String revisionAuthor, String revisionComment) {
        RevisionType revisionType = dasientEM.find(RevisionType.class, RevisionTypeId.RESURRECT);
        if (revisionType == null)
            throw new EJBException("RevisionType not found: " + RevisionTypeId.RESURRECT);

        RuleRevision ruleRevision = (RuleRevision) dasientEM.createNamedQuery("getRuleRevision")
                .setParameter("ruleId", ruleId).setParameter("revisionId", revisionId).getSingleResult();

        if (!ruleRevision.isDeleted())
            throw new EJBException(String.format("Revision %d of rule %d is not deleted, refusing to resurrect", revisionId, ruleId));

        Rule rule = ruleRevision.getRule();

        Revision newRevision = new Revision();
        newRevision.setRevisionAuthor(revisionAuthor);
        newRevision.setRevisionComment(revisionComment);
        newRevision.setRevisionTime(new Date());

        dasientEM.persist(newRevision);

        RuleState state = ruleRevision.getState();

        RuleRevision newRuleRevision = new RuleRevision();
        newRuleRevision.setType(revisionType);
        newRuleRevision.setDeleted(false);

        newRuleRevision.setRule(rule);
        rule.getRevisions().add(newRuleRevision);

        newRuleRevision.setRevision(newRevision);
        newRevision.setRuleRevision(newRuleRevision);

        newRuleRevision.setPrevRevision(ruleRevision);
        ruleRevision.setNextRevision(newRuleRevision);

        state.getRuleRevisions().add(newRuleRevision);
        newRuleRevision.setState(state);

        dasientEM.persist(newRuleRevision);

        return newRuleRevision;
    }

}
