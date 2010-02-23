package com.ecommerce.dasient.model;

import java.io.Serializable;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
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
@Table(name = "malicious_url")
public class MaliciousUrl implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "malicious_url_seq")
    @SequenceGenerator(name = "malicious_url_seq", sequenceName = "malicious_url_seq")
    @Column(name = "malicious_url_id")
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "scan_history_id", nullable = false)
    private ScanHistory scanHistory;

    public ScanHistory getScanHistory() {
        return scanHistory;
    }

    public void setScanHistory(ScanHistory scanHistory) {
        this.scanHistory = scanHistory;
    }

	// scanned urls
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "maliciousUrl", fetch = FetchType.LAZY)
    private Set<MaliciousSourceCodeSnippets> maliciousSourceCodeSnippets;

    public Set<MaliciousSourceCodeSnippets> getMaliciousSourceCodeSnippets() {
        return maliciousSourceCodeSnippets;
    }

    public void setMaliciousSourceCodeSnippets(Set<MaliciousSourceCodeSnippets> maliciousSourceCodeSnippets) {
        this.maliciousSourceCodeSnippets = maliciousSourceCodeSnippets;
    }

    @Lob
    @Basic(optional = false)
    @Column(name = "url", nullable = false)
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof MaliciousUrl)) {
            return false;
        }
        MaliciousUrl other = (MaliciousUrl) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ecommerce.dasient.model.MaliciousUrl[id=" + id + "]";
    }
}