package com.ecommerce.sbs;

import com.ecommerce.dasient.model.Revision;
import com.ecommerce.dasient.model.RevisionType;
import com.ecommerce.dasient.model.RuleAction;
import com.ecommerce.dasient.model.RuleRevision;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;

@Local
public interface RuleSetLocal {

    /**
     * Releases the stateful session bean after use.
     *
     * This should always be called after the interaction with the bean is
     * done to avoid the stateful session bean lingering around unnecessarily.
     *
     * The bean uses an extended persistence context which stays open until
     * the bean is released, ie. lazy fetching of database entities will
     * work until calling release().
     */

    void release();

    /**
     * Returns the contents of the lookup table for rule actions.
     *
     * @return all RuleAction records in the database, sorted by displayName
     */

    List<RuleAction> getRuleActions();

    /**
     * Returns the contents of the lookup table for revision types.
     *
     * The enum RevisionTypeId contains the revision types as well.
     * Use of the enum is preferred as it avoids database queries.
     *
     * @return all RevisionType records in the database, sorted by displayName
     */

    List<RevisionType> getRevisionTypes();

    /**
     * Returns the latest revision.
     *
     * @return the latest revision record, or null if there are no revisions
     */

    Revision getLatestRevision();

    /**
     * Returns the revision with the given id.
     *
     * @param revisionId id of the revision to fetch
     * @return the revision record, or null if not found
     */

    Revision getRevision(long revisionId);

    /**
     * Returns the revision preceding the revision with the given id.
     *
     * @param revisionId id of the revision to fetch the predecessor of
     * @return the revision record, or null if there is no preceding revision
     */

    Revision getPrecedingRevision(long revisionId);

    /**
     * Returns the revision succeeding the revision with the given id.
     *
     * @param revisionId id of the revision to fetch the successor of
     * @return the revision record, or null if there is no succeeding revision
     */

    Revision getSucceedingRevision(long revisionId);

    /**
     * Returns revision since the given time.
     *
     * @param fromTime the time since when to return the revisions of
     * @return a list of revisions from the given time till now
     */

    List<Revision> getRevisionsByAge(Date fromTime);

    /**
     * Returns all revisions that modified a given rule.
     *
     * @param ruleId the rule to filter the revisions by
     * @return a list of revisions that modified the given rule
     */

    List<Revision> getRevisionsByRule(long ruleId);

    /**
     * Returns all revision by a given author.
     *
     * @param author the author to return the revisions of
     * @return a list of revisions by the given author
     */

    List<Revision> getRevisionsByAuthor(String author);

    /**
     * Returns a list of all revision authors.
     *
     * @return a distinct list of all revision authors
     */

    List<String> getRevisionAuthors();

    /**
     * Returns the latest revision of a rule with a given id.
     *
     * @param ruleId the id of the rule to return the latest revision of
     * @return the latest revision of the rule, or null if not found
     */

    RuleRevision getRuleLatestRevision(long ruleId);

    /**
     * Returns a specific revision of a rule with a given id.
     *
     * @param ruleId the id of the rule to return the latest revision of
     * @param revisionId the id of the revision to return
     * @return the revision of the rule, or null if not found
     */

    RuleRevision getRuleRevision(long ruleId, long revisionId);

    /**
     * Returns the set of rules at a given revision.
     *
     * @param revisionId the id of the revision to return the rule set of
     * @return the set of rules at the given revision
     */

    List<RuleRevision> getRulesByRevision(long revisionId);

    /**
     * Returns the set of rules at a given revision, excluding those that are deleted.
     *
     * @param revisionId the id of the revision to return the rule set of
     * @return the set of rules at the given revision, excluding those that are deleted
     */

    List<RuleRevision> getAliveRulesByRevision(long revisionId);

    /**
     * Creates a new rule.
     *
     * @param revisionAuthor the author of the revision
     * @param revisionComment a comment for the revision
     * @param ruleDescription a description for the rule
     * @param ruleAction an action for the rule
     * @param ruleTerms a list of terms for the rule
     * @return the created RuleRevison record
     */

    RuleRevision createRule(String revisionAuthor, String revisionComment, String ruleDescription, String ruleAction, List<String> ruleTerms);

    /**
     * Updates a rule.
     *
     * If the given revision is not the latest revision of the rule, the operation will fail.
     *
     * @param ruleId the id of the rule to update
     * @param revisionId the revision of the rule to update
     * @param revisionAuthor the author of the revision
     * @param revisionComment a comment for the revision
     * @param ruleDescription a description for the rule
     * @param ruleAction an action for the rule
     * @param ruleTerms a list of terms for the rule
     * @return the created RuleRevison record
     */

    RuleRevision updateRule(long ruleId, long revisionId, String revisionAuthor, String revisionComment, String ruleDescription, String ruleAction, List<String> ruleTerms);

    /**
     * Deletes a rule.
     *
     * If the given revision is not the latest revision of the rule,
     * or the rule is already deleted, the operation will fail.
     *
     * @param ruleId the id of the rule to delete
     * @param revisionId the revision of the rule to delete
     * @param revisionAuthor the author of the revision
     * @param revisionComment a comment for the revision
     * @return the created RuleRevison record
     */

    RuleRevision deleteRule(long ruleId, long revisionId, String revisionAuthor, String revisionComment);

    /**
     * Resurrects a rule.
     *
     * If the given revision is not the latest revision of the rule,
     * or the rule is not deleted, the operation will fail.
     *
     * @param ruleId the id of the rule to delete
     * @param revisionId the revision of the rule to delete
     * @param revisionAuthor the author of the revision
     * @param revisionComment a comment for the revision
     * @return the created RuleRevison record
     */

    RuleRevision resurrectRule(long ruleId, long revisionId, String revisionAuthor, String revisionComment);

}
