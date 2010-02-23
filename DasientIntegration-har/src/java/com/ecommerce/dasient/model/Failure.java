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
@Table(name = "failure")
public class Failure implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "failure_seq")
    @SequenceGenerator(name = "failure_seq", sequenceName = "failure_seq")
    @Column(name = "failure_id")
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

    @Lob
    @Basic(optional = false)
    @Column(name = "code", nullable = false)
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Lob
    @Basic(optional = false)
    @Column(name = "reason", nullable = false)
    private String reason;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Failure)) {
            return false;
        }
        Failure other = (Failure) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ecommerce.dasient.model.Failure[id=" + id + "]";
    }
}