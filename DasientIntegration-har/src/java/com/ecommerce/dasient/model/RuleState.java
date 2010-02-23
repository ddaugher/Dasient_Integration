package com.ecommerce.dasient.model;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "rule_state")
public class RuleState implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "rule_state_seq")
    @SequenceGenerator(name = "rule_state_seq", sequenceName = "rule_state_seq")
    @Column(name = "rule_state_id", nullable = false)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @OneToMany(mappedBy = "state", fetch = FetchType.LAZY)
    private List<RuleRevision> ruleRevisions;

    public List<RuleRevision> getRuleRevisions() {
        return ruleRevisions;
    }

    public void setRuleRevisions(List<RuleRevision> ruleRevisions) {
        this.ruleRevisions = ruleRevisions;
    }

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "action_id", nullable = false)
    private RuleAction action;

    public RuleAction getAction() {
        return action;
    }

    public void setAction(RuleAction action) {
        this.action = action;
    }

    @Lob
    @Basic(optional = false)
    @Column(name = "description", nullable = false)
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @OneToMany(mappedBy = "ruleState", fetch = FetchType.LAZY)
    private List<RuleTerm> terms;

    public List<RuleTerm> getTerms() {
        return terms;
    }

    public void setTerms(List<RuleTerm> terms) {
        this.terms = terms;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof RuleState)) {
            return false;
        }
        RuleState other = (RuleState) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ecommerce.dasient.model.RuleState[id=" + id + "]";
    }

}
