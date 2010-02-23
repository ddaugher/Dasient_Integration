package com.ecommerce.dasient.jsf.ruleeditor;

import com.ecommerce.dasient.model.Revision;
import java.util.List;

public class ListRevisionsModelBean {

    private List<Revision> revisions;

    public List<Revision> getRevisions() {
        return revisions;
    }

    public void setRevisions(List<Revision> revisions) {
        this.revisions = revisions;
    }

}
