package com.ecommerce.dasient.model;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "web_storage")
public class WebStorage implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "web_storage_seq")
    @SequenceGenerator(name = "web_storage_seq", sequenceName = "web_storage_seq")
    @Column(name = "web_storage_id", nullable = false)
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

    @Column(name = "max_concurrent_cleans", nullable = false)
    private int maxConcurrentCleans = 1;

    public int getMaxConcurrentCleans() {
        return maxConcurrentCleans;
    }

    public void setMaxConcurrentCleans(int maxConcurrentCleans) {
        this.maxConcurrentCleans = maxConcurrentCleans;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof WebStorage)) {
            return false;
        }
        WebStorage other = (WebStorage) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ecommerce.dasient.model.WebStorage[id=" + id + "]";
    }

}
