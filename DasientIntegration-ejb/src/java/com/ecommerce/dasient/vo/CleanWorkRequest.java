package com.ecommerce.dasient.vo;

import java.io.Serializable;
import java.util.List;

public class CleanWorkRequest implements Serializable {

    private long workUnitId;

    public long getWorkUnitId() {
        return workUnitId;
    }

    public void setWorkUnitId(long workUnitId) {
        this.workUnitId = workUnitId;
    }

    private String controlPanel;

    public String getControlPanel() {
        return controlPanel;
    }

    public void setControlPanel(String controlPanel) {
        this.controlPanel = controlPanel;
    }

    private String webServer;

    public String getWebServer() {
        return webServer;
    }

    public void setWebServer(String webServer) {
        this.webServer = webServer;
    }

    private String webUsername;

    public String getWebUsername() {
        return webUsername;
    }

    public void setWebUsername(String webUsername) {
        this.webUsername = webUsername;
    }

    private int accountId;

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public static class DasientCommunication implements Serializable {

        private long scanHistoryId;

        public long getScanHistoryId() {
            return scanHistoryId;
        }

        public void setScanHistoryId(long scanHistoryId) {
            this.scanHistoryId = scanHistoryId;
        }

        private String rawRequest;

        public String getRawRequest() {
            return rawRequest;
        }

        public void setRawRequest(String rawRequest) {
            this.rawRequest = rawRequest;
        }

        private String rawResponse;

        public String getRawResponse() {
            return rawResponse;
        }

        public void setRawResponse(String rawResponse) {
            this.rawResponse = rawResponse;
        }

    }

    private List<DasientCommunication> triggers;

    public List<DasientCommunication> getTriggers() {
        return triggers;
    }

    public void setTriggers(List<DasientCommunication> triggers) {
        this.triggers = triggers;
    }

}
