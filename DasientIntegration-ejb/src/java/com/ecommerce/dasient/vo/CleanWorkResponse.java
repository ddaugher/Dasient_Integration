package com.ecommerce.dasient.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class CleanWorkResponse implements Serializable {

    private long workUnitId;

    public long getWorkUnitId() {
        return workUnitId;
    }

    public void setWorkUnitId(long workUnitId) {
        this.workUnitId = workUnitId;
    }

    private long ruleSetId;

    public long getRuleSetId() {
        return ruleSetId;
    }

    public void setRuleSetId(long ruleSetId) {
        this.ruleSetId = ruleSetId;
    }

    private String webServer;

    public String getWebServer() {
        return webServer;
    }

    public void setWebServer(String webServer) {
        this.webServer = webServer;
    }

    private Date startTime;

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    private Date finishTime;

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    public static class File implements Serializable {

        private String path;

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        private Date modifyTime;

        public Date getModifyTime() {
            return modifyTime;
        }

        public void setModifyTime(Date modifyTime) {
            this.modifyTime = modifyTime;
        }

        private Date changeTime;

        public Date getChangeTime() {
            return changeTime;
        }

        public void setChangeTime(Date changeTime) {
            this.changeTime = changeTime;
        }

        private Date newChangeTime;

        public Date getNewChangeTime() {
            return newChangeTime;
        }

        public void setNewChangeTime(Date newChangeTime) {
            this.newChangeTime = newChangeTime;
        }

        private String hash;

        public String getHash() {
            return hash;
        }

        public void setHash(String hash) {
            this.hash = hash;
        }

        private int mode;

        public int getMode() {
            return mode;
        }

        public void setMode(int mode) {
            this.mode = mode;
        }

        public static class Match implements Serializable {

            private long ruleId;

            public long getRuleId() {
                return ruleId;
            }

            public void setRuleId(long ruleId) {
                this.ruleId = ruleId;
            }

            private int start;

            public int getStart() {
                return start;
            }

            public void setStart(int start) {
                this.start = start;
            }

            private int end;

            public int getEnd() {
                return end;
            }

            public void setEnd(int end) {
                this.end = end;
            }

            private String literal;

            public String getLiteral() {
                return literal;
            }

            public void setLiteral(String literal) {
                this.literal = literal;
            }

        }

        private List<Match> matches;

        public List<Match> getMatches() {
            return matches;
        }

        public void setMatches(List<Match> matches) {
            this.matches = matches;
        }

    }

    private List<File> files;

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

}
