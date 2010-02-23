package com.ecommerce.dasient.model;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "revision_type")
@NamedQueries({
    @NamedQuery(name = "getRevisionTypes", query = "select type from RevisionType type order by type.displayName")
})
public class RevisionType implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @Enumerated(EnumType.STRING)
    @Column(name = "revision_type_id", nullable = false, length = 16)
    private RevisionTypeId id;

    public RevisionTypeId getId() {
        return id;
    }

    public void setId(RevisionTypeId id) {
        this.id = id;
    }

    @Basic(optional = false)
    @Column(name = "display_name", nullable = false, length = 16)
    private String displayName;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof RevisionType)) {
            return false;
        }
        RevisionType other = (RevisionType) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ecommerce.dasient.model.RevisionType[id=" + id + "]";
    }

}
