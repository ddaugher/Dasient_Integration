package com.ecommerce.dasient.model;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "clean_rule_match")
public class CleanRuleMatch implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "clean_rule_match_seq")
    @SequenceGenerator(name = "clean_rule_match_seq", sequenceName = "clean_rule_match_seq")
    @Column(name = "clean_rule_match_id")
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "clean_file_id", nullable = false)
    private CleanFile file;

    public CleanFile getFile() {
        return file;
    }

    public void setFile(CleanFile file) {
        this.file = file;
    }

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_revision_id", nullable = false)
    private RuleRevision ruleRevision;

    public RuleRevision getRuleRevision() {
        return ruleRevision;
    }

    public void setRule(RuleRevision ruleRevision) {
        this.ruleRevision = ruleRevision;
    }

    @Column(name = "start_pos", nullable = false)
    private int start;

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    @Column(name = "end_pos", nullable = false)
    private int end;

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    @Lob
    @Basic(optional = false)
    @Column(name = "literal", nullable = false)
    private String literal;

    public String getLiteral() {
        return literal;
    }

    public void setLiteral(String literal) {
        this.literal = literal;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof CleanRuleMatch)) {
            return false;
        }
        CleanRuleMatch other = (CleanRuleMatch) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ecommerce.dasient.model.CleanRuleMatch[id=" + id + "]";
    }

}
