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
@Table(name = "malicious_source_code_snippet")
public class MaliciousSourceCodeSnippets implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "malicious_source_code_snippet_seq")
    @SequenceGenerator(name = "malicious_source_code_snippet_seq", sequenceName = "malicious_source_code_snippet_seq")
    @Column(name = "snippet_id")
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "malicious_url_id", nullable = false)
    private MaliciousUrl maliciousUrl;

    public MaliciousUrl getMaliciousUrl() {
        return maliciousUrl;
    }

    public void setMaliciousUrl(MaliciousUrl maliciousUrl) {
        this.maliciousUrl = maliciousUrl;
    }

    @Lob
    @Basic(optional = false)
    @Column(name = "snippet", nullable = false)
    private String snippet;

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof MaliciousSourceCodeSnippets)) {
            return false;
        }
        MaliciousSourceCodeSnippets other = (MaliciousSourceCodeSnippets) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ecommerce.dasient.model.MaliciousSourceCodeSnippet[id=" + id + "]";
    }
}