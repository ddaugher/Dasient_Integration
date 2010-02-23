package com.ecommerce.dasient.model;

import java.io.Serializable;
import java.util.Set;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "clean_history")
public class CleanHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "clean_history_id", nullable = false)
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

    @Basic(optional = false)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_time", nullable = false)
    private Date startTime;

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    @Basic(optional = false)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "finish_time", nullable = false)
    private Date finishTime;

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "revision_id", nullable = false)
    private Revision revision;

    public Revision getRevision() {
        return revision;
    }

    public void setRevision(Revision revision) {
        this.revision = revision;
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "cleanHistory", fetch = FetchType.LAZY)
    private List<CleanFile> cleanFiles;

    public List<CleanFile> getCleanFiles() {
        return cleanFiles;
    }

    public void setCleanFiles(List<CleanFile> cleanFiles) {
        this.cleanFiles = cleanFiles;
    }

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "clean_history_triggers", joinColumns = {
        @JoinColumn(name = "clean_history_id", nullable = false)
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
        if (!(object instanceof CleanHistory)) {
            return false;
        }
        CleanHistory other = (CleanHistory) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ecommerce.dasient.model.CleanHistory[id=" + id + "]";
    }
}
