package com.ecommerce.dasient.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "web_server", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"control_panel_id", "logical_server_id"})
})
@NamedQueries({
    @NamedQuery(
        name = "getWebServerByName",
        query = "select ws from WebServer ws where ws.name = :name and ws.deleted = false"
    ),
    @NamedQuery(
        name = "getWebServerByLogicalServerId",
        query = "select ws from WebServer ws where ws.controlPanel = :controlPanel and ws.logicalServerId = :logicalServerId"
    )
})
public class WebServer implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "web_server_seq")
    @SequenceGenerator(name = "web_server_seq", sequenceName = "web_server_seq")
    @Column(name = "web_server_id")
    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    @Column(name = "clean_schedule", nullable = false)
    private int cleanSchedule;

    public int getCleanSchedule() {
        return cleanSchedule;
    }

    public void setCleanSchedule(int cleanSchedule) {
        this.cleanSchedule = cleanSchedule;
    }

    @Column(name = "logical_server_id", nullable = false)
    private int logicalServerId;

    public int getLogicalServerId() {
        return logicalServerId;
    }

    public void setLogicalServerId(int logicalServerId) {
        this.logicalServerId = logicalServerId;
    }

    // The field size is based on H-Sphere database field p_server.ip1
    @Basic(optional = false)
    @Column(name = "ip_address", length = 16, nullable = false)
    private String ipAddress;

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    // The field size is based on H-Sphere database field l_server.name
    @Basic(optional = false)
    @Column(name = "name", length = 128, nullable = false)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted = false;

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "web_server_storage", joinColumns = {
        @JoinColumn(name = "web_server_id", nullable = false, unique = true)
    }, inverseJoinColumns = {
        @JoinColumn(name = "web_storage_id", nullable = false)
    })
    private Set<WebStorage> storage;

    public WebStorage getStorage() {
        if (storage != null && !storage.isEmpty())
            return storage.iterator().next();
        else
            return null;
    }

    public void setStorage(WebStorage storage) {
        if (storage != null)
            this.storage = Collections.singleton(storage);
        else
            this.storage = null;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof WebServer)) {
            return false;
        }
        WebServer other = (WebServer) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ecommerce.dasient.model.WebServer[id=" + id + "]";
    }
}
