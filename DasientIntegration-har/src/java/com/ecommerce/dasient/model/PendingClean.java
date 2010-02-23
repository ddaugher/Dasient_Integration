package com.ecommerce.dasient.model;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "pending_clean", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"control_panel_id", "account_id"})
})
@NamedQueries({
    @NamedQuery(name = "getPendingCleanForAccount", query =
        "select pending " +
            "from PendingClean pending " +
            "inner join fetch pending.controlPanel " +
            "inner join fetch pending.webServer " +
        "where pending.controlPanel = :controlPanel " +
            "and pending.accountId = :accountId"),
    @NamedQuery(name = "getPendingCleansForWebServer", query =
        "select pending " +
            "from PendingClean pending " +
        "where pending.webServer = :webServer " +
        "order by pending.id"),
    @NamedQuery(name = "getPendingCleansForWebStorage", query =
        "select pending " +
            "from PendingClean pending " +
        "where pending.webServer.storage = :webStorage " +
        "order by pending.id"),
    @NamedQuery(name = "getAllPendingCleans", query =
        "select pending " +
            "from PendingClean pending " +
        "order by pending.id")
})
public class PendingClean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "clean_history_seq")
    @SequenceGenerator(name = "clean_history_seq", sequenceName = "clean_history_seq")
    @Column(name = "pending_clean_id")
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "web_server_id", nullable = false)
    private WebServer webServer;

    public WebServer getWebServer() {
        return webServer;
    }

    public void setWebServer(WebServer webServer) {
        this.webServer = webServer;
    }

    @Basic(optional = false)
    @Column(name = "account_id", nullable = false)
    private int accountId;

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    // The field size is based on H-Sphere database field unix_user.login
    @Basic(optional = false)
    @Column(name = "web_username", length = 20, nullable = false)
    private String webUsername;

    public String getWebUsername() {
        return webUsername;
    }

    public void setWebUsername(String webUsername) {
        this.webUsername = webUsername;
    }

    @Basic(optional = false)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "schedule_time", nullable = false)
    private Date scheduleTime;

    public Date getScheduleTime() {
        return scheduleTime;
    }

    public void setScheduleTime(Date scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "query_time", nullable = true)
    private Date queryTime;

    public Date getQueryTime() {
        return queryTime;
    }

    public void setQueryTime(Date queryTime) {
        this.queryTime = queryTime;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "pending_clean_triggers", joinColumns = {
        @JoinColumn(name = "pending_clean_id", nullable = false)
    }, inverseJoinColumns = {
        @JoinColumn(name = "scan_history_id", nullable = false, unique = true)
    })
    private Set<ScanHistory> triggers;

    public Set<ScanHistory> getTriggers() {
        return triggers;
    }

    public void setTriggers(Set<ScanHistory> triggers) {
        this.triggers = triggers;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof PendingClean)) {
            return false;
        }
        PendingClean other = (PendingClean) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ecommerce.dasient.model.PendingClean[id=" + id + "]";
    }

}
