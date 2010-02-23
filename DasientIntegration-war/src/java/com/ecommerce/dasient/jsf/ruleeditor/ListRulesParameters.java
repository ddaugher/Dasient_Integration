package com.ecommerce.dasient.jsf.ruleeditor;

public class ListRulesParameters {

    private Long revisionId;

    public Long getRevisionId() {
        return revisionId;
    }

    public void setRevisionId(Long revisionId) {
        this.revisionId = revisionId;
    }

    private boolean showAll = false;

    public boolean isShowAll() {
        return showAll;
    }

    public void setShowAll(boolean showAll) {
        this.showAll = showAll;
    }

    public void setShowAllToTrue() {
        showAll = true;
    }

}
