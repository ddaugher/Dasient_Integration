package com.ecommerce.dasient.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "revision")
@NamedQueries({
    @NamedQuery(name = "getLatestRevision", query =
        "select max(rev.id) " +
        "from Revision rev"),
    @NamedQuery(name = "getPrecedingRevisions", query =
        "select rev " +
        "from Revision rev " +
        "where rev.id < :revisionId " +
        "order by rev.id desc"),
    @NamedQuery(name = "getSucceedingRevisions", query =
        "select rev " +
        "from Revision rev " +
        "where rev.id > :revisionId " +
        "order by rev.id asc"),
    @NamedQuery(name = "getRevision", query =
        "select rev " +
        "from Revision rev " +
            "inner join fetch rev.ruleRevision rrev " +
            "inner join fetch rrev.rule " +
            "inner join fetch rrev.type " +
            "inner join fetch rrev.state state " +
            "inner join fetch state.action " +
        "where rev.id = :revisionId"),
    @NamedQuery(name = "getRevisionsByAge", query =
        "select rev " +
        "from Revision rev " +
            "inner join fetch rev.ruleRevision rrev " +
            "inner join fetch rrev.rule " +
            "inner join fetch rrev.type " +
            "inner join fetch rrev.state state " +
            "inner join fetch state.action " +
        "where rev.revisionTime < :fromTime " +
        "order by rev.id desc"),
    @NamedQuery(name = "getRevisionsByRule", query =
        "select rev " +
        "from Revision rev " +
            "inner join fetch rev.ruleRevision rrev " +
            "inner join fetch rrev.rule rule " +
            "inner join fetch rrev.type " +
            "inner join fetch rrev.state state " +
            "inner join fetch state.action " +
        "where rule.id = :ruleId " +
        "order by rev.id desc"),
    @NamedQuery(name = "getRevisionsByAuthor", query =
        "select rev " +
        "from Revision rev " +
            "inner join fetch rev.ruleRevision rrev " +
            "inner join fetch rrev.rule " +
            "inner join fetch rrev.type " +
            "inner join fetch rrev.state state " +
            "inner join fetch state.action " +
        "where rev.revisionAuthor = :author " +
        "order by rev.id desc"),
    @NamedQuery(name = "getRevisionAuthors", query =
        "select distinct rev.revisionAuthor " +
        "from Revision rev " +
        "order by rev.revisionAuthor asc")
})
public class Revision implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "revision_seq")
    @SequenceGenerator(name = "revision_seq", sequenceName = "revision_seq")
    @Column(name = "revision_id", nullable = false)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic(optional = false)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "revision_time", nullable = false)
    private Date revisionTime;

    public Date getRevisionTime() {
        return revisionTime;
    }

    public void setRevisionTime(Date revisionTime) {
        this.revisionTime = revisionTime;
    }

    @Basic(optional = false)
    @Column(name = "revision_author", nullable = false, length = 64)
    private String revisionAuthor;

    public String getRevisionAuthor() {
        return revisionAuthor;
    }

    public void setRevisionAuthor(String revisionAuthor) {
        this.revisionAuthor = revisionAuthor;
    }

    @Lob
    @Basic(optional = false)
    @Column(name = "revision_comment", nullable = false)
    private String revisionComment;

    public String getRevisionComment() {
        return revisionComment;
    }

    public void setRevisionComment(String revisionComment) {
        this.revisionComment = revisionComment;
    }

    @OneToMany(mappedBy = "revision", fetch = FetchType.LAZY)
    private List<RuleRevision> ruleRevision;

    public RuleRevision getRuleRevision() {
        if (ruleRevision != null && !ruleRevision.isEmpty())
            return ruleRevision.get(0);
        else
            return null;
    }

    public void setRuleRevision(RuleRevision ruleRevision) {
        if (ruleRevision != null)
            this.ruleRevision = Arrays.asList(ruleRevision);
        else
            this.ruleRevision = null;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Revision)) {
            return false;
        }
        Revision other = (Revision) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ecommerce.dasient.model.Revision[id=" + id + "]";
    }

}
