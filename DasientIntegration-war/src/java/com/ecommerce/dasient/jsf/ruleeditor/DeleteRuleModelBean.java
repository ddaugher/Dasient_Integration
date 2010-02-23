package com.ecommerce.dasient.jsf.ruleeditor;

import java.io.Serializable;

public class DeleteRuleModelBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private long ruleId;

    public long getRuleId() {
        return ruleId;
    }

    public void setRuleId(long ruleId) {
        this.ruleId = ruleId;
    }

    private long revisionId;

    public long getRevisionId() {
        return revisionId;
    }

    public void setRevisionId(long revisionId) {
        this.revisionId = revisionId;
    }

    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private String action;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    private String pattern1;

    public String getPattern1() {
        return pattern1;
    }

    public void setPattern1(String pattern1) {
        this.pattern1 = pattern1;
    }

    private String pattern2;

    public String getPattern2() {
        return pattern2;
    }

    public void setPattern2(String pattern2) {
        this.pattern2 = pattern2;
    }

    private String pattern3;

    public String getPattern3() {
        return pattern3;
    }

    public void setPattern3(String pattern3) {
        this.pattern3 = pattern3;
    }

    private String pattern4;

    public String getPattern4() {
        return pattern4;
    }

    public void setPattern4(String pattern4) {
        this.pattern4 = pattern4;
    }

    private String pattern5;

    public String getPattern5() {
        return pattern5;
    }

    public void setPattern5(String pattern5) {
        this.pattern5 = pattern5;
    }

    private String pattern6;

    public String getPattern6() {
        return pattern6;
    }

    public void setPattern6(String pattern6) {
        this.pattern6 = pattern6;
    }

    private String revisionComment;

    public String getRevisionComment() {
        return revisionComment;
    }

    public void setRevisionComment(String revisionComment) {
        this.revisionComment = revisionComment;
    }

}
