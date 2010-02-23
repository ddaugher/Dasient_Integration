package com.ecommerce.dasient.jsf.ruleeditor;

import com.ecommerce.dasient.model.Revision;
import java.util.List;

public class ReadAuthorModelBean {

    private String author;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    private List<Revision> revisions;

    public List<Revision> getRevisions() {
        return revisions;
    }

    public void setRevisions(List<Revision> revisions) {
        this.revisions = revisions;
    }

}
