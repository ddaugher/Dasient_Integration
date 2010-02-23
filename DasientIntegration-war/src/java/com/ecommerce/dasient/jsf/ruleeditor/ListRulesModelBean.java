package com.ecommerce.dasient.jsf.ruleeditor;

import java.util.List;

public class ListRulesModelBean {

    private String latestRevisionMappingId;

    public String getLatestRevisionMappingId() {
        return latestRevisionMappingId;
    }

    public void setLatestRevisionMappingId(String latestRevisionMappingId) {
        this.latestRevisionMappingId = latestRevisionMappingId;
    }

    private String specificRevisionMappingId;

    public String getSpecificRevisionMappingId() {
        return specificRevisionMappingId;
    }

    public void setSpecificRevisionMappingId(String specificRevisionMappingId) {
        this.specificRevisionMappingId = specificRevisionMappingId;
    }

    private Long revisionId;

    public Long getRevisionId() {
        return revisionId;
    }

    public void setRevisionId(Long revisionId) {
        this.revisionId = revisionId;
    }

    private Boolean showAll;

    public Boolean getShowAll() {
        return showAll;
    }

    public void setShowAll(Boolean showAll) {
        this.showAll = showAll;
    }

    private Long preceedingRevision;

    public Long getPreceedingRevision() {
        return preceedingRevision;
    }

    public void setPreceedingRevision(Long preceedingRevision) {
        this.preceedingRevision = preceedingRevision;
    }

    private Long succeedingRevision;

    public Long getSucceedingRevision() {
        return succeedingRevision;
    }

    public void setSucceedingRevision(Long succeedingRevision) {
        this.succeedingRevision = succeedingRevision;
    }

    public static class RuleEntry {
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

        private long noMatchesLast30days;

        public long getNoMatchesLast30days() {
            return noMatchesLast30days;
        }

        public void setNoMatchesLast30days(long noMatchesLast30days) {
            this.noMatchesLast30days = noMatchesLast30days;
        }

    };

    private List<RuleEntry> rules;

    public List<RuleEntry> getRules() {
        return rules;
    }

    public void setRules(List<RuleEntry> rules) {
        this.rules = rules;
    }

}
