package com.ecommerce.dasient.model;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "control_panel")
@NamedQueries({
    @NamedQuery(
        name = "getControlPanelsToSync",
        query = "select cp from ControlPanel cp where cp.syncEnabled = true order by cp.id"
    ),
    @NamedQuery(
        name = "getControlPanelByName",
        query = "select cp from ControlPanel cp where cp.name = :name"
    )
})
public class ControlPanel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @Column(name = "control_panel_id", nullable = false)
    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Basic(optional = false)
    @Column(name = "name", length = 50, nullable = false)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic(optional = false)
    @Column(name = "db_host", length = 50, nullable = false)
    private String dbHost;

    public String getDbHost() {
        return dbHost;
    }

    public void setDbHost(String dbHost) {
        this.dbHost = dbHost;
    }

    @Column(name = "sync_enabled", nullable = false)
    private boolean syncEnabled = false;

    public boolean isSyncEnabled() {
        return syncEnabled;
    }

    public void setSyncEnabled(boolean syncEnabled) {
        this.syncEnabled = syncEnabled;
    }

    @Column(name = "last_synced_domain_id", nullable = false)
    private int lastSyncedDomainId = 0;

    public int getLastSyncedDomainId() {
        return lastSyncedDomainId;
    }

    public void setLastSyncedDomainId(int lastSyncedDomainId) {
        this.lastSyncedDomainId = lastSyncedDomainId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ControlPanel)) {
            return false;
        }
        ControlPanel other = (ControlPanel) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ecommerce.dasient.model.ControlPanel[id=" + id + "]";
    }
}
