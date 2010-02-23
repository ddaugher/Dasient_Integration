package com.ecommerce.dasient.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.SequenceGenerator;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "domain", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"control_panel_id", "hsphere_id"})
})
@NamedQueries({
    @NamedQuery(
        name = "getDomainByName", query = "select d " +
            "from Domain d " +
            "where d.name = :name " +
                "and d.controlPanel.name = :controlpanel " +
                "and d.deleted = false"
    ),
    @NamedQuery(
        name = "getDomainById",
        query = "select d from Domain d where d.id = :id"
    ),
    @NamedQuery(
        name = "getDomainsForScanning",
        query = "select d from Domain d " +
            "where d.deleted = false " +
                "and (d.status like 'SCAN_NOW' or ((d.status is null or d.status like '') and d.nextScan <= :date)) " +
            "order by d.status asc, d.nextScan asc"
    )
})
public class Domain implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "domain_seq")
    @SequenceGenerator(name = "domain_seq", sequenceName = "domain_seq")
    @Column(name = "domain_id")
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic(optional = false)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "update_tms", nullable = false)
	private Date updateTms;

	public Date getUpdateTms() {
		return updateTms;
	}

	public void setUpdateTms(Date tms) {
		this.updateTms = tms;
	}

    @PrePersist
    @PreUpdate
    private void resetUpdateTms() {
        updateTms = new Date();
    }

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "control_panel_id", nullable = false)
    private ControlPanel controlPanel;

    public ControlPanel getControlPanel() {
        return controlPanel;
    }

    public void setControlPanel(ControlPanel controlPanel) {
        this.controlPanel = controlPanel;
    }

    @Basic(optional = false)
    @Column(name = "hsphere_id", nullable = false)
    private int hsphereId;

    public int getHsphereId() {
        return hsphereId;
    }

    public void setHsphereId(int hsphereId) {
        this.hsphereId = hsphereId;
    }

    @Basic(optional = false)
    @Column(name = "name", length = 200, nullable = false)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "status", length = 20, nullable = true)
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Basic(optional = false)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "next_scan", nullable = false)
    private Date nextScan;

    public Date getNextScan() {
        return nextScan;
    }

    public void setNextScan(Date nextScan) {
        this.nextScan = nextScan;
    }

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted = false;

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Domain)) {
            return false;
        }
        Domain other = (Domain) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ecommerce.dasient.model.Domain[id=" + id + "]";
    }
}
