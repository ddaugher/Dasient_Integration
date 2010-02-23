package com.ecommerce.dasient.model;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "request_ignored")
public class IgnoredRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "request_ignored_seq")
    @SequenceGenerator(name = "request_ignored_seq", sequenceName = "request_ignored_seq")
    @Column(name = "request_ignored_id")
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @OneToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "scan_history_id", nullable = false)
    private ScanHistory scanHistory;

    public ScanHistory getScanHistory() {
        return scanHistory;
    }

    public void setScanHistory(ScanHistory scanHistory) {
        this.scanHistory = scanHistory;
    }

    @Basic(optional = false)
    @Column(name = "requested_ip", length = 15, nullable = false)
    private String requestedIp;

    public String getRequestedIp() {
        return requestedIp;
    }

    public void setRequestedIp(String requestedIp) {
        this.requestedIp = requestedIp;
    }

    @Basic(optional = false)
    @Column(name = "actual_ip", length = 15, nullable = false)
    private String actualIp;

    public String getActualIp() {
        return actualIp;
    }

    public void setActualIp(String actualIp) {
        this.actualIp = actualIp;
    }

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof IgnoredRequest)) {
            return false;
        }
        IgnoredRequest other = (IgnoredRequest) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ecommerce.dasient.model.IgnoredRequest[id=" + id + "]";
    }
}