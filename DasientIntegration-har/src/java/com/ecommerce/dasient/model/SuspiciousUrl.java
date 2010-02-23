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
@Table(name = "suspicious_url")
public class SuspiciousUrl implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "suspicious_url_seq")
    @SequenceGenerator(name = "suspicious_url_seq", sequenceName = "suspicious_url_seq")
    @Column(name = "suspicious_url_id")
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
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "suspiciousUrl", fetch = FetchType.LAZY)
    private Set<SuspiciousSourceCodeSnippets> suspiciousSourceCodeSnippets;

    public Set<SuspiciousSourceCodeSnippets> getSuspiciousSourceCodeSnippets() {
        return suspiciousSourceCodeSnippets;
    }

    public void setSuspiciousSourceCodeSnippets(Set<SuspiciousSourceCodeSnippets> suspiciousSourceCodeSnippets) {
        this.suspiciousSourceCodeSnippets = suspiciousSourceCodeSnippets;
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
        if (!(object instanceof SuspiciousUrl)) {
            return false;
        }
        SuspiciousUrl other = (SuspiciousUrl) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ecommerce.dasient.model.SuspiciousUrl[id=" + id + "]";
    }
}