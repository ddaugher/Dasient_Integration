package com.ecommerce.dasient.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "clean_file")
public class CleanFile implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "clean_file_seq")
    @SequenceGenerator(name = "clean_file_seq", sequenceName = "clean_file_seq")
    @Column(name = "clean_file_id")
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "clean_history_id", nullable = false)
    private CleanHistory cleanHistory;

    public CleanHistory getCleanHistory() {
        return cleanHistory;
    }

    public void setCleanHistory(CleanHistory cleanHistory) {
        this.cleanHistory = cleanHistory;
    }

    @Lob
    @Column(name = "`path`", nullable = false)
    private String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Basic(optional = false)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "mtime", nullable = false)
    private Date modifyTime;

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    @Basic(optional = false)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ctime", nullable = false)
    private Date changeTime;

    public Date getChangeTime() {
        return changeTime;
    }

    public void setChangeTime(Date changeTime) {
        this.changeTime = changeTime;
    }

    @Basic(optional = false)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "new_ctime", nullable = false)
    private Date newChangeTime;

    public Date getNewChangeTime() {
        return newChangeTime;
    }

    public void setNewChangeTime(Date newChangeTime) {
        this.newChangeTime = newChangeTime;
    }

    @Column(name = "mode", nullable = false)
    private int mode;

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    @Basic(optional = false)
    @Column(name = "`hash`", length = 64, nullable = false)
    private String hash;

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    @OneToMany(mappedBy = "file", fetch = FetchType.LAZY)
    private List<CleanRuleMatch> matches;

    public List<CleanRuleMatch> getMatches() {
        return matches;
    }

    public void setMatches(List<CleanRuleMatch> matches) {
        this.matches = matches;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof CleanFile)) {
            return false;
        }
        CleanFile other = (CleanFile) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ecommerce.dasient.model.CleanFile[id=" + id + "]";
    }
}
