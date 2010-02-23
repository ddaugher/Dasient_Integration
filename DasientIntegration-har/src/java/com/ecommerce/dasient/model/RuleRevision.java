package com.ecommerce.dasient.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "rule_revision")
@NamedQueries({
     @NamedQuery(name = "getRuleRevision", query =
        "select distinct rrev " +
        "from RuleRevision rrev " +
            "inner join fetch rrev.revision rev " +
            "inner join fetch rrev.rule rule " +
            "inner join fetch rrev.type " +
            "inner join fetch rrev.state state " +
            "inner join fetch state.action " +
        "where rule.id = :ruleId " +
            "and rev.id = :revisionId"),
    @NamedQuery(name = "getRuleLatestRevision", query =
        "select rrev " +
        "from RuleRevision rrev " +
            "inner join fetch rrev.revision rev " +
            "inner join fetch rrev.rule rule " +
            "inner join fetch rrev.type " +
            "inner join fetch rrev.state state " +
            "inner join fetch state.action " +
            "left join rrev.nextRevision next " +
        "where rule.id = :ruleId " +
            "and next.id is null"),
    @NamedQuery(name = "getRulesByRevision", query =
        "select rrev " +
        "from RuleRevision as rrev " +
            "inner join fetch rrev.revision rev " +
            "inner join fetch rrev.rule rule " +
            "inner join fetch rrev.type " +
            "inner join fetch rrev.state state " +
            "inner join fetch state.action " +
            "left join rrev.nextRevision next " +
        "where rev.id <= :revisionId " +
            "and (next.id is null or next.id > :revisionId)"),
    @NamedQuery(name = "getAliveRulesByRevision", query =
        "select rrev " +
        "from RuleRevision as rrev " +
            "inner join fetch rrev.revision rev " +
            "inner join fetch rrev.rule rule " +
            "inner join fetch rrev.type " +
            "inner join fetch rrev.state state " +
            "inner join fetch state.action " +
            "left join rrev.nextRevision next " +
        "where rev.id <= :revisionId " +
            "and (next.id is null or next.id > :revisionId) " +
            "and rrev.deleted = false")
})
public class RuleRevision implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "rule_revision_seq")
    @SequenceGenerator(name = "rule_revision_seq", sequenceName = "rule_revision_seq")
    @Column(name = "rule_revision_id", nullable = false)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_id", nullable = false)
    private Rule rule;

    public Rule getRule() {
        return rule;
    }

    public void setRule(Rule rule) {
        this.rule = rule;
    }

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "revision_id", nullable = false, unique = true)
    private Revision revision;

    public Revision getRevision() {
        return revision;
    }

    public void setRevision(Revision revision) {
        this.revision = revision;
    }

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", nullable = false)
    private RevisionType type;

    public RevisionType getType() {
        return type;
    }

    public void setType(RevisionType type) {
        this.type = type;
    }

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted = false;

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "state_id", nullable = false)
    private RuleState state;

    public RuleState getState() {
        return state;
    }

    public void setState(RuleState state) {
        this.state = state;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "rule_revision_chain", joinColumns = {
        @JoinColumn(name = "next_rule_revision_id", nullable = false, unique = true)
    }, inverseJoinColumns = {
        @JoinColumn(name = "prev_rule_revision_id", nullable = false, unique = true)
    })
    private Set<RuleRevision> prevRevision;

    public RuleRevision getPrevRevision() {
        if (prevRevision != null && !prevRevision.isEmpty())
            return prevRevision.iterator().next();
        else
            return null;
    }

    public void setPrevRevision(RuleRevision prevRevision) {
        if (prevRevision != null)
            this.prevRevision = Collections.singleton(prevRevision);
        else
            this.prevRevision = null;
    }

    @ManyToMany(mappedBy = "prevRevision", fetch = FetchType.LAZY)
    private Set<RuleRevision> nextRevision;

    public RuleRevision getNextRevision() {
        if (nextRevision != null && !nextRevision.isEmpty())
            return nextRevision.iterator().next();
        else
            return null;
    }

    public void setNextRevision(RuleRevision nextRevision) {
        if (nextRevision != null)
            this.nextRevision = Collections.singleton(nextRevision);
        else
            this.nextRevision = null;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof RuleRevision)) {
            return false;
        }
        RuleRevision other = (RuleRevision) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ecommerce.dasient.model.RuleRevision[id=" + id + "]";
    }

}
